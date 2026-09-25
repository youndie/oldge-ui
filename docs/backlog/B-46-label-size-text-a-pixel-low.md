---
id: B-46
title: "Label-size text draws a pixel below Chrome's"
status: open
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
Divider's body text all match at no offset). Most of Chip's 6.2 % comes from this.

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
