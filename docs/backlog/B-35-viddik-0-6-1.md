---
id: B-35
title: "Take viddik 0.6.1 and delete the #44 workaround"
status: question
priority: infra
size: XS
stage: stage-1-core
blocked_by: [B-01]
---

# B-35 — Take viddik 0.6.1 and delete the #44 workaround

B-01 orders the per-target KSP tasks by hand because viddik 0.6.0 adds a commonMain srcDir
unconditionally (research §1.8, D9). The fix is `youndie/viddik@10f128b` on `main`, **not
released** on 2026-09-25.

- **The question, for a person:** has viddik 0.6.1 (or a later release containing `10f128b`)
  been published to Maven Central? Check
  `https://repo1.maven.org/maven2/io/github/youndie/viddik/viddik-annotations/maven-metadata.xml`.
  This item is a `question` so that the loop does not spend iterations discovering that it has
  not; whoever sees the release sets it to `open`.
- When open: bump the version, delete the workaround and its comment, and prove the fix with
  `./gradlew build` (the shape that failed), not `viddikRecord`.

- AC: no reference to `viddik#44` left in the build; `./gradlew build check` green.
- `sample` carries a copy of the workaround since B-41 added its Android and iOS targets. Delete
  both.
- Anchors: `oldge-core/build.gradle.kts`, `gradle/libs.versions.toml`.
