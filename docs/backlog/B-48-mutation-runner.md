---
id: B-48
title: "A mutation runner that names the failing test, and a re-check of B-12…B-18"
status: open
priority: P0
size: S
stage: stage-2-components
blocked_by: []
---

# B-48 — A mutation runner that names the failing test, and a re-check of B-12…B-18

Found in B-19. The component items' mutation checks ran from an ad-hoc shell loop that reported
Gradle's exit code, and a mutant was counted as killed on `exit=1`. In B-19 one such "kill" did not
hold up. The DatePicker's month-direction mutant survived once the loop printed which test
failed: the test's last line was a bare `onNodeWithContentDescription(…)`, and Compose's finders are
lazy, so it asserted nothing. B-19 fixed that test and the same bare line in B-17's reveal test.
But every behaviour-test kill claimed in B-12…B-18's squash commits was read the same way; some were
confirmed by name at the time, and the rest were not.

- **Decision.** A committed runner, `scripts/mutate.py`, takes a file, a literal to replace and its
  replacement, and the test filter. It applies the mutant, runs the tests, restores the file (and
  refuses to start on a dirty tree), and prints the names of the tests that failed. A mutant counts
  as killed only when the test it was aimed at is among them; a compile failure is reported as its
  own outcome, never as a kill. The rejected alternative is to keep the loop and read it more
  carefully: that is the reading that failed.
- AC: the runner, and a test of it — a mutant it must report killed, one it must report surviving,
  and one that fails to compile.
- AC: every behaviour-test mutant listed in B-12…B-18's commit bodies re-run through it. A survivor
  gets its test fixed (and the fix mutation-checked), and the corrected list goes into this item's
  findings.
- AC: `CLAUDE.md`'s gates section says component items use the runner.
- Anchors: `scripts/`, `oldge-core/src/desktopTest/kotlin/io/github/youndie/oldge/`.
