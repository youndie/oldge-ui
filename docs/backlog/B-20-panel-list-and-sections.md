---
id: B-20
title: "Panel, List, ListItem and ListSection"
status: done
priority: P1
size: M
stage: stage-2-components
blocked_by: [B-08, B-09, B-10, B-11, B-29, B-31]
---

# B-20 — Panel, List, ListItem and ListSection

The inset surfaces content lives in. ListItem's two stress-screen rules: a long value never eats the title (the title wraps to two lines), and in a list narrower than 24em the value moves under the title. ListSection's pill header sticks (a `stickyHeader` in a lazy list — say how the fixture shows it).

- **References**: `Panel_{Toxic,Media,Crystal}`, `List_{Toxic,Media,Crystal}`, `ListItem_{Toxic,Media,Crystal}`, `ListSection_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
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
  - `reference/design-system/components/Panel/README.md`, `reference/design-system/components/Panel/preview.html`
  - `reference/design-system/components/List/README.md`, `reference/design-system/components/List/preview.html`
  - `reference/design-system/components/ListItem/README.md`, `reference/design-system/components/ListItem/preview.html`
  - `reference/design-system/components/ListSection/README.md`, `reference/design-system/components/ListSection/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/containers/`

## Findings

- Parity, `summary.txt` in the squash commit. Everything is under 1.4 %, and what is left is glyph
  edges, with the 13 px bold panel title a pixel low (B-46):
  - Panel is 1.31 / 1.33 / 1.36 %;
  - List is 0.99 / 0.99 / 1.19 %;
  - ListItem is 0.79 / 0.79 / 0.92 %;
  - ListSection is 1.14 / 1.17 / 1.36 %.
- **The narrow list switches at 20em of the list's own 15 px, not 24em.** This item and the ListItem
  README say «24em». The CSS says `@container og-list (max-width: 20em)`, and Chrome was measured: a
  300 px content box wraps and 301 does not. `OldgeList` follows the CSS, 300 dp at the default
  scale and growing with the font. This is amended in research §1.7.
- **The ListSection preview's second section is 24 px down, not 12.** The sections are children of
  `.og-demo`, whose flex `gap` adds 12 to `.og-lsec + .og-lsec`'s margin. The first run was 15–18 %
  off because of it. `oldgeListSections(extraSpacing)` is how the surrounding layout's gap is said.
- **A deviation: the stuck header's place.** Chrome holds it inside the scroll's padding, 16 px down;
  Compose's `stickyHeader` holds it at the edge. See research §1.7; `ListSectionStates` shows
  Compose's.
- ListSection's sticky header needs a lazy list, so the component is `LazyListScope.oldgeListSections`,
  with the header on its own as `OldgeListSectionHeader`.
- **`OldgeSwitch` gained `showLabel`.** The List preview's switch has an `og-sr` label, and the
  Switch KDoc already promised that a row shows the label, which nothing implemented. Hidden, the
  label is the switch's name, and the empty text column keeps its 12 px gap: 68 dp in all. This
  change is outside the item's list; without it, the List preview could not be ported.
- `OldgeDemo(padded = false)` lets a fixture be its own scroll container with the padding inside it,
  as the ListSection preview's demo is.
- The READMEs' rules, in the API or `PanelListBehaviourTest`:
  - a row with an action or a chevron is one button, 52 dp (44 dense), and a plain row is not;
  - `selected` is said;
  - the title clamps at two lines;
  - the value takes at most 42 %, and moves under the title at 300 dp and at 360 dp at 1.2;
  - a switch in a row is named without showing its label;
  - the list is a collection under its label, and the panel title and section header are headings;
  - a header holds at the top while its section scrolls under it.
- **The first sticky test passed on a header that no longer stuck.** Its bounds were `Dp.Unspecified`,
  which pass any comparison. It is now `assertIsDisplayed()` first; the quirk is in the service
  document.
- Mutations through `scripts/mutate.py`: 15 of 15 killed by their aimed tests. The first
  `row-chevron-interactive` did not compile (warnings are errors), so it was rewritten. Goldens by
  name:
  - the row divider (List, ListItem, ListSection, all three States);
  - the stripe's parity and the pressed `surface-2` (`ListItemStates`);
  - the sunken panel's shadow (`Panel`).
- The States goldens cover what the previews do not show: a flush panel with a bare list; a pressed,
  a dense, a striped and a selected row; a long title and a narrow list; a section scrolled 40.
- Values the tokens do not hold, marked `// css literal:`:
  - rows 52 / 44;
  - lead icon 22, chevron 18, its nudge 4;
  - the section head padded 6, with its 0 4px 8px drop.
  The value's 0.875rem is written as the body's size scaled, with the reason at the constant.
