---
id: B-73
title: "OldgeSegmented: a disabled state"
status: open
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
