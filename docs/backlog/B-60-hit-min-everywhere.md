---
id: B-60
title: "Bring every control to hit-min without changing what is drawn"
status: open
priority: P2
size: M
stage: stage-2-components
blocked_by: [B-43]
---

# B-60 — Bring every control to `hit-min` without changing what is drawn

Found in B-43. The design system's rule, from its README: «Любой контрол — не меньше `hit-min`
(44px). Маленькие орбы видимо 30px, но зона касания — 44px» ("every control is at least hit-min;
small orbs are 30 px to see and 44 px to touch"). Its own CSS falls short of that rule in several
places, and the library follows the CSS, so `AccessibilityTest` reports 35 fixture sizes under 44 dp
(its `KNOWN_TOUCH`):

- the small Button (`og-btn--sm`, 36 px high): in Button, Banner's and Card's actions, Snackbar,
  Stepper and `OldgeActions` rows;
- Segmented's options (`og-seg__opt`) and Tabs (`og-tab`), 40 px;
- the calendar's days, 40 px high;
- PageDots' dots, 24 px wide (36 for the current one);
- the Composer's field, 38 px;
- a TextField's input, 42 px inside its 44 px box.

The CSS widens only the chips' targets, with `::after` insets.

- **The decision and its reason.** Widen each of these to 44 dp with `oldgeHitArea`, the way the
  chips already are: the touch target overhangs, and layout and drawing do not move. That is what
  the README's rule for the small orbs describes. It keeps every golden and every parity number
  as it is.
- **Rejected: making them 44 dp tall.** That changes the drawn size the design system specifies,
  and every page's parity with it.
- **Rejected: leaving them.** The README states the rule for every control. Only the CSS falls
  short, and the design system's own checklist says a shortfall is reported, not accepted as the
  rule.
- **Not covered:** a TextField's input is 42 inside a 44 dp box. Whether the box, not the input,
  should take the tap is part of this item's reading.

- AC: `AccessibilityTest`'s `KNOWN_TOUCH` is empty, and every golden is unchanged.
- AC: a tap 2 dp outside a small Button's drawn edge presses it (`ChipBehaviourTest`'s check
  style), with a mutant.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/press/HitArea.kt`,
  `oldge-core/src/desktopTest/kotlin/io/github/youndie/oldge/behaviour/AccessibilityTest.kt`.
