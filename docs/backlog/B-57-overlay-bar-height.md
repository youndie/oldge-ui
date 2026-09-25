---
id: B-57
title: "The overlay WindowBar is 72 px in Chrome and 64 here"
status: done
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

## Findings (2026-09-25)

- **The fix is the order of two modifiers.** The overlay bar's bottom padding now comes before its
  minimum height, so the 8 dp adds to the 64 dp as CSS's `content-box` does: 72 in all, with the
  content centred in the top 64. The veil is drawn before the padding, so it covers all 72. The
  plain bar has no vertical padding and stays 56.
- **Parity against main** (195 artboards). MediaScreen went from 1.19 / 1.29 / 1.21 % to
  0.38 / 0.42 / 0.46 %, under the 0.72 % floor, and no other artboard moved by 0.01. The current
  dot's fill (B-58) is left in that number.
- **Goldens:** WindowBarStates, whose overlay bar is 8 dp taller with its content 4 dp lower, and
  MediaScreen. Both were looked at.
- **Behaviour** (`BarBehaviourTest`): the overlay bar is 72 dp and the plain one 56.
- **Mutants:** 1 of 1 killed (the padding back inside the minimum), and B-24's nine all die.
- **An editing slip, again, caught before commit.** Appending the test sliced `BarBehaviourTest` at
  its class's last brace and dropped the file-level `NAV` after it, as in B-54. Restored, and the
  diff checked for removed lines.
