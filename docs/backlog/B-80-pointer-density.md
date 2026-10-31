---
id: B-80
title: "OldgeDensity.Pointer: control sizes for a mouse-driven desktop window"
status: done
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

## Findings (2026-11-01)

- **`OldgeDensity { Touch, Pointer }`**, given as `OldgeTheme(density = …)` and read as
  `OldgeTheme.density`. `Touch` is the default and changes nothing: no existing golden moved.
- **The numbers under `Pointer`**, the owner's from #21, in one place (`theme/OldgeDensity.kt`):
  - `hit-min` is 28 dp. It is a button's minimum width and an orb's minimum hit area.
  - Buttons are 28, 32 and 36 dp: small, medium and large.
  - An icon button is square at its height: 32 × 32 medium, 28 × 28 small. Under `Touch` it stays
    `hit-min` wide, as the design system has it.
  - The search bar is 32 dp, a medium button's height, and its input 30 dp, the bar inside its
    border. The clear orb is 30 dp drawn, and a 28 dp `hit-min` no longer inflates it to 44, so it
    sits whole inside the bar. That is the "44 dp input coerced into a 36 dp bar" of #21, gone.
- **`POINTER_MEDIUM` equals `space-6` by value only**, so it carries the marker naming its source,
  #21, which `NoTokenLiteralsTest` asks for.
- **Golden:** `PointerDensity` in three skins: a toolbar (two square icon buttons, a latched toggle,
  the search bar with its clear orb) and the three button sizes. Looked at: nothing clipped. A first
  cut of the fixture overflowed its 390 dp frame with a large button carrying an icon; that was the
  fixture, not the density.
- **Test:** `OldgeDensityTest` measures each size with no outer modifier under `Pointer`, checks that
  the clear orb is inside the bar, then switches to `Touch` and measures the design system's sizes
  again.
- **Mutants:** 4 of 4 killed (`--only B-80`): the button's height, the square icon button, the search
  bar's height and the orb's hit area.
- **Not covered here, filed as [B-83](B-83-pointer-density-everywhere.md):** the controls #21 did
  not measure, which still hold `hit-min` under `Pointer`: TextField, Select, Switch, Checkbox,
  Slider, Menu and the Balloon's close.
- **ABI:** `OldgeDensity`, `OldgeTheme.density`, and `OldgeTheme`'s new parameter.
