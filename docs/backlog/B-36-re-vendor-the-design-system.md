---
id: B-36
title: "A procedure for re-vendoring the design system"
status: open
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
