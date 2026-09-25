---
id: B-13
title: "Fab and Segmented"
status: done
priority: P1
size: S
stage: stage-2-components
blocked_by: [B-12]
---

# B-13 — Fab and Segmented

Fab: the floating action in accent, icon bounce on press. Segmented: a 2–4 option capsule whose selected option interpolates its gloss; the README's rule — a label over 12 characters means RadioGroup or Select instead — becomes a KDoc line, not a runtime check.

- **References**: `Fab_{Toxic,Media,Crystal}`, `Segmented_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
- **Read first**: the component's README (the rules), its `preview.html` (the demo to port), and
  its `.og-` block in `reference/design-system/components/bundle.css` (the exact numbers). The skill is `design-to-compose`
  from Step 2, since the references already exist (B-03).

- AC: each component is a public composable in `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/` taking every value from the theme
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
  - `reference/design-system/components/Fab/README.md`, `reference/design-system/components/Fab/preview.html`
  - `reference/design-system/components/Segmented/README.md`, `reference/design-system/components/Segmented/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/`

## Findings (2026-09-25)

- Parity (±16, floor 0.72 %): Fab 0.33 / 0.33 / 0.29 % (Toxic / Media / Crystal), below the floor,
  on the first run. Segmented 1.28 / 1.44 / 1.69 %, from 1.77 / 1.88 / 1.82 %, after one cause in
  the shared painter. `cssBox` painted a bordered rounded box's background and border one over the
  other, so their outer edge was covered twice. It now paints them in one layer clipped once, as
  Blink does. The measurements are in research §1.4, "As built, B-13", and include four older probes
  that moved with it. What is left in Segmented is half label edges (centred labels at a fractional
  x in Chrome, §1.10) and half the selected option's arcs.
- The painter change re-recorded 21 goldens outside this item, each by 0.06–0.24 %, all on bordered
  boxes: Button, ButtonStates, MaterialGloss, MaterialLcdGlow, MaterialPanel, Press (Flash, Focus).
  They were recorded again because the rule changed under them, not because they were stale.
- The Fab's frame is CSS's `background: <fill> padding-box, <rim> border-box` under a transparent
  3 px border. `cssBox` takes it as `borderBackground`. The focus ring is 3 px out on a Fab
  (`outline-offset: 3px`), so `OldgeIndication` takes a `ringOffset`. No golden shows a focused
  Fab, so that offset is unverified by pixels.
- Chrome's UA stylesheet makes every `button` `box-sizing: border-box`, so a segment's
  `min-height: 40px` includes its padding and border. The group is 46 px, as the reference measures.
- The READMEs' rules, in the API or `FabSegmentedBehaviourTest`: an icon Fab takes a non-null
  `contentDescription`, and an extended Fab is named by its label. `expanded` offers expand or
  collapse, the opposite of its state. Segmented takes 2 to 4 options (`require`), and each option is
  a radio button. The 12-character rule is a KDoc line, as the item decided.
- Mutations, each failing, restored:
  - in `FabSegmentedBehaviourTest`: icon Fab width, expand and collapse swapped, the option-count
    `require`, the segment's role;
  - in the goldens: the Fab highlight (a first attempt failed only on a compile warning and was
    redone), the 135° turn, the selected segment's border, the clip layer (27 goldens).
- Values the tokens do not hold, marked `// css literal:`:
  - Fab: 56, the 3 px frame, padding 16 / 20, icon 26, highlight 6 / 2 / 10, ring offset 3, dock
    bottom 88;
  - Segmented: 2 px inner padding and gap, min height 40, block padding 4, icon 18, 0.875rem text.
