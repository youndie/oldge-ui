---
id: B-74
title: "OldgeDialog: a maxWidth for the window, modal and not"
status: open
priority: P2
size: XS
stage: stage-5-consumers
blocked_by: []
---

# B-74 — OldgeDialog: a maxWidth for the window, modal and not

Filed as #27 by the owner. `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/containers/OldgeDialog.kt` caps the window at
`widthIn(max = 358.dp)`, the design system's phone width:

- **modal:** `modifier` goes to the full-screen `Box`; the window gets only `padding(space4)`, so it is
  never wider than 358 dp;
- **not modal:** a fixed `width(520.dp)` set first wins over the cap, but a range does not, so "as wide
  as 520, narrower in a narrow window" cannot be said.

kachok's dialogs are 520 and 420 dp, non-modal with a fixed width and the app's own scrim
(youndie/kachok#132).

- **The decision:** `maxWidth: Dp = 358.dp`, used by both windows in place of the literal. The default
  keeps today's look and every golden.
- Rejected: routing `modifier` to the window in the modal case. It changes what an existing caller's
  modifier means.

- AC: a modal dialog with `maxWidth = 520.dp` in a 900 dp window is 520 dp wide, and in a 400 dp window
  is the window less its padding; a non-modal one with `fillMaxWidth()` and `maxWidth = 520.dp` behaves
  the same. Goldens unchanged. With a mutant.
- Closes #27.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/containers/OldgeDialog.kt`.
