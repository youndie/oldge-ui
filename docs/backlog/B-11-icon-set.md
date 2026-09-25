---
id: B-11
title: "The 47 icons as ImageVectors, generated from the bundle"
status: done
priority: P0
size: S
stage: stage-1-core
blocked_by: [B-08, B-09]
---

# B-11 — The 47 icons as ImageVectors, generated from the bundle

The icon set is 47 single-path glyphs (the README lists 40; research §1.1) on a 24×24 grid in `bundle.js` (research §1.1). Nearly
every component uses one, so it comes before them.

- **The decision and its reason.** Generate `OldgeIcons.Home … OldgeIcons.Mail` from the
  bundle's icon table (path data parsed with Compose's path parser, fill `currentColor` →
  `LocalContentColor`), plus `OldgeIcon(icon, contentDescription, size = 24.dp)`. The generator
  reads the vendored `bundle.js`, so a changed icon fails a test until regenerated (as B-05).
  Check each path's fill rule and whether the bundle draws any icon as a stroke rather than a
  fill before generating.
- Rejected: exporting SVGs to Android XML drawables — not common code.

- AC: `Icon_{Toxic,Media,Crystal}` parity within the floor; `ScreenshotSuiteTest` no longer
  lists Icon.
- AC: a test that the generated set equals the bundle's key list, name for name.
- Anchors: `reference/design-system/components/Icon/preview.html`, `reference/design-system/components/bundle.js`,
  `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/icons/`.

## Findings (2026-09-25)

- The bundle draws every icon as one path, `fill="currentColor"`, `fill-rule="evenodd"`; no
  strokes. `scripts/generate_icons.py` keeps each path string verbatim and Compose's
  `addPathNodes` parses it; `checkOldgeIcons` holds the generated file to `bundle.js` in `check`.
- Parity: the glyphs match to 312 differing pixels of the whole preview; the labels carried the
  rest. **The cause was general, not the icons'**: Blink places text on a baseline computed from
  rounded metrics and a floored half-leading, Compose on unrounded ones — a pixel low for Silkscreen
  10/12. Fixed here, because the item's acceptance cannot be read without it (the one exception the
  backlog-item rule allows), as `OldgeText` and in `OldgeScriptText`: Icon 3.16 % → 1.54 %, and the
  floor re-measured (research §1.10). What is left is the pixel face's edges at a fractional x.
- Mutations, each failing, restored: the baseline rule without rounding (`CssBaselineTest`); an icon
  removed from the generated file (`checkOldgeIcons`, `OldgeIconsTest` twice).
- The default icon size is the grid (`GRID.dp`), which `NoTokenLiteralsTest` asked to be said:
  `24.dp` equals `space-5`.
