---
id: B-76
title: "OldgeSkin: a stable id to persist a choice by"
status: open
priority: P2
size: XS
stage: stage-5-consumers
blocked_by: []
---

# B-76 — OldgeSkin: a stable id to persist a choice by

Filed as #24 by the owner. `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/theme/OldgeSkin.kt` is an enum, and the only identifier a consumer
can store is its `name`. Renaming a skin in a release turns every stored choice into "unknown";
kachok falls back to the first skin (`OldgeKit.skinOf`) and the choice is lost.

- **The decision:** each skin gets `id: String`, never changed once released (`"toxic"`, `"media"`,
  `"crystal"`), and `OldgeSkin.byId(id): OldgeSkin?`. The KDoc says `id` is the persistence key and
  `name` is not.
- Rejected: documenting `name` as stable. An enum constant's name is source, and a rename is a
  refactor nobody reads as a wire change.

- AC: `byId(skin.id) == skin` for every skin, ids are distinct and lowercase, `byId("nope") == null`;
  a test pins the three released ids as literals, so changing one fails. With a mutant.
- Closes #24.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/theme/OldgeSkin.kt`.
