---
id: B-88
title: "OldgeProgressWell: a caller's colour for the well"
status: done
priority: P2
size: S
stage: stage-5-consumers
blocked_by: []
---

# B-88 — OldgeProgressWell: a caller's colour for the well

Filed as #49 by the owner. `OldgeProgressWell` (B-79, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/OldgeProgressBar.kt`) takes the
row's colour for the fill, but draws the well's ground and the gaps between its blocks in `c.well`.
In a list whose rows are tinted by state (the selected row in `select`, a failed one in red), the
skin's plain well sits inside the tinted row (youndie/kachok#136).

- **The decision:** `well: Color? = null`, used for the ground and the gaps; `null` keeps `c.well`.
  It follows B-79's `fill`: the row's colours belong to the caller's list, not to a skin token.
- AC: `OldgeProgressBar` and the default `OldgeProgressWell` goldens unchanged; goldens with a
  caller's well in three skins, looked at; a test that the ground and a gap take the colour; a mutant.
- Closes #49.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/OldgeProgressBar.kt`.

## Findings (2026-11-01)

- **`OldgeProgressWell(well = null)`** sets the well's ground and the gaps between its blocks; `null`
  keeps `c.well`. The 1 dp border (`edge`) and the sunken bevel stay the skin's, so a tinted well still
  reads as a well. `OldgeProgressBar` does not take it: a bar with its head line is not a list row.
- **Goldens:** `ProgressWellGround` in three skins: a well in a selected row's `select`; the skin's
  own well in the same row for comparison (the issue's complaint); and a failed row's red tint with a
  `danger` fill. I looked at all three. No golden recorded before moved. **Test:**
  `ProgressWellBehaviourTest.a_callers_well_is_the_ground_and_the_gaps` reads the ground past the fill
  and the first gap. **Mutants:** `--only B-88`.
