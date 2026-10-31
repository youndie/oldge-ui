---
id: B-79
title: "OldgeProgressWell: the compact well with a caller's fill, for a list row"
status: open
priority: P2
size: S
stage: stage-5-consumers
blocked_by: []
---

# B-79 — OldgeProgressWell: the compact well with a caller's fill, for a list row

Filed as #22 by the owner. `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/OldgeProgressBar.kt` is the 20 dp well under its own head
line, its fill colour only from `OldgeProgressStatus`. A dense list row wants the well at about 10 dp,
no head line, in the row's own colour (red for an error row, dim for a stopping one). kachok re-draws
the look by hand (`OldgeKit.ProgressBar`: the well, glossy 8 dp blocks with 2 dp gaps, the edge), so
the library's changes never reach it.

- **The decision:** a public `OldgeProgressWell(value, modifier, fill: Color? = null)` that draws only
  the well and its blocks at the height its modifier gives it (default 10 dp), and `OldgeProgressBar`
  drawn with it, so the two cannot drift. `fill = null` is the status colour of today.
- Rejected: a `compact` flag on `OldgeProgressBar`. A bar with its head line switched off by a boolean
  is two components behind one name.

- AC: `OldgeProgressBar`'s goldens and parity are unchanged; `OldgeProgressWell` has goldens at 10 dp in
  three skins with the default and a custom fill; value 0 and 1 draw no block and a full well. With a
  mutant.
- Closes #22.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/OldgeProgressBar.kt`.
