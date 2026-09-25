---
id: B-45
title: "Pin the public API, and make the KDoc name its README rule"
status: done
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

## Findings (2026-09-25)

- **The ABI is pinned** in `oldge-core/api/`: the desktop and Android JVM dumps (917 lines each)
  and the klib dump for iOS and wasm (1 101 lines). `checkKotlinAbi` is part of `check` by
  default in this Kotlin, so nothing had to be wired. Mutations were run against the committed
  dump, and each failed with "ABI check failed for project oldge-core":
  - a removed default (`oldgeGloss`'s `radius`);
  - `OldgeGloss.mid` made internal;
  - `OldgeMeter` made internal;
  - a public function added.

  Through `./gradlew check`, the first of them failed `:oldge-core:checkKotlinAbi`, and failed
  `sample`'s compilation too.
- **`material/`: only the gloss is public.** The README asks consumers to build their own glossy
  elements from three stops, and a Compose consumer could not match that without re-implementing
  the premultiplied-sRGB transition. `OldgeGloss`, `Modifier.oldgeGloss` and `animateOldgeGloss`
  wrap the internal `GlossStops`, `cssBox` and `animateGloss`. `sample`'s `ConsumerGlossTest` uses
  them from outside the library and checks three things:
  - the three stops land at 0 %, 55 % and 100 %;
  - a press moves every stop along the straight sRGB line;
  - under reduced motion, the change lands in the first frame.

  Its control is Compose's own Oklab `lerp`, which must fail the same straightness check. The
  rest stays internal, and the reason is in `docs/services/oldge-core.md` §3.
- **KDoc:** 51 public composables already named their component. Six did not: `OldgeIconButton`,
  `OldgeFilterChip`, `OldgeInputChip`, `OldgeFabDock`, `OldgeListSectionHeader` and
  `OldgeLazyPullRefresh`. `component_catalog.py --check` now requires the name, and its unit test
  shows the rule failing both ways.
- **Mutants:** 2 of 2 killed in `ConsumerGlossTest`: the transition made instant, and the middle
  stop dropped.
