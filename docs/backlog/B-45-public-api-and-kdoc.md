---
id: B-45
title: "Pin the public API, and make the KDoc name its README rule"
status: open
priority: P3
size: S
stage: stage-4-product
blocked_by: [B-40]
---

# B-45 — Pin the public API, and make the KDoc name its README rule

Before anybody depends on the library, its surface should be deliberate: which material
modifiers become public (the README invites consumers to build their own glossy elements "the
same way"), and a pinned ABI so that an accidental change shows in review.

- **The decision and its reason.** `checkKotlinAbi` in `check` against a committed dump
  (kvadrant-ui does this for desktop); decide and document which of `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/material/` becomes
  public; every public composable's KDoc names the design-system README it implements.
- Not covered: publication — there is no remote, and where this library is published is the
  owner's decision.

- AC: `./gradlew check` fails on an unrecorded public API change (mutation in the commit body).
- Anchors: `oldge-core/api/`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/`.
