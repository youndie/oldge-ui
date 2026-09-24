---
id: B-08
title: "The parity harness, and the floor measured on Divider"
status: open
priority: P0
size: M
stage: stage-1-core
blocked_by: [B-03, B-07]
---

# B-08 — The parity harness, and the floor measured on Divider

viddik's parity defaults (5 % of pixels, ±16 per channel) are a starting point, not a
measurement (design-to-compose, Step 5). What "only the text residual" looks like here has to be
learned on one component a person — here, the item's own reading of the three images — declares
done, and every later number is read against it.

- **The decision and its reason.**
  - `OldgeDemo` — the test harness that is `.og-demo`: a column, `max-width` 390, padding
    `space-4`, gap `space-3`, on `skinBody()`, inside `OldgeTheme(skin, reducedMotion = true,
    texture = <B-04's outcome>)`, with the test-only portable text style (kvadrant-ui's
    `portableTypography`, research §1.9) applied to every family.
  - The fixture size comes from `design/manifest.json`, read at compile time by a small helper
    or copied into the annotation — say which; a fixture whose size disagrees with its reference
    is a failing test, not a `SIZE_MISMATCH` line nobody reads.
  - Divider is the first component: its preview is text above, a plain etched line, a line
    with the label «или», text below — text, a surface and a hairline, no icon.
- **The floor**: the `mismatchPercent` of `Divider_{Toxic,Media,Crystal}` once the diff shows
  only glyph edges, with the three `_DIFF` images committed under `docs/research/parity-floor/`
  and the numbers written into research as §1.10 "What parity's residual looks like here". The
  design tolerance is then set explicitly in `oldge-core/build.gradle.kts` with that section
  cited, and `designStrict` decided (on, or off with the reason).
- Rejected: raising the tolerance until everything passes (CLAUDE.md); a floor measured on a
  component with an icon — the icon set is B-11 and would add its own residual.

- AC: Divider parity within the chosen tolerance in all three skins; the three `summary.txt`
  lines and the token table in the commit body.
- AC: research §1.10 exists, names the floor per skin, and shows the `_DIFF` images.
- Anchors: `oldge-core/src/desktopTest/kotlin/io/github/youndie/oldge/harness/OldgeDemo.kt`, `reference/design-system/components/Divider/preview.html`,
  `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/containers/Divider.kt`, `oldge-core/build.gradle.kts`.
