---
id: B-43
title: "Accessibility: roles, 44 dp targets, colour never alone"
status: open
priority: P2
size: M
stage: stage-4-product
blocked_by: [B-42]
---

# B-43 — Accessibility: roles, 44 dp targets, colour never alone

The design system states three rules (README "Фокус и доступность", "Отступы и касание"): every
control is at least `hit-min` (44 px) to touch; the focus ring is visible on every background
in every skin; colour is never the only carrier of meaning (an error is a colour, an icon and a
word; ON/OFF is written in the switch).

- **The decision and its reason.** Tests over the catalogue registry (B-42), not per component:
  every node with a click action has a touch target ≥ 44 dp; every such node has a role and a
  label; every status-coloured element in the registry has a text or content description.
  Contrast is the design system's claim (≥ 4.5:1 for text, ≥ 3:1 for `edge` and `focus`); a test
  computes it from the generated tokens for the pairs the README names, per skin — a failing pair
  is kept and reported, as the design system's own checklist says, not silently re-coloured.
- Rejected: auditing by eye.

- AC: the three registry-wide tests and the contrast test green, or each failure an item.
- Anchors: `oldge-core/src/desktopTest/kotlin/io/github/youndie/oldge/behaviour/`, `reference/design-system/README.md`.
