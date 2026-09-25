---
id: B-40
title: "Showcase screens: launcher, feed, settings"
status: open
priority: P2
size: L
stage: stage-3-screens
blocked_by: [B-11, B-12, B-13, B-14, B-15, B-16, B-17, B-18, B-19, B-20, B-21, B-22, B-23, B-24, B-25, B-26, B-27, B-28, B-29, B-30, B-31, B-32, B-33, B-34, B-47, B-50, B-51, B-52, B-37, B-38, B-39]
---

# B-40 — Showcase screens: launcher, feed, settings

Launcher: WindowBar, CategoryTabs, ActionTiles, BottomNav — the Nero StartSmart screen. Feed: a SearchBar with an avatar, a ChipGroup filter, media Cards. Settings: Segmented to choose the skin, a List of Switches, a Select. This item also ends `ScreenshotSuiteTest`'s not-yet-built list: it must be empty when this item is done.

- **The decision and its reason.** The page previews are the design system's own proof that its
  components compose; rebuilt from this library's components with no screen-specific drawing,
  they are the same proof for the Compose side. A screen that needs a drawing the library does
  not offer is a finding: a missing component or parameter becomes a `B-<next free>` item, not
  a private composable in the screen.
- The screens live in `sample` (not in the library) with their fixtures in `sample`'s
  `desktopTest`, which therefore gets viddik too; the references for them are copied from
  B-03's output into `sample/src/desktopTest/snapshots/design/`, or B-03's renderer learns a
  per-module output — say which.
- **References**: `Launcher_{Toxic,Media,Crystal}`, `FeedScreen_{Toxic,Media,Crystal}`, `SettingsScreen_{Toxic,Media,Crystal}`. The fixture size is the page's root size from the manifest.
- Interaction the preview demonstrates (the chat's send, the auth mode switch, the inbox swipe)
  is a Compose UI test, not a golden.

- AC: parity per skin read against the floor; `summary.txt` in the commit body; every
  component gap found here is a new item, listed in the commit body.
- AC: `./gradlew check` and `make check` green.
- Anchors:
  - `reference/design-system/components/Launcher/README.md`, `reference/design-system/components/Launcher/preview.html`
  - `reference/design-system/components/FeedScreen/README.md`, `reference/design-system/components/FeedScreen/preview.html`
  - `reference/design-system/components/SettingsScreen/README.md`, `reference/design-system/components/SettingsScreen/preview.html`
  - `sample/src/commonMain/kotlin/io/github/youndie/oldge/sample/`

## Iteration 1 (2026-09-25)

- **The three previews were surveyed against the public API, and nothing is missing.**
  - Launcher: WindowBar with a Badge in the centre, CategoryTabs, ActionTile with tones and
    descriptions, Accordion (a closed one with no body), a bare List of dense ListItems, Meter,
    BottomNav.
  - Feed: SearchBar with an Avatar trailing, the scrolling ChipGroup, Card with `media` and with
    `bar`/`barIcon`, Fab, the inline Snackbar.
  - Settings: Segmented, List with a label, Switch with `showLabel = false`, Select with option
    icons, Slider with `valueText`, TextField.
  - The Fab and the Snackbar's absolute positions, and the `.grid` of tiles, are the page's own
    layout, which `sample` reproduces.
- **Ordered after the other pages.** This item's AC ends `ScreenshotSuiteTest`'s not-yet-built
  list, which names all nine pages. B-38 and B-39 now wait on B-54, B-55 and B-56, so this item
  could not close the list if it ran first. `blocked_by` now says what the AC already implied:
  B-37, B-38 and B-39 come first.
- **For whoever closes the list.** The nine pages' fixtures live in `sample` (B-37), and
  oldge-core's registry cannot see them. The suite test has to learn a page's fixtures from
  `sample` rather than from its own registry: from `sample`'s goldens, or from a check in
  `sample`. It must not simply drop the pages from its list. oldge-core still holds its own copy
  of the page references (B-03's output). Each one's hash equals `sample`'s, B-37 checked.
