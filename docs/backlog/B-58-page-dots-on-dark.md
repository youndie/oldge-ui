---
id: B-58
title: "PageDots on a photo: the current dot is the cascade's, grey with the accent glow"
status: open
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
