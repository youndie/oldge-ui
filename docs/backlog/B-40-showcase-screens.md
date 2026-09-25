---
id: B-40
title: "Showcase screens: launcher, feed, settings"
status: done
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

## Iteration 2 (2026-09-25): done

- **The screens.** `LauncherScreen`, `FeedScreen` and `SettingsScreen` are in `sample`, from
  public components.
  - Settings takes its `skin` and an `onSkinChange`: the page's Segmented shows the skin it is
    rendered in, and an app changes the theme above it.
  - The Launcher's `.grid` stretches each tile to its row, as a grid item is. The first fixture
    left «Загрузить файлы» at its own height, and the page read 2.71–3.06 %.
  - The Feed's Fab and Snackbar are placed as the page places them: the Fab 148 dp up while the
    Snackbar is there and 88 without it.
- **References** were rendered into `sample` with `--out`. Each `sha256` equals oldge-core's, 27
  of 27.
- **Parity, raw, against the floor of 0.72 %:**

  | Page | Toxic | Media | Crystal |
  |---|---|---|---|
  | Launcher | 1.35 % | 1.41 % | 1.61 % |
  | FeedScreen | 1.78 % | 1.88 % | 1.87 % |
  | SettingsScreen | 1.48 % | 1.55 % | 1.66 % |

  All three match their references as rendered. What is left is the text-heavy components' own
  residual. No component gap was found.
- **`ScreenshotSuiteTest`'s not-yet-built list is empty.**
  - The nine pages live in `sample`, whose registry oldge-core cannot see, so a page now counts as
    built when `sample` holds its golden in every skin.
  - `sample`'s new `SampleSuiteTest` holds its fixtures to its goldens and its page references,
    both ways. So a golden there stands for a fixture.
  - oldge-core's `desktopTest` declares `sample`'s snapshots as an input, so the count is not left
    `UP-TO-DATE` over a changed set.
  - Checked by removing `Launcher_Toxic.png` from `sample`: both suites fail and name it.
- **Interaction** (`ScreenBehaviourTest`):
  - Closing the Snackbar lets the Fab down by the page's 60 dp.
  - Choosing «Crystal» asks the app for `OldgeSkin.Crystal`.
- **Mutants:** 2 of 2 killed (the Fab stays up, the skin is not asked for).
