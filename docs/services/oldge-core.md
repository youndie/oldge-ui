---
id: oldge-core
title: oldge-core — the component library module
type: service
module: oldge-core
tech_stack: [Kotlin 2.4.20, Compose Multiplatform 1.12.0, viddik 0.6.0.40]
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
components — those are `sample` (B-37…B-40). It is published as `io.github.youndie:oldge-core` to
Reposilite by `.github/workflows/publish.yaml` on a GitHub release (B-67).

## 2a. Code anchors

| File | What is there |
|---|---|
| `oldge-core/build.gradle.kts` | targets, dependencies, viddik |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/` | the library, package `io.github.youndie.oldge` |
| `oldge-core/src/desktopTest/kotlin/io/github/youndie/oldge/` | fixtures and desktop tests |
| `oldge-core/src/desktopTest/snapshots/` | goldens; `design/` under it holds the parity references (B-03) |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/type/` | the font families, the companion join (`oldgeTextOf`), the generated `FontCoverage` |
| `oldge-core/src/commonMain/composeResources/font/` | the seven bundled font files; their licences are in `composeResources/files/` |
| `scripts/fonts/` | the DejaVu subset and the coverage generator (fontTools, run by hand; the tests hold their output) |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/press/` | `OldgeIndication` (flash, focus ring; the theme's `LocalIndication`), `oldgePressScale`, `oldgePopIn` (`og-pop`, shared by the chip's and the checkbox's marks) and `oldgePopSoftIn` (`og-pop-soft`), and `oldgeHitArea` (a touch target larger than the drawn box that takes no more room) |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/material/CssBox.kt` | the CSS-box painter every material is built on (backgrounds: colour, `linear-gradient`, `radial-gradient(circle at …)`; `CssCorners` for a box rounded only on top, bottom (B-24) or right (B-26), `CssCornerRadii` for a radius per corner (B-34)); `Materials.kt` the materials, `Gloss.kt` the gloss transition |
| `oldge-core/src/desktopTest/kotlin/io/github/youndie/oldge/harness/` | `OldgeDemo` (the `.og-demo` frame), the pinned text style, `FixtureSizeTest` |
| `scripts/probes/` | probes: HTML from the design system's classes and components, rendered as references for shapes no preview shows — the materials (B-07), `FieldProbe`, a reveal field and a three-line field (B-53), and `ActionsProbe`, the `og-actions` row (B-56) |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/material/Turbulence.kt` | the grain and speckle alpha, a port of `feTurbulence` that matches Chrome (research §1.3) |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/theme/` | `OldgeTheme`, `OldgeSkin`, `OldgeTypography`, `OldgeMotion`, the platform reduced-motion `expect`. `OldgeTheme(platformTextStyle = …)` pins text rendering on every type style for screenshots (B-52); the test harness's `PortableTextStyle` goes through it, and `PlatformTextStyleTest` holds it |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/icons/` | `OldgeIcon` and the generated `OldgeIcons` (`scripts/generate_icons.py`; `checkOldgeIcons` in `check`) |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/` | `OldgeButton`, `OldgeIconButton`, `OldgeOrbButton` (B-12), `OldgeActions` — `og-actions`, the button row that wraps to a column (B-56), `OldgeFab`, `OldgeExtendedFab`, `OldgeFabDock`, `OldgeSegmented` (B-13), the three chips and `OldgeChipGroup` (B-14); `ButtonBehaviourTest`, `FabSegmentedBehaviourTest` and `ChipBehaviourTest` hold the READMEs' behaviour rules |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/containers/` | `OldgeDivider` (B-11), `OldgeCard`, `OldgeActionTile` (B-21), `OldgePanel`, `OldgeList` with `OldgeListItem` (an icon or, since B-54, any `lead` element), and `oldgeListSections` with `OldgeListSectionHeader` for a `LazyColumn` (B-20), `OldgeAccordion` (B-22), `OldgeSwipeRow` (B-23), `OldgeDialog`, `OldgeBottomSheet` (B-27), `OldgeScreenBody` — `.og-body`, a screen's root, which the test harness's `OldgeDemo` stands on too (B-50); `CardTileBehaviourTest`, `PanelListBehaviourTest`, `AccordionBehaviourTest`, `SwipeRowBehaviourTest`, `WindowBehaviourTest` and `ScreenBodyBehaviourTest` hold their READMEs' behaviour rules |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/` | `OldgeMenu`, `OldgeTooltip`, `OldgeBalloon` (B-28), `OldgeWindowBar`, `OldgeBottomNav` (B-24), `OldgeCategoryTabs`, `OldgeTabs` (B-25), `OldgeNavDrawer`, `OldgeStepper`, `OldgePageDots` (B-26); `MenuTooltipBalloonBehaviourTest`, `BarBehaviourTest`, `TabsBehaviourTest` and `DrawerStepperBehaviourTest` hold their READMEs' behaviour rules |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/` | `OldgeReadout`, `OldgeMeter`, `OldgeProgressBar` (B-29), `OldgeSpinner`, `OldgeSkeleton` (B-30), `OldgeBadge`, `OldgeAvatar` (B-31), `OldgeBanner`, `OldgeSnackbar`, `OldgeEmptyState` (B-32), `OldgePullRefresh` (B-33) and `OldgeLazyPullRefresh` round a `LazyColumn`, for `oldgeListSections` inside a refresh (B-55), `OldgeChatBubble`, `OldgeTypingIndicator`, `OldgeComposer` (B-34), with the LCD tag and text in `Lcd.kt` and the frozen loop phase in `Loop.kt`; `LcdBehaviourTest`, `LoopBehaviourTest`, `BadgeAvatarBehaviourTest`, `NoticeBehaviourTest`, `PullRefreshBehaviourTest` and `ChatBehaviourTest` hold their READMEs' behaviour rules |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/` | `OldgeCheckbox`, `OldgeRadioGroup` (B-15), `OldgeSwitch`, `OldgeSlider` (B-16), `OldgeTextField` (B-17), `OldgeSelect`, `OldgeCodeInput` (B-18), `OldgeDatePicker` (B-19), `OldgeSearchBar` (B-47), sharing `Field.kt`'s label and help line; `CheckBehaviourTest`, `SwitchSliderBehaviourTest`, `TextFieldBehaviourTest`, `SelectCodeBehaviourTest`, `DatePickerBehaviourTest` and `SearchBarBehaviourTest` hold their READMEs' behaviour rules |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/type/OldgeText.kt` | text on the CSS baseline — use it, not `BasicText`, for any text a reference shows. Public since B-51, for a consumer's text between the components; `sample`'s `ConsumerTextTest` holds it from outside the library |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/tokens/OldgeTokens.kt` | the generated token layer (`scripts/generate_tokens.py`; `checkOldgeTokens` in `check`) |
| `scripts/design-references.mjs` | renders the parity references into `snapshots/design/` (`make references`); `scripts/tokens-css.mjs` compiles the tokens for it |
| `oldge-core/api/` | the pinned public API: `desktop/` and `android/` JVM dumps and the klib dump for iOS and wasm (B-45) |
| `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/material/OldgeGloss.kt` | the one public part of `material/`: `OldgeGloss`, `Modifier.oldgeGloss`, `animateOldgeGloss`, for a consumer's own glossy element (B-45); `sample`'s `ConsumerGlossTest` holds it from outside the library |
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
* **The public API is pinned** (B-45). `abiValidation {}` puts `checkKotlinAbi` into `check`,
  against the dumps in `oldge-core/api/`. A public change is recorded with
  `./gradlew :oldge-core:updateKotlinAbi` in the same commit, so it arrives as a diff in `api/`
  that somebody reads. Without that, `check` fails. Four mutations were each caught, with "ABI
  check failed", and not by the exit code alone:
  - a removed default;
  - a `val` made internal;
  - a composable made internal;
  - a public function added.

  **What is public.** The components, the tokens, the theme, the icons and `OldgeText` are public.
  From `material/`, only the gloss is. The README invites a consumer to build glossy elements of
  their own "the same way", from three stops of one family, and that cannot be done well from
  outside: the stops are transitioned in premultiplied sRGB, and `animateColorAsState` works in
  Oklab. The CSS-box painter, the bezels and the texture stay internal. They are how the library
  matches Chrome, they still change item by item, and pinning them would make each such fix a
  breaking change. *Rejected:* making `material/` public as it is, which is a surface of 40
  internals nobody designed as an API.
