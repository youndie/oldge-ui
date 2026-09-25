---
id: B-48
title: "A mutation runner that names the failing test, and a re-check of B-12…B-18"
status: done
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

## Findings (2026-09-25)

- `scripts/mutate.py` and its manifest `scripts/mutants.json`:
  - it applies a mutant, forces the test task to rerun (`--rerun`), reads the JUnit XML, restores
    the file byte for byte, and refuses a file with uncommitted changes;
  - it reports killed / survived / compile-error / not-applied, with the names of the tests that
    failed;
  - `scripts/test_mutate.py` runs it against a stub build for the four outcomes, and is in
    `make gate`.

  The runner's own test was mutation-checked. Counting any failure as a kill fails
  `test_another_test_failing_is_not_a_kill`; not restoring the file fails four tests. Both were
  restored.
- **The re-check: 35 behaviour mutants from B-12…B-19, 33 killed and 2 survived.** Both survivors
  were tests that passed for another reason:
  - **B-12 `min-width`**: «OK» at the small size is 46 dp by itself, so the 44 dp minimum was
    never exercised. The test now uses «1» (about 34 dp) and asserts exactly 44.
  - **B-14 `no-vertical-reach`**: Compose stretches any pointer target under
    `minimumTouchTargetSize` (48 dp) on its own, more than the chip's 46. A touch 4 px above the
    chip landed with the chip's own reach removed. The test now sets that minimum to zero, so only
    `oldgeHitArea` can catch the touch. The quirk is in `docs/services/oldge-core.md`.

  Re-run after the fixes, both are killed by their aimed tests. The claims in B-12's and B-14's
  squash commits were right about the other mutants and wrong about these two; this item's commit
  corrects them.
- Every other mutant is killed by the test it names, including the ones earlier items had verified
  only by exit code (B-13, B-14, and most of B-16…B-18).
- `CLAUDE.md` now sends component items through the runner.
