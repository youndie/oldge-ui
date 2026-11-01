---
id: B-83
title: "OldgeDensity.Pointer for the controls B-80 left at hit-min"
status: wip
priority: P3
size: M
stage: stage-5-consumers
blocked_by: []
---

# B-83 — `OldgeDensity.Pointer` for the controls B-80 left at `hit-min`

B-80 gave the pointer sizes to what #21 measured in kachok's toolbar: `OldgeButton`,
`OldgeIconButton`, `OldgeOrbButton`'s hit area and `OldgeSearchBar`. #21 also asks that every
control read its size from the theme. These still hold `hit-min`, 44 dp, under `Pointer`:

- `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeTextField.kt`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeSelect.kt`: the box's minimum height;
- `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeSwitch.kt`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeCheckbox.kt`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeSlider.kt`: the row or track's height;
- `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/OldgeMenu.kt`: an item's height;
- `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/OldgeBalloon.kt`: the close button's box.

- **The decision:** each reads `OldgeTheme.density.hitMin` (or B-80's 28/32/36) in place of the
  token, with a golden under `Pointer` per control, looked at. A settings form in kachok is the case
  that will show it: a 44 dp field beside a 32 dp search bar.
- Not covered: the design system's own sizes under `Touch`. They do not move.

- AC: under `Pointer` each listed control measures its pointer size with no outer modifier, its
  content uncut; under `Touch` no golden moves. Mutant per control.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/theme/OldgeDensity.kt`.
