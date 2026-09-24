---
id: B-11
title: "The 40 icons as ImageVectors, generated from the bundle"
status: open
priority: P0
size: S
stage: stage-1-core
blocked_by: [B-08, B-09]
---

# B-11 — The 40 icons as ImageVectors, generated from the bundle

The icon set is 40 single-path glyphs on a 24×24 grid in `bundle.js` (research §1.1). Nearly
every component uses one, so it comes before them.

- **The decision and its reason.** Generate `OldgeIcons.Home … OldgeIcons.Error` from the
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
