---
id: B-07
title: "Materials: gloss, bevels, shadows, LCD glass, chrome rings, the textured body"
status: open
priority: P0
size: L
stage: stage-1-core
blocked_by: [B-04, B-06]
---

# B-07 — Materials: gloss, bevels, shadows, LCD glass, chrome rings, the textured body

The design system's look is six materials, and every component is a combination of them
(research §1.4, §1.5; README "Форма", "Материал и текстура").

- **The decision and its reason.** Internal modifiers and brushes in `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/material/`:
  - `glossFill(family)` — three stops `*-hi`, base (at 55 %), `*-lo` of one token family, each
    stop an `animateColorAsState` so a state change interpolates (the `@property` behaviour);
    plus the `gloss` highlight on the top 36 % of the shape, which never extends under text.
  - `bevel(Raised | Sunken | Bezel)` — the 1 px inset edge pairs of `shadow-raised/-sunken/-bezel`.
    **Measure first** whether `Modifier.innerShadow` with zero blur and a 1 px offset draws the
    same pixels as the CSS inset shadow (research §1.4 hypothesis) on one Panel-like box against
    a Chrome render of a `div` with that `box-shadow`; if not, draw the two edges in
    `drawWithCache`. Record the answer in research §1.4.
  - `dropShadow` for `shadow-orb/-window/-balloon`, `lcdGlow` for `shadow-lcd-glow` (none in
    Crystal).
  - `chromeRing` — the hi/lo chrome ring round every circular element.
  - `lcdGlass` — the sunken LCD well with `lcd`, `lcd-ink`, `lcd-dim`, `lcd-ghost`.
  - `skinBody()` — the screen body: `body-hi` → `ground` gradient to 70 %, the `glow` radial in
    the bottom-right corner, and the grain from B-04; `bezelTexture()` — weave (Toxic), speckle
    (Crystal), nothing (Media). All texture honours `OldgeTheme.texture`.
- Rejected: public API for these in this item — they become public only when a consumer needs
  one (the README invites "build your own glossy elements the same way"; that is B-45's call).

- AC: a fixture per material per skin in group `Material` (goldens), and a parity comparison of
  the bevel measurement above in the commit body.
- AC: a test that a gloss fill's three colours are mid-way between the two states at half the
  transition duration (a clock-driven test through `mainClock`), and equal to the target at once
  under `reducedMotion`.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/material/`, `reference/design-system/components/bundle.css` (`.og-gloss`,
  `.og-bezel`, `.og-orb__core`, `--og-grain-mask`).
