---
id: B-47
title: "SearchBar"
status: open
priority: P1
size: S
stage: stage-2-components
blocked_by: [B-17, B-31]
---

# B-47 — SearchBar

The sunken capsule in a chrome ring (`box-shadow: shadow-sunken, 0 0 0 2px chrome-lo`), 48 px
minimum, with a magnifier that wiggles on focus and a round clear button that pops in once there is
text; `trailing` takes the consumer's element, usually an Avatar. Split out of B-17, because its
preview's trailing Avatar is B-31's: the fixture is ported string for string, Avatar included.
Built on the same `BasicTextField` as TextField (B-17).

- **References**: `SearchBar_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
- **Read first**: the component's README, its `preview.html`, and its `.og-search` block in
  `reference/design-system/components/bundle.css`; the skill is `design-to-compose` from Step 2.

- AC: a public composable in `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/`
  taking every value from the theme (research D4); exceptions named in the commit body.
- AC: the preview ported as one fixture per skin, string for string, at the manifest's size, in
  `OldgeDemo`; `viddikDesignParity --component "SearchBar*"` read against the floor, every
  non-`MATCH` fixture read as reference / `_ACTUAL` / `_DIFF` (at most five rounds).
- AC: the states the README names (focused with the magnifier's wiggle at rest, with and without
  text) as goldens in `SearchBarStates`; the clear button is an icon-only control and takes a
  label; `ScreenshotSuiteTest` no longer lists SearchBar; `./gradlew check` and `make check` green.
- Anchors:
  - `reference/design-system/components/SearchBar/README.md`, `reference/design-system/components/SearchBar/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/`
