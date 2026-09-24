---
id: B-03
title: "Render the reference PNGs from the design system's previews"
status: done
priority: P0
size: M
stage: stage-0-spikes
blocked_by: [B-02]
---

# B-03 — Render the reference PNGs from the design system's previews

Parity needs one PNG per component per skin, and the design system has live previews, not static
artboards (research §1.2, D3). This item writes the renderer and commits its output.

- **The decision and its reason.** `scripts/design-references.mjs` (node, no npm dependencies,
  Chrome through its CLI or DevTools protocol — node 24 has `WebSocket`):
  1. compile `reference/design-system/tokens.json` into `tokens.css` in the shape the type's
     `format.md` specifies (research §1.2) — a unit test on the compiler against three tokens
     whose compiled form is known (a per-skin colour, an alias, a spacing);
  2. wrap each `components/<Name>/preview.html`: `<html data-theme="<skin>">`, tokens.css,
     `bundle.css` **with its Google Fonts `@import` removed**, `@font-face` rules for the six
     bundled faces of B-02 and `--font-ui/-title/-lcd/-pixel` overridden to them (lcd and
     pixel as `"Share Tech Mono", "PT Mono"` and `Silkscreen, Tiny5` so Chrome's per-glyph
     fallback reproduces the per-script join), React 18.3.1 UMD, `bundle.js`, the preview body;
  3. render with `--headless=new --force-prefers-reduced-motion --force-device-scale-factor=1
     --hide-scrollbars`, 390 px wide (the `og-demo` max width; the page previews at their own
     root width), at the **content's measured height**, once per skin;
  4. write `oldge-core/src/desktopTest/snapshots/design/<Name>_<Skin>.png` and `design/manifest.json`: design-system
     version (`design-system.json` → `lastChange.at`), Chrome version, per stem its size.
- React comes from jsDelivr at render time. Vendor the two UMD files into
  `scripts/vendor/` instead if a second render differs from the first — the renderer must be
  offline-reproducible, and this is where it is checked.
- `Cover` is not rendered (it is the artifact's cover, not a component).
- Rejected: the design-to-compose skill's `canvas-references.mjs` as is — it expects a Claude
  Design canvas of static `.dc.html` artboards; the wrapper here is what makes a live preview
  static.

- AC: `make references` renders all 50 component previews and 9 page previews in 3 skins —
  177 PNGs — and every one is looked at (the Read tool) before commit; a blank, a broken
  layout or a visible fallback glyph is a finding written into the commit body, not a PNG
  committed silently.
- AC: rendering twice produces byte-identical PNGs (or the difference is measured and the
  cause fixed — vendored React, a disabled caret, a fixed date); the second run is in the
  commit body.
- AC: a reference whose preview has a live clock or random data is named in the manifest with
  what was fixed (the preview is never edited — research D3; the wrapper may stub `Date`).
- Anchors: `scripts/design-references.mjs`, `oldge-core/src/desktopTest/snapshots/design/manifest.json`,
  `reference/design-system/components/bundle.css`,
  `kotlin-fullstack/skills/design-to-compose/scripts/canvas-references.mjs` (the prior art).

## Findings (2026-09-25)

- 177 references and `manifest.json` committed; a second render into a scratch directory was
  **byte-identical** (0 of 177 differ). Every PNG was looked at, on 3-skin contact sheets, before
  and after the last change to the renderer.
- Three causes of a wrong or unstable reference, each recorded in research §1.2 with the fix: the
  design system's reduced-motion rule misses non-`og-` elements (ChatScreen, MediaScreen moved
  between renders); BottomSheet's `max-height: 70vh` clips itself in a content-sized frame, so the
  frame is the card's `@dsCard` height; DatePicker reads the clock, so `Date` is pinned.
- React 18.3.1 is vendored in `scripts/vendor/` from the start rather than after a failed second
  render: offline reproducibility is the requirement, and the network is the variable most likely
  to move.
- `tokens.css` needed the per-style classes too (`.display`, `.pixel-tag` …) — the stress screens
  use them; the compiler has node tests in `make check` (`node --test 'scripts/*.test.mjs'`).
- Pages are clipped to `.phone`: 390×760 except EdgeNarrow 320×680 and EdgeScale 390×860.
- The research said 40 icons; the bundle has 47 (research §1.1 and B-11 corrected).
- `viddikDesignParity` now fails with "No design reference matched any fixture" — expected until
  B-08 adds the first fixture with a reference; `viddikVerify` ignores `design/` (green, `--rerun`).
- Not in `make check`: the render needs Chrome and rewrites 177 files; `make references` runs it.
