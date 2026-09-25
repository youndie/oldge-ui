---
id: B-41
title: "The sample app on desktop, Android and iOS, with a skin switcher"
status: done
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

## Findings (2026-09-25)

- **The app.** `OldgeSampleApp` in `sample`'s `commonMain` hosts the nine pages of B-37 to B-40
  behind three of the library's own controls:
  - a Select of the page;
  - a Segmented of the skin, shared with the Settings page's own, so choosing a skin there
    changes the app's;
  - a Switch that sets the system font to 200 % for the page under it. EdgeScale always runs at
    200 %, as its fixture does.

  The controls stay at the platform's own size, so a page at 200 % never pushes them away.
- **Desktop.** `./gradlew :sample:run` opens it in a 420 × 900 window. Checked by running it for
  60 s: the process stayed up and the log has no exception. A window screenshot needs
  screen-recording rights this session does not have. The UI is asserted instead by
  `SampleAppBehaviourTest`, which renders the same `OldgeSampleApp`: the page choice opens
  Settings, and choosing Crystal there makes both switchers say Crystal.
- **iOS.** `scripts/ios-sample-app.sh`, adapted from kvadrant-ui's, links the simulator executable,
  assembles the `.app` with its `Info.plist` and oldge-core's assembled compose resources (the
  fonts), and installs and launches it.
  - It runs on the iPhone 17 Pro simulator (iOS 27.0) with the bundled fonts, showing the
    Launcher: [screenshot](../images/b-41-ios-launcher.png).
- **Android.** `sample` has an Android library target and `sample-android` is the application that
  hosts it, since AGP 9 refuses an application plugin in a KMP module.
  - `./gradlew :sample-android:assembleDebug` builds `sample-android-debug.apk`, and the APK
    carries every bundled font under `assets/composeResources/`.
  - `com.android.application` had to be declared, unapplied, in the root build. In its module
    alone Gradle refused it, since AGP was already on the classpath "with an unknown version".
- **viddik#44 again.** With a second target `sample` has a `kspCommonMainKotlinMetadata`, so the
  ordering workaround B-37 removed from it is back. B-35 deletes both.
- **Mutants:** 2 of 2 killed (Settings' skin not reaching the app, the page choice ignored).
  `./gradlew build check` is green with the new targets.

## Question (2026-09-25): how is the Android app to be seen running?

One acceptance criterion is not met: "the Android app installs and shows the Launcher in all three
skins on an emulator". There is no emulator on this Mac. Its Android SDK has platforms,
build-tools and platform-tools, but no `emulator` package and no system image. The Linux box has
no Android SDK recorded, and an emulator under WSL2 would need nested virtualisation.

Everything short of running it is done: the APK builds and carries the fonts.

The choices, for the owner:

1. **Install the emulator and one system image on the Mac** (`sdkmanager "emulator"
   "system-images;android-37;google_apis;arm64-v8a"`, a download of the order of 2–3 GB). Then the
   loop installs the APK and takes the three screenshots.
2. **A device**: `./gradlew :sample-android:installDebug` onto whatever is attached, and the
   screenshots taken there.
3. **Accept the APK build and its fonts as the Android evidence**, and drop the emulator from the
   criterion. That is kvadrant-ui's position ("installed onto whatever is plugged in").

The loop does not download or install anything on its own. Until this is answered the item stays
`question`, and the rest of it is merged.

## Decision (2026-09-25)

The owner chose to look at the app **on desktop JVM**. The criterion "the Android app installs and
shows the Launcher in all three skins on an emulator" is dropped: no emulator is installed, and
none is needed for this item. The Android evidence stays what it was: the APK builds, and it
carries the fonts.

- `./gradlew :sample:run` started the app again on 2026-09-25, after B-35 and B-61. It is the
  `MainKt` process, a 420 × 900 window, left open for the owner to look at. The log has no
  exception.
- A screenshot of the window was not taken: access to the window was declined, which is the
  owner's call. The UI that window shows is asserted headless by `SampleAppBehaviourTest`, which
  renders the same `OldgeSampleApp`.
- Android and iOS stay as recorded above: the APK builds with its fonts, and the iOS simulator
  shows the Launcher.
