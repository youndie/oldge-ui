---
id: B-85
title: "OldgeTextField: a highlighted border in accent-ink"
status: open
priority: P2
size: S
stage: stage-5-consumers
blocked_by: []
---

# B-85 — OldgeTextField: a highlighted border in `accent-ink`

Filed as #46 by the owner. `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeTextField.kt` draws its well's border in `edge`, or in
`danger` when there is an `error`, and in nothing else. A settings screen marks a value that differs
from its default; kachok rings such a field in `accent-ink` by drawing its own 1 dp border over the
well, with the `xs` radius copied from the component (youndie/kachok#132, #136). A change to the
component's radius or border width would leave that copy behind.

The design system has no such state; `accent-ink` is its accent for text and lines on the page.

- **The decision:** `highlighted: Boolean = false`, drawn as the well's own border in `accent-ink`.
  `error` wins over it. It is a state, not a colour, so the skins keep choosing the colour.
- Rejected: `borderColor: Color? = null`. Any colour a caller passes would bypass the token layer
  (research D4) and the contrast checks.

- AC: default goldens and parity unchanged; a highlighted field and a highlighted field with an
  error in three skins, looked at; `accent-ink` against the field's ground is checked in
  `ContrastTest` at the non-text 3:1; a behaviour test with a mutant.
- Closes #46.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeTextField.kt`.
