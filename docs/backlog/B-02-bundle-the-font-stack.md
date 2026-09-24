---
id: B-02
title: "Bundle six faces for four families, joined per script run"
status: done
priority: P0
size: M
stage: stage-0-spikes
blocked_by: [B-01]
---

# B-02 — Bundle six faces for four families, joined per script run

Research §1.6 measured it: three of the design's four families cannot be shipped as named, and
the two free faces it names have no Cyrillic at all. D6 is the answer; this item makes it real.

- **The decision and its reason.** Bundle, through compose-resources in `oldge-core`:
  DejaVu Sans Condensed 400 + 700 (`ui`), Fira Sans 700 (`title`), Share Tech Mono 400 + PT Mono
  400 (`lcd`), Silkscreen 400 + Tiny5 400 (`pixel`). Static files only; the DejaVu files subset to
  Latin, Latin-1, Cyrillic and the punctuation the design uses (`· — « » … № ₽ ←→` — derive the
  set by scanning every `preview.html` and `README.md` under `reference/design-system/`, not by
  guessing), with the Bitstream Vera licence's rename clause read and quoted in the commit.
  Every licence file goes next to its face.
- **Per-script join.** A companion face is applied to Cyrillic runs by splitting the string into
  script runs and giving each an explicit `SpanStyle(fontFamily = …)` — an internal
  `oldgeTextOf(text, family)` that every component's text goes through for the `lcd` and
  `pixel` families. Before building it, answer the open question of §1.6 by measurement:
  render «ХРАНИЛИЩЕ SYNC» in a `FontFamily` of Silkscreen alone and look at what draws the
  Cyrillic — a system face, a tofu box, or nothing. Write the answer into research §1.6 in
  place of "Not verified".
- Re-run the font measurement of §1.6 on the **bundled** (subset) files and commit the script
  as `scripts/research/font_metrics.py`, so the table is re-checkable; the numbers must not
  move by more than rounding, and if they do the research table is corrected, not the script.
- Rejected: letting the fallback find a Cyrillic glyph (§1.6); a variable font for anything —
  kvadrant-ui and konekt measured variable instancing as a source of cross-OS drift.

- AC: a desktop test renders one string per family in both scripts and asserts, through
  `TextLayoutResult`, that no glyph is `.notdef` and that each run's resolved family is the
  intended face; with the join disabled the test fails (checked by mutation).
- AC: a coverage test scans every string literal in every fixture under `oldge-core/src/desktopTest/kotlin/io/github/youndie/oldge/` and fails on a
  code point no bundled face of the fixture's family covers; viddik's `glyphCheck` is evaluated
  and either enabled with `glyphCheckFont` or rejected in the commit with the reason (it reads
  one font; this library has six).
- AC: the AAR of `oldge-core` contains the six font files (`unzip -l` in the commit body).
- Anchors: `oldge-core/src/commonMain/composeResources/font/`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/type/`,
  `kvadrant-ui/kvadrant-core/src/commonMain/kotlin/` (its per-script join, research D8 there).

## Findings (2026-09-25)

- Answer to research §1.6's open question, by measurement: no per-glyph fallback inside a
  `FontFamily`; the host draws what the face lacks (research §1.6, `CompanionJoinTest`).
- "Each run's resolved family" cannot be read from `TextLayoutResult`, so the join test measures
  it: a run's width inside the joined string equals its width alone in the intended face, and the
  joined render differs in pixels from the design face alone (width cannot separate PT Mono from the
  host's monospace — both 0.6 em). Mutation (the join replaced by `append(run)`): all three render
  tests and two `ScriptRunsTest` cases fail; restored, green.
- The coverage scan found a miscount in research §1.1: **51** components (50 with previews), not 52;
  corrected there and in B-03 (177 references, not 183), B-09, B-42, `backlog.md`,
  `docs/services/oldge-core.md`. Nine stale item numbers in the research are corrected too.
- `glyphCheck` not enabled — reason in research D6, "As built".
- AAR: seven `.ttf` and six licence texts under `assets/composeResources/io.github.youndie.oldge.resources/`.
- Fixture strings are scanned only in files that carry `@ViddikScreenshot`, so a unit test's own
  strings (the supplementary-plane case) do not count as design text.