* **Every public composable's KDoc names its design-system component in backticks**, such as "the
  design system's `Meter`". That name leads a reader to `components/<Name>/README.md`.
  `component_catalog.py --check` enforces it in `make check`. A public function beyond the
  exports needs a KDoc at all (B-45).
* **The set is guarded, not just each image.** `ScreenshotSuiteTest` fails on a fixture without a
  golden or a golden without a fixture, a parity fixture without a reference or a reference nobody
  compares, and a design-system component with neither fixtures nor a place on its explicit
  not-yet-built list — which every component item shrinks. The B-01 canary (`OldgeCanary`) was
  deleted there, once real fixtures existed.

## 4. Dependencies

| Kind | Name | What for |
|---|---|---|
| Build | sborka `0.4.0.91` (`io.github.youndie.sborka.settings`, `.kmp`, `.lint`) | repositories, the `wip` catalog, toolchain 25, explicit API, warnings as errors, ktlint |
| Test | viddik `0.6.0.40`, the wip build of `10f128b` from reposilite, until 0.6.1 is on Central | goldens, design parity |

## 5. Motion

Parity renders with reduced motion (research §1.2), so it sees none of this. The motion is held by
[`MotionTest`](../../oldge-core/src/desktopTest/kotlin/io/github/youndie/oldge/behaviour/MotionTest.kt)
and by behaviour tests elsewhere. `MotionTest` runs each entrance on a clock driven by hand, and
requires three things of it. Halfway, it is neither where it started nor where it ends. Once over,
it is within 0.1 % of what reduced motion draws at once. Its halfway frame matches a golden of its
own in `oldge-core/src/desktopTest/snapshots/motion/`, so a changed easing shows. A missing golden
is written and reported as a failure, never passed silently.

