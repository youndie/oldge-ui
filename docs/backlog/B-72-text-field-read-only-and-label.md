---
id: B-72
title: "OldgeTextField: a read-only state, a hidden label, and a label heard once"
status: wip
priority: P1
size: S
stage: stage-5-consumers
blocked_by: [B-71]
---

# B-72 — OldgeTextField: a read-only state, a hidden label, and a label heard once

Filed as #25 by the owner. `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeTextField.kt` has three gaps a settings form meets:

- **No read-only state.** A field that shows a current value but takes no typing (a folder chosen
  through *Browse…*) can only be `enabled = false`, which draws it at 75 % and announces it as
  disabled. `BasicTextField` takes `readOnly`; the component does not pass it. kachok makes the field
  unfocusable instead (youndie/kachok#132), which also drops it from the tab order, so the value can
  no longer be selected or copied.
- **The visible label cannot be hidden.** A settings row that shows its own label on the left still
  gets the one above the well. `OldgeSwitch` already has `showLabel = false`.
- **The label is read twice.** `FieldLabel(label)` is a text node, and the editable node sets
  `contentDescription = label` on top of it.

- **The decision:** `readOnly: Boolean = false`, passed to `BasicTextField`, at full opacity,
  focusable and selectable; `showLabel: Boolean = true` as on `OldgeSwitch`, the label then only the
  accessible name; and with the label visible, the field is named by the label node (merged), not by
  a second `contentDescription`.
- Rejected: keeping `enabled = false` for read-only. It says "unavailable" about a value in use.
- Blocked by B-71 only because both touch the reveal orb in the same file.

- AC: a read-only field is drawn as an enabled one (its golden equals the enabled golden), takes focus
  by Tab, lets its text be selected, and typing changes nothing; its semantics have no `Disabled`.
- AC: with `showLabel = false` no label text node is drawn and the field's name is still the label;
  with it visible, a semantics dump finds the label once. Mutants for each.
- Closes #25.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeTextField.kt`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/Field.kt`.
