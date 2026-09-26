---
id: B-68
title: "Every build of main publishes 0.1.0.<build>, through sborka's publish-wip"
status: done
priority: infra
size: XS
stage: stage-4-product
blocked_by: [B-67]
---

# B-68 — Every build of main publishes 0.1.0.<build>, through sborka's publish-wip

The owner's decision of 2026-09-27: versions are `0.1.0.<buildNumber>`, as in the rest of the
portfolio. B-67 published by a GitHub release, with `version` as the whole number.

- **The decision.** `gradle.properties`' `version` is the head, `0.1.0`. `publish.yaml` calls
  sborka's reusable `publish-wip.yaml` on every push to `main`. That workflow does four things:
  - it appends the run number through `-PVERSION`, using sborka's `determine-version`;
  - it runs `./gradlew check` in a step of its own;
  - it publishes with `publishAllPublicationsToWipRepository`;
  - its consumer job resolves the root coordinate afterwards, as a consumer names it.
- **The runner is `macos-latest`.** The check before the publish is the full gate, whose goldens are
  the Mac's (research §1.9), and one host must build the root and every target. *Rejected:* keeping
  the release trigger. Two ways to publish would be two sets of version numbers.
- `0.1.0` itself, published by B-67, stays on the host as it is.
- AC: a push to `main` publishes `0.1.0.<run>` for all six coordinates, and the consumer job
  resolves the root.
- Anchors: `.github/workflows/publish.yaml`, `gradle.properties`.

## Findings (2026-09-27)

- **`0.1.0.2` is the first build-number version:** publish run 36278903385, the push of PR #5's
  merge `3873af0`. The run number carries on from B-67's release run, which was #1.
  `determine-version` gave `0.1.0.2`, `./gradlew check` passed in its own step, and then the
  publish ran.
- **On the host:** all six coordinates answer 200 for their POM and their artefact. The root's
  `maven-metadata.xml` lists `0.1.0` and `0.1.0.2`, with `<latest>0.1.0.2`. The consumer job
  (proba) resolved `io.github.youndie:oldge-core:0.1.0.2`.
- **The check's tests came from the build cache** (`desktopTest FROM-CACHE`), and that is sound. A
  squash merge leaves the tree identical to the pull request's head, so every test input was the
  same as in the pull request's `gradle` job, which had just passed. `viddikVerify` and
  `checkKotlinAbi` ran.
