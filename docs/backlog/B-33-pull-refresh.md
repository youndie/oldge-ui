---
id: B-33
title: "PullRefresh"
status: done
priority: P1
size: S
stage: stage-2-components
blocked_by: [B-20, B-30]
---

# B-33 — PullRefresh

The glossy disc that comes down from the top. The pull is a Compose UI test; the reference is the preview's resting/refreshing state as it renders under reduced motion.

- **References**: `PullRefresh_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
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
  - `reference/design-system/components/PullRefresh/README.md`, `reference/design-system/components/PullRefresh/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/`

## Findings

- Parity, `summary.txt` in the squash commit: PullRefresh is 1.63 / 2.25 / 2.32 %, in one round. The
  reference is the preview's first frame: `refreshing` starts true, so it is the 56 px indicator
  with the disc at rest under reduced motion, over the bare list in a 220 px `.og-panel`.
- **The disc is the Spinner's.** `.og-pull__disc::before` is the same conic sweep with the same
  13.5 % hole, so the ring's drawing moved out of `OldgeSpinner` into the shared `drawConicRing`.
  All the goldens, the Spinner's among them, were green after the move and before PullRefresh used
  it. The pull disc has no gloss cap: it has no `::after`.
- **The gesture is nested scroll, not a pointer handler.** The component scrolls its content, and
  what the scroll cannot use at its top becomes the pull, at bundle.js's 0.55 of the finger, up to
  120 px. So the gesture works only at the very top of the scroll, as the README says, with no check
  of its own. Pushing back up takes the pull in before the list scrolls. At release (the fling),
  past 64 px it calls `onRefresh`, and the pull goes back.
- The indicator follows the pull through `dur-slow` on the spring, as the CSS `height` transition
  lags the finger. The disc turns 4° per px of pull and grows from 0.4 to full size at 54 px.
  While refreshing, it spins on the loop phase, standing still under reduced motion.
- Past the threshold the disc lights: a 2 px `accent` ring and a 10 px glow, `.og-pull__ind--ready`.
- The README's rules, in the API or `PullRefreshBehaviourTest`:
  - let go past 64 px it refreshes, and short of it it does not;
  - the pull starts only at the top of the scroll;
  - past the threshold the disc lights, read from a pixel just outside the disc, where only the
    ready ring draws;
  - while it refreshes, a screen reader hears «Обновляю».
  The README's «offer the refresh as a button too» is the caller's, and the KDoc says so.
- Mutations through `scripts/mutate.py`: 5 of 5 killed by their aimed tests, among them a pull that
  starts anywhere, not only at the top. Goldens by name: the conic ring and the refreshing
  indicator's height (`PullRefresh`).
- `PullRefreshStates` is the rest state: the disc folded away and the list at the panel's top.
- Values the tokens do not hold, marked `// css literal:`: threshold 64, most 120, refreshing 56,
  disc 32, ready ring 2 and glow 10.
