---
id: B-60
title: "Bring every control to hit-min without changing what is drawn"
status: dropped
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

## Findings (2026-09-25): dropped, its premise refuted

**The controls were never under `hit-min` to touch.** Compose's hit testing extends a pointer target
smaller than `ViewConfiguration.minimumTouchTargetSize` to that size for a touch that hits nothing
else directly. On this platform the size is 48 × 48 dp. A mouse gets no extension.

Measured with a probe: a 20 dp clickable is pressed by a touch 10 dp outside it, and not by a mouse
click at the same point. And in the widened small Button's test, a touch 6 dp above its drawn edge
pressed it with a 4 dp overhang of the library's own.

So:

- **B-43's 35 "shortfalls" measured the controls' own sizes**, which are what a mouse reaches, not
  what a touch reaches. B-43 dismissed `touchBoundsInRoot` because it "could never fall short": it
  cannot, because the platform guarantees exactly what the design system's rule asks for touch.
- **The widening this item was for** (`oldgeHitArea` on the small Button, Segmented, Tabs and the
  calendar's days) worked, and no golden moved. It was reverted anyway. It gains nothing for touch,
  and it makes a mouse differ from the design system's, where a mouse in Chrome reaches only the
  drawn 36 px of a small button.
- **What replaces the check** is a test of the guarantee the rule rests on,
  `AccessibilityTest.touch_reaches_hit_min_through_the_platforms_own_extension`:
  - the platform's minimum is at least `hit-min`;
  - a touch 6 dp above a small Button's drawn edge presses it;
  - a mouse click there does not.

  Its positive control, a `LocalViewConfiguration` with a zero minimum, makes it fail with "did not
  reach". `KNOWN_TOUCH` is gone.
- **The chips' own overhang (B-14) stays.** It is the design system's `::after` inset, the one place
  the CSS widens a target, and for a mouse it is what Chrome does. Its mutant in B-43 is dropped, as
  it aimed at the removed check; B-14's five still hold the chips.
