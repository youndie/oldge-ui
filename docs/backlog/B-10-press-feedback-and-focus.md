---
id: B-10
title: "Press feedback: the gloss flash, the squash, and the focus ring"
status: done
priority: P0
size: M
stage: stage-1-core
blocked_by: [B-07]
---

# B-10 — Press feedback: the gloss flash, the squash, and the focus ring

Every pressable element in the design system flashes from the touch point, squashes and springs
back (research §1.5), and every focusable one draws a 2 px `focus` ring 2 px outside
(README "Фокус"). One mechanism, installed by the theme, so no component can forget it.

- **The decision and its reason.** An `IndicationNodeFactory` provided as `LocalIndication` by
  `OldgeTheme` (confirm the factory's signature in CMP 1.12 foundation first — research §1.5 found
  it by name only): the flash is a radial `gloss` spot growing from the press position,
  clipped to the element's shape, fading over `dur-press`; off when `pressFlash = false` or
  `reducedMotion`. The squash is a separate `Modifier.oldgePressScale(interactionSource,
  pressed = 0.86…0.95)` with `dur-instant` in and a spring (`ease-spring`, `dur-base`) out,
  because the amount differs per component and an indication cannot know it. The focus ring
  is drawn by the indication too, **gated on keyboard input mode** — kvadrant-ui's D19: on
  desktop a click takes focus, and a ring drawn on focus alone shows in every pressed golden.
- Rejected: a `Modifier.clickable` wrapper per component — the one-mechanism-in-the-theme
  design is what made kvadrant-ui's tilt reach every control.

- AC: goldens of a pressed and a focused test surface per skin (group `Press`); a test that the
  flash's centre is the press position; a test that nothing is drawn with `pressFlash = false`.
- AC: a test over a clickable that asserts it **is** focused before asserting no ring was drawn
  in pointer mode (without that line the test passes for a control that was never focusable).
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/press/`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/theme/OldgeTheme.kt`,
  `reference/design-system/components/bundle.js` (lines 1–40: the flash).

## Findings (2026-09-25)

- `OldgeIndication(shape)` (`press/OldgeIndication.kt`) is `LocalIndication` under `OldgeTheme`; a
  component passes its own shape. The flash and the ring are drawn by `drawOldgeFlash` /
  `drawOldgeFocusRing`, which the `Press` goldens call too, frozen at a quarter of the flash's run —
  a golden of the live indication would photograph the end of an animation.
- CSS details carried over: the spot's size clamp (28…56 dp), the gradient sized to the farthest
  corner, `screen` blending, scale on one keyframe interval and opacity on two, each eased — CSS
  applies the timing function per interval.
- Compose emits no `FocusInteraction` for focus taken in touch mode; the ring test therefore takes
  focus in keyboard mode, switches to touch (asserting focus, no ring), and back (ring).
- Mutations, each failing its test, restored: the `pressFlash` switch ignored; the flash drawn 4 px
  off the press; the ring drawn without the keyboard gate.
- `oldgePressScale` has no test of its own here: the amount is per component, and B-12 (Button,
  OrbButton) is its first user and where its behaviour is asserted.
- The CSS outline's radius grows with its offset; the ring here keeps the shape's radius. On a 4 dp
  corner the difference is under a pixel; a component with a large radius that shows it is a
  finding for that component.
