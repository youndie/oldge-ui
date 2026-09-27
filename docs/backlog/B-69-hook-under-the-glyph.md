---
id: B-69
title: "CategoryTabs: the hook's leg stands under the glyph's centre"
status: done
priority: P2
size: XS
stage: stage-2-components
blocked_by: []
---

# B-69 — CategoryTabs: the hook's leg stands under the glyph's centre

Asked by the owner on 2026-09-27, looking at the goldens: under the current category (the
«Документы» of the Launcher), the dash should stand exactly under the centre of the icon.

**What the design does.** The dash is the left leg of the label's «hook»:
`.og-cat__label { position: absolute; left: 6px; … border-left: 2px solid var(--ink); border-bottom: 2px solid var(--ink) }`
inside a `.og-cat { width: 56px; place-items: center }`. So the leg stands 6 px in from the tab's
left edge, with its centre at 7 px, while the glyph's centre is at 28. The design system's own
reference draws it there, and the golden matched it.

- **The decision, the owner's: a deviation from the design.** The label is placed at
  `(56 − 2) / 2 = 27 px`, so that the centre of the 2 px leg is the tab's centre line, which is the
  centred glyph's. The label, its padding and the stroke along its bottom follow unchanged, to the
  right of the leg.
- **Parity will move, and it is expected.** CategoryTabs and the Launcher page, which shows it,
  now differ from the references by the label's 21 px shift. The references are not edited, since
  they are the design system's rendering. If the owner changes the design system artifact to
  match, re-vendoring (B-36) brings the references back in line.
- AC: the hook's leg is centred on the current glyph, to within half a pixel. The CategoryTabs and
  Launcher goldens are re-recorded and looked at. With a mutant.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/OldgeCategoryTabs.kt`.

## Findings (2026-09-27)

- **`LABEL_X = (CAT_WIDTH - HOOK) / 2`**, 27 px. `TabsBehaviourTest.the_hooks_leg_stands_under_the_current_glyphs_centre`
  checks, on the first tab and on the third, that the leg's centre is the tab's centre line to
  within half a pixel.
- **Moving the leg exposed a clipping the design's 6 px had hidden.** A current last tab's label
  hangs past its tab. At 6 px in, «Фото» overhung by exactly the row's 16 px end padding. At 27 px
  it overhung by 37 px, and the scrolling `Row` cut it off to «Фот». In CSS an absolutely placed
  child adds to its scroll container's scrollable overflow, so the browser would have made the row
  longer rather than cut the label.
  - The row is now a `Layout`. Each tab reports its label's end through an alignment line, and the
    row is as wide as the larger of the tabs plus their end padding and the furthest label.
  - `TabsBehaviourTest.a_current_last_tabs_label_is_inside_the_row` holds it.
- **Goldens re-recorded and looked at.** viddik recorded exactly the images that show the tabs:
  - `CategoryTabs` and `CategoryTabsStates`, in three skins;
  - `Launcher`, in three skins;
  - the motion golden `draw`.

  Nothing else moved.
- **Parity, as expected, by the label's shift.** Against the design system's references, which
  are not edited:

  | | Toxic | Media | Crystal |
  |---|---|---|---|
  | CategoryTabs, before | 0.48 % | 0.50 % | 1.01 % |
  | CategoryTabs, after | 2.76 % | 2.79 % | 3.33 % |
  | Launcher, before | 1.35 % | 1.41 % | 1.61 % |
  | Launcher, after | 1.75 % | 1.80 % | 2.00 % |

  All are still within the 5 % report threshold; the totals stay 168/174 and 24/27.
- **Mutants:** 2 of 2 killed: the leg back at the design's 6 px, and the row ignoring the label's
  end.
