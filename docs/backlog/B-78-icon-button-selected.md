---
id: B-78
title: "OldgeIconButton: a selected state for a toolbar toggle"
status: done
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

## Findings (2026-11-01)

- **The design system has no toggle button**, so the latched look was chosen here, as the item said,
  from goldens of both candidates:
  - **the held bevel** (Secondary's `:active`, inverted chrome and `shadow-sunken`): rendered and
    looked at in Toxic and Crystal, and barely told apart from off. Raised and sunken chrome differ by
    a shadow and a flipped gradient, which at 44 dp does not read as a state;
  - **the accent gloss**: what every "on" in the design system already is, namely
    `.og-seg__opt[aria-checked="true"]`, the selected filter chip and the lit Switch. It reads as on
    in all three skins. **Chosen.**
- **No `accent-rim` on a latched toggle.** The rim is what the Button README gives the Primary, «всегда
  в ободке `accent-rim`», so a toggle that is on is told apart from the screen's one Primary action.
  The `IconToggle` golden shows them side by side.
- **`selected: Boolean? = null`.**
  - `null` is today's button: `Role.Button`, no toggle state.
  - `false` is drawn exactly as `null` (pixel for pixel in the test), and is `toggleable`, with
    `Role.Checkbox` and `ToggleableState.Off`.
  - `true` is `On`, in the latched look, and presses as a Primary does: the gradient turns over and it
    sinks.
  - A disabled toggle is drawn disabled whatever `selected` is; its state is still in the semantics.
- **Goldens:** `IconToggle` in three skins, the library's own (no preview has a toggle). No existing
  golden moved.
- **Tests:** `IconToggleBehaviourTest`, two cases: the semantics and the click, and off drawn as plain
  with on drawn unlike off.
- **Mutants:** 2 of 2 killed (`--only B-78`). **ABI:** `OldgeIconButton` gained a parameter.
- Local note: `:sample` once failed incremental compilation on `OldgeSkin` after B-76's change. That
  was a stale cache, gone after `:sample:clean`, and CI builds fresh.
