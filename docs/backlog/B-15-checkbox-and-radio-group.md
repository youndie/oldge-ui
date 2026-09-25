---
id: B-15
title: "Checkbox and RadioGroup"
status: done
priority: P1
size: S
stage: stage-2-components
blocked_by: [B-08, B-09, B-10, B-11]
---

# B-15 — Checkbox and RadioGroup

A sunken square with a green check, the whole row a 44 px target; the radio group's sunken circles. Both `toggleable`/`selectable` with the right semantics role.

- **References**: `Checkbox_{Toxic,Media,Crystal}`, `RadioGroup_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
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
  - `reference/design-system/components/Checkbox/README.md`, `reference/design-system/components/Checkbox/preview.html`
  - `reference/design-system/components/RadioGroup/README.md`, `reference/design-system/components/RadioGroup/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/`

## Findings (2026-09-25)

- Parity (±16, floor 0.72 %):
  - Checkbox: 1.22 / 1.21 / 1.12 % (Toxic / Media / Crystal), on the first run.
  - RadioGroup: 1.83 / 1.83 / 1.76 %.

  The `_DIFF` is red only on glyph and circle edges, except for the RadioGroup legend: it is the
  13 px label style and draws a pixel low (about half of RadioGroup's diff). That is B-46's
  cause, recorded there, not a new one. The rows are aligned (a best-offset search puts every row
  label at 0, 0).
- `og-pop` now lives in one place, `oldgePopIn` in `press/`, shared by the chip's check and the
  checkbox's check and radio dot. The chip goldens are unchanged by the move.
- `:disabled` changes the box's fill, border and colour and keeps its sunken bevel; the radio dot
  keeps its gradient. Both as bundle.css has them. A disabled golden (`CheckboxStates`) shows an
  empty disabled checkbox and a disabled radio group with a choice.
- The READMEs' rules, in the API or `CheckBehaviourTest`:
  - the whole row is the 44 dp target, and tapping its label toggles it;
  - a disabled checkbox or radio ignores taps;
  - the roles are Checkbox and RadioButton;
  - a group takes 2 to 6 options (`require`);
  - «the label is a statement that becomes true» is a KDoc line.
- Mutations, each failing, restored:
  - in `CheckBehaviourTest`: disabled made enabled, the row's minimum height, the option-count
    `require`, the radio role;
  - in the goldens: the disabled fill, the dot's gradient centre, the legend gap.
- Values the tokens do not hold, marked `// css literal:`: box 20, check 16, dot 10, legend gap 2.
