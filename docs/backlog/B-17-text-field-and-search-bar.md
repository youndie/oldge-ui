---
id: B-17
title: "TextField"
status: done
priority: P1
size: M
stage: stage-2-components
blocked_by: [B-08, B-09, B-10, B-11]
---

# B-17 — TextField

TextField: label above, a sunken white well, help and error lines (error = colour + icon + word), password reveal, multiline, minimum rather than fixed height. Built on `BasicTextField` (research D2).

*Split on 2026-09-25, when the item was picked:* SearchBar's preview shows an Avatar in its trailing
slot, which is B-31's, so its parity fixture cannot be ported string for string before B-31 is done.
SearchBar moved to **B-47**, blocked by B-31. TextField needs nothing from B-31.

- **References**: `TextField_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
- **Read first**: the component's README (the rules), its `preview.html` (the demo to port), and
  its `.og-` block in `reference/design-system/components/bundle.css` (the exact numbers). The skill is `design-to-compose`
  from Step 2, since the references already exist (B-03).

- AC: each component is a public composable in `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/` taking every value from the theme
  (research D4) — `grep -nE "Color\(0x|[0-9]+\.dp|[0-9]+\.sp" ` over the new files finds only
  token-layer references, and any exception is named in the commit body with its reason.
- AC: the design system's preview is ported as **one fixture per skin**, string for string and in
  the same order — `@ViddikScreenshot(group = "<Name>", name = "Toxic" | "Media" | "Crystal")` at the
  size `design/manifest.json` records for `<Name>_<Skin>.png`, inside the `OldgeDemo` harness (B-08).
- AC: `./gradlew :oldge-core:viddikDesignParity --component "<Name>*"` run for every component here;
  each fixture's `mismatchPercent` read against the floor B-08 measured, every non-`MATCH` fixture
  read as reference / `_ACTUAL` / `_DIFF` (at most five rounds per component, design-to-compose
  Step 5), and `summary.txt` for these fixtures in the squash commit's body with the token table.
- AC: the states the preview does not show but the component's README names (pressed, disabled,
  selected, focused, error — whichever apply) each have a golden fixture in group `<Name>States`;
  those are goldens, not parity, and are recorded for these fixtures only (`git status` shows no
  other PNG).
- AC: the rules in each component's README that are behaviour rather than look (an icon-only
  control needs a label, a minimum touch target, what a disabled control ignores) are encoded in
  the API or in a test, and the KDoc names the README rule it came from.
- AC: `ScreenshotSuiteTest` (B-09) no longer lists these components as missing; `./gradlew check`
  and `make check` green.

- Anchors:
  - `reference/design-system/components/TextField/README.md`, `reference/design-system/components/TextField/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/`

## Findings (2026-09-25)

- Parity (±16, floor 0.72 %): TextField 1.44 / 1.43 / 1.41 % (Toxic / Media / Crystal), from
  1.73 / 1.72 / 1.70 % after one cause. A single-line `BasicTextField` measured 19 px for a
  20 px line, so the row centred a 41 px field in 44 and «Павел» and «pavel@» landed a pixel low,
  while the placeholder, a full `OldgeText` line, did not. The inner line now has `line-height ×
  lines` as its minimum; the quirk is in `docs/services/oldge-core.md`. What is left is the three
  labels (13 px, B-46: each best at +1 px) and glyph edges.
- A first attempt, putting the field on `onCssBaseline`, did nothing and was removed. The field's
  `FirstBaseline` is already 15, as CSS's; the offset was its height, not its baseline.
- The split: SearchBar is **B-47**, blocked by B-31, whose Avatar its preview shows. That is
  recorded at the top of this item.
- The README's rules, in the API or `TextFieldBehaviourTest`:
  - an error is colour, icon and word, and a screen reader hears the field as invalid with the
    message (`error` semantics);
  - the reveal button is an OrbButton that says what it will do («Показать пароль» /
    «Скрыть пароль»);
  - `lines` makes the multiline field;
  - the height is a minimum (42 dp at 100 %, at least 62 dp at a 200 % system font);
  - a disabled field takes no input;
  - the label names the field for a screen reader;
  - «a placeholder is an example, not a label» is a KDoc line.
- The focus ring follows `:focus-within`, so it shows on any focus, not only the keyboard's. A
  focused golden (`TextFieldStates`) holds it, with the reveal button, a three-line field and a
  disabled one.
- Mutations, each failing, restored:
  - in `TextFieldBehaviourTest`: the label, the error semantics, the reveal wording, disabled made
    enabled, the line's minimum height;
  - in the goldens: the error border, the focus ring, the input's 11 px padding.
- Values the tokens do not hold, marked `// css literal:`: gap 6, icon 20, error icon 14, input
  padding 11, the reveal button's end 2 (`margin-right: -10px` off `space-3`).
