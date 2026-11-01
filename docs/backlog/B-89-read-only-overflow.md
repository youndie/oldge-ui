---
id: B-89
title: "OldgeTextField: a read-only value with the overflow the caller chooses"
status: done
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

## Findings (2026-11-01)

- **`OldgeTextField(overflow = TextOverflow.Clip)`.** While a single-line read-only field is not
  focused, its value is drawn as text with the caller's cut, over the input, which is then hidden.
  Focused, the input is shown and the value is whole. The input's node keeps the whole value for a
  screen reader either way. An editable, multiline or password field ignores the parameter, and the
  default `Clip` draws nothing new.
- **Compose Desktop ignores `StartEllipsis` and `MiddleEllipsis`.** On 1.12.1 a `BasicText` with
  either of them, `maxLines = 1` and `softWrap` true or false, is drawn exactly as with `Ellipsis`:
  Skia's paragraph has only an end ellipsis. The first goldens showed it. So the decision to take
  Compose's `TextOverflow` holds for the API, but those two cuts are made by the library: a
  `TextMeasurer` binary search for the most characters that fit with «…». `Start` keeps the end;
  `Middle` keeps both ends, the end taking the odd character; a surrogate pair is never split.
  `Ellipsis` is still Compose's own.
- **Goldens:** `TextFieldOverflow` in three skins: a 220 dp read-only path cut at its end (the input's
  own), at its start (`…wnloads/iso/ubuntu-24.04`) and in its middle (`/home/user/…ubuntu-24.04`); I
  looked at all three. No golden recorded before moved, and B-72's pixel-equality test is unchanged.
  **Tests:** `TextFieldOverflowTest`, two cases:
  - the four cuts draw four different images; focused, the field is drawn as the plain one; the
    accessible value is whole;
  - an editable field ignores the overflow.

  **Mutants:** `--only B-89`.
