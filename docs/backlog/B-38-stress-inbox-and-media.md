---
id: B-38
title: "Stress screens: the long list and full-screen media"
status: open
priority: P2
size: L
stage: stage-3-screens
blocked_by: [B-20, B-23, B-24, B-26, B-30, B-31, B-32, B-33, B-50, B-51, B-52]
---

# B-38 — Stress screens: the long list and full-screen media

Inbox: sticky sections, swipe rows, pull to refresh, a long scrolling list — the content between the bar and the navigation must scroll (`min-height: 0` in the CSS), or at a large font the navigation leaves the screen. Media: no frame, a transparent WindowBar over the image, PageDots.

- **The decision and its reason.** The page previews are the design system's own proof that its
  components compose; rebuilt from this library's components with no screen-specific drawing,
  they are the same proof for the Compose side. A screen that needs a drawing the library does
  not offer is a finding: a missing component or parameter becomes a `B-<next free>` item, not
  a private composable in the screen.
- The screens live in `sample` (not in the library) with their fixtures in `sample`'s
  `desktopTest`, which therefore gets viddik too; the references for them are copied from
  B-03's output into `sample/src/desktopTest/snapshots/design/`, or B-03's renderer learns a
  per-module output — say which.
- **References**: `InboxScreen_{Toxic,Media,Crystal}`, `MediaScreen_{Toxic,Media,Crystal}`. The fixture size is the page's root size from the manifest.
- Interaction the preview demonstrates (the chat's send, the auth mode switch, the inbox swipe)
  is a Compose UI test, not a golden.

- AC: parity per skin read against the floor; `summary.txt` in the commit body; every
  component gap found here is a new item, listed in the commit body.
- AC: `./gradlew check` and `make check` green.
- Anchors:
  - `reference/design-system/components/InboxScreen/README.md`, `reference/design-system/components/InboxScreen/preview.html`
  - `reference/design-system/components/MediaScreen/README.md`, `reference/design-system/components/MediaScreen/preview.html`
  - `sample/src/commonMain/kotlin/io/github/youndie/oldge/sample/`
