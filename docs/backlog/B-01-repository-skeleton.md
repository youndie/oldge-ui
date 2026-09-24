---
id: B-01
title: "Skeleton: modules, targets, the sborka conventions, viddik, one gate"
status: open
priority: P0
size: M
stage: stage-1-core
---

# B-01 — Skeleton: modules, targets, the sborka conventions, viddik, one gate

There is no build. Everything else needs one, and the build's shape is what kvadrant-ui already
paid for: copy its wiring, not its history (research §1.9).

- **The decision and its reason.** `settings.gradle.kts` applies `io.github.youndie.sborka.settings`
  at **0.4.0.91** and takes Kotlin, KSP, AGP and Compose Multiplatform from its `wip` catalog
  (kotlin 2.4.20, CMP 1.12.0 — research §1.8), so a compiler bump is a sborka bump. Modules:
  `oldge-core` (the library, `sborka.kmp` + `lint`; publication is not wired — there is no
  remote, and publishing is a later decision) and `sample` (desktop only for now; B-41 grows it).
  Targets of `oldge-core`: `jvm("desktop")`, android (`com.android.kotlin.multiplatform.library`,
  AGP declared `apply false` in the root build file), `iosArm64`, `iosSimulatorArm64`, `wasmJs`
  (research D10). `androidResources { enable = true }` from the start (research §1.9).
- viddik **0.6.0** applied to `oldge-core` with `verifyOnCheck = true`, fixtures in `desktopTest`,
  `showroomTargets` off, and the #44 workaround — the per-target KSP tasks ordered after
  `kspCommonMainKotlinMetadata` — with a comment naming `youndie/viddik#44` (research D9).
  **The acceptance for the workaround is `./gradlew build`, not `viddikRecord`**: the bug does
  not show under `viddikRecord` alone.
- `desktopTest` has `implementation(compose.desktop.currentOs)`; no published source set does.
- Package `io.github.youndie.oldge`, explicit API on (sborka default).
- The `Makefile` stays the documentation gate; `CLAUDE.md` already names `make check` and
  `./gradlew check` as the two gates, and this item makes the second one exist.
- `docs/services/oldge-core.md` from the template: what the module owns, its targets, how to
  build and test it, quirks (the #44 workaround is the first), with code anchors that now exist;
  the coverage map in `docs/README.md` lists it.
- Rejected: a hand-written version catalog as in the kvadrant-ui of August — the portfolio moved
  the compiler into sborka's catalog so that twenty repositories stop drifting.
- Not covered: publication, CI (there is no remote), Dokka, the site.

- AC: `./gradlew build` and `./gradlew check` green on this mac with one trivial public
  composable and one viddik fixture of it (its golden recorded and committed) — the fixture
  proves viddik runs inside `check`, and is deleted by the first real component item or kept
  as the suite's canary; which, is said in the commit.
- AC: `./gradlew :oldge-core:tasks --group verification` lists `viddikVerify`,
  `viddikRecord` and `viddikDesignParity`.
- AC: every target compiles: `./gradlew :oldge-core:compileKotlinIosSimulatorArm64
  :oldge-core:compileKotlinWasmJs :oldge-core:compileAndroidMain` (or the names the build
  prints) green.
- Anchors: `settings.gradle.kts`, `build.gradle.kts`, `oldge-core/build.gradle.kts`,
  `kvadrant-ui/settings.gradle.kts`, `kvadrant-ui/kvadrant-core/build.gradle.kts`.