`bundle.css` has **31** `@keyframes`, not the 34 that B-44's text counted.

| `@keyframes` | Component (CSS selector) | Held by |
|---|---|---|
| `og-balloon-in` | Balloon, Tooltip (`.og-tip__in`), ChatBubble | `MotionTest` `balloon-in`, `balloon-in-bubble` |
| `og-banner-in` | Banner | `MotionTest` `banner-in` |
| `og-blink` | Switch lamp coming on; CodeInput caret | `MotionTest` `blink` (the Switch). The caret's loop has no test. |
| `og-bump` | Badge count | `BadgeAvatarBehaviourTest.a_count_bumps_when_it_changes_and_not_when_it_appears` |
| `og-dial` | Skeleton circle | `LoopBehaviourTest.without_reduced_motion_they_move`, `…under_reduced_motion_the_disc_the_scanner_and_the_arc_stand_still` |
| `og-draw` | CategoryTabs label | `MotionTest` `draw` |
| `og-drawer-in` | NavDrawer | `MotionTest` `drawer-in` |
| `og-fab-in` | Fab | `MotionTest` `fab-in` |
| `og-fade` | Tabs panel; the Dialog, BottomSheet and NavDrawer scrims | `MotionTest` `fade-tabs-panel`. The scrims are inside the `window-in-dialog`, `sheet-in` and `drawer-in` frames. |
| `og-flicker` | Readout digits, CodeInput digit | `MotionTest` `flicker` (the Readout) |
| `og-glint` | the press flash (`OldgeIndication`) | `OldgeIndicationTest.the_flash_is_centred_on_the_press` |
| `og-hop` | EmptyState orb, BottomNav icon, CategoryTabs glyph (`oldgeHopIn`) | `MotionTest` `hop` (the EmptyState) |
| `og-item-in` | Menu items, staggered | `MotionTest` `window-in-menu`: its halfway frame holds the items mid-stagger |
| `og-led` | Avatar's online lamp | `BadgeAvatarBehaviourTest.an_online_lamp_pulses_and_stands_still_under_reduced_motion` |
| `og-pop` | Checkbox and Chip check, Stepper done (`oldgePopIn`) | `MotionTest` `pop` (the Checkbox) |
| `og-pop-soft` | Segmented option, BottomNav capsule, ProgressBar detail icon, SearchBar and Composer orbs, DatePicker day (`oldgePopSoftIn`) | `MotionTest` `pop-soft` (the ProgressBar); `BarBehaviourTest.the_capsule_pops_when_a_section_becomes_current_and_not_under_reduced_motion` |
| `og-pulse-ring` | Stepper current step | `DrawerStepperBehaviourTest.the_current_ring_pulses_and_stands_still_under_reduced_motion` |
| `og-scan` | Skeleton | `LoopBehaviourTest.without_reduced_motion_they_move` and its reduced-motion twin |
| `og-seg-in` | Meter segments, 28 ms apart | `MotionTest` `seg-in` |
| `og-sheet-in` | BottomSheet | `MotionTest` `sheet-in` |
| `og-shimmer` | — | **Not applicable:** defined, and no rule in `bundle.css` uses it |
| `og-shine` | primary Button while pressed | `MotionTest` `shine`; `ButtonBehaviourTest.quick_presses_leave_no_glints_queued_after_the_last_release` (B-62) |
| `og-slide` | indeterminate ProgressBar | `LoopBehaviourTest.an_indeterminate_bar_slides_and_stands_still_under_reduced_motion` |
| `og-slide-l` | DatePicker, next month | `MotionTest` `slide-l` |
| `og-slide-r` | NavDrawer items; DatePicker, previous month | `DrawerStepperBehaviourTest.the_items_come_in_one_after_another` |
| `og-snack-in` | Snackbar | `MotionTest` `snack-in` |
| `og-spin` | Spinner, PullRefresh disc | `LoopBehaviourTest.without_reduced_motion_they_move` and its reduced-motion twin |
| `og-typing` | TypingIndicator dots | `ChatBehaviourTest.the_typing_indicator_says_who_types_and_its_dots_hop_but_not_under_reduced_motion` |
| `og-unfold` | Accordion body | `MotionTest` `unfold` |
| `og-wiggle` | SearchBar magnifier on focus | `SearchBarBehaviourTest.the_magnifier_wiggles_on_focus_and_not_under_reduced_motion` |
| `og-window-in` | Dialog, Menu | `MotionTest` `window-in-dialog`, `window-in-menu` |

