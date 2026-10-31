---
id: B-75
title: "OldgeText: an onTextLayout callback"
status: done
priority: P2
size: XS
stage: stage-5-consumers
blocked_by: []
---

# B-75 — OldgeText: an onTextLayout callback

Filed as #23 by the owner. `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/type/OldgeText.kt` has no `onTextLayout`. A consumer that measures
text (an ellipsis inside a path, a fitted column) falls back to `BasicText` with the same style and
loses `OldgeText`'s placement on the CSS baseline (B-51), so the two can sit a pixel apart. kachok's
`OldgeKit.Text` does this whenever a caller passes a layout callback.

- **The decision:** `onTextLayout: ((TextLayoutResult) -> Unit)? = null`, forwarded to the text it
  lays out. The result is the one of the text actually drawn, after the baseline shift.

- AC: a test receives a `TextLayoutResult` whose `lineCount` and `size` match the drawn text, and
  `OldgeText` with and without the callback draws the same pixels. With a mutant (the callback not
  forwarded).
- Closes #23.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/type/OldgeText.kt`.

## Findings (2026-10-31)

- **`onTextLayout: ((TextLayoutResult) -> Unit)? = null`**, forwarded to the `BasicText` that
  `OldgeText` lays out, so the result is the text actually drawn: its lines, widths and offsets.
- **Its vertical positions are before the baseline move.** `onCssBaseline` moves the laid-out text
  by a whole pixel or none, in a layout modifier outside `BasicText`; the result is in `BasicText`'s
  own coordinates. That serves what the issue asks for (widths, line count, an ellipsis's offset
  along a line); the KDoc says so for a caller that draws at a line's top.
- **Test:** `OldgeTextLayoutTest`. The callback is called. A 160 dp sentence wraps, and the result's
  size is the node's. The text draws the same pixels with the callback and without it.
- **Goldens:** none moved. **Mutants:** 1 of 1 killed (`--only B-75`). **ABI:** `OldgeText` gained a
  parameter.
