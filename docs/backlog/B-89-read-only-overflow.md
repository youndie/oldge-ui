---
id: B-89
title: "OldgeTextField: a read-only value with the overflow the caller chooses"
status: open
priority: P2
size: M
stage: stage-5-consumers
blocked_by: []
---

# B-89 — OldgeTextField: a read-only value with the overflow the caller chooses

Filed as #50 by the owner. A read-only `OldgeTextField` (B-72, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeTextField.kt`) cuts a
long value at its end. For a path the end is what matters: kachok's 180 dp folder fields show
`/home/user/Down` of `/home/user/Downloads/iso`, where its Material kit showed
`/home/…/Downloads/iso`.

- **The decision:** `overflow: TextOverflow = TextOverflow.Clip` on the field, applied only while it is
  read-only and not focused, where the value is drawn as text; Compose's own `Ellipsis`,
  `StartEllipsis` and `MiddleEllipsis` cover the three cases #50 lists. Focused, the field shows the
  whole value for selecting and copying, as today.
- Rejected: a new `OldgeOverflow` enum. Compose's `TextOverflow` already names these, and
  `OldgeText` takes it.

- AC: the default read-only golden and B-72's pixel-equality test unchanged; goldens with a middle
  and a start ellipsis in three skins, looked at; a test that focus shows the whole value and that
  the accessible value is the whole value either way; a mutant.
- Closes #50.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeTextField.kt`.
