---
id: B-44
title: "Motion: entrances, loops and the stagger, as the README lists them"
status: done
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

## Findings (2026-09-25)

- **31 `@keyframes`, not 34.** Each is now mapped to a component and a test, in the table in
  [`docs/services/oldge-core.md` §5](../services/oldge-core.md#5-motion). `og-shimmer` is not
  applicable: it is defined, and no rule uses it. The CodeInput caret's loop is the one use of a
  keyframe with no test of its own; `og-blink` is held through the Switch lamp.
- **One golden per entrance, at the halfway frame, not three.** Each scene's start is compared
  against its end: they must differ. Its end is compared against the reduced-motion render: they
  must match within 0.1 %. So 0 % and 100 % are assertions, and only the halfway frame is a
  golden. There are 20 of them, under `oldge-core/src/desktopTest/snapshots/motion/`, and each was
  looked at.
- **A real defect, fixed in scope.** The Meter's `og-seg-in` stagger was one `Animatable` eased
  with `out` as a whole, so every segment had landed by the halfway mark. Now the run is linear,
  and each segment eases its own slice. Reduced-motion parity could not show it, since there
  `appear` is 1 from the start.
- **Loops are not entrances.** The indeterminate bar's `og-slide` got a two-frame test in
  `LoopBehaviourTest`, next to the Spinner's: it moves, and it stands still under reduced motion.
- **Mutants:** 10 of 10 killed (`scripts/mutants.json`, item B-44):
  - the Fab's entrance removed, re-eased, or landing at 0.9;
  - the drawer's and the month's slide made instant;
  - the Meter eased as a whole again;
  - the hop flattened;
  - the lamp held steady;
  - the glint never run;
  - the indeterminate bar held still.
