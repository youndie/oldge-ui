---
id: B-30
title: "Spinner and Skeleton"
status: done
priority: P1
size: S
stage: stage-2-components
blocked_by: [B-08, B-09, B-10, B-11]
---

# B-30 — Spinner and Skeleton

The two loops: Spinner's rotating disc, Skeleton's sunken slots with the segment scanner. Both are still under reduced motion; the moving forms get a clock-driven golden at a fixed frame.

- **References**: `Spinner_{Toxic,Media,Crystal}`, `Skeleton_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
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
  - `reference/design-system/components/Spinner/README.md`, `reference/design-system/components/Spinner/preview.html`
  - `reference/design-system/components/Skeleton/README.md`, `reference/design-system/components/Skeleton/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/`

## Findings (2026-09-25)

- Parity (±16, floor 0.72 %):
  - Skeleton: 0.10 / 0.14 / 0.13 % (Toxic / Media / Crystal), on the first run.
  - Spinner: 1.29 / 1.34 / 0.86 %, from 1.38 / 1.69 / 1.13 % after one cause. The orb shadow
    showed through the disc's hole, because Compose's `dropShadow` fills its shape where CSS paints
    an outer shadow only outside the box; it is drawn with the disc clipped out now. What is left
    is the conic sweep's interpolation and the rim's edge.
- What the references show under reduced motion follows the design's own rule, a 1 ms, single run
  that ends at the base style. The scanners rest off the left edge and are clipped, the dials show
  their first 100° lit from 12 o'clock, and the spinner rests unturned. The components under
  reduced motion draw exactly that.
- **The moving forms are held at a fixed frame** through `LocalOldgeLoopPhase` (internal, in
  `Loop.kt`). A golden cannot wait for an endless loop to settle, and viddik's capture has no clock
  to stop. `SkeletonStates` shows the disc turned, the scanners out of step, and the dials stepped.
  The scanner's `drop-shadow` glow is not drawn, and no reference shows it.
- Conic gradients are Compose's sweep turned −90°: CSS's start at 12 o'clock, Compose's at 3. The
  hole and the dial ring take CSS's radial masks against the farthest-corner radius, the box's
  half-diagonal.
- The READMEs' rules, in the API or `LoopBehaviourTest`:
  - the spinner is an indeterminate progress named by its label, or «Загрузка»;
  - a skeleton says nothing to a screen reader;
  - under reduced motion the disc, the scanner and the arc stand still, and without it they move;
  - the dial lights its first 100°, sampled by pixel.
- **A golden missed a mutant.** Cutting the lit arc from 100° to 70° stayed within viddik's
  0.05 % pixel budget. It is now held by the pixel test, and the quirk is in
  `docs/services/oldge-core.md`.
- Mutations through `scripts/mutate.py`: 5 of 5 behaviour mutants killed by their aimed tests
  (`lit-arc` among them, after the pixel test). The skeleton's silence has no mutant, since its
  forms say nothing even without the clearing. Goldens by name: the scanline pitch, the spinner's
  hole, and its shadow kept outside.
- Values the tokens do not hold, marked `// css literal:`:
  - Spinner: discs 24 / 48 / 72.
  - Skeleton:
    - line 14 with margins 5, the scanline 3 + 1;
    - scanner 54 (72 on media) with 6 px blocks 3 apart, inset 2;
    - media 120 high with its scanner 8 up and 10 high, glyph 40;
    - circles 44 and 36, the dial inset 3.
