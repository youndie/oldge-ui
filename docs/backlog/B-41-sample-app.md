---
id: B-41
title: "The sample app on desktop, Android and iOS, with a skin switcher"
status: open
priority: P2
size: M
stage: stage-4-product
blocked_by: [B-40]
---

# B-41 — The sample app on desktop, Android and iOS, with a skin switcher

A green `check` says nothing about Android or iOS (research D10). The sample is where the
library is seen running on them.

- **The decision and its reason.** `sample` gains Android and iOS targets and hosts the nine
  screens of B-37…B-40 behind a skin switcher and a text-scale switch; Android as a
  `sample-android` application module (AGP 9 forbids an application plugin in a KMP module —
  kvadrant-ui's research §1.13), iOS through a script like kvadrant-ui's
  `scripts/ios-sample-app.sh` (no Xcode project), which also copies the compose resources into
  the bundle — without that the app dies on its first frame naming a font.
- Rejected: an Android-only sample — the design system is mobile, and iOS is where a missing
  resource shows first.

- AC: `./gradlew :sample:run` opens the desktop app; the Android app installs and shows the
  Launcher in all three skins on an emulator (screenshot in the commit body); the iOS app runs on
  a simulator (screenshot in the commit body).
- Anchors: `sample/`, `sample-android/`, `scripts/ios-sample-app.sh`,
  `kvadrant-ui/scripts/ios-sample-app.sh`.
