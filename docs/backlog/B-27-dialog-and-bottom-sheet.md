---
id: B-27
title: "Dialog and BottomSheet"
status: done
priority: P1
size: M
stage: stage-2-components
blocked_by: [B-12, B-20]
---

# B-27 — Dialog and BottomSheet

Windows in the skin's frame: Dialog with `shadow-window`, a title bar with an orb close button, textured body; BottomSheet with its tab, title and close, sliding in with an overshoot, scrim tap closes. Both have an `inline` form for documentation — the previews use it, so that is what parity compares; the overlay form gets a golden.

- **References**: `Dialog_{Toxic,Media,Crystal}`, `BottomSheet_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
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
  - `reference/design-system/components/Dialog/README.md`, `reference/design-system/components/Dialog/preview.html`
  - `reference/design-system/components/BottomSheet/README.md`, `reference/design-system/components/BottomSheet/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/containers/`

## Findings

- Parity, `summary.txt` in the squash commit, on the previews' forms (the Dialog's plain window and
  the sheet's `inline`):
  - Dialog is 2.86 / 2.95 / 3.03 %: the 17 px title a pixel low (B-46), and glyph edges.
  - BottomSheet is 1.10 / 1.18 / 1.18 %.
- **BottomSheet's first run was 6 %, and none of it was the sheet's look.** The preview's button
  after it stretches to the demo's width, because `.og-demo` is a flex column. The fixture now
  says so. The quirk is in the service document.
- **The overlay forms fill their parent.** A modal dialog centres its window on a scrim, 16 px from
  the edges; an overlay sheet sits at the bottom on its scrim. There is no popup window, so the
  caller puts either over the screen in a `Box`, as with the NavDrawer. Both say `dialog()` with
  their title as `paneTitle`, which is `role="dialog"` and `aria-labelledby`.
- **A dialog cannot sit inside the clickable scrim** (Compose throws), so the scrim is its sibling.
  A modal's scrim takes the taps under it and does not close it, since bundle.js's modal has no
  scrim handler. A sheet's scrim closes it, as the README says.
- The sheet's `max-height: 70vh` is 70 % of the height the overlay is given, the screen it
  covers. Inline it is unbounded: the preview never reaches it, and a composable inline has no
  viewport of its own.
- The window bodies are one helper, `windowBody` in `Materials.kt`: `body-hi` to `ground`, the
  `glow` ellipse at the bottom right, and the grain (`.og-dialog__body::before`). The title bar is
  shared between the two components as `WindowBar`.
- The README's rules, in the API or `WindowBehaviourTest`:
  - the title is a heading and the orb «Закрыть» closes;
  - no `onClose`, no orb;
  - only a modal dialog, or a sheet over a screen, is a dialog;
  - a modal takes the taps meant for the screen under it;
  - closed, nothing is drawn;
  - the sheet's scrim closes it;
  - the sheet's body is at most 70 %, the sheet measured to the dp.
  The actions' order and «Удалить» rather than «OK» are the caller's words; the KDoc says so, as
  the README does.
- Mutations through `scripts/mutate.py`: 8 of 8 killed by their aimed tests. Goldens by name:
  - the bodies' grain (`Dialog`, `BottomSheet` and both States);
  - the sheet's tab (`BottomSheet`, `BottomSheetStates`), after a first try whose `sed` did not
    match the reformatted line and so changed nothing;
  - the window shadow (`Dialog`, `DialogStates`).
- The States goldens are the overlay forms the previews never render: a modal dialog over a
  screen, and a sheet over one.
- Values the tokens do not hold, marked `// css literal:`:
  - dialog at most 358 wide, framed 6, bar 48, icon 22, rising 14;
  - sheet at most 480 wide, framed 6, tab 44 × 6 at 8 above and 2 below, bar 44.
