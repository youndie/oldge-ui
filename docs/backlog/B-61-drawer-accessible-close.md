---
id: B-61
title: "The NavDrawer has no accessible way to close"
status: done
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

## Decision (2026-09-25)

The owner chose **2**: the scrim becomes an accessible «Закрыть» action, invisible, and nothing drawn changes.

## Findings (2026-09-25)

- The drawer's scrim keeps `oldgeScrimTaps` for the pointer. It also gets its own semantics: the
  content description «Закрыть», `Role.Button`, and an `onClick` labelled «Закрыть» that calls
  `onClose`. The word is the one the dialog's and the sheet's close orbs say, so there is no new
  parameter, and `checkKotlinAbi` is unchanged. Localising the library's own strings is a
  separate concern: the orb hard-codes the word too.
- Nothing drawn changed: NavDrawer's goldens and parity pass as they were, under `viddikVerify`.
- **Test:** `DrawerStepperBehaviourTest.a_screen_reader_closes_it_through_the_scrims_close_button`
  finds the node by name and role, runs its click action, and sees the drawer gone. Its control
  is the inline drawer, which has no scrim and must have no such node.
- **Mutants:** 3 of 3 killed: the action closing nothing, the role removed, the name removed.
- **Not verified with a real screen reader** (TalkBack, VoiceOver). That needs the app running on
  a device, which is B-41's open question.
