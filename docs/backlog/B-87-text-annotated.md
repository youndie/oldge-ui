---
id: B-87
title: "OldgeText: an AnnotatedString overload"
status: done
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

## Findings (2026-11-01)

- **`OldgeText(text: AnnotatedString, …)`** takes the same parameters as the string overload,
  `onTextLayout` included. It moves the line onto the CSS baseline of `style`; a span in another face
  keeps its own metrics inside that line.
- **The string overload does not delegate to it, contrary to the decision.** Routed through
  `BasicText`'s annotated overload, six goldens of long titles moved (`AccordionStates_*`,
  `WindowBarStates_*`): the two overloads of `BasicText` lay out an ellipsis differently. So each
  `OldgeText` calls its own `BasicText`, and the placement exists once in a shared modifier
  (`onCssBaselineOf`), which is what the decision was for. With that, no golden moved.
- **The B-51 mutant moved with the code.** `consumer-text-reported-baseline` now removes the move in
  the shared modifier, which is where the string overload's placement lives.
- **Tests:** `OldgeTextAnnotatedTest`, two cases. An annotated text is drawn pixel for pixel where
  the plain one is, and not where `BasicText` puts it (13 px in a 16 px line, as `ConsumerTextTest`
  does, so the control can tell them apart). A red span is drawn red. **Mutants:** `--only B-87`, and
  B-51's again.
