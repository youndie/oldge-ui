---
id: B-14
title: "Chip and ChipGroup"
status: done
priority: P1
size: S
stage: stage-2-components
blocked_by: [B-12]
---

# B-14 — Chip and ChipGroup

Three kinds — `assist`, `filter` (selected fills with accent, the check pops with a turn), `input` (with remove) — and `ChipGroup` with a label and an optional single scrolling row. Visible height 34, hit area 46.

- **References**: `Chip_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
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
  - `reference/design-system/components/Chip/README.md`, `reference/design-system/components/Chip/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/`

## Findings (2026-09-25)

- Parity (±16, floor 0.72 %): Chip 6.24 / 6.34 / 6.47 % (Toxic / Media / Crystal), over the 5 %
  tolerance, which is a report here (`designStrict` off). The `_DIFF` is red on label glyphs and
  on the box edges of drifted chips. Two causes, neither in the chips:
  - Every 13 px label is a pixel low against Chrome, though the chip and its line box are where CSS
    puts them. Probed, not fixed; it is **B-46**, with the numbers, the probe
    (`scripts/research/baseline-probe.mjs`) and a refuted first fix.
  - A label's width is rounded up to a pixel, so each chip after it drifts by one (research §1.10,
    B-12).
- **The input chip is 36 px, not 34**: `span.og-chip` is content-box, and only buttons are
  border-box in Chrome's UA sheet. The reference measures it, and `ChipBehaviourTest` holds it.
- The 46 px touch zone is `oldgeHitArea`: a layout that reports the drawn size while the clickable
  after it is measured larger. Compose hit-tests outside a parent's bounds, and a touch 4 px above
  the drawn chip presses it (`ChipBehaviourTest`). The remove button's 44 px zone round its 22 px
  disc is the same modifier.
- The chip's flash is drawn over the label (`OldgeIndication(flashOverContent = true)`): its
  `.og-chip__fx` is positioned and paints over in-flow content. No golden shows a flash (reduced
  motion), so this is unverified by pixels.
- **A B-13 painter defect, fixed here because the item's pressed goldens showed it**: the clip
  layer's erase ended on the layer's edge and, under a press's scale, left a faint frame. B-13's
  three pressed-button goldens carried it and were re-recorded (research §1.4).
- The README's rules, in the API or `ChipBehaviourTest`:
  - 34 dp drawn and 46 dp touched;
  - the input chip's remove button takes a required `removeLabel` (icon-only);
  - the filter chip toggles.
- Mutations, each failing, restored:
  - in `ChipBehaviourTest`: no vertical reach, the input chip border-box, the remove label, the
    toggle, the remove button's reach;
  - in the goldens: the erase edge (6), the 6 px gap (6).
- Values the tokens do not hold, marked `// css literal:`: height 34, gap 6, icon 16, reach 6 / 2,
  remove disc 22 with pull 6, reach 11, icon 12, ring offset 1, scroll padding 4.
