---
id: B-40
title: "Showcase screens: launcher, feed, settings"
status: open
priority: P2
size: L
stage: stage-3-screens
blocked_by: [B-11, B-12, B-13, B-14, B-15, B-16, B-17, B-18, B-19, B-20, B-21, B-22, B-23, B-24, B-25, B-26, B-27, B-28, B-29, B-30, B-31, B-32, B-33, B-34, B-47, B-50, B-51]
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
