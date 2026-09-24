---
id: B-04
title: "Spike: can the grain be Chrome's feTurbulence, pixel for pixel?"
status: open
priority: P0
size: S
stage: stage-0-spikes
blocked_by: [B-01]
---

# B-04 — Spike: can the grain be Chrome's feTurbulence, pixel for pixel?

The body grain is an SVG `feTurbulence` mask at 5–7 % alpha over every screen (research §1.3). A
different noise is a faint diff over the whole reference, which is exactly what hides a real one.

- **The hypothesis.** The SVG 1.1 specification's reference C implementation of `feTurbulence`
  (fractalNoise, seed 0, `stitchTiles`), ported to Kotlin, generates the 160×160 tile at
  `baseFrequency .85, numOctaves 2` and the 128×128 speckle at `1.3` identical to Chrome's
  within ±2 per channel, after the same `feColorMatrix`.
- Measure: render the two SVG tiles alone in headless Chrome at device scale 1, render the
  Kotlin tiles to PNG in a desktop test, compare per pixel. Report the max channel deviation
  and the share of pixels beyond ±2.
- **If it holds**: D8's tile is that port, and parity fixtures render with texture on.
  **If it does not**: parity fixtures and references both render with texture off
  (`data-og-texture="off"` in the wrapper, `texture = false` in the harness), the texture gets
  its own golden, and research D8 records the measured gap. Either way research §1.3's
  hypothesis becomes a fact or a refutation in writing.
- Not covered: drawing the texture in components (B-07).

- AC: the comparison numbers for both tiles in research §1.3, with the files that produced them
  committed (`oldge-core/src/desktopTest/kotlin/io/github/youndie/oldge/material/TurbulenceTileTest.kt` and the two Chrome PNGs under
  `oldge-core/src/desktopTest/snapshots/design/texture/`).
- AC: B-03's wrapper and B-08's harness agree on the texture switch the outcome chose — written
  as a line in `backlog.md` → "Decisions not to re-litigate".
- Anchors: `reference/design-system/components/bundle.css` (`--og-grain-mask`),
  `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/material/`.
