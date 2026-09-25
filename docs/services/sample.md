---
id: sample
title: sample — the library's first consumer, and the stress screens
type: service
module: sample
tech_stack: [Kotlin 2.4.20, Compose Multiplatform 1.12.0, viddik 0.6.0]
owner: unassigned
depends_on:
  - oldge-core
  - viddik
publishes: []
---

# sample

## 1. Responsibility

The design system's page previews rebuilt from this library's **public** API. It holds the stress
screens (B-37 to B-39) and the showcase screens (B-40), each held to the page's reference by
parity. Because it is a separate module, nothing in it can reach an `internal`: a screen that
needs a drawing or a text the library does not offer publicly fails to compile. That is the check
that the library is enough to build a screen, and it has already found three gaps: B-50, B-51 and
B-52.

It is not the library. A screen here is a composition of components, with no drawing of its own. A
missing piece becomes a library item, not a private composable (B-37's decision).

## 2a. Code anchors

| File | What is there |
|---|---|
| `sample/src/commonMain/kotlin/io/github/youndie/oldge/sample/AuthScreen.kt` | the sign-in stress page (B-37) |
| `sample/src/commonMain/kotlin/io/github/youndie/oldge/sample/ChatScreen.kt` | the chat stress page (B-37) |
| `sample/src/commonMain/kotlin/io/github/youndie/oldge/sample/InboxScreen.kt` | the long-list stress page: sticky sections in a lazy pull to refresh, swipe rows, the empty and error filters (B-38) |
| `sample/src/commonMain/kotlin/io/github/youndie/oldge/sample/MediaScreen.kt` | the full-screen media stress page: the overlay bar, the page's own photo stand-in and scrim, PageDots on dark (B-38) |
| `sample/src/desktopMain/kotlin/io/github/youndie/oldge/sample/Main.kt` | the desktop window; screens are shown there once B-41 builds the sample app |
| `sample/src/commonMain/kotlin/io/github/youndie/oldge/sample/EdgeScreens.kt` | the Edge stress pages: 320 dp in German, and the system font at 200 % (B-39) |
| `sample/src/desktopTest/kotlin/io/github/youndie/oldge/sample/EdgeClippingTest.kt` | no text on the Edge pages is cut but the BottomNav's own ellipses (B-39) |
| `sample/src/desktopTest/kotlin/io/github/youndie/oldge/sample/ScreenFixtures.kt` | `Phone`: the preview's `.phone` harness, and the parity fixtures |
| `sample/src/desktopTest/kotlin/io/github/youndie/oldge/sample/ScreenBehaviourTest.kt` | what a page demonstrates as interaction: the sign-in mode switch, a sent message joining its run |
| `sample/src/desktopTest/kotlin/io/github/youndie/oldge/sample/ConsumerTextTest.kt` | `OldgeText` held to Chrome's ink from outside the library (B-51) |
| `sample/src/desktopTest/snapshots/design/` | the page references and their manifest |
| `sample/build.gradle.kts` | viddik and the golden-directory input, as oldge-core has them |

## 3. How it is built

- **The references are rendered here, not copied.** Run
  `node scripts/design-references.mjs --only 'AuthScreen_*' --out sample/src/desktopTest/snapshots/design`,
  once per page. `--only` matches the whole stem, so `AuthScreen` alone renders nothing. oldge-core
  keeps its own copies of the page references from B-03. B-37 checked that the two renders are the
  same file: each `sha256` in this manifest equals oldge-core's.
- **`Phone` is the mock-up, not the screen.** It clips the screen to `radius-xl` over the page's
  body, because the reference is photographed clipped to `.phone`.
  - The phone's `shadow-window` falls in the photographed corners. It belongs to the mock-up and
    is not drawn.
  - Text is pinned through `OldgeTheme(platformTextStyle = …)` (B-52) with oldge-core's harness
    value, so parity reads against the same floor as the components.
- **No viddik#44 workaround here.** With one target there is no `kspCommonMainKotlinMetadata`, and
  the copied block failed the build on the missing task. B-35 deletes oldge-core's block only.
- **Mutants of this module's tests** need the runner pointed at it: `python3 scripts/mutate.py
  scripts/mutants.json --only B-NN --command "./gradlew :sample:desktopTest --tests {tests}
  --rerun --console=plain -q" --results sample/build/test-results/desktopTest`.

## 4. Dependencies

| Kind | Name | What for |
|---|---|---|
| Module | [oldge-core](oldge-core.md) | the components, through their public API only |
| Build | viddik 0.6.0 | goldens and design parity for the screens |

## 5. Quirks

- **`TextLayoutResult.hasVisualOverflow` does not mean cut.** Its width half compares the node with
  the paragraph, which is laid out at the width available, so every text that does not fill its
  line "overflows". A cut is a line cut off at the bottom, an ellipsized line, or a line wider than
  its node (`EdgeClippingTest`, B-39).

- **A page's own `<style>` is the page's content, drawn here.** MediaScreen's photograph, scrim
  and caption (`.media`, `.media__bottom`) are no design-system component. They are drawn in
  `sample` from theme colours, each literal marked with its source. What a design-system class
  draws must come from the library (B-38).
- **A page preview's `justify-content: flex-end` in a scrolling column** (ChatScreen's lane) is a
  `Box` aligned to the bottom holding the scrolling column. A short lane then sits at the bottom
  and a long one scrolls from its top, as the browser's does. `Arrangement.Bottom` inside
  `verticalScroll` does nothing, because the scrolling column is as tall as its content.
