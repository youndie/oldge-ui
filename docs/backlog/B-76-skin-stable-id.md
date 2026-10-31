---
id: B-76
title: "OldgeSkin: a stable id to persist a choice by"
status: done
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

## Findings (2026-11-01)

- **`OldgeSkin.id`**: `"toxic"`, `"media"`, `"crystal"`. Read back with
  `OldgeSkin.byId(id): OldgeSkin?`, `null` for an id no skin has (one written by a newer release,
  say), so the caller decides the fallback. The KDoc names `id` as the persistence key, and `name` as
  source that a rename may change.
- **The sample app stores no skin**, so nothing in this repository moves to the id.
- **Test:** `OldgeSkinIdTest`:
  - the three released ids pinned as literals;
  - every skin found by its own id;
  - ids distinct and lowercase;
  - an unknown id, and a skin's `name`, give `null`.
- **Goldens:** none moved. **Mutants:** 2 of 2 killed (`--only B-76`): a released id renamed, and
  `byId` falling back to the name. **ABI:** `id`, and `byId` on a new companion.
