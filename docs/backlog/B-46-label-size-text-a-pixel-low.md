---
id: B-46
title: "Label-size text draws a pixel below Chrome's"
status: done
priority: P1
size: S
stage: stage-2-components
blocked_by: []
---

# B-46 — Label-size text draws a pixel below Chrome's

Found in B-14 on the Chip preview. The label style is 700 13 px in a 16 px line, and in every chip
it draws one pixel below the reference: shifted up a pixel, our label matches Chrome's to a mean
of 4 per pixel, and unshifted to 24. The small button's 13 px label, in a 20 px line, is a pixel low
too. The 15 px styles are not (the medium and large button labels, Segmented, the Fab label and
Divider's body text all match at no offset). Most of Chip's 6.2 % comes from this, and so does
about half of RadioGroup's 1.8 %: its legend is the label style and is a pixel low as well (B-15).
*B-21:* it is not only the 13 px `ui` style. Card's titles, the `title` face (Fira Sans Bold 17 px in a
22 px line), are a pixel low as well (best at +1 px). Chrome's layout baseline, probed with
`FAMILY=title scripts/research/baseline-probe.mjs`, is again the one `cssBaseline` computes: 16 for
17/22, 15 for 15/20 and 16/20, 19 for 20/24. The captions and body text in the same cards are not
low. The AC's size list should take in the `title` face.
*B-28:* Balloon's body, 13 px in an 18 px line, is a pixel low too (best at +1), while its 15 px
titles are exact. It is about 3 of Balloon's 3.7 %.
*B-29:* ProgressBar's heads, the label style, are a pixel low as well; they carry most of its 2.6 %.

**What is verified:**

- **Layout agrees.** The label's line box starts 9 px into the chip in Compose, as in CSS
  (1 px border + (32 − 16) / 2).
- **Chrome's baseline agrees with `cssBaseline`.** `scripts/research/baseline-probe.mjs` renders
  through the reference wrapper, and Chrome puts the first baseline of the `ui` face at 12 for
  13/16 and 14 for 13/20, as the rule computes.
- **Compose's paragraph does not.** Its `firstBaseline` for 13/16 is **12.43**, and
  `FirstBaseline`, rounded, reports 12, so `onCssBaseline` shifts nothing. For 15/20 it is 14.96,
  which is why the 15 px styles land.

**What is not explained.** Moving the text by the remainder, so that the paragraph's exact baseline
is 12.0, still leaves the ink about half a pixel low (mean 12.0 at +1 against 13.9 at 0). It also
cost Divider, Icon and Fab 0.02–0.04 points each, because a layer changes how the text is
rasterised. Either Chrome draws this face's glyphs higher than Skia does for the same baseline at
this size, or the line is not what either side reports. That is the question to settle before
anything is changed.

- AC: the probe is extended to report Chrome's glyph ink top, not only the layout baseline, for the
  `ui` face at every size the tokens use (12, 13, 15, 17), with Compose's for the same text beside it.
- AC: whatever moves the 13 px ink onto Chrome's does not cost the 15 px styles or Divider's floor.
  Chip, Button, Segmented, Fab and Divider are measured before and after, and the table goes into
  research §1.10.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/type/OldgeText.kt`,
  `scripts/research/baseline-probe.mjs`.

## Findings

- **The cause.** Compose draws the first baseline on a whole pixel: the centred baseline from the
  unrounded ascent and descent, rounded half up. The paragraph's `firstBaseline` is not that pixel:
  12.43 reported, 13 drawn, for 13 px in a 16 px line, where Chrome's CSS baseline is 12. `OldgeText`
  rounded the report, which is on the right side of .5 at most sizes and on the wrong one at 13 px
  and in the 17 px titles, the two sizes that were low. Research §1.10 has the account.
- **Measured.** `scripts/research/ink-probe.mjs` gives Chrome's ink per line box for every size the
  tokens use, 12 cases. `InkMatchesChromeTest` holds Compose's ink centre to Chrome's within 0.25 px.
  After the fix all 12 are within 0.1; before, 13 px and 17/22 were off by a pixel.
- **Two experiments failed on the way, and both are written down.**
  - A fractional translate of the drawing (the first attempt, as B-14 had tried with a layer) moved
    nothing below half a pixel: the text was already on its pixel, and a translate only resamples
    it. A scan found every case flipping at −0.5.
  - The report's fraction does not predict the drawn pixel: across line heights, the report went
    down while the drawn baseline went up.
- **The fix.** `onCssBaseline` shifts by `cssBaseline − composeBaseline`, whole pixels, computed
  from the bundled face's metrics, with no reading of the paragraph.
- **Parity.** Across all 165 references, 67 improved by 0.05 points or more (Balloon −2.0, Chip
  −1.8, Banner −1.8, Select −1.4, ProgressBar −1.3, WindowBar −1.0) and none worsened. The AC's
  controls, Divider, Fab, Segmented and Icon, are unchanged; the table is in research §1.10.
- **117 goldens re-recorded.** Each changed by the text rows alone: at most 3.05 % of pixels
  (BannerStates, Banner, Chip, Balloon), with a median of 0.67 %.
- Mutations through `scripts/mutate.py`: 2 of 2 killed by `InkMatchesChromeTest`. One places by the
  CSS baseline alone, as if Compose drew there; one rounds the drawn baseline down.
- **Left for B-49:** `OldgeScriptText` (the lcd and pixel faces) still places by the reported
  baseline. It is the likely cause of the Stepper's lcd digit sitting a pixel high.
