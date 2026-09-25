---
id: B-66
title: "Select's arrow stays dipped, and its focus ring stays, after a click"
status: done
priority: P2
size: XS
stage: stage-2-components
blocked_by: []
---

# B-66 — Select's arrow stays dipped, and its focus ring stays, after a click

Found by the owner in the desktop app: once the Select has been opened, its arrow stays down and
does not come back.

A probe of the Select's interactions shows `Focus, Press, Release, Enter, Exit` after a click, and
nothing after a choice. A click focuses the Select, and nothing ever unfocuses it: Compose keeps the
focus when the user clicks elsewhere, while a browser blurs. The arrow dips on
`pressed || focused || open`, so after the first click it stays down, and the focus ring
(`if (focused)`) stays with it.

The CSS is `.og-select:active .og-select__arrow, .og-select:focus-within .og-select__arrow { … }`
and `.og-select:focus-within { outline: 2px solid var(--focus) }`. In Chrome that holds after a
click too, and comes back as soon as the user clicks elsewhere.

- **The decision.** The arrow dips while the Select is pressed or open, or while it has keyboard
  focus. The ring shows for keyboard focus only, as every other control's does through
  `OldgeIndication` (`ring && focused && keyboard`). *Rejected:* clearing the focus after a choice.
  A keyboard user would lose their place, and a click elsewhere still would not blur.
- AC: after a click, a choice and the list closing, the Select is at rest, arrow up and no ring.
  Focused from the keyboard, it shows the ring and the dipped arrow. With a mutant.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeSelect.kt`.

## Findings (2026-09-25)

- The Select reads `focused && InputMode.Keyboard` for its dip and its ring, as `OldgeIndication`
  does for every other control. It is pressed or open for a pointer.
- **Test:** `SelectCodeBehaviourTest.after_a_choice_the_arrow_comes_back_up_and_keyboard_focus_still_dips_it`
  clicks the Select open with the mouse and picks an option. The Select is then pixel for pixel
  its resting self. Its control: Tab onto the Select from the button before it, and the frame
  differs, with the ring and the dip.
- **Mutants:** 2 of 2 killed: any focus dips the arrow (the old behaviour), and keyboard focus
  ignored.
- **The same pattern, checked elsewhere.** TextField, SearchBar and Composer draw their ring on
  plain `focused`. That is right for them: a text field that holds the focus holds the caret and
  takes typing, in a browser and here alike. Only the Select, which is not a field, kept a focus it
  had no use for.
