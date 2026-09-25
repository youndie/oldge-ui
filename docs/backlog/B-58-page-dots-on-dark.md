---
id: B-58
title: "PageDots on a photo: the current dot is the cascade's, grey with the accent glow"
status: done
priority: P2
size: XS
stage: stage-2-components
blocked_by: [B-25]
---

# B-58 — PageDots on a photo: the current dot is the cascade's, grey with the accent glow

Found in B-38, on MediaScreen: the first reference with `onDark` dots. The PageDots preview has
none. In the reference the current dot is the stretched 24 px capsule in the dark dots' grey,
with the accent's glow round it. Here it is filled with the accent.

The CSS (`bundle.css`) explains the reference:

- `.og-dots__dot[aria-current="true"] > span { width: 24px; background: accent; border-color:
  accent-rim; box-shadow: 0 0 6px accent }`
- `.og-dots--dark .og-dots__dot > span { background: rgba(255,255,255,0.35); border-color:
  rgba(0,0,0,0.5) }`

Both selectors have the same specificity (two classes or attributes and an element), and the dark
rule comes later. So on a photo it wins for the fill and the border, and the current dot keeps
only its width and its glow.

- **The decision and its reason.** Draw what the CSS draws: on dark, the current dot takes the
  dark dots' fill and border and keeps its width and glow. The vendored CSS is the specification
  here, as it was for the Composer (D11). The README's rule, «текущая растянута и подсвечена»
  ("the current one stretched and lit"), still holds: it is stretched, and it is lit by the glow.
- **Rejected: keeping the accent fill** because the cascade looks accidental. That is a guess at
  the author's intent against the stylesheet. If the owner reads it as a bug in the design
  system, it is fixed there and re-vendored (B-36), and the library follows.
- AC: a behaviour test samples the current dot's fill on dark (the dark grey) and on the body
  (the accent), with a mutant. MediaScreen's parity drops, and PageDots' goldens are unchanged.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/OldgePageDots.kt`.

## Findings (2026-09-25)

- **The cascade, followed.** On dark the current dot keeps the dark dots' fill and edge, and only
  its width (24 dp) and its accent glow say it is current. On the body nothing changes.
- **A second cause, found on the way.** With the grey fill in place the dot still read green,
  because the dark fill is translucent (35 % white).
  - `cssBox` draws a blurred outer shadow as Compose's `dropShadow`, which fills the whole shape;
    CSS paints an outer shadow only outside the box. Under an opaque fill the difference cannot be
    seen. Under a translucent one, the glow under the fill showed through.
  - The dot now draws its glow as a layer of its own, clipped to outside the pill, the way the
    Spinner already does (B-30).
  - `cssBox` itself is unchanged: every other blurred shadow sits under an opaque fill. The
    service document records the quirk for the next translucent one.
- **Parity against main** (195 artboards). MediaScreen went from 0.38 / 0.42 / 0.46 % to
  0.31 / 0.36 / 0.40 %, and nothing else moved by 0.01.
- **Goldens:**
  - PageDotsStates' on-dark row: the current dot is grey.
  - PageDots: 24 edge pixels by at most 17 levels, where the glow no longer overlaps the pill's
    antialiased edge.
  - MediaScreen.
- **Behaviour** (`DrawerStepperBehaviourTest`): the current dot's centre is grey on black, lighter
  than the black and not lime, and lime on the body.
  - Its first version sampled a pixel of the row's empty width. There, black passed as "grey",
    until the row was sized to the dots.
- **Mutants:** 2 of 2 killed (the accent fill back on dark, the glow drawn under the fill), and
  B-25's twelve all die.
