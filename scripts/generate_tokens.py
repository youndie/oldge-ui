#!/usr/bin/env python3
"""Generate the Kotlin token layer from the vendored design system's tokens.json (research D4, B-05).

    python3 scripts/generate_tokens.py            # write the file
    python3 scripts/generate_tokens.py --check    # fail if the committed file differs

Standard library only, so `./gradlew check` can run `--check` on any machine with a python3. The
output is committed rather than generated into build/: a reviewer sees a token change as a Kotlin
diff next to the JSON diff that caused it, and an IDE indexes it without a build (kvadrant-ui made
the same call, its D12).

Every token of every family is emitted — the design system is small and closed (research §1.1), and
`OldgeTokensTest` holds the generated values to an independent reading of the same JSON, so an
unread token is still a checked one.

Conversions (research D5): a CSS px is a dp, a rem is 16 sp and stays a rem number here (the theme
turns it into sp), an alpha in rgba() is rounded to 8 bits the way a browser stores it, an alias
`{name}` resolves at generation time, and a colour a skin does not define inherits the first skin's.
"""
import argparse
import json
import math
import pathlib
import re
import sys

ROOT = pathlib.Path(__file__).resolve().parents[1]
SOURCE = ROOT / "reference/design-system/tokens.json"
TARGET = ROOT / "oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/tokens/OldgeTokens.kt"


def camel(name: str) -> str:
    head, *rest = name.split("-")
    return head + "".join(part[:1].upper() + part[1:] for part in rest)


def title(name: str) -> str:
    return name[:1].upper() + name[1:]


def kdoc(name: str, indent: str) -> str:
    """The token's name; its usage note stays in tokens.json, in the design system's language."""
    return f"{indent}/** Token `{name}`; its usage note is in reference/design-system/tokens.json. */\n"


def argb(value: str) -> str:
    """A CSS colour as `0xAARRGGBB`."""
    value = value.strip()
    m = re.fullmatch(r"#([0-9a-fA-F]{3,8})", value)
    if m:
        h = m.group(1).lower()
        if len(h) in (3, 4):
            h = "".join(c * 2 for c in h)
        if len(h) == 6:
            h += "ff"
        if len(h) != 8:
            raise ValueError(f"not a colour: {value}")
        return f"0x{h[6:8]}{h[0:6]}".upper().replace("0X", "0x")
    m = re.fullmatch(r"rgba?\(\s*([\d.]+)\s*,\s*([\d.]+)\s*,\s*([\d.]+)\s*(?:,\s*([\d.]+)\s*)?\)", value)
    if m:
        # Half rounds up, as a browser rounds a float channel to 8 bits. Python's round() rounds
        # half to even, which put rgba(0,0,0,0.7) one level below Chrome's 179 (B-05, caught by
        # OldgeTokensTest's second reading of the JSON).
        r, g, b = (math.floor(float(x) + 0.5) for x in m.groups()[:3])
        a = 1.0 if m.group(4) is None else float(m.group(4))
        return f"0x{math.floor(a * 255 + 0.5):02X}{r:02X}{g:02X}{b:02X}"
    raise ValueError(f"not a colour: {value}")


def resolve(tokens, name, skin, first, depth=0):
    if depth > 16:
        raise ValueError(f"alias chain too deep at {name}")
    token = tokens[name]
    value = token["value"]
    value = value if isinstance(value, str) else value.get(skin, value[first])
    alias = re.fullmatch(r"\{(.+)\}", value)
    return resolve(tokens, alias.group(1), skin, first, depth + 1) if alias else value


def px(value: str) -> str:
    """A CSS length in px (or a unitless 0) as a Kotlin dp literal."""
    m = re.fullmatch(r"(-?[\d.]+)(px)?", value.strip())
    if not m or (m.group(2) is None and float(m.group(1)) != 0):
        raise ValueError(f"not a px length: {value}")
    number = float(m.group(1))
    return f"{number:g}.dp" if number != int(number) else f"{int(number)}.dp"


def split_commas(value: str):
    parts, depth, current = [], 0, ""
    for c in value:
        depth += c == "("
        depth -= c == ")"
        if c == "," and depth == 0:
            parts.append(current)
            current = ""
        else:
            current += c
    return [p.strip() for p in parts + [current] if p.strip()]


def shadows(value: str):
    """A CSS box-shadow list as `OldgeShadow(...)` constructor calls."""
    if value.strip() == "none":
        return []
    out = []
    for layer in split_commas(value):
        colour = re.search(r"(rgba?\([^)]*\)|#[0-9a-fA-F]{3,8})\s*$", layer)
        if not colour:
            raise ValueError(f"shadow without a colour: {layer}")
        words = layer[: colour.start()].split()
        inset = words[:1] == ["inset"]
        lengths = [px(w) for w in words[1 if inset else 0 :]]
        if not 2 <= len(lengths) <= 4:
            raise ValueError(f"shadow with {len(lengths)} lengths: {layer}")
        lengths += ["0.dp"] * (4 - len(lengths))
        out.append(
            f"OldgeShadow(inset = {'true' if inset else 'false'}, offsetX = {lengths[0]}, offsetY = {lengths[1]}, "
            f"blur = {lengths[2]}, spread = {lengths[3]}, color = Color({argb(colour.group(1))}))"
        )
    return out


