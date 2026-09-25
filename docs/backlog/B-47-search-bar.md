---
id: B-47
title: "SearchBar"
status: done
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

## Findings

- Parity, `summary.txt` in the squash commit: SearchBar is 1.40 / 1.50 / 1.23 %, after one round
  from 3.45–3.94 %. Two causes:
  - `.og-search__end` holds the clear orb and the trailing Avatar with no gap of its own. As direct
    children of the bar they had taken its 8 px gap, and the orb sat 8 px left.
  - The text started 2 px early: Chrome gives an `<input>` `padding: 1px 2px`, and the design's CSS
    never overrides it. The quirk is in the service document.
- The field is the Composer's `BasicTextField`, single line: 1rem / 1.25rem, at least a line tall
  (B-17's 19 px line), in a 44 px box.
- The magnifier runs `og-wiggle` (0, −14°, 10°, −5°, 0, each interval on `ease-out`) each time the bar
  takes focus, and turns `ink`. `:focus-within` draws the focus ring 3 px out. The ring round the
  capsule is `0 0 0 2px chrome-lo`, a `cssBox` spread ring after `shadow-sunken`.
- `role="search"` has no Compose equivalent. The field is named by `label`, else by the placeholder,
  as bundle.js's `aria-label` is.
- The README's rules, in the API or `SearchBarBehaviourTest`:
  - the field is named by its label or its placeholder;
  - the clear button comes with text, is named «Очистить», and empties the field;
  - Enter submits the text;
  - the magnifier wiggles on focus, and not under reduced motion.
- Mutations through `scripts/mutate.py`: 5 of 5 killed by their aimed tests. Goldens by name: the
  chrome ring, the input's padding, and the end group's missing gap.
- `SearchBarStates`: focused and empty (the wiggle at rest, the ring), focused with text (the clear
  orb), and at rest empty. `focused()` from the TextField fixtures is shared for it now.
- Values the tokens do not hold, marked `// css literal:`: bar 48, input 44, end padding 4, ring 2,
  glass 20, focus offset 3, text 1rem, and the UA's 2 px.
