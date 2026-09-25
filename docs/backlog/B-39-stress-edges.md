---
id: B-39
title: "Stress screens: 320 dp with German strings, and the system font at 200 %"
status: open
priority: P2
size: L
stage: stage-3-screens
blocked_by: [B-13, B-15, B-17, B-20, B-21, B-24, B-25, B-31, B-32, B-50, B-51, B-52, B-56]
---

# B-39 — Stress screens: 320 dp with German strings, and the system font at 200 %

EdgeNarrow at 320 dp with long German strings: list values move under titles, button rows become columns, a long choice leaves Segmented for RadioGroup. EdgeScale: root font 200 % — the fixture is `Density(1f, fontScale = 2f)` (research §1.7); every control grows in height and nothing is clipped. A clipped text is found by a test over `TextLayoutResult.hasVisualOverflow` across the screen's texts, not only by the eye.

- **The decision and its reason.** The page previews are the design system's own proof that its
  components compose; rebuilt from this library's components with no screen-specific drawing,
  they are the same proof for the Compose side. A screen that needs a drawing the library does
  not offer is a finding: a missing component or parameter becomes a `B-<next free>` item, not
  a private composable in the screen.
- The screens live in `sample` (not in the library) with their fixtures in `sample`'s
  `desktopTest`, which therefore gets viddik too; the references for them are copied from
  B-03's output into `sample/src/desktopTest/snapshots/design/`, or B-03's renderer learns a
  per-module output — say which.
- **References**: `EdgeNarrow_{Toxic,Media,Crystal}`, `EdgeScale_{Toxic,Media,Crystal}`. The fixture size is the page's root size from the manifest.
- Interaction the preview demonstrates (the chat's send, the auth mode switch, the inbox swipe)
  is a Compose UI test, not a golden.

- AC: parity per skin read against the floor; `summary.txt` in the commit body; every
  component gap found here is a new item, listed in the commit body.
- AC: `./gradlew check` and `make check` green.
- Anchors:
  - `reference/design-system/components/EdgeNarrow/README.md`, `reference/design-system/components/EdgeNarrow/preview.html`
  - `reference/design-system/components/EdgeScale/README.md`, `reference/design-system/components/EdgeScale/preview.html`
  - `sample/src/commonMain/kotlin/io/github/youndie/oldge/sample/`

## Iteration 1 (2026-09-25)

Both previews were surveyed against the public API before any screen code was written. One gap,
filed as [B-56](B-56-actions-row.md) and blocking this item: `og-actions`, the design system's
button row that wraps to a column. The README names it as the answer to squeezed button labels,
and both pages end in one.

Everything else exists:

- WindowBar, RadioGroup, List and ListItem (with the narrow list's value under the title, B-20),
  Switch, ChipGroup with FilterChip, Banner (`boxed`, actions), Tabs with a page, TextField and
  BottomNav.
- The Switch in a list row, whose label is visually hidden (the page's `og-sr` span), is
  `showLabel = false`.

Fixture sizes from the manifest: EdgeNarrow 320 × 680 and EdgeScale 390 × 860. EdgeScale's root
font of 200 % is `Density(1f, fontScale = 2f)` (research §1.7). Every page size in `rem` grows with
it, and `px` sizes do not, as in Compose `sp` grows and `dp` does not.
