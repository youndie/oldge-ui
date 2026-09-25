---
id: B-23
title: "SwipeRow"
status: done
priority: P1
size: M
stage: stage-2-components
blocked_by: [B-20]
---

# B-23 — SwipeRow

A row that reveals actions on a left swipe. The swipe is verified by a Compose UI test (`performTouchInput { swipeLeft() }`) and the revealed state by a golden; the reference shows the resting state only.

- **References**: `SwipeRow_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
- **Read first**: the component's README (the rules), its `preview.html` (the demo to port), and
  its `.og-` block in `reference/design-system/components/bundle.css` (the exact numbers). The skill is `design-to-compose`
  from Step 2, since the references already exist (B-03).

- AC: each component is a public composable in `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/containers/` taking every value from the theme
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
  - `reference/design-system/components/SwipeRow/README.md`, `reference/design-system/components/SwipeRow/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/containers/`

## Findings

- Parity, `summary.txt` in the squash commit: SwipeRow is 0.79 / 0.90 / 0.96 %, at the floor, with no
  round needed. **The item's premise was out of date:** it says the reference shows the resting
  state only, but the preview's first row is `defaultOpen`, so the open state is in the reference
  and is measured by parity. The swipe itself is the UI test, as the item asks.
- **Inside a SwipeRow the list's divider and stripe do not reach the row.** CSS draws them on
  `.og-list > li + li > .og-item`, and inside `.og-swipe__front` the item is no longer the `li`'s
  own child: the reference has no rule between its two rows. The SwipeRow hands its row a list place
  of index 0, not striped; the narrow layout still reaches it.
- The gesture is a horizontal `draggable`. It leaves vertical drags to the scroll, and it keeps
  bundle.js's rubber band (a quarter of the finger past either end) and its release rule (open
  past half the actions' width).
- **A deviation:** the row trails the finger by the platform's touch slop. `draggable` counts from
  the end of the slop, and bundle.js from the first pixel. The quirk is in the service document.
- Open, a tap on the row shuts it and does not reach the row's own action; this is an overlay over
  the open row, as bundle.js's `onClickCapture` is. An action shuts the row and runs.
- The README's «repeat them in the row's menu for a screen reader» is done by the component: the
  actions are also the row's `customActions`.
- The README's rules, in the API or `SwipeRowBehaviourTest`:
  - a left swipe opens it by the actions' width, and a right one shuts it;
  - let go short of half it shuts, past half it opens;
  - past its ends it resists and comes back;
  - a vertical drag does not move it;
  - a tap on an open row shuts it without pressing it;
  - an action runs and shuts it, and focusing an action opens it;
  - the actions are the row's accessibility actions;
  - one to three actions, with danger last (both `require`).
- Mutations through `scripts/mutate.py`: 9 of 9 killed by their aimed tests.
  - `swipe-half` and `swipe-vertical` first survived. The first test's drags lay on either side of
    both thresholds; the vertical test looked only after the release, when a short move settles
    shut anyway. Both now hold the drag and read the row while the finger is down.
  - That found the harness quirk (every other `moveBy` call dropped) and the slop.
  - Goldens by name: the danger action's sunken well, the actions' `edge` rule, and the divider
    kept out of the rows (`SwipeRow`, `SwipeRowStates`).
- `SwipeRowStates`: three actions open (accent, chrome, danger), and a single action open.
- Values the tokens do not hold, marked `// css literal:`: action 72 wide, icon 20, gap 2.
