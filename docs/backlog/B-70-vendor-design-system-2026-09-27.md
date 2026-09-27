---
id: B-70
title: "Re-vendor the design system: the hook under the glyph, in the design itself"
status: done
priority: infra
size: XS
stage: stage-4-product
blocked_by: [B-69]
---

# B-70 — Re-vendor the design system: the hook under the glyph, in the design itself

The owner asked for B-69's change in the design system too, so that the library and the design
agree again. It is re-vendored by `CLAUDE.md`'s procedure (B-36).

## Findings (2026-09-27)

- **The artifact, before the edit,** was the vendored snapshot: `lastChange` 2026-09-24T21:57:36Z,
  and `bundle.css` and CategoryTabs' README byte for byte the same. So nothing unseen came in with
  the edit.
- **The edit, artifact version 23:**
  - `bundle.css`: `.og-cat__label { left: 6px }` became `left: calc(50% - 1px)`. `.og-cat` is the
    label's containing block, 56 px wide, so that is 27 px, and it stays right if the tab's width
    ever changes.
  - CategoryTabs' README gained one line saying where the hook's leg stands.
  - The index's `lastChange` is 2026-09-27T19:12:58Z, via Claude Code. Every other key was kept.
- **Diff** (`vendor_design_system.py diff`):

  ```
  Design system 2026-09-24T21:57:36Z -> 2026-09-27T19:12:58Z.
  README.md changed: CategoryTabs.
  components/bundle.css changed.
  ```

- **Regenerated.** The tokens and icons did not change. The references were re-rendered: all of
  oldge-core's (`make references`), then each of `sample`'s nine pages. Only CategoryTabs and the
  Launcher changed, and every other reference came out byte for byte the same. `design-references.mjs`
  takes one `--only`: given several, it keeps the last, so each page is its own run.
- **Parity is back to the numbers from before B-69:**
  - CategoryTabs: 0.48 / 0.50 / 1.01 %, where B-69's deviation had made it 2.76 / 2.79 / 3.33 %;
  - Launcher: 1.35 / 1.41 / 1.61 %, where it had been 1.75 / 1.80 / 2.00 %.

  The deviation is closed.
