---
id: B-78
title: "OldgeIconButton: a selected state for a toolbar toggle"
status: open
priority: P2
size: S
stage: stage-5-consumers
blocked_by: []
---

# B-78 — OldgeIconButton: a selected state for a toolbar toggle

Filed as #20 by the owner. `OldgeIconButton` (`oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/OldgeButton.kt`) has no on/off state.
kachok draws an on toggle as `Primary` and an off one as `Secondary` (youndie/kachok#129), which
guesses at the design system's intent and breaks "one Primary per screen" with two toggles on.
The design system's selected look elsewhere is `OldgeFilterChip`: the accent fill and a check.

- **The decision:** `selected: Boolean? = null`. `null` is today's button; `false` is today's button
  with toggle semantics; `true` is the latched look. Which latched look — the pressed bevel held, or
  the filter chip's accent fill — is chosen in the item from goldens of both, and the reason recorded
  here; the one that still reads as "on" in all three skins wins.
- Semantics: `Role.Checkbox`-style toggleable with `stateDescription`, so a screen reader hears a
  toggle, on or off.

- AC: `selected = true` and `false` are visibly different in three skins (goldens), `null` is
  unchanged (goldens equal); a semantics test finds `ToggleableState.On`/`Off`, and `null` has no
  toggle state. With a mutant.
- Closes #20.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/OldgeButton.kt`.
