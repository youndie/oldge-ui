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
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/press/` | `OldgeIndication` (flash, focus ring; the theme's `LocalIndication`), `oldgePressScale`, `oldgePopIn` (`og-pop`, shared by the chip's and the checkbox's marks) and `oldgePopSoftIn` (`og-pop-soft`), and `oldgeHitArea` (a touch target larger than the drawn box that takes no more room) |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/material/CssBox.kt` | the CSS-box painter every material is built on (backgrounds: colour, `linear-gradient`, `radial-gradient(circle at …)`; `CssCorners` for a box rounded only on top, bottom (B-24) or right (B-26), `CssCornerRadii` for a radius per corner (B-34)); `Materials.kt` the materials, `Gloss.kt` the gloss transition |
| `oldge-core/src/desktopTest/kotlin/io/github/youndie/oldge/harness/` | `OldgeDemo` (the `.og-demo` frame), the pinned text style, `FixtureSizeTest` |
| `scripts/probes/` | material probes: HTML from the design system's classes, rendered as references for the materials' parity fixtures |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/material/Turbulence.kt` | the grain and speckle alpha, a port of `feTurbulence` that matches Chrome (research §1.3) |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/theme/` | `OldgeTheme`, `OldgeSkin`, `OldgeTypography`, `OldgeMotion`, the platform reduced-motion `expect` |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/icons/` | `OldgeIcon` and the generated `OldgeIcons` (`scripts/generate_icons.py`; `checkOldgeIcons` in `check`) |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/` | `OldgeButton`, `OldgeIconButton`, `OldgeOrbButton` (B-12), `OldgeFab`, `OldgeExtendedFab`, `OldgeFabDock`, `OldgeSegmented` (B-13), the three chips and `OldgeChipGroup` (B-14); `ButtonBehaviourTest`, `FabSegmentedBehaviourTest` and `ChipBehaviourTest` hold the READMEs' behaviour rules |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/containers/` | `OldgeDivider` (B-11), `OldgeCard`, `OldgeActionTile` (B-21), `OldgePanel`, `OldgeList` with `OldgeListItem`, and `oldgeListSections` with `OldgeListSectionHeader` for a `LazyColumn` (B-20), `OldgeAccordion` (B-22), `OldgeSwipeRow` (B-23), `OldgeDialog`, `OldgeBottomSheet` (B-27); `CardTileBehaviourTest`, `PanelListBehaviourTest`, `AccordionBehaviourTest`, `SwipeRowBehaviourTest` and `WindowBehaviourTest` hold their READMEs' behaviour rules |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/` | `OldgeMenu`, `OldgeTooltip`, `OldgeBalloon` (B-28), `OldgeWindowBar`, `OldgeBottomNav` (B-24), `OldgeCategoryTabs`, `OldgeTabs` (B-25), `OldgeNavDrawer`, `OldgeStepper`, `OldgePageDots` (B-26); `MenuTooltipBalloonBehaviourTest`, `BarBehaviourTest`, `TabsBehaviourTest` and `DrawerStepperBehaviourTest` hold their READMEs' behaviour rules |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/` | `OldgeReadout`, `OldgeMeter`, `OldgeProgressBar` (B-29), `OldgeSpinner`, `OldgeSkeleton` (B-30), `OldgeBadge`, `OldgeAvatar` (B-31), `OldgeBanner`, `OldgeSnackbar`, `OldgeEmptyState` (B-32), `OldgePullRefresh` (B-33), `OldgeChatBubble`, `OldgeTypingIndicator`, `OldgeComposer` (B-34), with the LCD tag and text in `Lcd.kt` and the frozen loop phase in `Loop.kt`; `LcdBehaviourTest`, `LoopBehaviourTest`, `BadgeAvatarBehaviourTest`, `NoticeBehaviourTest`, `PullRefreshBehaviourTest` and `ChatBehaviourTest` hold their READMEs' behaviour rules |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/` | `OldgeCheckbox`, `OldgeRadioGroup` (B-15), `OldgeSwitch`, `OldgeSlider` (B-16), `OldgeTextField` (B-17), `OldgeSelect`, `OldgeCodeInput` (B-18), `OldgeDatePicker` (B-19), sharing `Field.kt`'s label and help line; `CheckBehaviourTest`, `SwitchSliderBehaviourTest`, `TextFieldBehaviourTest`, `SelectCodeBehaviourTest` and `DatePickerBehaviourTest` hold their READMEs' behaviour rules |
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
* **A single-line `BasicTextField` is a pixel shorter than its line.** With the body style
  (15 px in a 20 px line) its inner field measures 19 px, so a row that centres it rounds the text a
  pixel low. `OldgeTextField` gives the inner line `line-height × lines` as a minimum; any later
  field built on `BasicTextField` (SearchBar, B-47) needs the same.
