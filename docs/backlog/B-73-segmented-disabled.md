---
id: B-73
title: "OldgeSegmented: a disabled state"
status: done
priority: P2
size: XS
stage: stage-5-consumers
blocked_by: []
---

# B-73 — OldgeSegmented: a disabled state

Filed as #26 by the owner. `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/OldgeSegmented.kt` has no `enabled` parameter: every
segment is `selectable` with `Role.RadioButton`, always takes the press, and always pulses. A choice
shown but fixed for the running session has nothing to draw with; kachok swallows the press in
`onSelect` (youndie/kachok#132), and the control still looks and sounds live.
`OldgeTextField`, `OldgeSwitch` and `OldgeButton` all take `enabled`.

- **The decision:** `enabled: Boolean = true`. When false the group is drawn at the text field's 75 %,
  with no press scale and no indication, and `selectable(enabled = false)`, so the semantics say
  disabled.
- Rejected: per-segment `enabled`. Nobody asked for it, and the design system has no look for one
  greyed option among live ones.

- AC: a disabled Segmented ignores a click (selection unchanged, `onSelect` not called), its semantics
  carry `Disabled` on each option, and a golden shows it at 75 %. With a mutant.
- Closes #26.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/OldgeSegmented.kt`.

## Findings (2026-10-31)

- **`enabled: Boolean = true`** on `OldgeSegmented`. Off, the group is drawn at 75 %, the text field's
  `.og-field--disabled` (the design system has no disabled Segmented, so the number is borrowed and
  marked as a CSS literal). Each option is `selectable(enabled = false)`: no press, so neither the
  squash nor the indication, and `Disabled` in the semantics.
- **Golden:** `SegmentedDisabled` in three skins, the library's own (no preview shows it, so there is
  no reference and no parity number). Looked at: the selection still reads, dimmed. No other golden
  moved.
- **Test:** `FabSegmentedBehaviourTest.a_disabled_segmented_takes_no_press_and_says_so` — a click
  changes nothing and calls nothing, both options are not enabled, and a held press draws the same
  pixels as rest.
- **Mutants:** 1 of 1 killed (`--only B-73`). **ABI:** `OldgeSegmented` gained a parameter.
