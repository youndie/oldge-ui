---
id: B-87
title: "OldgeText: an AnnotatedString overload"
status: open
priority: P2
size: S
stage: stage-5-consumers
blocked_by: []
---

# B-87 — OldgeText: an `AnnotatedString` overload

Filed as #48 by the owner. `OldgeText` (`oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/type/OldgeText.kt`) takes only a `String`, so text
with styled spans has nothing that places it on the design system's baseline. kachok's empty state
(«Drop a `.torrent` anywhere…», the extension in mono) goes through `BasicText` and is the one line
in the window off `OldgeText`'s baseline (youndie/kachok#136).

- **The decision:** `OldgeText(text: AnnotatedString, …)` with the same parameters, `onTextLayout`
  included, and the `String` overload delegating to it, so the baseline placement exists once.
- Rejected: a separate `OldgeRichText`. It is the same component with another text type.

- AC: every existing `OldgeText` golden unchanged; a test that an annotated text and the same plain
  text sit on the same baseline, and that a span keeps its style; a mutant.
- Closes #48.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/type/OldgeText.kt`.
