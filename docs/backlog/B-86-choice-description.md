---
id: B-86
title: "OldgeCheckbox and OldgeRadioOption: a muted second line under the label"
status: open
priority: P2
size: M
stage: stage-5-consumers
blocked_by: []
---

# B-86 — OldgeCheckbox and OldgeRadioOption: a muted second line under the label

Filed as #47 by the owner. `OldgeCheckbox` and `OldgeRadioOption` (`oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeCheckbox.kt`)
each take a single line of text. kachok's dialogs need a second, muted line under three choices
(the directory a removal deletes, what a sequential download costs the swarm, a choice not offered
yet), and so still draw their own checkboxes and radio buttons. A line drawn outside the component
does not align with the label, does not take the label's press and is not in the accessible
description.

- **The decision:** `description: String? = null` on `OldgeCheckbox` and on `OldgeRadioOption`,
  drawn under the label in the field help line's style, aligned with the label's text, inside the
  clickable row; for a screen reader it is the row's supporting text, read after the label.
- **Left for the item to decide, with a reason:** the trailing `badge` #47 offers as optional. A
  disabled option with a badge beside it is a second request; it is taken only if the design
  system's Badge fits the row without a new size.
- Rejected: a content slot for the label. A slot loses the alignment and the semantics the issue asks
  the component to own.

- AC: goldens without a description unchanged; goldens with one, checkbox and radio, enabled and
  disabled, in three skins, looked at; a test that a press on the description toggles and that the
  description is announced; mutants.
- Closes #47.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeCheckbox.kt`.