* **A Compose test finder asserts nothing by itself.** `onNodeWithText(…)` is lazy; a line that ends
  there passes whether the node exists or not. Every lookup ends in an `assert…`, a `perform…` or
  `assertExists()` (found in B-19; B-48 re-checks the earlier items' mutation claims).
* **Compose stretches small touch targets on its own.** A pointer target smaller than
  `ViewConfiguration.minimumTouchTargetSize` (48 dp, desktop included) is hit from that far out,
  which covers more than a chip's 46 dp. A test of `oldgeHitArea` must set the minimum to zero,
  or it passes with the modifier gone (`ChipBehaviourTest`, found by B-48). The modifier still
  sets the target's semantics bounds, which Compose's stretch does not.
* **A golden cannot see a small feature change.** viddik lets 0.05 % of a fixture's pixels differ,
  about 80 px in a 390 × 420 fixture. A skeleton dial's lit arc cut from 100° to 70° stays inside
  that (B-30), and a pixel-sampling behaviour test holds it instead. A mutation that survives only
  through a golden is a finding, not a pass.
* **Compose's `dropShadow` fills its shape; CSS's outer shadow does not.** Under an opaque box the
  two look the same. Through a hole (the spinner's) the Compose one shows, so the spinner draws its
  shadow with the disc clipped out (B-30).
* **A CSS `min-width` can be inert at the default size and bind only at a smaller font.** The
  count badge's 22 px is already filled by one lcd digit, so a test at the default size cannot see it.
  `BadgeAvatarBehaviourTest` holds it at a font scale of 0.75 (B-31).
* **A node that is composed but not placed has `Dp.Unspecified` bounds, and they pass any
  comparison.** A lazy list keeps an item that scrolled out composed. `getUnclippedBoundsInRoot()`
  on it returns NaN edges, and the ordering checks against them held. A header that no longer
  stuck passed "is at the top" this way, until `assertIsDisplayed()` came first (B-20's
  `section-head-not-sticky` mutant).
* **`OldgeList` gives its rows their place through a composition local.** The divider, the stripe
  and the narrow layout are drawn by `OldgeListItem` from `LocalOldgeListRow`. A row that is not an
  `OldgeListItem` gets none of them, as in CSS, where they belong to `.og-item`. The divider is
  drawn inside the row's 52 dp, as the CSS `border-top` is (B-20).
* **Under reduced motion every duration is zero, so a component's own `if (reduced)` guard is often
  unobservable.** An animation of 0 ms is at rest by the first frame a test can capture. B-24's
  mutant that dropped the hop's guard was equivalent and was taken out of the manifest. The guards
  that matter are on loops, which have no duration to zero.
* **A CSS padding sits inside the border (`box-sizing: border-box`), and a Compose `padding` next
  to a hand-drawn frame does not know the frame is there.** Each such component adds the border to
  its padding. Button (B-12) was 2 px narrow for the want of it, and Tabs (B-25) had every label a
  pixel left and half a pixel high. When a component draws its own frame, check this first.
* **A CSS margin on a flex child changes the line's height.** Tabs' resting `margin-top: 4px`
  makes the list 44 px inside its border, while the current tab's `margin-bottom: -1px` lets it
  reach over that border. Both are `layout` modifiers on the tab. Without the first, the block was
  a pixel high and parity read 6.3 % (B-25).
* **A drag injected one `performTouchInput { moveBy(…) }` call at a time reaches `draggable` only
  every other call.** A row that moved at half the finger's speed was the harness, not the
  component: the deltas arrived on alternate steps (B-23, traced in the component). A drag whose
  held position matters is injected as one gesture, `down` and all its `moveBy`s in one block.
* **`draggable` counts from the end of the touch slop; bundle.js counts from the first pixel.**
  The SwipeRow trails the finger by the platform's slop, where the design's row, once past its
  8 px, is at the finger's full offset. Tests that hold a drag add
  `LocalViewConfiguration.current.touchSlop` to the distance.
* **A size modifier cannot undercut the minimum an earlier one set.** `fillMaxWidth(0.86f)
  .widthIn(max = 304.dp)` gives 86 %, not the smaller of the two, since `fillMaxWidth` fixes the
  minimum and `widthIn` only coerces inside it. The other order takes 86 % of 304. CSS's
  `min(304px, 86vw)` is one `layout` that measures with the smaller. The NavDrawer read 4–9 %
  until it was (B-26).
* **A dialog cannot be a child of a clickable node.** Compose throws on composition ("merge
  function called on unmergeable property IsDialog") when `dialog()` semantics sit inside a
  `clickable`, which merges descendants. A scrim that takes taps is therefore the window's
  sibling in a `Box`, not its parent (B-27).
* **A child of the demo's `.og-demo` stretches to its width.** It is a flex column with the default
  `align-items: stretch`. A button placed straight in a preview is full width in the reference, so
  the fixture passes `fillMaxWidth()`. The component is not changed (B-27, the BottomSheet preview's
  «Открыть шторку поверх»).
* **`DemoText` inherits its colour, as a preview's bare text does.** It forced `ink` until B-32,
  and on the Banner's yellow that left the text invisible. It now takes the colour its container's
  `LocalOldgeTextStyle` gives, and `ink` only when none does.
* **`ch` is measured, not assumed.** `1ch` is the width of «0» in the element's face: 0.572 em in
  the bundled DejaVu Sans Condensed, read from its `hmtx`. It is not the ½ em a first draft of
  EmptyState's `max-width: 30ch` guessed (B-32).
* **A paragraph's `firstBaseline` is not where Compose draws the glyphs.** It draws on a whole
  pixel, the centred baseline from unrounded metrics rounded half up, and the reported value can sit
  on the other side of .5 (12.43 reported, 13 drawn, for 13 px in a 16 px line). Place text by
  `composeBaseline`, not by `FirstBaseline`. A fractional translate cannot move text below half a
  pixel; it only resamples it (B-46, research §1.10).
* **kotlinx-datetime is an `api` dependency** (B-19): `OldgeDatePicker` takes and gives
  `LocalDate`, so a consumer compiles against it; the version is `wip`'s.
* **`local.properties`** (git-ignored) must name the Android SDK (`sdk.dir=…`), or configuration
  fails with "SDK location not found".
