---
id: B-35
title: "Take viddik 0.6.1 and delete the #44 workaround"
status: done
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

## Findings (2026-09-25)

- **The owner said yes**, but **0.6.1 is not on Central**: `maven-metadata.xml` lists 0.4.0, 0.5.0
  and 0.6.0, and the 0.6.1 plugin POM returns 404. Central releases are made by hand, not by
  viddik's CI, so the loop cannot make one.
- **Deviation:** instead it takes the wip build that *is* the fix. That is reposilite's
  `0.6.0.40`, from `publish-viddik-snapshot.yaml` run #40, whose head is `10f128b`. It is a fixed
  build number, and the build already resolves from that repository for sborka. Moving to 0.6.1
  when it is released is one line in `gradle/libs.versions.toml`.
- **Both workaround blocks are deleted,** oldge-core's and sample's. No `viddik#44` is left in any
  build file.
- **`./gradlew build`, the shape that failed, is green** on 0.6.0.40: BUILD SUCCESSFUL, with
  `kspCommonMainKotlinMetadata` run in both modules. **The control:** 0.6.0 with the blocks
  deleted fails `:oldge-core:compileKotlinDesktop` with "uses this output of task
  ':oldge-core:kspCommonMainKotlinMetadata' without declaring an explicit or implicit dependency".
