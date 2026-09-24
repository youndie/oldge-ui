---
id: B-06
title: "OldgeTheme: three skins, typography, shapes, motion, and the three switches"
status: open
priority: P0
size: M
stage: stage-1-core
blocked_by: [B-02, B-05]
---

# B-06 — OldgeTheme: three skins, typography, shapes, motion, and the three switches

Everything a component reads comes through one theme, so that a skin change is a value change
and a fixture can pin every switch (research D7).

- **The decision and its reason.** `OldgeTheme(skin: OldgeSkin = OldgeSkin.Toxic,
  reducedMotion: Boolean = <platform>, texture: Boolean = true, pressFlash: Boolean = true,
  content)` providing the generated tokens through `staticCompositionLocalOf` and one accessor
  object `OldgeTheme.colors / .type / .spacing / .radii / .motion / .shadows`. `OldgeSkin` is
  the three skins and nothing else: a new skin is a new set of token values (the README says so).
- Typography: `OldgeTypography` with the 11 styles of `tokens.json`, `1rem = 16.sp`, line height
  set explicitly on every style, the family resolved from B-02's faces. `LocalTextStyle` and
  `LocalContentColor` defaults: `body` in `ink`.
- Motion: `OldgeMotion` — the durations, the easings, and `reducedMotion`, under which every
  duration the library uses is zero (the design system's `prefers-reduced-motion` rules,
  research §1.2 — including the indeterminate ProgressBar, whose static look changes).
- The platform's reduced-motion setting per target is an `expect` read once in `OldgeTheme`;
  where a target has no API for it the default is `false` and that is said in KDoc.
- Rejected: a `MaterialTheme` underneath (research D2).

- AC: a desktop test switches the skin inside one composition and reads back a token per family
  changing; a test pins that `reducedMotion = true` yields zero durations from `OldgeMotion`.
- AC: `Density(1f, fontScale = 2f)` doubles every text style's size and line height and no
  other token (the EdgeScale precondition, research §1.7).
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/theme/`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/tokens/`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/type/`.
