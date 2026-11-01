---
id: B-81
title: "Select and CodeInput: the label is heard twice, as TextField's was"
status: done
priority: P2
size: XS
stage: stage-5-consumers
blocked_by: []
---

# B-81 — Select and CodeInput: the label is heard twice, as TextField's was

Found in B-72. `OldgeSelect` and `OldgeCodeInput` draw `FieldLabel(label)` above their control, a
text node, and then name the control with `contentDescription = label` on top of it
(`oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeSelect.kt`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeCodeInput.kt`). A screen reader meets the label as text
and again as the control's name, which is what #25 reported for `OldgeTextField`.

- **The decision: the same as B-72's.** The visible label is drawn but left out of the semantics tree
  (`FieldLabel(label, Modifier.clearAndSetSemantics {})`), and the control's own name says it once.
- Not covered: a `showLabel` on these two. Nobody asked for it; it is a line each if somebody does.

- AC: a semantics dump of each finds the label once, as the control's name; no golden moves. With a
  mutant per component.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeSelect.kt`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeCodeInput.kt`.

## Findings (2026-11-01)

- **As B-72:** `FieldLabel(label, Modifier.clearAndSetSemantics {})` in `OldgeSelect` and
  `OldgeCodeInput`; each control keeps `contentDescription = label`. `LabelOnceTest` finds the label
  once in the unmerged tree for each.
- **Two of the sample app's tests found a field by its visible label text:** the sign-in screen's
  «Код из СМС» and the settings page's «Синхронизировать через». They now look it up by its name,
  as they already did the e-mail field. They still assert that the field appears and goes.
- **Goldens:** none moved. **Mutants:** 2 of 2 killed (`--only B-81`).
