---
id: oldge-core
title: oldge-core — the component library module
type: service
module: oldge-core
tech_stack: [Kotlin 2.4.20, Compose Multiplatform 1.12.0, viddik 0.6.0]
owner: unassigned
depends_on:
  - sborka
  - viddik
publishes: []
---

# oldge-core

## 1. Responsibility

The library: the token layer, the theme with its three skins, the bundled fonts, the material
primitives (gloss, bevels, grain), the press feedback, the icon set and the 51 components of the
design system in [`reference/design-system/`](../../reference/design-system/). It also owns the
screenshot suite and the parity fixtures, in `desktopTest`.

It deliberately does **not** own: any Material artefact (research D2); the screens that stress the
components — those are `sample` (B-37…B-40); publication — there is no remote, and nothing is wired
to publish (B-01).

## 2a. Code anchors

| File | What is there |
|---|---|
| `oldge-core/build.gradle.kts` | targets, dependencies, viddik, the #44 workaround |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/` | the library, package `io.github.youndie.oldge` |
| `oldge-core/src/desktopTest/kotlin/io/github/youndie/oldge/` | fixtures and desktop tests |
| `oldge-core/src/desktopTest/snapshots/` | goldens; `design/` under it holds the parity references (B-03) |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/type/` | the font families, the companion join (`oldgeTextOf`), the generated `FontCoverage` |
| `oldge-core/src/commonMain/composeResources/font/` | the seven bundled font files; their licences are in `composeResources/files/` |
| `scripts/fonts/` | the DejaVu subset and the coverage generator (fontTools, run by hand; the tests hold their output) |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/press/` | `OldgeIndication` (flash, focus ring; the theme's `LocalIndication`), `oldgePressScale`, `oldgePopIn` (`og-pop`, shared by the chip's and the checkbox's marks), and `oldgeHitArea` (a touch target larger than the drawn box that takes no more room) |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/material/CssBox.kt` | the CSS-box painter every material is built on (backgrounds: colour, `linear-gradient`, `radial-gradient(circle at …)`); `Materials.kt` the materials, `Gloss.kt` the gloss transition |
| `oldge-core/src/desktopTest/kotlin/io/github/youndie/oldge/harness/` | `OldgeDemo` (the `.og-demo` frame), the pinned text style, `FixtureSizeTest` |
| `scripts/probes/` | material probes: HTML from the design system's classes, rendered as references for the materials' parity fixtures |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/material/Turbulence.kt` | the grain and speckle alpha, a port of `feTurbulence` that matches Chrome (research §1.3) |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/theme/` | `OldgeTheme`, `OldgeSkin`, `OldgeTypography`, `OldgeMotion`, the platform reduced-motion `expect` |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/icons/` | `OldgeIcon` and the generated `OldgeIcons` (`scripts/generate_icons.py`; `checkOldgeIcons` in `check`) |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/` | `OldgeButton`, `OldgeIconButton`, `OldgeOrbButton` (B-12), `OldgeFab`, `OldgeExtendedFab`, `OldgeFabDock`, `OldgeSegmented` (B-13), the three chips and `OldgeChipGroup` (B-14); `ButtonBehaviourTest`, `FabSegmentedBehaviourTest` and `ChipBehaviourTest` hold the READMEs' behaviour rules |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/` | `OldgeCheckbox`, `OldgeRadioGroup` (B-15), `OldgeSwitch`, `OldgeSlider` (B-16); `CheckBehaviourTest` and `SwitchSliderBehaviourTest` hold their READMEs' behaviour rules |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/type/OldgeText.kt` | text on the CSS baseline — use it, not `BasicText`, for any text a reference shows |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/tokens/OldgeTokens.kt` | the generated token layer (`scripts/generate_tokens.py`; `checkOldgeTokens` in `check`) |
| `scripts/design-references.mjs` | renders the parity references into `snapshots/design/` (`make references`); `scripts/tokens-css.mjs` compiles the tokens for it |
| `gradle/libs.versions.toml` | sborka, viddik, the Android SDK levels — and nothing the `wip` catalog carries |
| `settings.gradle.kts` | the sborka settings plugin and its version |

## 3. How it is built

* **Versions come from two catalogs.** `wip` — published by the sborka release named in
  `settings.gradle.kts` — carries Kotlin, KSP, AGP, Compose Multiplatform and the Compose
  coordinates (`wip.compose.foundation` …). `libs` carries only what `wip` does not. A compiler
  bump is a sborka bump, never an edit here. The one exception is
  `compose.components.resources`: it is not in `wip`, and the Compose plugin's accessor is the one
  place its version follows the plugin's, so it stays, deprecation warning included.
* **Targets:** `jvm("desktop")`, android (AGP's KMP library plugin, declared `apply false` in the
  root build file, `androidResources` enabled), `iosArm64`, `iosSimulatorArm64`, `wasmJs`
  (`binaries.executable()`, required by the Compose plugin's own check, CMP-4906). `sample` is
  desktop only until B-41.
* **The suite is desktop.** viddik renders on the JVM; fixtures live in `desktopTest`;
  `verifyOnCheck` is on, so `./gradlew check` runs `viddikVerify`. A green `check` says nothing
  about Android or iOS (research D10).
* **The set is guarded, not just each image.** `ScreenshotSuiteTest` fails on a fixture without a
  golden or a golden without a fixture, a parity fixture without a reference or a reference nobody
  compares, and a design-system component with neither fixtures nor a place on its explicit
  not-yet-built list — which every component item shrinks. The B-01 canary (`OldgeCanary`) was
  deleted there, once real fixtures existed.

## 4. Dependencies

| Kind | Name | What for |
|---|---|---|
| Build | sborka `0.4.0.91` (`io.github.youndie.sborka.settings`, `.kmp`, `.lint`) | repositories, the `wip` catalog, toolchain 25, explicit API, warnings as errors, ktlint |
| Test | viddik `0.6.0` | goldens, design parity |

## 6. Quirks

* **The viddik #44 workaround.** viddik 0.6.0 adds
  `build/generated/ksp/metadata/commonMain/kotlin` to commonMain unconditionally, and in this
  module's shape `./gradlew build` then fails every task that reads commonMain with "uses this
  output of task ':oldge-core:kspCommonMainKotlinMetadata' without declaring an explicit or
  implicit dependency" — reproduced in B-01 before the workaround existed. The block at the end of
  `oldge-core/build.gradle.kts` orders those tasks after `kspCommonMainKotlinMetadata`. Ordering the
  compile tasks alone was the first attempt and was not enough: ktlint's commonMain check failed the
  same way, and ktlint's tasks are not `SourceTask`s, so they are matched by name. A new kind of
  task that walks commonMain (Dokka, a sources jar) will need the same. Deleted by B-35.
* **`viddikRecord` alone never showed the bug.** Only `build` schedules the metadata KSP task; the
  acceptance for anything touching this block is `./gradlew build`.
* **A one-unit colour change is invisible to the goldens**: the per-channel tolerance is ±2, so the
  mutation that proves the gate must move a channel further than that.
* **A face in a `FontFamily` never falls back to another face in it.** A glyph the face lacks is
  drawn by the host, differently on every machine — so the `lcd` and `pixel` families' text must go
  through `oldgeTextOf` with its companion (research §1.6, D6). `DesignStringCoverageTest` fails
  when the design or a fixture uses a character no bundled face of a family can draw.
* **A test that reads a file outside its module declares it as an input** (`desktopTest` declares
  `reference/design-system/`), or Gradle leaves the test UP-TO-DATE over a changed file and `check`
  stays green — measured in B-05.
* **`compileIosMainKotlinMetadata` runs without -Werror**, and only it: CMP 1.12.0's graph puts
  androidx.lifecycle 2.11.0 and JetBrains' lifecycle fork 2.9.6 — one `unique_name` — on the shared
  iOS metadata, and the KLIB loader warns. Per-target iOS compilations keep -Werror. Remove the block
  when a CMP release stops producing the pair.
* **A pressed golden must wait a frame before it presses.** viddik runs effects on an unconfined
  dispatcher, so a fixture's `LaunchedEffect` that emits `PressInteraction.Press` at once runs before
  the control's `collectIsPressedAsState` subscribes, and the interaction flow keeps no replay: the
  press is dropped and the golden photographs the rest state, green for ever. `pressed()` in
  `actions/ButtonFixtures.kt` waits with `withFrameNanos` first. Found in B-12, by a mutation of the
  pressed look that survived; the check for any state golden is removing the state and watching it
  go red.
* **`local.properties`** (git-ignored) must name the Android SDK (`sdk.dir=…`), or configuration
  fails with "SDK location not found".