A keyframe used by several components is held through one of them. The others share its code
(`press/Pop.kt` for the pops and the hop), not a copy of it.

## 6. Quirks

* **The viddik #44 bug, and its workaround, are gone** (B-35). viddik 0.6.0 put
  `build/generated/ksp/metadata/commonMain/kotlin` on commonMain unconditionally. In this module's
  shape, `./gradlew build` then failed every task that reads commonMain with "uses this output of
  task ':oldge-core:kspCommonMainKotlinMetadata' without declaring an explicit or implicit
  dependency". B-01 ordered those tasks by hand. viddik `10f128b` fixes it. On its wip build
  0.6.0.40 the block is deleted, and `./gradlew build` is green. The control is 0.6.0 without the
  block, which fails with that message again. If the error comes back after a viddik bump, the fix
  was lost, and ordering the tasks again only hides it.
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
* **A line height equal to the font size is ignored.** Compose lays `14.sp` in `14.sp` out as if
  no line height were set: the face's own taller line, the baseline at its ascent. The lcd readouts
  and the Avatar's initials sat off by it. `OldgeText` and `OldgeScriptText` pass every style
  through `withHonouredLine()`, which nudges such a line by 1.0001. A text component that
  bypasses both must do the same (B-49).
* **A CSS box's content is inside its border; a Compose `cssBox` draws the border over its
  content.** Give the content the border as padding on all four sides, as CSS lays it out, or a
  child as tall as the box sits over the border and the box does not grow. TextField padded only
  the sides, so its 44 dp reveal orb left the field 44 where Chrome makes it 46 (B-53).
