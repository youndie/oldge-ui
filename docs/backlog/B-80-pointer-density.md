---
id: B-80
title: "OldgeDensity.Pointer: control sizes for a mouse-driven desktop window"
status: open
priority: P2
size: M
stage: stage-5-consumers
blocked_by: [B-71, B-78]
---

# B-80 — OldgeDensity.Pointer: control sizes for a mouse-driven desktop window

Filed as #21 by the owner. The touch minimums make a desktop toolbar too large: `OldgeIconButton` is
held to `hit-min` (44 dp) wide, `OldgeSearchBar` has `min-height: 48px` and a 44 dp input, and
`OldgeButtonSize.Small` is 36 dp. kachok gets 36 × 36 icon buttons, a 36 dp search bar and a 26 dp
banner button only by an outer size modifier winning over inner minimums (youndie/kachok#129, #130),
which nothing guarantees.

B-60 found that a touch is extended to 48 dp by Compose's hit testing and a mouse is not; this item
is the other side of that: for a pointer the drawn minimums buy nothing.

- **The decision, the owner's:** a density in the theme, `OldgeDensity.Touch` (default, today's sizes)
  and `OldgeDensity.Pointer`, taken by `OldgeTheme(density = …)`. `Pointer` lowers `hit-min`, the
  search bar's heights and the button heights to roughly 28, 32 and 36 dp; the exact numbers are set
  in the item from goldens and recorded here. Each component reads its sizes from the theme, not
  the CSS literal.
- `Touch` changes nothing: every golden and parity number stays.
- Rejected: documenting the outer-modifier trick. It depends on modifier order, and the 44 dp input in
  a 36 dp bar depends on Compose coercing it.
- Blocked by B-71 (the search bar) and B-78 (the icon button) only because they touch the same code.

- AC: under `Pointer` an icon button, a search bar and a small button measure the recorded sizes with
  no outer modifier, and their content is not clipped (goldens in three skins); under `Touch` every
  existing golden is unchanged. With a mutant per component.
- Closes #21.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/theme/OldgeTheme.kt`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/OldgeButton.kt`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeSearchBar.kt`.
