---
id: B-69
title: "CategoryTabs: the hook's leg stands under the glyph's centre"
status: wip
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
