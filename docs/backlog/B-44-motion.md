---
id: B-44
title: "Motion: entrances, loops and the stagger, as the README lists them"
status: open
priority: P3
size: M
stage: stage-4-product
blocked_by: [B-40]
---

# B-44 — Motion: entrances, loops and the stagger, as the README lists them

Parity sees the design only with reduced motion (research §1.2), so the motion itself is
checked nowhere else. The README lists it exactly: windows and menus unfold from their anchor,
sheets and the snackbar slide in with an overshoot, menu and drawer items appear in a 30 ms
stagger, the selected thing hops, checks pop with a turn; loops only where a process runs.

- **The decision and its reason.** Each component item implements its own motion; this item
  audits the whole set against the README's list and the `@keyframes` in `bundle.css` (34 of
  them), and adds clock-driven goldens at fixed frames (`mainClock.advanceTimeBy`) for the
  entrances — a golden at 0 %, 50 % and 100 % of each is what makes an easing change visible.
- Rejected: comparing against Chrome frames — a mid-animation reference depends on Chrome's
  frame timing.

- AC: every `@keyframes` in `bundle.css` is mapped to a component and a test, or named as not
  applicable, in a table in `docs/services/oldge-core.md`.
- Anchors: `reference/design-system/components/bundle.css` (`@keyframes`), `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/`.
