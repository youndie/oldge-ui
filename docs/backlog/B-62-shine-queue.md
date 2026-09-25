---
id: B-62
title: "The primary button's glint queues up across quick presses"
status: done
priority: P2
size: XS
stage: stage-2-components
blocked_by: []
---

# B-62 — The primary button's glint queues up across quick presses

Found by the owner in the desktop app: the glints flying over «Войти» pile up. `rememberShine`
collects presses one at a time, and each runs a full 600 ms sweep, so a quick run of presses queues
a sweep per press, and they play one after another after the clicking has stopped.

The CSS runs the glint only while the button is held:
`.og-btn--primary:active::after { animation: og-shine var(--og-tr) var(--og-out) both; }`. A new
press restarts it, and a release removes it.

- **The decision.** On a press, the sweep restarts from the beginning. On a release or a cancel,
  it stops and the glint is gone. *Rejected:* keeping the sweep going after the release, just
  without the queue. The design says `:active`, and a sweep that outlives the press is what piled
  up.
- AC: after a run of quick presses, the button is at rest 700 ms after the last release. A sweep
  still runs while the button is held (MotionTest `shine`). With a mutant.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/OldgeButton.kt`.

## Findings (2026-09-25)

- **Reproduced first.** On the old code, `ButtonBehaviourTest.quick_presses_leave_no_glints_queued_after_the_last_release`
  does five 50 ms presses, 50 ms apart. 700 ms after the last release, 532 pixels still differed
  from the resting button: the queued sweeps were still flying.
- **The fix:** `collectLatest` over the button's interactions. A press restarts the sweep, and a
  release or a cancel ends it, which also cancels a sweep still running. A quick click therefore
  shows only the start of a sweep, as a quick click in Chrome shows only the start of
  `:active::after`. MotionTest's `shine`, which holds the press, still sees the whole sweep.
- **Mutants:** 2 of 2 killed:
  - `collect` instead of `collectLatest`, the queue back;
  - a release that leaves the glint where it was.
- The press flash (`og-glint`, `OldgeIndication`) is a different effect. It is one flash per press,
  each of which fades on its own, and it was not what piled up.