* **Compose extends a small touch target by itself.** A pointer target under
  `ViewConfiguration.minimumTouchTargetSize` (48 dp here) is widened to it for a touch that hits
  nothing else, and not for a mouse. So the design system's 44 dp rule holds on touch for every
  control without a widening of the library's own. A control's laid-out size is what a mouse
  reaches, as in Chrome (B-60, which found that B-43 had measured the mouse).
* **`cssBox`'s blurred outer shadow fills the shape too.** It is Compose's `dropShadow`, drawn
  under the whole shape, where CSS paints an outer `box-shadow` only outside the box. Under an
  opaque fill the two look the same. Under a translucent fill the shadow shows through: PageDots on
  a photo was green, not grey. Draw such a glow as its own layer clipped to outside the shape, as
  PageDots and the Spinner do (B-58, B-30).
* **A box without `box-sizing` is `content-box`.** Its `min-height` is the content's, and its
  padding adds to it. `.og-winbar--overlay { min-height: 64px; padding-bottom: 8px }` is 72 px in
  Chrome. In Compose that is the padding before `defaultMinSize`, not after it (B-57).
* **An empty single-line `BasicTextField` measures 21 px for a 20 px line**, and 19 with a
  value. Give a single line exactly its line's height (`Modifier.height(line)`), not a minimum
  (B-17, B-53).
* **An `<input>` has padding the design never wrote.** Chrome's style sheet gives it `1px 2px`, so
  a bare input's text starts 2 px in. `.og-search__input` sets `border` and `background` but not
  `padding`, and the SearchBar's text sat 2 px early until the field took the same padding (B-47).
  A component built on a raw `<input>` or `<textarea>` should be read with the user-agent style in
  mind.
* **kotlinx-datetime is an `api` dependency** (B-19): `OldgeDatePicker` takes and gives
  `LocalDate`, so a consumer compiles against it; the version is `wip`'s.
* **`local.properties`** (git-ignored) must name the Android SDK (`sdk.dir=…`), or configuration
  fails with "SDK location not found".
* **A stagger eased as a whole lands early.** One `Animatable` over the whole `fast + 28 ms × n`
  run, with `out` easing on it, brought every Meter segment in by the halfway mark. CSS eases each
  segment on its own. The whole run is linear, and each segment's slice of it is eased. Found by
  `MotionTest` `seg-in` (B-44).
* **The test clock moves in whole frames.** `mainClock.advanceTimeBy(10)` advances 16 ms, so a
  probe that counts its own steps reads time 1.6 times too slow. For a `steps(1)` animation, the
  halfway mark is a step's edge, and a frame taken there falls on either side of it. `MotionTest`
  takes the Switch lamp's frame 5/8 of the way through, in the middle of the second dim run (B-44).
* **`Hyphens.Auto` is a no-op on desktop and iOS.** Skiko's paragraph has no hyphenation at all,
  and only Android's `StaticLayout` honours it. The four texts that bundle.css hyphenates pass it
  anyway, and `HyphenationTest` holds the request:
  - ListItem's title and value;
  - ActionTile's text;
  - Segmented's option.

  Its canary fails when Skia starts to hyphenate (research §1.10, B-59).
* **The drawer's scrim is the one scrim a screen reader hears.** The design system's scrims are
  plain `div`s, and B-43 took them out of the tree. The sheet and the dialog each have a «Закрыть»
  orb, but the drawer has none, so it could be left only by choosing an entry. At the owner's
  choice, its scrim is announced as a «Закрыть» button whose action calls `onClose`, as Material's
  scrim is. Nothing drawn changed (B-61).
* **Menu's left strip is its icon column.** `.og-menu` paints `pill-lo` under its first 40 px, the
  XP-menu gutter, and each item's icon sits in it. A Select opens its options in the same Menu.
  The strip goes when no option has an icon, because empty it reads as a stray stripe. Menu keeps
  it always, as the design draws it (B-63).
* **A click focuses, and Compose never blurs.** Clicking elsewhere does not take the focus away, as
  it does in a browser. So a `:focus-within` look on a control that is not a text field has to be
  keyboard focus (`InputMode.Keyboard`), or it stays on after the first click. The Select's arrow
  and ring did (B-66).
