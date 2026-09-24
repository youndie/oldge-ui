---
id: B-42
title: "The component catalogue: a previews registry and a generated docs/components.md"
status: open
priority: P2
size: S
stage: stage-4-product
blocked_by: [B-40]
---

# B-42 — The component catalogue: a previews registry and a generated docs/components.md

Past fifty components, "which composable is in which file and what shows it" is a question an
agent asks every session. kvadrant-ui answers it with a generated catalogue that `check` holds
against the sources.

- **The decision and its reason.** A registry `id → @Composable` of one bare instance per
  component, a generator writing `docs/components.md` (component, file, design-system group,
  reference stems, the design-system README), and a check that fails when the file is stale or
  names a component that no longer exists. Link it from `CLAUDE.md`'s "How to start a session".
- Rejected: a hand-written table — the kind of list that is wrong within a sprint.

- AC: the catalogue lists all 51 components; editing one row by hand fails the check.
- Anchors: `docs/components.md`, `scripts/component_catalog.py`,
  `kvadrant-ui/scripts/component_catalog.py`.
