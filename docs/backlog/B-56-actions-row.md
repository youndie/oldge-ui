---
id: B-56
title: "OldgeActions: the design system's og-actions button row, which wraps to a column"
status: open
priority: P2
size: S
stage: stage-2-components
blocked_by: [B-12]
---

# B-56 — `OldgeActions`: the design system's `og-actions` button row, which wraps to a column

Found in B-39. Both Edge pages end in `<div class="og-actions">` holding two buttons. The design
system's README gives the class as its answer to a row of buttons that squeezes its labels:
«Ряд кнопок сжимал подписи — класс `og-actions` переносит кнопки в столбец». The CSS
(`bundle.css`):

- `.og-actions { display: flex; flex-wrap: wrap; justify-content: flex-end; gap: space-2 }`
- `.og-actions > .og-btn { flex: 1 1 auto }`

That is, the buttons share a row, each growing from its own width. When they do not fit, the next
one wraps to a line of its own and fills it: at 320 dp in German, the buttons stand in a column.

The library has no public form of it. Card, Dialog and Banner lay out their own action rows
internally with `FlowRow`, and a screen that sets two buttons at its end has nothing to reach for.

- **The decision and its reason.** A public `OldgeActions { … }` in `actions/`: a `FlowRow` with
  the class's gap and end alignment, whose children grow as `flex: 1 1 auto` does. It is the
  design system's own class, named in its README, so it belongs in the library and not in each
  screen.
- **Rejected: telling consumers to write the `FlowRow`.** `flex: 1 1 auto` is not Compose's
  `weight` by default. `weight` shares the free space from a zero basis, and CSS grows each
  button from its own width. The difference shows as unequal buttons, which is exactly what a
  consumer would get wrong.
- **Not covered:** the action rows inside Card, Dialog and Banner. They may move onto this later,
  but their goldens are their own evidence.

- AC: at 390 dp two buttons share a row, grown from their own widths as Chrome grows them (widths
  held to Chrome's within a pixel, measured from the Edge references). At 320 dp with the Edge
  page's German labels they wrap, one per line, each the full width. Behaviour tests, with
  mutants.
- AC: the Edge pages (B-39) use it.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/`.
