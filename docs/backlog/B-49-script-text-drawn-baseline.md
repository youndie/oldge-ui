---
id: B-49
title: "OldgeScriptText places text by the reported baseline, not the drawn one"
status: open
priority: P1
size: S
stage: stage-2-components
blocked_by: [B-46]
---

# B-49 — OldgeScriptText places text by the reported baseline, not the drawn one

Found in B-46. `OldgeText` now shifts text by the pixel Compose **draws** the first baseline on
(`composeBaseline`: the centred baseline from unrounded metrics, rounded half up). The
paragraph's reported `firstBaseline` is not that pixel: 12.43 for 13 px in a 16 px line, drawn at 13.
`OldgeScriptText`, which draws the lcd and pixel faces joined with their companions, still places
its text by the strut's reported `firstBaseline`. That is the assumption B-46 disproved.

It is the likely cause of the lcd digits that sit a pixel high: the Stepper's 14 px digit
(B-26, B-34 findings). It is a hypothesis until the ink probe says so.

- AC: `scripts/research/ink-probe.mjs` and `InkMatchesChromeTest` take in the lcd face
  (Share Tech Mono) and the pixel face (Silkscreen) at the sizes the tokens and the components use
  (8, 10, 13, 14, 16, 20, 34), with Chrome's ink centre recorded beside each.
- AC: if they are off, `OldgeScriptText` is placed by the drawn baseline, as `OldgeText` is, and the
  guard holds them. Readout, Meter, Stepper, Switch, DatePicker and Divider are measured before and
  after, and the table goes into research §1.10.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/type/ScriptText.kt`,
  `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/type/OldgeText.kt`.
