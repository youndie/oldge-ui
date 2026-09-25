---
id: B-61
title: "The NavDrawer has no accessible way to close"
status: question
priority: P2
size: S
stage: stage-2-components
blocked_by: [B-43]
---

# B-61 — The NavDrawer has no accessible way to close

Found in B-43. The design system closes its drawer in two ways, both by pointer: a tap on the scrim
(`og-drawer-scrim`, a `div` with a click handler, `bundle.js`) and choosing an entry. Its sheet and
dialog each have a «Закрыть» button; its drawer does not.

B-43 took the scrims out of the accessibility tree, as the design system has them. Before that the
library announced them as an unnamed button the size of the screen. So now a screen-reader user can
leave the drawer only by choosing an entry. Compose has no system Back for the library to hook on
every platform.

## Question

This is a design decision, so it is the owner's:

1. **A «Закрыть» orb in the drawer's header**, as the sheet has. That is a visible change the design
   system does not draw, and it changes NavDrawer's goldens and parity.
2. **The scrim as an accessible «Закрыть» action**: invisible, announced as a button that closes the
   drawer (Material's scrim does this). That is a string and a control the design system does not
   have, but nothing drawn changes.
3. **Leave it** as the design system has it, and record it as a known limit.

The loop recommends 2: it changes nothing drawn and matches the platform convention. It does not
decide it.
