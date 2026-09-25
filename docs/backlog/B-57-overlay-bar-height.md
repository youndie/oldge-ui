---
id: B-57
title: "The overlay WindowBar is 72 px in Chrome and 64 here"
status: open
priority: P2
size: XS
stage: stage-2-components
blocked_by: [B-24]
---

# B-57 — The overlay WindowBar is 72 px in Chrome and 64 here

Found in B-38, on MediaScreen: the first reference with an overlay bar in it. The WindowBar preview
has none, so B-24 held the overlay only to its own golden.

- **Chrome.** `.og-winbar` sets no `box-sizing`, so it is `content-box`.
  `.og-winbar--overlay { min-height: 64px; padding-bottom: 8px }` is then a 64 px content box
  with 8 px under it: 72 px in all, the title and orbs centred in the top 64.
- **Here.** `OldgeWindowBar` applies the minimum before the padding
  (`defaultMinSize(64).padding(bottom = 8)`), which is `border-box`: 64 in all, the content
  centred in 56.
- **The result on the page.** Everything in the bar sits 4 px higher than Chrome's, and the dark
  veil ends 8 px short.

- **The decision and its reason.** The padding goes outside the minimum, as `content-box` has it.
  The non-overlay bar has no vertical padding and does not change.
- AC: a behaviour test holds the overlay bar at 72 dp and the plain one at 56, with a mutant.
  MediaScreen's parity drops, and WindowBarStates' golden is re-recorded and looked at.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/OldgeWindowBar.kt`.
