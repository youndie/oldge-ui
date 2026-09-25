---
id: B-26
title: "NavDrawer, Stepper and PageDots"
status: done
priority: P1
size: M
stage: stage-2-components
blocked_by: [B-24, B-31]
---

# B-26 — NavDrawer, Stepper and PageDots

NavDrawer: header in the frame, active item a pill, items appearing in a 30 ms stagger. Stepper: the wizard steps, current step pulsing (a loop: off under reduced motion). PageDots: LCD dots for pagers.

- **References**: `NavDrawer_{Toxic,Media,Crystal}`, `Stepper_{Toxic,Media,Crystal}`, `PageDots_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
- **Read first**: the component's README (the rules), its `preview.html` (the demo to port), and
  its `.og-` block in `reference/design-system/components/bundle.css` (the exact numbers). The skill is `design-to-compose`
  from Step 2, since the references already exist (B-03).

- AC: each component is a public composable in `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/` taking every value from the theme
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
  - `reference/design-system/components/NavDrawer/README.md`, `reference/design-system/components/NavDrawer/preview.html`
  - `reference/design-system/components/Stepper/README.md`, `reference/design-system/components/Stepper/preview.html`
  - `reference/design-system/components/PageDots/README.md`, `reference/design-system/components/PageDots/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/`

## Findings

- Parity, `summary.txt` in the squash commit:
  - NavDrawer is 0.72 / 0.76 / 0.74 %, at the floor after one round.
  - Stepper is 3.09 / 3.10 / 2.79 %: the lcd digit in each core is a pixel high and the small
    buttons' labels a pixel low, the baseline family B-46 holds. The labels themselves match row
    for row.
  - PageDots is 0.25 / 0.38 / 0.26 %.
- **NavDrawer's first run was 4–9 %, and none of it was the drawer's look.** Its width,
  `min(304px, 86vw)`, was written first as a `widthIn` before a `fillMaxWidth` (86 % of 304 = 261)
  and then after it. The second order can never be less than 86 % of the parent (308 in the
  preview, 335 over a 390 screen). It is now one `layout` that measures with the smaller; the
  quirk is in the service document.
- `86vw` is taken as 86 % of the drawer's parent, the only viewport a composable sees; over a
  screen, that is the screen.
- **The stagger is 35 ms, not the item's 30.** `.og-drawer__item`'s `animation-delay` is
  `calc(var(--i) * 35ms + 80ms)`, and bundle.css is what the design runs.
- `CssCorners` gained `End`, the right-hand pair of the drawer's `0 xl xl 0`. The blurred
  shadow's shape now follows the corners too. The `grain()` helper paints the texture alone, as
  `.og-drawer::before` does.
- Over a screen the drawer fills its parent with the scrim, which a tap closes. There is no popup,
  so the caller puts it over the screen in a `Box`. Not `open`, nothing is drawn.
- The Stepper draws its troughs and fills from the row, between column centres less 20 px each
  side, as `.og-step + .og-step::before` / `::after` span them. The orbs paint over. The current
  step is `selected` (for `aria-current="step"`), and a done step's tick is named «готово».
- PageDots are at most seven (`require`): the README says past seven a counter is shown instead,
  and the design gives the counter no look to build, so the API asks for it rather than inventing
  one.
- The READMEs' rules, in the API or `DrawerStepperBehaviourTest`:
  - the drawer's current destination is selected, a tap changes it, and a section is only a title;
  - the drawer is 304 dp or 86 %;
  - the scrim closes it, and a closed one is not there;
  - the drawer's items come in one after another;
  - the current step is selected, done steps are ticked, and the trough fills up to the current step;
  - the ring pulses, and not under reduced motion;
  - three to five steps and at most seven dots;
  - every dot is a named 44 px button, and the current one is 36 wide.
- Mutations through `scripts/mutate.py`: 17 of 17 killed by their aimed tests.
  - `step-ring-still` first survived: at 100 ms the done step's tick was still popping in, so two
    frames differed without the ring. The test now samples after the pop.
  - Goldens by name: the drawer's right-only corners and its grain (`NavDrawer`,
    `NavDrawerStates`), the trough's sunken inset (`Stepper`, `StepperStates`) and the dot's glow
    (`PageDots`, `PageDotsStates`).
- The States goldens:
  - the drawer over a screen on its scrim, a later item current;
  - the first step current, three steps finished, and five steps at the last;
  - seven dots at either end, and dots over a photograph.
- Values the tokens do not hold, marked `// css literal:`:
  - drawer 304, items 48 with a 2 px gap, icon 22, slide 24, stagger 80 + 35;
  - orb 32 with a 2 px rim, tick 16, digit 14, ring 10, label gap 6;
  - joins 20 in, trough 6 at 13, fill 4 at 15, radius 2;
  - dots 24 / 36 × 44, capsule 8 / 24, glow 6, gap 2, focus 8 in.
