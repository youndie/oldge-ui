---
id: B-29
title: "Readout, Meter and ProgressBar"
status: done
priority: P1
size: M
stage: stage-2-components
blocked_by: [B-08, B-09, B-10, B-11]
---

# B-29 — Readout, Meter and ProgressBar

The LCD family: Readout's glowing digits (`readout`, `readout-sm`, no glow in Crystal), Meter's segment scale with low/mid/peak colours, ProgressBar's XP segments. The indeterminate ProgressBar under reduced motion is a full bar at 50 % opacity — that is what the reference shows (research §1.2). Cyrillic in a readout goes through B-02's per-script join.

- **References**: `Readout_{Toxic,Media,Crystal}`, `Meter_{Toxic,Media,Crystal}`, `ProgressBar_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
- **Read first**: the component's README (the rules), its `preview.html` (the demo to port), and
  its `.og-` block in `reference/design-system/components/bundle.css` (the exact numbers). The skill is `design-to-compose`
  from Step 2, since the references already exist (B-03).

- AC: each component is a public composable in `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/` taking every value from the theme
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
  - `reference/design-system/components/Readout/README.md`, `reference/design-system/components/Readout/preview.html`
  - `reference/design-system/components/Meter/README.md`, `reference/design-system/components/Meter/preview.html`
  - `reference/design-system/components/ProgressBar/README.md`, `reference/design-system/components/ProgressBar/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/`

## Findings (2026-09-25)

- Parity (±16, floor 0.72 %):
  - Meter: 1.22 / 1.20 / 1.19 % (Toxic / Media / Crystal), on the first run.
  - Readout: 2.20 / 2.28 / 2.24 %. The glass and the digits line up; the second and third readouts
    are 1–2 px wider, because their text widths are rounded up to whole pixels (research §1.10,
    B-12).
  - ProgressBar: 2.64 / 2.62 / 2.59 %, from 3.45–3.50 % after one cause. Under reduced motion the
    indeterminate bar's `opacity: .5` belongs to the whole element. Faded one draw at a time, the
    well-coloured gaps between the blocks came out wrong. It is now one layer at half opacity and
    matches Chrome's to the pixel. What is left is the heads (13 px, B-46, noted there) and the
    detail lines.
- The LCD tag (`pixel-tag` in `lcd-dim`, upper case) and LCD text (the lcd face joined with its
  companion) are shared in `Lcd.kt`. A readout's «°C» and a meter's «ГБ» go through the join
  (research D6). Readout's small size is the CSS's 1.375rem / 1.5rem with the readout's tracking,
  not the `readout-sm` token (1rem / 1.25rem), which is for a different place.
- The Meter's cells are fractional (`flex: 1`, 2 px apart), and each edge is rounded to a pixel as
  Chrome snaps a painted box. A cell's colour comes from where it sits: over 60 % of the scale
  `meter-mid`, over 85 % `meter-peak`.
- `oldgePopSoftIn` (`og-pop-soft`) joins `oldgePopIn` in `press/`; the ProgressBar's done and error
  lines pop in with it.
- The READMEs' rules, in the API or `LcdBehaviourTest`:
  - a readout is heard as its label, value and unit;
  - a meter's `valueText` is required and heard with its level;
  - a progress bar reports its value, or that it has none;
  - done and error add a word (and an icon), not only a colour;
  - «one to three readouts a screen» is a KDoc line.

  The running indeterminate bar is motion, and no golden shows it; the harness renders reduced
  motion, as the references do.
- Mutations through `scripts/mutate.py`: 6 of 6 behaviour mutants killed by their aimed tests.
  Goldens by name: the ghost 8s, the peak threshold, the fill's group opacity, the block width.
- Values the tokens do not hold, marked `// css literal:`:
  - Readout: gap 2, small digits 1.375rem / 1.5rem, unit 0.875rem / 1rem;
  - Meter: gap 6, bar 12, cell gap 2, radius 1, value 0.9375rem / 1.125rem;
  - ProgressBar: well 20 with padding 2, fill radius 1, blocks 8 with gaps of 2, status icon 16.
