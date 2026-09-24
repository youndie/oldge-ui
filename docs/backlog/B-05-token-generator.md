---
id: B-05
title: "Generate the token layer from tokens.json"
status: open
priority: P0
size: M
stage: stage-1-core
blocked_by: [B-01]
---

# B-05 — Generate the token layer from tokens.json

Research D4: 46 colours × 3 skins, 7 shadows per skin, 11 type styles, durations and easings are
generated, never typed.

- **The decision and its reason.** A generator in the build (a Gradle task in `oldge-core`
  writing into `build/generated/`, or a script whose output is committed — choose, and say why in
  the commit; kvadrant-ui's D12 is the precedent to read first) produces:
  `OldgeColors` (an `@Immutable` class, one property per token, KDoc = the token's `usage`) and
  one instance per skin; `OldgeSpacing`, `OldgeRadii`, `OldgeDurations`; `OldgeEasings` as
  `CubicBezierEasing` (check that Compose accepts `y` outside 0…1 — `ease-bounce` has
  `-0.6` and `1.6` — and record the answer in research §1.5); shadows parsed from the CSS
  `box-shadow` strings into a typed `OldgeShadow` (inset or drop, offset, blur, spread, colour,
  per skin; `none` → empty); type styles as data (family key, size in rem, line height in rem,
  weight, letter spacing in em).
- Aliases `{name}` resolve at generation time; a token missing a skin's value inherits the
  first skin's (the type's rule, research §1.2).
- Rejected: parsing CSS at runtime — a wasm or iOS consumer should not ship a CSS parser to read
  constants.

- AC: a desktop test re-reads `reference/design-system/tokens.json` and asserts every token of
  every family is present with the value the generator produced — so a re-vendored design system
  with a changed token fails the build until regenerated.
- AC: a mutation — changing one colour in the vendored file — fails that test (commit body).
- AC: no colour, length or duration literal from `tokens.json` appears in hand-written Kotlin
  (a grep in the test over `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/`, excluding the generated directory).
- Anchors: `reference/design-system/tokens.json`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/tokens/`,
  `kvadrant-ui/kvadrant-core/build.gradle.kts` (its token generator).
