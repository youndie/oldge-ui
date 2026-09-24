package io.github.youndie.oldge.tokens

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The generated token layer against a second, independent reading of `tokens.json` (B-05).
 *
 * `scripts/generate_tokens.py --check` already fails `check` when the committed file is stale; this
 * test is what fails when the generator itself converts a value wrongly — it parses the same JSON
 * with different code and compares every token of every family, per skin, through
 * [OldgeTokenIndex], which points at the generated properties rather than restating them.
 */
class OldgeTokensTest {
    private val tokens: JsonObject =
        Json
            .parseToJsonElement(
                File("../reference/design-system/tokens.json").readText(),
            ).jsonObject
    private val skins = tokens.obj("color")["themes"]!!.jsonArray.map { it.jsonObject.str("id") }

    @Test
    fun the_skins_are_the_design_systems() = assertEquals(skins, OldgeTokenIndex.skins)

    @Test
    fun every_colour_matches_in_every_skin() {
        for (skin in skins) {
            val generated = OldgeTokenIndex.colors(skin)
            val expected = family("color").associate { it.str("name") to parseColor(value(it, skin)) }
            assertEquals(expected.keys, generated.keys, "colour tokens of $skin")
            for ((name, colour) in expected) assertEquals(colour, generated[name], "$skin $name")
        }
    }

    @Test
    fun every_shadow_matches_in_every_skin() {
        for (skin in skins) {
            val generated = OldgeTokenIndex.shadows(skin)
            val expected = family("shadow").associate { it.str("name") to parseShadows(value(it, skin)) }
            assertEquals(expected.keys, generated.keys, "shadow tokens of $skin")
            for ((name, layers) in expected) assertEquals(layers, generated[name], "$skin $name")
        }
    }

    @Test
    fun spacing_radii_and_durations_match() {
        assertEquals(family("spacing").associate { it.str("name") to px(it.str("value")) }, OldgeTokenIndex.spacing)
        assertEquals(family("radius").associate { it.str("name") to px(it.str("value")) }, OldgeTokenIndex.radius)
        assertEquals(
            family("duration").associate {
                it.str("name") to it.str("value").removeSuffix("ms").toInt()
            },
            OldgeTokenIndex.duration,
        )
    }

    @Test
    fun easings_match_at_every_tenth() {
        val expected = family("easing").associate { it.str("name") to parseEasing(it.str("value")) }
        assertEquals(expected.keys, OldgeTokenIndex.easing.keys)
        for ((name, easing) in expected) {
            for (i in 0..10) {
                val t = i / 10f
                assertEquals(
                    easing.transform(t),
                    OldgeTokenIndex.easing.getValue(name).transform(t),
                    1e-6f,
                    "$name at $t",
                )
            }
        }
    }

    /**
     * Research §1.5's open question: Compose accepts a cubic-bezier whose y leaves 0…1, and the curve
     * really overshoots — the design system's spring and bounce depend on it.
     */
    @Test
    fun spring_overshoots_and_bounce_undershoots() {
        val springPeak = (0..100).maxOf { OldgeEasings.spring.transform(it / 100f) }
        val bounceLow = (0..100).minOf { OldgeEasings.bounce.transform(it / 100f) }
        assertTrue(springPeak > 1.05f, "ease-spring peaks at $springPeak")
        assertTrue(bounceLow < -0.05f, "ease-bounce dips to $bounceLow")
    }

    @Test
    fun every_type_style_matches() {
        val expected =
            tokens
                .obj("type")["groups"]!!
                .jsonArray
                .flatMap { group ->
                    val g = group.jsonObject
                    g["styles"]!!.jsonArray.map { style ->
                        val s = style.jsonObject
                        val familyKey = (s["family"] ?: g["family"])!!.jsonPrimitive.content
                        s.str("name") to
                            OldgeTypeStyle(
                                family =
                                    OldgeFontFamilyKey.entries.single {
                                        it.name.equals(
                                            familyKey,
                                            ignoreCase = true,
                                        )
                                    },
                                sizeRem = s.str("fontSize").removeSuffix("rem").toFloat(),
                                lineHeightRem = s.str("lineHeight").removeSuffix("rem").toFloat(),
                                weight = s["fontWeight"]!!.jsonPrimitive.int,
                                letterSpacingEm =
                                    s["letterSpacing"]
                                        ?.jsonPrimitive
                                        ?.content
                                        ?.removeSuffix("em")
                                        ?.toFloat() ?: 0f,
                            )
                    }
                }.toMap()
        assertEquals(expected, OldgeTokenIndex.type)
    }

    private fun JsonObject.obj(key: String) = getValue(key).jsonObject

    private fun JsonObject.str(key: String) = getValue(key).jsonPrimitive.content

    private fun family(key: String) = tokens.obj(key)["tokens"]!!.jsonArray.map { it.jsonObject }

    /** A skin's value, the first skin's when the skin has none, and an alias followed. */
    private fun value(
        token: JsonObject,
        skin: String,
    ): String {
        val raw: JsonElement = token.getValue("value")
        val text =
            if (raw is JsonPrimitive) {
                raw.content
            } else {
                raw.jsonObject[skin]?.jsonPrimitive?.content
                    ?: raw.jsonObject
                        .getValue(skins.first())
                        .jsonPrimitive.content
            }
        val alias = Regex("""^\{(.+)}$""").find(text)?.groupValues?.get(1) ?: return text
        return value(family("color").single { it.str("name") == alias }, skin)
    }

    private fun parseColor(text: String): Color {
        val t = text.trim()
        if (t.startsWith("#")) {
            val hex = t.drop(1).let { h -> if (h.length <= 4) h.map { "$it$it" }.joinToString("") else h }
            val rgb = hex.take(6).toLong(16)
            val alpha = if (hex.length == 8) hex.substring(6).toInt(16) else 255
            return Color((alpha.toLong() shl 24) or rgb)
        }
        val parts =
            Regex("""rgba?\(([^)]*)\)""")
                .find(t)!!
                .groupValues[1]
                .split(",")
                .map { it.trim().toFloat() }
        val alpha = ((parts.getOrElse(3) { 1f }) * 255).roundToInt()
        return Color(parts[0].roundToInt(), parts[1].roundToInt(), parts[2].roundToInt(), alpha)
    }

    private fun parseShadows(text: String): List<OldgeShadow> {
        if (text.trim() == "none") return emptyList()
        return Regex(""",(?![^(]*\))""").split(text).map { layer ->
            val colour = Regex("""(rgba?\([^)]*\)|#[0-9a-fA-F]+)\s*$""").find(layer.trim())!!
            val words =
                layer
                    .trim()
                    .substring(0, colour.range.first)
                    .trim()
                    .split(Regex("\\s+"))
            val inset = words.first() == "inset"
            val lengths = words.drop(if (inset) 1 else 0).map(::px) + List(4) { 0.dp }
            OldgeShadow(inset, lengths[0], lengths[1], lengths[2], lengths[3], parseColor(colour.value))
        }
    }

    private fun px(text: String): Dp = text.removeSuffix("px").toFloat().dp

    private fun parseEasing(text: String): Easing {
        if (text == "linear") return LinearEasing
        val (a, b, c, d) =
            Regex("""cubic-bezier\(([^)]*)\)""").find(text)!!.groupValues[1].split(",").map {
                it.trim().toFloat()
            }
        return CubicBezierEasing(a, b, c, d)
    }
}

private fun assertEquals(
    expected: Float,
    actual: Float,
    tolerance: Float,
    message: String,
) = assertTrue(abs(expected - actual) <= tolerance, "$message: expected $expected, got $actual")
