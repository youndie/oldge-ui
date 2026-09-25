---
id: B-67
title: "A public repository, CI, and 0.1.0 published to Reposilite"
status: done
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

## Findings (2026-09-25)

- **The repository:** `youndie/oldge-ui`, public. Before the first push, the history was scanned
  for tokens, keys, private addresses and oversized blobs. None were found, and the only email is
  the commit author's, as in kvadrant-ui.
- **The credentials:** `vedutsya-raboty/infra`'s `reposilite-token.yaml` (run 36133523546) was
  given the six coordinates:
  - `oldge-core`;
  - its `-desktop`, `-android`, `-iosarm64`, `-iossimulatorarm64` and `-wasm-js`.

  It set `REPOSILITE_USER` and `REPOSILITE_SECRET` on the repository, and no value passed through
  this machine.
- **CI:** `check.yaml` on `main`'s head `add79f4` was green in both jobs. `check` ran on
  ubuntu-latest and `gradle` on macos-latest.
- **The release:** `v0.1.0`, on `add79f4`. `publish.yaml` (run 36134514486) matched the tag to
  the version, published, and got a 200 for every coordinate's POM. Asked again from here, each
  coordinate answers 200 for its POM and for its artefact: the root's `.module`, the desktop
  `.jar`, the Android `.aar`, and the iOS and wasm `.klib`s. The iOS klibs were built on Linux, as
  kvadrant-ui's are.
- **A consumer resolves it.** A scratch Kotlin/JVM project with `implementation("io.github.youndie:oldge-core:0.1.0")`
  and the filtered Reposilite repository resolves `oldge-core-desktop:0.1.0` and `kotlinx-datetime-jvm`.
- **Found on the way:** the Renovate app opened its onboarding pull request, «chore: Configure
  Renovate», as soon as the repository existed. It is the owner's to accept or close.
