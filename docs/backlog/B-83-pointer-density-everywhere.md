---
id: B-83
title: "OldgeDensity.Pointer for the controls B-80 left at hit-min"
status: done
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

## Findings (2026-11-01)

- **Under `Pointer`, measured with no outer modifier** (`OldgeDensityControlsTest`):

  | | Touch | Pointer |
  |---|---|---|
  | TextField's box | 44 | 32 |
  | Select | 44 | 32 |
  | Switch row | 44 | 30, its track |
  | Checkbox row | 44 | 28 |
  | Slider | 44 | 28 |
  | Menu item | 44 | 28 |
  | Balloon's close | 44 | 28 |

- **TextField and Select take the search bar's 32 dp**, so the fields of a form line up with
  it. The other five take the pointer `hit-min`, 28. The TextField's input is not just
  shrunk: its vertical padding becomes whatever centres its line in 32 dp (5 dp for the 20 dp line),
  in place of the CSS's 11. Otherwise the 42 dp input would hold the box at 44 whatever its minimum
  said (B-53).
- **Everything fits inside the smaller boxes:** the Switch's 30 dp track, the Checkbox's 20 dp box,
  the Slider's 26 dp thumb, the menu's 18 dp icons on 20 dp lines, and the reveal orb's 30 dp.
- **Golden:** `PointerForm` in three skins: a read-only field, a select, a switch, a checkbox, a
  slider and a balloon. Looked at: nothing cut. No `Touch` golden moved.
- **Mutants:** 8 of 8 killed (`--only B-83`), one per control and one for the field's padding.
- A first full gate stopped with "Gradle build daemon has been stopped: stop command received". That
  came from outside this session (another project's 9.7.1 daemon was running on the mac), not from a
  test, and the rerun was green.
