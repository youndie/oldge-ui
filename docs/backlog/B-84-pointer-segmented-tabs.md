---
id: B-84
title: "OldgeDensity.Pointer for Segmented and Tabs"
status: done
priority: P3
size: S
stage: stage-5-consumers
blocked_by: []
---

# B-84 — `OldgeDensity.Pointer` for Segmented and Tabs

Filed as #45 by the owner. B-80 and B-83 gave the pointer sizes to the buttons, the forms, the menu
and the balloon. Two controls still hold their touch heights at either density:

- `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/OldgeSegmented.kt`: an option's `MIN_HEIGHT`, 40 dp
  (`.og-seg__opt { min-height: 40px }`);
- `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/OldgeTabs.kt`: `TAB`, 40 dp at rest, and `TAB_CURRENT`, 44 dp for the current tab.

kachok's settings row sets its segmented choice to 32 dp from outside (youndie/kachok#136), the
modifier-order trick #21 asked to replace, and its details tabs keep a 44 dp current tab.

- **The decision:** both heights come from `LocalOldgeDensity`, as B-83 did for the forms. Under
  `Pointer`: a segment at 32 dp, the medium button's and the text field's pointer height; a tab at
  28 dp at rest and 32 dp when current, keeping the design system's 4 dp step between the two.
- Rejected: a size parameter on each control. The density is the one switch a window sets (B-80).

- AC: `Touch` goldens and parity unchanged; `Pointer` goldens for both in three skins, looked at; a
  behaviour test of the heights at both densities, with a mutant.
- Closes #45.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/OldgeSegmented.kt`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/OldgeTabs.kt`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/theme/OldgeDensity.kt`.

## Findings (2026-11-01)

- **Segmented:** under `Pointer`, the whole group is 32 dp (`POINTER_MEDIUM`), the text field's and
  the medium button's height. So an option's minimum is 32 less the group's 1 dp border and 2 dp
  padding on each side, 26 dp. "A segment at 32 dp" in #45 is read as the group: that is what kachok
  set from outside, and what stands level with the field beside it.
- **Tabs:** under `Pointer`, the current tab is 32 dp and a tab at rest is 28 dp (`POINTER_SMALL`). The
  4 dp step and the rest tab's `margin-top: 4px` are kept, so the list is 32 dp, as 44 is for a finger.
- Both read the existing pointer heights: no new size, and nothing changes under `Touch`. No golden
  recorded before moved, and the public API is unchanged.
- **Goldens:** `PointerChoices` in three skins: a 32 dp field with a segmented choice level with it,
  and three tabs; I looked at all three. **Test:**
  `OldgeDensityControlsTest.segmented_and_tabs_are_32_dp_for_a_pointer_and_keep_their_touch_heights`
  checks 32/32/28 dp under `Pointer` and 46/44/40 dp under `Touch`. **Mutants:** `--only B-84`.
