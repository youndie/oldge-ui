---
id: B-79
title: "OldgeProgressWell: the compact well with a caller's fill, for a list row"
status: done
priority: P2
size: S
stage: stage-5-consumers
blocked_by: []
---

# B-79 — OldgeProgressWell: the compact well with a caller's fill, for a list row

Filed as #22 by the owner. `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/OldgeProgressBar.kt` is the 20 dp well under its own head
line, its fill colour only from `OldgeProgressStatus`. A dense list row wants the well at about 10 dp,
no head line, in the row's own colour (red for an error row, dim for a stopping one). kachok re-draws
the look by hand (`OldgeKit.ProgressBar`: the well, glossy 8 dp blocks with 2 dp gaps, the edge), so
the library's changes never reach it.

- **The decision:** a public `OldgeProgressWell(value, modifier, fill: Color? = null)` that draws only
  the well and its blocks at the height its modifier gives it (default 10 dp), and `OldgeProgressBar`
  drawn with it, so the two cannot drift. `fill = null` is the status colour of today.
- Rejected: a `compact` flag on `OldgeProgressBar`. A bar with its head line switched off by a boolean
  is two components behind one name.

- AC: `OldgeProgressBar`'s goldens and parity are unchanged; `OldgeProgressWell` has goldens at 10 dp in
  three skins with the default and a custom fill; value 0 and 1 draw no block and a full well. With a
  mutant.
- Closes #22.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/OldgeProgressBar.kt`.

## Findings (2026-11-01)

- **`OldgeProgressWell(value, contentDescription, modifier, fill)`**, public. `OldgeProgressBar` now
  draws its 20 dp well with it. That move was pixel-exact: no ProgressBar golden moved, so the two
  cannot drift.
- **10 dp by default, and a caller's height first wins.** The well's own `height(10.dp)` comes after
  the caller's modifier, so `Modifier.height(14.dp)` gives 14. The blocks are drawn inside the 1 dp
  border and 2 dp padding, so a well of 6 dp or less shows none; the KDoc says so.
- **A caller's fill without a typed colour.** The accent's gradient has no counterpart for an
  arbitrary colour, so a `fill` is drawn solid under the skin's `gloss` as a 1 px inset line along the
  top. That is `.og-select__arrow`'s `inset 0 1px 0 var(--gloss)`, the design system's own treatment
  of a small glossy part. The blocks and gaps are the same.
- **The name is required.** A first cut made `contentDescription` optional with no default, and the
  library's own `AccessibilityTest` rejected the unnamed wells in the fixture. A Russian default would
  have repeated B-71's problem. So the caller names it, as `OldgeIconButton`'s caller must.
- **Found on the way:** four fixed Russian names B-71 missed: ProgressBar's «Выполнение», Stepper's
  «готово», PageDots' «Страница N из M» and DatePicker's day names. Filed as
  [B-82](B-82-remaining-fixed-names.md).
- **Goldens:** `ProgressWell` in three skins: the accent at 0, 45 and 100 %, `danger`, `inkMuted`, and
  a 14 dp well; looked at. **Tests:** `ProgressWellBehaviourTest`, three cases: heights, semantics, and
  the caller's colour in a block. **Mutants:** 2 of 2 killed (`--only B-79`).
