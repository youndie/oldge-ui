---
id: B-56
title: "OldgeActions: the design system's og-actions button row, which wraps to a column"
status: done
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

## Findings (2026-09-25)

- **`OldgeActions { … }` is a custom layout, not a `FlowRow`.**
  - Each child's width starts from its own `maxIntrinsicWidth`: `flex-basis: auto`, capped at the
    line's width (`flex-shrink: 1`).
  - Lines are filled greedily with the `space-2` gap between buttons.
  - Each line's free space is shared equally (`flex-grow: 1`); the pixels left over by the
    division go to the first buttons, one each.
  - The children are measured once, at their width.
  - The caller writes plain buttons with no modifier. That is what the item asked for, since
    Compose's `weight` shares space from a zero basis and makes the buttons equal.
- **Two deliberate differences from the CSS.**
  - A line's buttons are centred in it where CSS stretches them (`align-items: stretch`). They
    share a height, and stretching would measure each one twice.
  - An unbounded width (a horizontal scroll) lays everything on one line, since there is nothing
    to wrap to.
- **Held to Chrome.** No preview shows `og-actions`, so `scripts/probes/ActionsProbe` renders the
  design system's own class three ways, and the `ActionsProbe` fixture compares:
  - two buttons sharing 358 px;
  - the Edge page's German pair in 288 px, where they wrap;
  - three unequal small buttons.

  Every button edge on all three rows is within a pixel of Chrome's, as the AC asks. Parity is
  3.49 / 3.53 / 3.63 %, the buttons' text residual (Button alone is 4.0–4.4 %).
- **Behaviour** (`ActionsBehaviourTest`):
  - Two buttons in 358 dp share the line, fill it, and grow by equal shares from their own widths.
  - The German pair in 288 dp wraps, each 288 dp wide.
- **Mutants:** 3 of 3 killed (equal shares from nothing, never wrapping, no growth).
- **Not moved:** Card's, Dialog's and Banner's own action rows still use their `FlowRow`s, and
  their goldens are unchanged.
