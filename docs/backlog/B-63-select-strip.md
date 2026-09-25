---
id: B-63
title: "Select's list shows the Menu's empty icon strip"
status: done
priority: P2
size: XS
stage: stage-2-components
blocked_by: []
---

# B-63 — Select's list shows the Menu's empty icon strip

Found by the owner in the desktop app. The page Select («Экран») opens with a strange strip down
its left side, and the owner noted that the design shows it too.

**What it is.** It is the Menu's icon column, an XP / Office 2003 menu gutter:
`.og-menu { background: linear-gradient(90deg, var(--pill-lo) 0 40px, var(--surface) 40px) }`.
Each item is laid out as `grid-template-columns: 40px 1fr auto`, with its icon centred in the
strip. The design system's Menu preview has icons in it, so there it reads as a column. The Select
opens its options in `OldgeMenu` (B-18). The design system's own Select is a native `<select>` with
no list of its own, so that part is the library's choice. The page names have no icons, and the
strip is empty.

- **The decision.** Select's list drops the strip, and the icon column with it, when none of its
  options has an icon. It keeps both when any does. `OldgeMenu` stays as the design draws it,
  strip and all, since there the design is explicit. *Rejected:* dropping the strip from Menu too.
  The design paints it on every menu, and Menu's parity would move.
- AC: an icon-less Select's open list has no strip, and its labels start 12 px into the row. A
  Select with icons, and every Menu, keep the strip. Menu's goldens are unchanged. With a mutant.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/OldgeMenu.kt`,
  `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeSelect.kt`.

## Findings (2026-09-25)

- `OldgeMenu` now delegates to an internal `OldgeMenuPopup(…, strip)`, and the public Menu
  always passes `strip = true`. The public API is unchanged, and `checkKotlinAbi` holds it. Select
  passes `strip = options.any { it.icon != null }`. With no strip, a row's 40 px icon column
  becomes a `space-3` pad, as the closed Select's value has. Its label starts 16 px into the list:
  the 1 px border, the 3 px row inset and the 12 px pad.
- **Test:** `SelectCodeBehaviourTest.an_icon_less_selects_list_has_no_empty_strip_and_one_with_icons_keeps_it`
  opens a Select of two page names. The label is at 16 dp, and `surface` is where the strip would
  be. Its control is the same Select with icons: the label at 44 dp and `pill-lo`, so the probe
  pixel does see the strip.
- **Mutants:** 3 of 3 killed: the strip always on (the old behaviour), always off, and drawn
  despite `strip = false`.
- Menu's goldens and parity are unchanged; see `viddikVerify` in the gate.
