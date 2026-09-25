---
id: B-53
title: "A password field with reveal is 46 px in Chrome and 44 here"
status: done
priority: P2
size: XS
stage: stage-2-components
blocked_by: [B-17]
---

# B-53 — A password field with reveal is 46 px in Chrome and 44 here

Found in B-37, on the sign-in screen: the first reference with a reveal field in it. The TextField
preview has none, so B-17 never measured one.

- **Chrome.** The reveal button is `og-orb og-orb--sm`, whose box is `max(var(--orb),
  var(--hit-min))`, so 44 px square, not 30. The field box is `min-height: var(--hit-min)` with a
  1 px border in `border-box`, so its content is 42 px. A 44 px flex item grows it to 46.
- **Here.** `OldgeTextField` keeps the reveal field at 44. The field's inside measured 41 px here
  against 43 in the reference.
- **The result on the page.** Everything below the password field on AuthScreen sits 2 px high.
  That is most of the page's raw 7.1–7.2 %: with that band shifted back, it is 2.4–2.6 %.

- **The decision and its reason.** The field grows round its reveal orb as the flex box does. The
  number comes from the CSS, not from this measurement: the orb's 44 px box plus the border.
- AC: a behaviour test holds a reveal field to 46 dp and a plain one to 44, with a mutant.
- AC: AuthScreen's parity drops, and TextFieldStates' golden is re-recorded and looked at.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeTextField.kt`.

## Findings (2026-09-25)

- **The cause was the layout, not the orb.** The field's row padded its content by the border on
  the sides only. The 44 dp orb (`max(30px, hit-min)`, as the AC predicted) then sat over the top
  and bottom borders, and the box stayed at its 44 dp minimum. Padding the border on all four
  sides, as CSS lays a box out, makes it 46.
- **That exposed a pixel the border had hidden.** An empty single-line `BasicTextField` measures
  21 px for its 20 px line; with a value it is 19, which B-17 already floored. With the content
  inside the border, every empty field grew to 45.
  - TextField's own parity rose from 0.88 to 4.4 % on that one pixel.
  - A single line now has exactly its line's height, and TextField is back at 0.85–0.88 %.
- **Held to Chrome directly, not by arithmetic.** No preview has a reveal field or a multiline
  one. So `scripts/probes/FieldProbe` renders both through the design system's own `TextField`,
  and the `FieldProbe` fixture compares them.

  | | Chrome | Before | After |
  |---|---|---|---|
  | Reveal field's box | 46 px | 44 | 46 |
  | Three-line field's box | 84 px | 82 | 84 |
  | FieldProbe parity (Toxic / Media / Crystal) | — | 5.68 / 6.69 / 6.11 % | 0.47 / 0.48 / 0.50 % |

  The three-line field had the same fault, unmeasured until now: its content sat over the border
  too.
- **Parity against main** (168 artboards). AuthScreen went from 7.10 / 7.24 / 7.14 % to
  1.92 / 1.96 / 2.03 %, and no other artboard moved by 0.01 or more.
- **Goldens re-recorded:**
  - TextFieldStates: the password field is 2 px taller, and the three-line field is too.
  - AuthScreen: everything under the password field has moved 2 px.
  - FieldProbe is new.
- **Behaviour** (`TextFieldBehaviourTest`): the box is 44 with a value and empty, 46 with reveal,
  and 84 with three lines.
- **Mutants:** 2 of 2 killed (content over the border; an empty line left free), and B-17's five
  all die.
  - B-17's `line-minimum` survived at first: the single line's minimum had moved to
    `Modifier.height`, so the mutant only reached the multiline branch. It is aimed at the
    single line again.
  - The multiline minimum turned out equivalent: `minLines = 3` already gives the input 60 px. It
    was removed rather than kept untested.
