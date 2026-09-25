---
id: B-67
title: "A public repository, CI, and 0.1.0 published to Reposilite"
status: wip
priority: infra
size: S
stage: stage-4-product
blocked_by: []
---

# B-67 — A public repository, CI, and 0.1.0 published to Reposilite

The owner's decisions of 2026-09-25:

- the repository becomes public, `youndie/oldge-ui`;
- the vendored design system goes public with it;
- the first version is 0.1.0, published to Reposilite;
- the publishing credentials are issued by the infra job, not by hand;
- the mention of konekt stays, since konekt is open.

This lifts B-01's "not covered: publication".

- **The decision.** As kvadrant-ui does:
  - **`sborka.publish`** on `oldge-core`. The POM carries the code's Apache 2.0, plus the fonts'
    licences: OFL 1.1 and DejaVu's.
  - **`check.yaml`.** `make check` runs on `ubuntu-latest`, and `./gradlew check` on
    `macos-latest`, because the goldens are a claim about the Mac rasteriser (research §1.9).
  - **`publish.yaml`.** A GitHub release publishes from `ubuntu-latest`, after checking that the
    tag names `gradle.properties`' version.
  - **The Reposilite token** comes from `vedutsya-raboty/infra`'s `reposilite-token.yaml`, routed to
    every published coordinate, the per-target ones included.
  - **The loop** stops merging locally. It opens a pull request, and merges on green from the pull
    request's own head.
- *Rejected:* publishing from this Mac with a token in `~/.gradle`. Nothing would record what was
  published from which commit, and the token would pass through the laptop.
- AC:
  - `v0.1.0` exists as a release, and every one of its coordinates answers 200 from Reposilite.
  - The check workflow is green on `main`.
- Anchors: `oldge-core/build.gradle.kts`, `.github/workflows/`, `gradle.properties`.
