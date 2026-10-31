---
id: B-77
title: "OldgeButton: a Danger variant for an irreversible confirm"
status: wip
priority: P2
size: S
stage: stage-5-consumers
blocked_by: []
---

# B-77 — OldgeButton: a Danger variant for an irreversible confirm

Filed as #19 by the owner. `OldgeButtonVariant` has `Primary`, `Secondary` and `Plain`. For an
irreversible *Delete* a consumer chooses between the lime "go" of `Primary` and a `Secondary` that no
longer marks the action as different; kachok's remove dialog uses `Secondary`.

- **The decision, the owner's: a deviation from the design system,** which has no such button. A
  `Danger` variant drawn from the skin's `danger` token, with `Primary`'s gloss, bevel and press, in
  every skin. The KDoc says when: the confirm of an action that cannot be undone, one per dialog.
- **No reference exists for it**, so it closes on its own goldens, looked at in three skins, not on a
  parity number. If the owner adds it to the design system artifact, re-vendoring (B-36) brings a
  reference.
- Rejected: a `colors` override on `Primary`. It gives every caller a way to break the one-accent rule.

- AC: `OldgeButton(variant = Danger)` is drawn in `danger` with `Primary`'s materials (goldens in three
  skins, both sizes, pressed and disabled), and its label passes the contrast check the other variants
  pass. With a mutant.
- Closes #19.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/OldgeButton.kt`.
