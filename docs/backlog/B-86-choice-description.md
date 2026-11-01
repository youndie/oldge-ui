---
id: B-86
title: "OldgeCheckbox and OldgeRadioOption: a muted second line under the label"
status: done
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

## Findings (2026-11-01)

- **`OldgeCheckbox(description = null)` and `OldgeRadioOption(description = null)`.** The line is
  the TextField help line's style (`caption` in `ink-muted`), under the label and aligned with its
  text, inside the clickable row. The row is still at least `hit-min` and grows to fit two lines.
- **Alignment:** with a description, the box sits on the label's line rather than in the middle of
  both lines. The box and the `body` line are both 20 px, so the box's top is the line's top. Without
  a description the row is laid out as before (`align-items: center`). No golden recorded before moved.
- **For a screen reader:** the row's merged node says the label, then the description. The issue
  offered `stateDescription`, but that would replace "checked" / "not checked", so it was not used.
- **The badge #47 offers as optional is left out.** The description line already covers "a choice
  shown but not offered yet": a disabled option with «Появится позже» under it. A badge beside the
  label would be a second layout for one row and a new size the design system does not give. If
  kachok still wants it, it is a new ask.
- **Goldens:** `CheckDescription` in three skins: a checked box with a path, a disabled box, and a
  radio group with a described option and a described disabled one. I looked at all three.
  **Tests:** `CheckDescriptionTest`, two cases: what is heard, and a press on the description
  toggling or picking. **Mutants:** `--only B-86`.
