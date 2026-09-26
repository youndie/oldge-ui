---
id: B-68
title: "Every build of main publishes 0.1.0.<build>, through sborka's publish-wip"
status: wip
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
