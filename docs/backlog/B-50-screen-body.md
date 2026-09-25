---
id: B-50
title: "A public screen body: the skin's .og-body for an app's root"
status: done
priority: P2
size: S
stage: stage-2-components
---

# B-50 — A public screen body: the skin's `.og-body` for an app's root

Found in B-37. A screen in this design system stands on `.og-body`: `body-hi` to `ground` over
the top 70 %, the `glow` ellipse at the bottom-right corner, the grain, and `ink` text in the `ui`
face at 0.9375rem / 1.25rem (`bundle.css`, `body, .og-body` and `.og-textured::before`).

- The README names it as the screen container's surface: «`og-textured` (для
  контейнера-экрана)».
- All nine page previews put their `.phone` on it. The rule is identical in each: the body
  gradient, a glow of 120 % × 60 %, and `og-textured`.

The library already paints it: `Modifier.skinBody()` in `material/Materials.kt`, which the test
harness's `OldgeDemo` stands on. But it is `internal`. An app cannot put its root on the skin, and
neither can the stress screens, which B-37 says must use no drawing the library does not offer.

- **The decision and its reason.** A public composable (`OldgeScreenBody`, or a name the KDoc pass
  settles) that paints `.og-body` and provides `ink` body text to its content, as the CSS class
  does. It is what a consumer's `setContent` wraps first, and it is the surface every screen item
  (B-37 to B-40) needs.
- **Rejected: a public `Modifier.oldgeBody()`.** A modifier alone cannot provide the text style
  that `.og-body` sets. The caller would have to know to do that as well.
- **Rejected: a private composable in `sample`.** That is the drawing B-37 forbids, and it would
  leave consumers without the surface.
- **Not covered:**
  - The preview's phone mock-up (the `radius-xl` clip, `shadow-window`, and the 16 px margin) is
    the device frame the page is photographed in. It belongs to the screen fixtures' harness, not
    the library.
  - The phone's glow is 60 % high where `.og-body`'s is 70 %. The screen items read that
    difference in their parity numbers; this item does not add a parameter for it.

- AC: `OldgeScreenBody { … }` paints what `OldgeDemo` paints today, and `OldgeDemo` stands on it,
  so every existing golden is unchanged. That is the check that the public surface is the same
  drawing.
- AC: its content reads `ink` in the body style from `LocalOldgeTextStyle`: a behaviour test, with
  a mutant.
- AC: turning texture off drops the grain, as `data-og-texture="off"` does.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/material/Materials.kt`,
  `oldge-core/src/desktopTest/kotlin/io/github/youndie/oldge/harness/OldgeDemo.kt`.

## Findings (2026-09-25)

- **`OldgeScreenBody` exists and is the drawing the goldens were already made of.**
  - It lives in `containers/OldgeScreenBody.kt`: a `Box` on `skinBody()`, which provides
    `type.body` in `ink` and `ink` as the content colour, as `.og-body` sets them.
  - `OldgeDemo` now stands on it in place of its private `Box(…skinBody())`.
  - `viddikVerify` reports no mismatch over every golden. So the public surface draws exactly what
    every fixture drew.
- **The name.** The design system calls the class `og-body`. `OldgeBody` would read as a text
  style next to `type.body`, so the composable is `OldgeScreenBody`. B-45's KDoc pass may still
  rename it.
- **Behaviour** (`ScreenBodyBehaviourTest`):
  - Under a container that hands down red 40 sp text, the content still reads `ink` at the body
    size and line.
  - The top row of a textured body varies, and with texture off it is one colour.
- **Mutants:** 4 of 4 killed.
  - The outer text style kept.
  - The outer content colour kept.
  - The grain drawn always.
  - The grain never drawn.
- **Left as they were:** three probes in `desktopTest` (`PressFixtures`, `IconFixtures`,
  `MaterialProbes`) still paint `skinBody()` directly. They are fixed-size backdrops for material
  and icon probes, not screens, and the internal modifier is what they measure.
