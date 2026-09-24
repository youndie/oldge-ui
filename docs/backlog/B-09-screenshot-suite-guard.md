---
id: B-09
title: "ScreenshotSuiteTest: every component, every skin, both ways"
status: open
priority: P0
size: S
stage: stage-1-core
blocked_by: [B-08]
---

# B-09 — ScreenshotSuiteTest: every component, every skin, both ways

A reference nobody compares is decoration; a fixture with no reference scores nothing and reads
as `MISSING_REFERENCE` in a report nobody opens. The suite guards the set, in both directions,
against the inventory research §1.1 fixed.

- **The decision and its reason.** A desktop test that fails when: the fixture registry is empty;
  a fixture has no golden, or a golden no fixture (kvadrant-ui's guard); a parity fixture
  `<Name>_<Skin>` has no reference in `design/`, or a reference has no fixture; a component
  exported by `reference/design-system/components/index.d.ts` has no parity fixture **and is not
  on the explicit, shrinking list of not-yet-built components** in the test. Every component
  item removes its names from that list; B-40 ends it empty.
- The golden and reference directories are declared as the test task's inputs, or Gradle
  leaves it UP-TO-DATE over a changed set (kvadrant-ui's lesson).
- viddik's own sanitising (`[^A-Za-z0-9_.-]` → `_`, per character) is the naming rule, not
  kvadrant-ui's — the two disagree on `.` and repeated separators (research §1.8).
- Rejected: a coverage percentage — a list of names says which component is missing.

- AC: deleting one golden, one reference, or one fixture each fails the test with the name in
  the message (three mutations in the commit body).
- AC: the not-yet-built list is exactly the 52 components minus those with fixtures at this
  commit.
- Anchors: `oldge-core/src/desktopTest/kotlin/io/github/youndie/oldge/behaviour/ScreenshotSuiteTest.kt`,
  `kvadrant-ui/kvadrant-core/src/desktopTest/kotlin/` (its `ScreenshotSuiteTest`).
