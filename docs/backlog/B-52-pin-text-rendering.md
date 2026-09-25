---
id: B-52
title: "A public way to pin text rendering, for a consumer's screenshots"
status: open
priority: P2
size: S
stage: stage-2-components
---

# B-52 — A public way to pin text rendering, for a consumer's screenshots

Found in B-51. oldge-core's goldens and parity fixtures pin the platform's text hinting and
smoothing (`PortableText`: hinting off, antialiasing, subpixel positioning; research §1.9). A
golden then means the same on another operating system, and parity is read against a floor
measured that way (B-08, research §1.10).

The harness does it by rebuilding the typography with a pinned `PlatformTextStyle` and providing
it through `LocalOldgeTypography`. Both the `OldgeTypography` constructor and that local are
`internal`. So a consumer cannot pin text rendering for their own screenshot tests. Neither can
`sample`, whose screen fixtures (B-37 to B-40) would otherwise render text as the platform likes
and read parity against a floor measured under different settings.

- **The decision and its reason.** One public parameter on `OldgeTheme`: the platform text style
  applied to every style of the typography, `null` (the platform's own) by default. A consumer
  passes viddik's `ViddikPlatformTextStyle`, or their own, in tests, and `PortableText` in both
  modules becomes that parameter.
- **Rejected: a public `OldgeTypography` constructor and local.** That opens the typography to
  replacement, and the design system fixes its faces (research D6). A consumer who only wants
  stable screenshots should not be handed the fonts.
- **Rejected: pinning inside the library by default.** A consumer's app would lose the platform's
  own rendering, which is right for an app and wrong only for a screenshot.
- **Not covered:** which settings are right. `PortableText`'s are the measured ones and stay.

- AC: `OldgeTheme(platformTextStyle = …)` reaches every style of `OldgeTheme.type`. A behaviour
  test reads it back from each style, with a mutant that drops one style.
- AC: oldge-core's `PortableText` becomes the parameter, and every golden is unchanged. That is
  the check that the public hook pins exactly what the harness pinned.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/theme/OldgeTheme.kt`,
  `oldge-core/src/desktopTest/kotlin/io/github/youndie/oldge/harness/OldgeDemo.kt`.
