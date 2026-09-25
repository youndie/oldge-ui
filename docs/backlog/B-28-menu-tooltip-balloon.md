---
id: B-28
title: "Menu, Tooltip and Balloon"
status: open
priority: P1
size: M
stage: stage-2-components
blocked_by: [B-12]
---

# B-28 — Menu, Tooltip and Balloon

Menu: the XP menu — icon strip, blue selection. Tooltip: the small yellow plate. Balloon: the XP tray balloon, identical in every skin (the README says so — a test asserts the three skins' Balloon fixtures are pixel-identical), with a tail top, bottom or none.

- **References**: `Menu_{Toxic,Media,Crystal}`, `Tooltip_{Toxic,Media,Crystal}`, `Balloon_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
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
  - `reference/design-system/components/Menu/README.md`, `reference/design-system/components/Menu/preview.html`
  - `reference/design-system/components/Tooltip/README.md`, `reference/design-system/components/Tooltip/preview.html`
  - `reference/design-system/components/Balloon/README.md`, `reference/design-system/components/Balloon/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/`

- *Added by B-18:* `OldgeSelect` opens a minimal stand-in list (`OptionList` in
  `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeSelect.kt`), 4 px under the
  frame as `.og-menu` opens. When Menu exists, Select opens Menu's popup instead, and the stand-in
  goes.