def rem(value: str) -> str:
    m = re.fullmatch(r"([\d.]+)rem", value)
    if not m:
        raise ValueError(f"not a rem: {value}")
    return f"{float(m.group(1)):g}f"


def em(value) -> str:
    if value is None:
        return "0f"
    m = re.fullmatch(r"([\d.]+)em", value)
    if not m:
        raise ValueError(f"not an em: {value}")
    return f"{float(m.group(1)):g}f"


def easing(value: str) -> str:
    if value.strip() == "linear":
        return "LinearEasing"
    m = re.fullmatch(r"cubic-bezier\(([^)]*)\)", value.strip())
    if not m:
        raise ValueError(f"not an easing: {value}")
    return "CubicBezierEasing(" + ", ".join(f"{float(x):g}f" for x in m.group(1).split(",")) + ")"


def millis(value: str) -> str:
    m = re.fullmatch(r"(\d+)ms", value.strip())
    if not m:
        raise ValueError(f"not a duration: {value}")
    return m.group(1)


def strip(prefix: str, name: str) -> str:
    return camel(name[len(prefix) :] if name.startswith(prefix) else name)


def generate() -> str:
    t = json.loads(SOURCE.read_text())
    skins = [s["id"] for s in t["color"]["themes"]]
    first = skins[0]
    colors = {tok["name"]: tok for tok in t["color"]["tokens"]}
    shadow_tokens = {tok["name"]: tok for tok in t["shadow"]["tokens"]}
    lines = []
    w = lines.append
    w("// GENERATED by scripts/generate_tokens.py from reference/design-system/tokens.json.")
    w("// Do not edit: `./gradlew check` runs the generator with --check, and OldgeTokensTest holds every")
    w("// value to its own reading of the JSON.")
    w("@file:Suppress(\"ktlint\")")
    w("")
    w("package io.github.youndie.oldge.tokens")
    w("")
    w("import androidx.compose.animation.core.CubicBezierEasing")
    w("import androidx.compose.animation.core.Easing")
    w("import androidx.compose.animation.core.LinearEasing")
    w("import androidx.compose.runtime.Immutable")
    w("import androidx.compose.ui.graphics.Color")
    w("import androidx.compose.ui.unit.Dp")
    w("import androidx.compose.ui.unit.dp")
    w("")

    # Colours.
    w("/**")
    w(" * The design system's colour tokens for one skin. A new skin is a new instance of this class:")
    w(" * the components read roles, never values (the design system's README, the section on skins).")
    w(" */")
    w("@Immutable")
    w("public class OldgeColors(")
    for name, tok in colors.items():
        w(kdoc(name, "    ") + f"    public val {camel(name)}: Color,")
    w(") {")
    w("    public companion object {")
    for skin in skins:
        w(f"        /** The {title(skin)} skin. */")
        w(f"        public val {title(skin)}: OldgeColors =")
        w("            OldgeColors(")
        for name in colors:
            w(f"                {camel(name)} = Color({argb(resolve(colors, name, skin, first))}),")
        w("            )")
        w("")
    lines[-1:] = ["    }", "}", ""]

    # Shadows.
    w("/** One layer of a CSS box-shadow: `inset` draws inside the shape's outline, a drop shadow outside it. */")
    w("@Immutable")
    w("public data class OldgeShadow(")
    w("    public val inset: Boolean,")
    w("    public val offsetX: Dp,")
    w("    public val offsetY: Dp,")
    w("    public val blur: Dp,")
    w("    public val spread: Dp,")
    w("    public val color: Color,")
    w(")")
    w("")
    w("/** The design system's shadow tokens for one skin; each is a list of layers, empty for `none`. */")
    w("@Immutable")
    w("public class OldgeShadows(")
    for name, tok in shadow_tokens.items():
        w(kdoc(name, "    ") + f"    public val {strip('shadow-', name)}: List<OldgeShadow>,")
    w(") {")
    w("    public companion object {")
    for skin in skins:
        w(f"        /** The {title(skin)} skin. */")
        w(f"        public val {title(skin)}: OldgeShadows =")
        w("            OldgeShadows(")
        for name in shadow_tokens:
            layers = shadows(resolve(shadow_tokens, name, skin, first))
            if layers:
                w(f"                {strip('shadow-', name)} =")
                w("                    listOf(")
                for layer in layers:
                    w(f"                        {layer},")
                w("                    ),")
            else:
                w(f"                {strip('shadow-', name)} = emptyList(),")
        w("            )")
        w("")
    lines[-1:] = ["    }", "}", ""]

    # Skin-independent families.
    def constants(obj, doc, family, prefix, kind, convert):
        w(f"/** {doc} */")
        w(f"public object {obj} {{")
        for tok in t[family]["tokens"]:
            w(kdoc(tok["name"], "    ") + f"    public val {strip(prefix, tok['name'])}: {kind} = {convert(tok['value'])}")
            w("")
        lines[-1:] = ["}", ""]

    constants("OldgeSpacing", "The 4 px spacing scale and the minimum touch target.", "spacing", "", "Dp", px)
    constants("OldgeRadii", "Corner radii; `pill` is 999 px, which is fully round at any size the design uses.", "radius", "radius-", "Dp", px)
    constants("OldgeDurations", "Durations in milliseconds. Under reduced motion the theme uses none of them (research D7).", "duration", "dur-", "Int", millis)
    constants("OldgeEasings", "The design system's four curves.", "easing", "ease-", "Easing", easing)

    # Type styles, as data: the theme resolves the family and turns rem into sp.
    w("/** The design system's font families (research D6 says which faces each one bundles). */")
    w("public enum class OldgeFontFamilyKey { " + ", ".join(title(k) for k in t["type"]["families"]) + " }")
    w("")
    w("/** A type style as the design system states it: rem for size and line height, em for spacing. */")
    w("@Immutable")
    w("public data class OldgeTypeStyle(")
    w("    public val family: OldgeFontFamilyKey,")
    w("    public val sizeRem: Float,")
    w("    public val lineHeightRem: Float,")
    w("    public val weight: Int,")
    w("    public val letterSpacingEm: Float,")
    w(")")
    w("")
    w("/** The design system's type styles. */")
    w("public object OldgeTypeStyles {")
    for group in t["type"]["groups"]:
        for s in group["styles"]:
            family = title(s.get("family", group["family"]))
            w(kdoc(s["name"], "    ") + f"    public val {camel(s['name'])}: OldgeTypeStyle =")
            w(
                f"        OldgeTypeStyle(OldgeFontFamilyKey.{family}, {rem(s['fontSize'])}, {rem(s['lineHeight'])}, "
                f"{int(s['fontWeight'])}, {em(s.get('letterSpacing'))})"
            )
            w("")
    lines[-1:] = ["}", ""]

    # The index the test reads: token name -> the generated property, never a second literal.
    w("/**")
    w(" * Every generated value by its design-system token name, per skin where it varies. It points at")
    w(" * the properties above rather than restating them, so a test that compares it with tokens.json")
    w(" * tests the properties.")
    w(" */")
    w("internal object OldgeTokenIndex {")
    w(f"    val skins: List<String> = listOf({', '.join(repr(s).replace(chr(39), chr(34)) for s in skins)})")
    w("")
    w("    fun colors(skin: String): Map<String, Color> {")
    w("        val c = when (skin) {")
    for skin in skins:
        w(f"            \"{skin}\" -> OldgeColors.{title(skin)}")
    w("            else -> error(\"no skin $skin\")")
    w("        }")
    w("        return mapOf(")
    for name in colors:
        w(f"            \"{name}\" to c.{camel(name)},")
    w("        )")
    w("    }")
    w("")
    w("    fun shadows(skin: String): Map<String, List<OldgeShadow>> {")
    w("        val s = when (skin) {")
    for skin in skins:
        w(f"            \"{skin}\" -> OldgeShadows.{title(skin)}")
    w("            else -> error(\"no skin $skin\")")
    w("        }")
    w("        return mapOf(")
    for name in shadow_tokens:
        w(f"            \"{name}\" to s.{strip('shadow-', name)},")
    w("        )")
    w("    }")
    w("")
    for obj, family, prefix, kind in [
        ("OldgeSpacing", "spacing", "", "Dp"),
        ("OldgeRadii", "radius", "radius-", "Dp"),
        ("OldgeDurations", "duration", "dur-", "Int"),
        ("OldgeEasings", "easing", "ease-", "Easing"),
    ]:
        w(f"    val {family}: Map<String, {kind}> =")
        w("        mapOf(")
        for tok in t[family]["tokens"]:
            w(f"            \"{tok['name']}\" to {obj}.{strip(prefix, tok['name'])},")
        w("        )")
        w("")
    w("    val type: Map<String, OldgeTypeStyle> =")
    w("        mapOf(")
    for group in t["type"]["groups"]:
        for s in group["styles"]:
            w(f"            \"{s['name']}\" to OldgeTypeStyles.{camel(s['name'])},")
    w("        )")
    w("}")
    w("")
    return "\n".join(lines)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    text = generate()
    if args.check:
        if not TARGET.exists() or TARGET.read_text() != text:
            print(f"{TARGET.relative_to(ROOT)} is stale: run python3 scripts/generate_tokens.py", file=sys.stderr)
            return 1
        print(f"{TARGET.relative_to(ROOT)} matches tokens.json")
        return 0
    TARGET.parent.mkdir(parents=True, exist_ok=True)
    TARGET.write_text(text)
    print(TARGET.relative_to(ROOT))
    return 0


if __name__ == "__main__":
    sys.exit(main())
