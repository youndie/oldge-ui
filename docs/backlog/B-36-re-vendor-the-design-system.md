---
id: B-36
title: "A procedure for re-vendoring the design system"
status: done
priority: P3
size: S
stage: stage-4-product
blocked_by: [B-03]
---

# B-36 — A procedure for re-vendoring the design system

The design system is edited live in its artifact (`lastChange` 2026-09-24 was the sixth revision
in a day). `reference/design-system/` is a snapshot; references, tokens and icons are generated
from it. A silent overwrite would move all three at once and nothing would say which change came
from where.

- **The decision and its reason.** `scripts/vendor-design-system` documents (and, where the
  Artifact tool is not needed, performs) the re-sync: fetch the published files into a scratch
  directory, diff against `reference/design-system/`, and write the diff summary — tokens changed,
  components added/removed, previews changed — into the commit body. The regeneration (tokens,
  icons, references) follows in the same branch, and each changed reference is looked at.
- A check that `design/manifest.json`'s design-system version equals
  `reference/design-system/design-system.json`'s `lastChange.at`, so references rendered from an
  older snapshot fail `check`.
- Rejected: fetching the design system at build time — the build would depend on a signed-in
  claude.ai session, and a reference would change without a commit.

- AC: the version check exists and fails on a mismatched manifest (mutation in the commit body).
- AC: the procedure is in `CLAUDE.md` under a heading of its own.
- Anchors: `reference/design-system/design-system.json`, `oldge-core/src/desktopTest/snapshots/design/manifest.json`,
  `CLAUDE.md`.

## Findings (2026-09-25)

- **`scripts/vendor_design_system.py`** has three subcommands:
  - `check`, in `make check`: every reference manifest (oldge-core's and `sample`'s) must name the
    snapshot it was rendered from.
  - `diff <fetched>`: tokens added, removed and changed; components added and removed; previews,
    READMEs and the shared files changed. One line each, for the commit body.
  - `apply <fetched>`: replaces the snapshot entirely, so a file the design system dropped goes too.

  Fetching stays with the Artifact tool, as the item decided. The procedure is in `CLAUDE.md` under
  "Re-vendoring the design system".
- **Run on the live artifact, not only on a copy.** The artifact's 125 `project/` files were
  fetched as the procedure says, and `diff` found `2026-09-24T21:57:36Z -> 2026-09-24T21:57:36Z`,
  "No file changed". Its file list equals the snapshot's. So the snapshot is the live design system
  as of today, and the procedure works end to end.
  - The fetch found one thing the procedure had to say: the design system is under `project/` in
    the artifact, beside the Design System type's own files.
- **The check fails on a mismatched manifest.** Setting oldge-core's manifest to
  `2026-09-23T10:00:00Z` made `make check` exit 2, naming the manifest and the snapshot's version.
  `test_vendor_design_system.py` (in the gate) holds that, and a diff naming a changed token
  (`hit-min`), an added component and a changed preview, and an unchanged copy saying so.
