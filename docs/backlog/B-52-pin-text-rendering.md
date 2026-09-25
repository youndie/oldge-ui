---
id: B-52
title: "A public way to pin text rendering, for a consumer's screenshots"
status: done
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

## Findings (2026-09-25)

- **The hook is one parameter.** `OldgeTheme(platformTextStyle: PlatformTextStyle? = null)` passes
  the style to `oldgeTypography`, which copies it onto each of the eleven styles. The faces, sizes
  and lines stay the design system's. `null` leaves every style's `platformStyle` as it was.
- **The harness goes through it, and nothing moved.**
  - oldge-core's `PortableText` wrapper is gone. In its place is the `PortableTextStyle` value,
    passed to `OldgeTheme` by `OldgeDemo`, the Icon fixture and `InkMatchesChromeTest`.
  - `viddikVerify` reports no mismatch over every golden, and the ink guard passes. The public hook
    pins exactly what the private rebuild did.
- **A stale sentence, corrected.** The harness's KDoc said it pinned "through viddik's
  `ViddikPlatformTextStyle`". It never did: the import was unused. The value differs from viddik's
  in subpixel positioning, which research §1.10 turned on on purpose (1.16 % → 0.74 % on Divider).
  The KDoc now says so, and the import is gone.
- **Behaviour** (`PlatformTextStyleTest`): with the parameter, every style of `OldgeTheme.type`
  and the inherited text style carry it. Without it, none do.
- **Mutants:** 2 of 2 killed.
  - The theme ignores the parameter.
  - One style (`pixelTag`) is built without it.
- **For B-37 to B-40.** `sample`'s fixtures pin by passing the same `PlatformTextStyle` to
  `OldgeTheme`. No copy of a harness function is needed, only of the value.
