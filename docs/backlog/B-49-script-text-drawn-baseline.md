---
id: B-49
title: "OldgeScriptText places text by the reported baseline, not the drawn one"
status: done
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

## Findings (2026-09-25)

**Confirmed, for one size.** The ink probe now takes the lcd face at 12/16, 13/16, 14/14, 16/20,
20/20, 24/24, 28/28, 32/32 and 34/34, the pixel face at 8/10 and 10/12, and three mixed lines
(lcd 14/14 and 16/20 «ГБ», pixel 10/12 «ИЛИ»). Chrome's centre for each is recorded in
`InkMatchesChromeTest`. On main, one case was off: **lcd 14 px in a 14 px line, the ink a pixel
high** (4.50 against Chrome's 5.50; «ГБ» 4.72 against 5.64). That is the Stepper's digit. Every
other size agreed already, because its reported and drawn baselines round to the same pixel.

**The fix is two rules.**
- `OldgeScriptText` shifts by the CSS baseline less `composeBaseline`, as `OldgeText` does since
  B-46.
- **Compose ignores a line height equal to the font size.** A multiplier of exactly 1 is laid out
  as no line height at all: the face's own, taller line, with the baseline at its ascent.
  `composeBaseline` assumes the centred line, so every same-size line (lcd 14/14 to 34/34) needs the
  line honoured. `withHonouredLine()` nudges it by 1.0001 in both `OldgeText` and `OldgeScriptText`.
  A special case inside `composeBaseline` was tried first. It fixed the ink but regressed the Avatar,
  whose node was still the taller natural line and so sat off-centre in the face.

**Companion metrics are dominated.** PT Mono (885/235 per 1000) sits inside Share Tech Mono
(885/242), and Tiny5 (875/250 per em) inside Silkscreen (1030/250). So the line is the primary
face's alone. A max-over-faces version was written and then removed as dead: its mutant survived
because it was equivalent. `CompanionMetricsTest` pins the dominance instead, so a font swap that
breaks it fails there.

**Parity against main, head of the branch** (162 artboards; none worse by 0.01 or more):

| Component (Toxic / Media / Crystal) | Before | After |
|---|---|---|
| Avatar | 2.32 / 2.49 / 2.39 % | 2.11 / 2.23 / 2.14 % |
| Stepper | 2.77 / 2.77 / 2.47 % | 2.74 / 2.74 / 2.44 % |
| NavDrawer | 0.65 / 0.68 / 0.66 % | 0.63 / 0.65 / 0.63 % |
| Readout, Meter, Switch, DatePicker, Divider, CodeInput, Badge | — | unchanged |

Stepper moved only 0.03, since its gap is mostly elsewhere (B-26). The hypothesis that this rule
explained the lcd digits in general is refuted for every size but 14/14.

**Mutants** (`scripts/mutate.py --only B-49`): 3 of 3 killed.
- The line not honoured in `OldgeScriptText`.
- The reported baseline used instead of the drawn one.
- A companion taller than its primary.

B-46's two still die. Dropping the nudge in `OldgeText` has no behaviour case (no `ui` or title
token sets its line to its size), so it was checked as a golden mutation. `viddikVerify` named
Avatar ×3 (0.53–0.55 %) and NavDrawer ×3.

Twelve goldens were re-recorded: Avatar, Stepper, StepperStates and NavDrawer, at most 0.47 % of
pixels moved. The probe's fractional title cases (11.52, 15.84, 23.04 px) print `centre null`: the
canvas scan cannot read a fractional line box. They are not in the guard.
