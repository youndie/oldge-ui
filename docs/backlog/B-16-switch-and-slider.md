---
id: B-16
title: "Switch and Slider"
status: done
priority: P1
size: M
stage: stage-2-components
blocked_by: [B-08, B-09, B-10, B-11]
---

# B-16 — Switch and Slider

Switch: a sunken capsule with a chrome knob and an ON/OFF lamp (one of the two fixed-size texts, research §1.7), the fill interpolating, the knob on `ease-bounce`. Slider: a sunken trough with an accent fill and a ball thumb.

- **References**: `Switch_{Toxic,Media,Crystal}`, `Slider_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
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
  - `reference/design-system/components/Switch/README.md`, `reference/design-system/components/Switch/preview.html`
  - `reference/design-system/components/Slider/README.md`, `reference/design-system/components/Slider/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/`

## Findings (2026-09-25)

- Parity (±16, floor 0.72 %), on the first run:
  - Switch: 1.05 / 1.06 / 1.13 % (Toxic / Media / Crystal);
  - Slider: 1.30 / 1.30 / 1.41 %.

  The `_DIFF`s are red on glyph and knob or ball edges. The Slider's head is the 13 px label style,
  which is B-46's.
- `cssBox` paints `radial-gradient(circle at x y, …)` (`CssBackground.Radial`: farthest-corner
  radius, stops in fractions of it). The Switch knob, the Slider ball and the radio dot use it; the
  radio dot's private brush from B-15 is gone and its golden did not move.
- The lamp is one of the design's two fixed-size texts (research §1.7): 8 px on 10 px as dp
  converted to sp, so the system font scale does not grow it. No fixture runs at a font scale other
  than 1, so pixels do not verify this yet. EdgeScale (B-39) will.
- The Slider follows the README's "a native range input": 1000 steps, arrows move one, Page
  Up/Down a tenth, Home/End the ends. A screen reader gets the label, `valueText` as the state and
  `setProgress`. A press anywhere on the 44 dp strip moves the ball to the finger, on the thumb
  travel Chrome uses: 1 px inside the track, `travel = width − 2 − 26`.
- `OldgeSwitch` takes an `interactionSource`, so a golden can hold it pressed. The held knob is
  30 px, and when on it starts at 22 so its right edge stays put (`SwitchStates`).
- The READMEs' rules, in the API or `SwitchSliderBehaviourTest`:
  - the whole Switch row is a 44 dp switch and acts at once (no confirm step);
  - a disabled switch ignores taps;
  - the Slider is named, reads its value text, can be set, answers the native keys, and moves the
    ball to the finger;
  - «use a Checkbox when a Save confirms» is a KDoc line.
- Mutations, each failing, restored:
  - in `SwitchSliderBehaviourTest`: the switch role, disabled made enabled, the key step, the
    finger-to-value mapping, the value text;
  - in the goldens: the held knob width, the trough's hard stop, the knob's gradient stop.
- Values the tokens do not hold, marked `// css literal:`:
  - Switch: track 56 × 30, knob 24 / 30 held, knob positions 2 / 28 / 22, lamp inset 9 and
    8 px / 10 px;
  - Slider: head gap 2, trough 8, ball 26.
