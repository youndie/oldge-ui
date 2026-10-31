---
id: B-72
title: "OldgeTextField: a read-only state, a hidden label, and a label heard once"
status: done
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

## Findings (2026-10-31)

- **`readOnly: Boolean = false`**, passed to `BasicTextField`. A read-only field draws exactly as an
  enabled one (pixel for pixel in `TextFieldReadOnlyLabelTest`), takes focus, lets its text be
  selected and copied, and shows no caret: Compose hides the cursor of a read-only field itself.
  It takes no typing in the strongest way: its node has no `SetText` action at all, and
  `IsEditable` is false. The reveal orb keeps working, since showing a value is not typing.
- **`showLabel: Boolean = true`**, as on `OldgeSwitch`. Off, the field is only its 44 dp box, and the
  label still names it.
- **Heard once.** Compose has no "labelled by" relation, so the issue's "name the field by its label
  node" has no direct form. The field keeps its `contentDescription = label`, and the visible label
  is drawn with `clearAndSetSemantics {}`: one node says the label, the field's own. The existing
  tests that find a field by its label (`onNodeWithContentDescription`) pass unchanged.
- **Found on the way:** `OldgeSelect` and `OldgeCodeInput` read their label twice the same way. Filed
  as [B-81](B-81-select-code-input-label-once.md), not folded in.
- **ABI:** `OldgeTextField` gained two parameters; recompile against this build (source-compatible).
- **Goldens:** none moved. **Mutants:** 3 of 3 killed (`--only B-72`).
