---
id: research-architecture
title: oldge-ui — architecture research
type: research
status: active
date: 2026-09-25
---

# Research: the architecture of oldge-ui

oldge-ui is a design system — Nero StartSmart, AIMP and Windows XP Media Center seen through a
toxic lime accent — built in a Claude Design System artifact, and this repository is its Compose
Multiplatform implementation. This document says what was verified before any Kotlin existed,
against what, which decisions were taken on that basis and what each one rejected, and which
questions are still open and which backlog item answers each.

*Item numbers corrected in B-02:* this document was written before the backlog's final numbering,
and nine of its references pointed one or two items off (the grain spike is B-04, the token
generator B-05, the press feedback B-10, the parity floor B-08). They now match `backlog.md`.

It is amended, not rewritten. When the implementation contradicts something here, the correction
goes at the point of divergence and keeps the reason the first idea failed.

## 0. Where these facts come from, and how much each is worth

| Source | Worth | Why |
|---|---|---|
| The design system, vendored in `reference/design-system/` | **authoritative for intent**, not for implementation | It is the brief. Its CSS is a web implementation of the intent; where the CSS and the README disagree, the README states the intent and the CSS is evidence of how one renderer realised it. |
| A render of the design system's preview in headless Chrome | **authoritative for the reference** | This is what parity measures against (D3). It is a measurement of Chrome, not of the design. |
| Published artefacts (Maven Central, the Reposilite at `reposilite.kotlin.website`, the Gradle cache) | **verified** | Read at the version pinned, by unpacking or by `maven-metadata.xml`. |
| Sibling repositories (`kvadrant-ui`, `viddik`, `sborka`) | **verified for what they say about themselves** | kvadrant-ui is the closest precedent — a design-language component library on the same toolchain — and its lessons are cited by file, not by recollection. |
| Recollection of how Compose behaves | **not a source** | Anything from memory is written as a hypothesis below, with the item that checks it. |

## 1. Verified facts

### 1.1 The brief is a design system of 51 components, 9 screens and 3 skins

| Fact | Where verified |
|---|---|
| The design system is the claude.ai artifact `CL8BafGgX4GgYdJXNEZttC`, type "Design System", version `1790287085-ae36`, title `oldge-ui`, namespace `OldgeUI`, last changed 2026-09-24T21:57:36Z. | `reference/design-system/design-system.json` |
| Vendored whole on 2026-09-25: `README.md` (the brand book), `tokens.json`, and per component `components/<Name>/README.md` + `preview.html`, plus `bundle.css` (85,792 B), `bundle.js` (53,670 B) and `index.d.ts`. | `reference/design-system/` |
| **51 components** exported by the bundle, **50 with a preview** (`ChipGroup` rides with `Chip` and has no preview of its own) — *corrected in B-02: this row said 52, a miscount of the list beside it; `grep -c` over the exports gives 51*: Icon, Button, OrbButton, Segmented, WindowBar, CategoryTabs, BottomNav, Panel, Accordion, List, ListItem, ActionTile, Dialog, TextField, Select, Checkbox, RadioGroup, Switch, Slider, ProgressBar, Meter, Readout, Badge, Balloon, Card, Chip, ChipGroup, Fab, Menu, BottomSheet, NavDrawer, Tabs, Snackbar, Tooltip, SearchBar, Spinner, Divider, Avatar, Stepper, DatePicker, Banner, Skeleton, CodeInput, EmptyState, ListSection, SwipeRow, PullRefresh, ChatBubble, TypingIndicator, Composer, PageDots. | `reference/design-system/components/index.d.ts` (`export … function`) |
| **9 page previews** marked `page` in their card marker: six stress screens (AuthScreen, InboxScreen, ChatScreen, MediaScreen, EdgeNarrow, EdgeScale) and three showcase screens (Launcher, FeedScreen, SettingsScreen); plus a `Cover`. | line 1 of each `components/<Name>/preview.html` |
| Every preview's line 1 is `<!-- @dsCard group="…" height=N … -->`; the groups are Действия, Иконки, Контейнеры, Навигация, Обратная связь, Формы, Чат, Стресс-экраны, Экраны. A component preview is an `og-demo` column: `max-width: 390px`, padding `space-4`, gap `space-3`. | `components/*/preview.html`; `components/bundle.css` (`.og-demo`) |
| **Three skins** — `toxic` (first, the default), `media`, `crystal` — are the colour themes. 46 colour tokens, most with a value per skin; shadows per skin too. | `tokens.json` → `color.themes`, `color.tokens`, `shadow.tokens` |
| Token families: color 46, spacing 7 (`space-1…6` = 4, 8, 12, 16, 24, 32 px; `hit-min` 44 px), radius 6 (2, 4, 8, 12, 20, 999 px), shadow 7, duration 6 (90, 160, 260, 420, 600, 1200 ms), easing 4 (`cubic-bezier(0.34, 1.56, 0.64, 1)`, `cubic-bezier(0.68, -0.6, 0.32, 1.6)`, `cubic-bezier(0.2, 0.8, 0.2, 1)`, `linear`), type 11 styles in 2 groups. | `tokens.json` |
| **47 icons**, one SVG path each on a 24×24 grid: home search gear user bell star doc image note film folder disc flame cloud lock grid download upload refresh plus minus edit trash play pause levels back chevron down chevrons menu close check help info warn ok error eye eye-off attach send more heart share key mail. *Corrected in B-03: this row said 40 — the README's list, which stops at `error`; the bundle's table has seven more, and the Icon preview draws all 47.* | `components/bundle.js` (the icon table, `home: 'M12 3l9.5 8.5…'`) |

**Consequence.** The inventory is fixed and enumerable, so the backlog can cover it completely and
a check can prove that it did: 50 component previews and 9 pages × 3 skins — 177 PNGs — is the reference set, and a component with no
reference, or a reference with no component, is countable (B-09).

### 1.2 References can be rendered from the previews, but `tokens.css` has to be compiled here

| Fact | Where verified |
|---|---|
| A preview is not a standalone page: its frame preloads `tokens.css`, `bundle.css`, React 18 and `bundle.js`, and sets `<html data-theme="<first theme>">`. | `claude.ai/artifact/CL8BafGgX4GgYdJXNEZttC!/artifact-type/reference/format.md`, "The preview.html contract" (read 2026-09-25 from the artifact) |
| `tokens.css` is generated by the artifact page and is **not published**: reading `claude.ai/artifact/CL8BafGgX4GgYdJXNEZttC!/project/tokens.css` returns "no file is published at that path". | Artifact read of `CL8BafGgX4GgYdJXNEZttC`, 2026-09-25 |
| Its compiled shape is specified: `:root, [data-theme="<first>"] { --<color>; --<shadow> }`, one `[data-theme="<id>"]` block per further theme, `:root { --<space>; --<radius>; --<other>; --font-<key> }`; an alias `{name}` compiles to `var(--name)`. | `format.md`, "tokens.css as compiled" |
| **Measured, 2026-09-25:** a 40-line script compiling `tokens.json` to that shape, a wrapper page (tokens.css, `bundle.css`, React 18.3.1 UMD from jsDelivr, `bundle.js`, the preview's body) and `Google Chrome --headless=new --force-prefers-reduced-motion --force-device-scale-factor=1 --window-size=390,220` produced the Button preview in all three skins, visually matching the artifact's own cards. | the spike in the session scratchpad; the script is B-03's starting point |
| `bundle.css` loads Share Tech Mono and Silkscreen from Google Fonts by `@import`, and names Tahoma / Trebuchet MS for everything else, which Chrome on this mac resolves to the system's Microsoft fonts. | `components/bundle.css` line 1; `tokens.json` → `type.families` |
| Under `prefers-reduced-motion` every entrance is instant, Switch and Accordion lose their transitions, and one component changes its *static* look: the indeterminate ProgressBar becomes a full bar at 50 % opacity. | `bundle.css`, the five `@media (prefers-reduced-motion: reduce)` blocks |

**Measured in B-03, and three of them are the design system's own defects:**

| Fact | Where verified |
|---|---|
| The whole set — 50 component previews and 9 pages × 3 skins, 177 PNGs — renders in ~40 s, and **a second render is byte-identical** to the first (0 of 177 differ), once the three causes below are handled. | `scripts/design-references.mjs`; `oldge-core/src/desktopTest/snapshots/design/manifest.json` (sha256 per stem) |
| **The design system's reduced-motion rule does not reach every moving element.** It matches `[class*="og-"]`, so the TypingIndicator's dots (bare `<i>` inside `.og-typing__dots`, `animation: og-typing … infinite`) keep bouncing, and a page's own classes (`.media__glyph`, `.media__top`, `.media__bottom` in MediaScreen) keep animating — measured as 72 px differing in ChatScreen and 22,770 px in MediaScreen_Crystal between two renders. The renderer applies the design's own rule to every element. | `reference/design-system/components/bundle.css` line 758; `components/MediaScreen/preview.html` |
| **A preview's layout can depend on the frame it is shown in.** BottomSheet's body is `max-height: 70vh`, so rendered in a frame sized to its content the body clips itself and the button below it disappears. The renderer uses the card's own frame — the `@dsCard` height, growing to fit — which is what the artifact shows. NavDrawer's `min(304px, 86vw)` is the only other viewport-relative size, and it depends on the width alone. | `bundle.css` (`.og-sheet__body`, `.og-drawer`) |
| DatePicker marks "today" from `new Date()` inside the bundle; the renderer pins `Date` to 2026-09-24T12:00, the demo's own date. | `bundle.js` line 514 |
| A page preview is a `.phone` element (390×760, EdgeNarrow 320×680, EdgeScale 390×860) in a 16 px card margin with `shadow-window`; the reference is clipped to the `.phone` element, since the fixture is the screen alone. | `components/*/preview.html` |

**Consequences.**

1. The reference pipeline is this repository's code, not the artifact's: compile tokens, wrap the
   preview, render. It needs `node` and Chrome, both present on this mac (`node` v24.8.0 at
   `/opt/homebrew/bin/node`; `/Applications/Google Chrome.app`).
2. References are rendered **with reduced motion**, because otherwise an entrance animation or a
   spinner is photographed at whatever frame the virtual-time budget ended on. The Compose fixtures
   therefore render with the theme's reduced-motion switch on (D7), and that is why reduced motion
   is a theme property rather than something read from the platform inside each component.
3. The fonts in the reference must be the fonts the Compose side bundles, or every glyph is a diff
   (D3, D6). The wrapper overrides `--font-ui`, `--font-title`, `--font-lcd`, `--font-pixel` with
   `@font-face` rules pointing at the repository's own font files, and does not use the Google
   Fonts `@import`.
4. A preview's `height=N` is a card height that "grows to fit". The reference is rendered in exactly
   that frame (*amended in B-03*: this said "at the content's measured height", which clipped
   BottomSheet — above), and the fixture takes its size from the reference manifest; a component
   fixture is therefore mostly body below a demo column, as in the artifact's card.

### 1.3 The grain is Chrome's `feTurbulence`, and a different noise is a diff everywhere

| Fact | Where verified |
|---|---|
| Body, dialog, sheet and drawer grain is a 160×160 SVG `feTurbulence type='fractalNoise' baseFrequency='.85' numOctaves='2' stitchTiles='stitch'` passed through `feColorMatrix` and used as a **mask** over the skin's `grain` colour; the bezel speckle is a 128×128 tile at `baseFrequency='1.3'`. | `bundle.css` line 738 (`--og-grain-mask`) and the rules after it |
| `grain` is 5–7 % alpha: toxic `rgba(190,255,140,0.07)`, media `rgba(255,255,255,0.05)`, crystal `rgba(30,60,20,0.07)`; `bezel-weave` and `bezel-speckle` are transparent in the skins that do not use them. | `tokens.json` |
| `data-og-texture="off"` on any ancestor removes every texture layer. | `bundle.css` line 754 |
| The README asks the native implementation to generate the texture procedurally (a shader or a density-aware pre-generated tile), never a stretched bitmap. | `reference/design-system/README.md`, "Материал и текстура" |

**Consequence.** Any noise that is not the SVG turbulence algorithm differs from the reference on
every textured pixel, at low amplitude but over the whole screen — exactly the kind of diff that
hides a real one. The SVG 1.1 specification publishes the reference implementation of
`feTurbulence` in C, so a Kotlin port with the same seed can in principle generate the same tile.
~~Hypothesis~~ **Confirmed in B-04, with three corrections to the specification's reading.** The
SVG 1.1 reference implementation ported to Kotlin (`oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/material/Turbulence.kt`)
reproduces Chrome 153's tiles: the 128 px speckle **exactly** (16,384 of 16,384 pixels), the 160 px
grain within ±2 (25,590 of 25,600 exact, none beyond ±2). What it took, each found by measuring
against `oldge-core/src/desktopTest/snapshots/design/texture/*_chrome.png` rather than by reading Blink:

| Chrome's behaviour | What a literal port gets instead | Where verified |
|---|---|---|
| Stitching is over the **filter region** — the box plus 10 % a side — rounded down to whole pixels: 192 px for the grain (so 0.85 becomes 163/192), 153 px for the speckle, not 153.6 | mean error 46 of 255 at the tile's own 160 px: uncorrelated noise | `TurbulenceTileTest`; mutation `FILTER_REGION_SCALE = 1.0` → max deviation 181 / 255 |
| The noise is sampled at `(x + 1, y + 1)` | at the pixel centre (+0.5): max deviation 216 | mutation `SAMPLE_OFFSET = 0.5` |
| The noise is quantised to 8 bits **before** the colour matrix | the speckle's slope of 9 turns one level into nine: 434 px beyond ±2 | mutation without the quantisation |

The statistics alone were never the problem: the first, misaligned port already had Chrome's mean
(102.19 against 102.15) and share of zeros (1.42 % against 1.49 %), which is what said the algorithm
and the seed were right and the coordinates were not. Inside the visible tile the stitching wrap is
never reached, so the stitch itself cannot be verified from these renders — only the frequency it
implies.

### 1.4 Depth is inset bevels, not soft shadows — and Compose 1.12 has both in common code

| Fact | Where verified |
|---|---|
| `shadow-raised` and `shadow-sunken` are pairs of 1 px **inset** shadows (a light and a dark edge); `shadow-bezel` likewise; `shadow-orb`, `shadow-window` and `shadow-balloon` are outer drop shadows; `shadow-lcd-glow` is a 6 px glow, `none` in Crystal. | `tokens.json` → `shadow.tokens` |
| `Modifier.dropShadow` and `Modifier.innerShadow` are in **commonMain** of CMP ui 1.12.0 (package `androidx.compose.ui.draw`), with `Shadow`, `DropShadowPainter`, `InnerShadowPainter` in `androidx.compose.ui.graphics.shadow`. | `ui-metadata-1.12.0.jar!/commonMain/default/linkdata/package_androidx.compose.ui.draw/7_draw.knm`; `ui-graphics-metadata-1.12.0.jar!/commonMain/…/package_androidx.compose.ui.graphics.shadow/` |
| `ShaderBrush`, `ImageShader`, `LinearGradientShader`, `RenderEffect` are in commonMain; **no `RuntimeShader`/`RuntimeEffect` is** — only skiko's `org.jetbrains.skia.RuntimeEffect` on the skiko targets and `android.graphics.RuntimeShader` on Android. | `ui-graphics-metadata-1.12.0.jar!/commonMain/…/package_androidx.compose.ui.graphics/`; `skiko-awt-0.150.1.jar!/org/jetbrains/skia/RuntimeEffect.class` |

**Consequences.** A 1 px inset bevel is two 1 px strokes inside the shape's outline, and whether
`innerShadow` with zero blur and a 1 px offset draws exactly that is a **hypothesis** (B-07): the
cheaper and certainly exact alternative is drawing the two edges ourselves in `drawWithCache`. A
runtime shader would be an `expect`/`actual` on every target, so the grain is a generated
`ImageBitmap` tile through an `ImageShader` — common code, one implementation, no per-platform
shader language (D8).

### 1.5 Gloss is three gradient stops that animate, not a gradient that swaps

| Fact | Where verified |
|---|---|
| Every glossy fill is `linear-gradient(var(--og-g1), var(--og-g2) 55%, var(--og-g3))` over three registered colour properties (`@property --og-g1…3`), so a press or a selection **interpolates** the stops instead of swapping the gradient. Press in: `dur-fast`; release: `dur-base`. | `bundle.css` lines 386–389; README "Движение" |
| A highlight band `gloss` covers the top 36 % of a glossy element and never sits under text. | `bundle.css` `.og-gloss::before`; README "Форма" |
| The press feedback is a gloss flash from the touch point, bounded by the element's shape, fading over `dur-press` (600 ms); a squash to 0.86–0.95 in `dur-instant` and a spring back in `dur-base`; `data-og-press="none"` on an ancestor turns the flash off. | `bundle.js` lines 11–17; README "Движение" |
| The primary button additionally runs a diagonal glint across itself on press. | README "Движение"; `bundle.css` `og-shine` |

**Consequence.** A glossy surface in Compose is three `animateColorAsState` values feeding one
`Brush.verticalGradient(0f to g1, 0.55f to g2, 1f to g3)`; the press flash is an
`IndicationNodeFactory` (present in foundation commonMain — found by name in
`foundation-metadata-1.12.0.jar!/commonMain/…/package_androidx.compose.foundation/07_foundation.knm`,
not yet by decoded signature, so its exact shape is B-10's to confirm) installed as the theme's
`LocalIndication`, which is also how kvadrant-ui made one press feedback reach every clickable
(`kvadrant-ui/CLAUDE.md`, "The focus ring lives in the indication").

### 1.6 Fonts: three of the four families cannot be shipped as specified

Measured 2026-09-25 with HarfBuzz shaping (default features, kerning on) and fontTools; the width is
the sum of advances over `unitsPerEm` for RU = «Синхронизировать при подключении Создать резервную
копию» and LA = "The quick brown fox jumps over the lazy dog 0123456789". Coverage is the 66
Russian letters U+0410–U+044F, U+0401, U+0451.

| Fact | Where verified |
|---|---|
| Tahoma, Verdana and Trebuchet MS ship only with Microsoft products and may not be redistributed or embedded in an application; the copies on this mac are © 2006 Microsoft, EULA-only, `fsType` 8. | learn.microsoft.com/en-us/typography/font-list/tahoma; learn.microsoft.com/en-us/typography/fonts/font-faq; `name`/`OS/2` tables of `/System/Library/Fonts/Supplemental/Tahoma.ttf` |
| **Share Tech Mono has no Cyrillic** (0 of 66): OFL, reserved font name "Share", subsets `latin` only, one static Regular. | `google/fonts:ofl/sharetechmono/METADATA.pb`, `OFL.txt`; fontTools `getBestCmap` |
| **Silkscreen has no Cyrillic** (0 of 66): OFL, `latin` + `latin-ext`, static Regular and Bold. | `google/fonts:ofl/silkscreen/METADATA.pb`; fontTools |
| So the design system's own examples — the pixel tag «ХРАНИЛИЩЕ», the readout «23,4 ГБ из 64» — are drawn in Chrome by whatever the stack falls back to (`ui-monospace` / Tahoma), not by the face the design names. | the two rows above; `tokens.json` → `type.families`, the `readout-sm` sample; README "Голос" |
| **DejaVu Sans Condensed** against Tahoma: RU +3.5 %, LA +2.2 % (Regular); RU −0.5 %, LA +0.5 % (Bold); x-height .547 vs .545, cap .729 vs .727, x/cap .750 vs .750; line height 1.164 vs 1.207; static 400 and 700; full Cyrillic; ~650 KB and 6,253 glyphs per file. It is already in the design's own `ui` stack. | measurement; `dejavu-fonts/dejavu-fonts@version_2_37` (LICENSE in the ttf zip: Bitstream Vera terms, DejaVu changes public domain) |
| Runner-up for `ui`: Fira Sans (RU +2.7 %, Bold −10.0 %) — the Bold is too narrow. PT Sans −5.1 % / −16.5 %. Noto Sans and Open Sans are variable-only. DejaVu Sans (regular width) is Verdana's twin (RU +1.7 % against Verdana) and 15 % wider than Tahoma. | measurement |
| **Fira Sans Bold** against Trebuchet MS Bold: RU −2.3 %, LA −5.9 %, static, OFL. Ubuntu Bold is closer (RU +3.3 %, LA −1.0 %, line height 1.149 vs 1.161) but under the Ubuntu Font Licence, not OFL. | measurement; `google/fonts:ufl/ubuntu/METADATA.pb` (`license: "UFL"`) |
| **PT Mono** as the Cyrillic companion of Share Tech Mono: same vertical proportions (cap .700, x-height .500, line height 1.120 vs 1.127), full Cyrillic, static, OFL — but a 0.60 em advance against 0.54 em. Oxanium, Aldrich, Chakra Petch, Orbitron, VT323, Major Mono Display, Kode Mono have no Cyrillic; Jura and Exo 2 have Cyrillic and proportional digits. | measurement; `google/fonts:ofl/ptmono/OFL.txt` |
| **Tiny5** as the Cyrillic companion of Silkscreen: same cap height (0.625 em), full Cyrillic, static Regular only, OFL, grid 1/8 em. Pixelify Sans is missing О and П (U+041E, U+041F; upstream issue #4 and PR #5 open). | measurement; `google/fonts:ofl/tiny5`; `eifetx/Pixelify-Sans` issue #4 |
| **Compose does not fall back to a second font in the same `FontFamily`; the host draws the missing glyphs.** Set in Silkscreen alone, «Х» and «Л» render as two different inked shapes — real glyphs, not a `.notdef` box, and Silkscreen has none — and `FontFamily(Silkscreen, Tiny5)` renders «SYNC ХРАНИЛИЩЕ» pixel-identical to Silkscreen alone. Widths at 20 sp: «ХРАНИЛИЩЕ» 129.72 px by the host, 112.50 px in Tiny5; «ГБ» 23.62 px by the host, 24.00 px in PT Mono (both monospace at 0.6 em, so width alone cannot tell those two apart — the join test compares pixels too). *Answered in B-02; this row said "not verified".* | `oldge-core/src/desktopTest/kotlin/io/github/youndie/oldge/type/CompanionJoinTest.kt` |
| The measurement above re-run on the **bundled, subset** DejaVu files gives the same numbers to two decimals (RU 30.65 / 33.63 em, x/cap .750). | `scripts/research/font_metrics.py` |
| AWT's `Font.canDisplay` reports Default_Ignorable code points (U+200B, U+FE00–FE0F, U+FEFF …), controls and separators as displayable in **every** font, cmap or not — 63 code points each for Share Tech Mono and Silkscreen. | `oldge-core/src/desktopTest/kotlin/io/github/youndie/oldge/type/FontCoverageTest.kt` |

**Consequences.** The Compose library cannot draw what Chrome drew on this mac, so the reference
must be rendered with the library's fonts, not the other way round (§1.2, consequence 3). And the
design's two free faces are kept exactly where they are drawable — Latin and digits — with a
Cyrillic companion joined per script run, because in the design's own intent a pixel tag is set in
a pixel face whatever its script (D6).

### 1.7 Text is in rem, and the stress screens test it at 200 % and at 320 dp

| Fact | Where verified |
|---|---|
| Every type style is in `rem` (`display` 1.75rem/2rem 700 … `pixel-tag` 0.625rem/0.75rem); `bundle.css` sets 125 sizes in rem and keeps px for geometry. Only the Switch lamp and the Stepper digit have a fixed text size. | `tokens.json` → `type.groups`; `bundle.css`; README "Типографика" |
| EdgeScale sets the root font size to `200%`; EdgeNarrow renders at 320 px with long German strings. | `components/EdgeScale/preview.html` (`fontSize = '200%'`); `components/EdgeNarrow/README.md` |
| Controls with text have a minimum height, not a fixed one; a row's long value wraps under its title once the list is narrower than 24em. | README "Стресс-экраны" |

**Consequence.** `1rem` is `16.sp` and a CSS px is a dp (D5); EdgeScale is a fixture with
`Density(1f, fontScale = 2f)`, and it is the test that no component fixed a height a text lives in.

### 1.8 The toolchain

| Fact | Where verified |
|---|---|
| viddik's latest release is **0.6.0** (2026-09-20); 0.4.0 and 0.5.0 before it. Design parity (`viddikDesignParity`) arrived in 0.5.0. | `repo1.maven.org/maven2/io/github/youndie/viddik/viddik-annotations/maven-metadata.xml` |
| 0.6.0 records **only the goldens a verification would reject** (#30), refuses to photograph text its font cannot draw when `glyphCheck` is on (#41), shards fixtures and shares one scene (#42, #43). | `youndie/viddik@v0.6.0` log, `v0.5.0..v0.6.0` |
| **0.6.0 has an unreleased bug in exactly this module's shape:** it adds `build/generated/ksp/metadata/commonMain/kotlin` to commonMain unconditionally, so a KMP module with KSP, several targets and `showroomTargets` off fails `build` with an implicit-dependency error on every per-target KSP task. `viddikRecord` alone passes. Fixed on `main` by `10f128b` (#45, closes #44), not released. | `youndie/viddik@10f128b`, `viddik-gradle-plugin/src/main/kotlin/io/github/youndie/viddik/gradle/ViddikPlugin.kt` |
| `viddik-annotations` 0.6.0 publishes android, desktop, iosArm64, iosSimulatorArm64 — **no wasmJs**. It is built on CMP 1.12.0 and Kotlin 2.4.20. | `viddik-annotations-0.6.0.module`, `viddik-annotations-desktop-0.6.0.module` on Central |
| `ViddikPlatformTextStyle`, `viddikTypography`, `normalizeVerticalMetrics` are JVM-only, in `viddik-testing-core`; `Modifier.viddikStableGlyphs()` is common. | `youndie/viddik@v0.6.0:viddik-testing-core/src/jvmMain/…/core/ViddikFonts.kt`; `…/viddik-annotations/src/commonMain/…/ViddikStableGlyphs.kt` |
| Parity reads references from `<snapshotsDir>/design/<group>_<name>.png` (anything outside `[A-Za-z0-9_.-]` becomes `_`), defaults 5 % of pixels and ±16 per channel, report-only unless `designStrict`, results in `build/reports/screenshots/design/`. Golden defaults: 0.05 % and ±2. | `youndie/viddik@v0.6.0:README.md` "Design parity"; `ViddikExtension.kt` |
| sborka's latest is **0.4.0.91**; its published catalog pins kotlin 2.4.20, ksp 2.3.12, agp 9.4.0, composeMultiplatform **1.12.0**. CMP 1.12.1 is out, but the portfolio takes CMP from the catalog. | `reposilite.kotlin.website/snapshots/io/github/youndie/sborka/catalog/maven-metadata.xml`; `~/.gradle/caches/modules-2/files-2.1/io.github.youndie.sborka/catalog/0.4.0.91/` |
| `sborka.kmp` turns on explicit API and warnings-as-errors; a wasm target needs `sborka.repositoriesMode` other than `FAIL_ON_PROJECT_REPOS` (kvadrant-ui uses `PREFER_PROJECT`). | `youndie/sborka:build-logic/…/kmp.gradle.kts`, `settings.settings.gradle.kts` |

### 1.9 What kvadrant-ui already paid for

kvadrant-ui is the same kind of repository — a design language as a CMP library, with viddik — and
these are lessons it recorded in its own files, cited so they can be re-read rather than trusted.

| Lesson | Where it is recorded |
|---|---|
| A golden with text records the **rasteriser**: macOS and FreeType agree on glyph boxes and disagree on edge pixels, and no tolerance that absorbs the second rasteriser still catches a weight change. The suite runs where it was recorded. | `kvadrant-ui/CLAUDE.md`, "Documentation checks" (B-35) |
| Every fixture builds its type ramp with a test-only `portableTypography(...)` that pins hinting and smoothing through `ViddikPlatformTextStyle`; the library never overrides the platform's hinting for its consumers. | `kvadrant-ui/CLAUDE.md`, "Every glyph in a golden comes from a bundled file" |
| `ScreenshotSuiteTest` guards the set both ways: empty registry, fixture without a golden, golden without a fixture. A test reading the golden directory declares it as a task input. | `kvadrant-core/src/desktopTest/…/behaviour/ScreenshotSuiteTest.kt` |
| Name goldens in ASCII. | `kvadrant-ui/CLAUDE.md`, "Screenshots" |
| `androidResources { enable = true }` in every module with an Android target, or the AAR ships without its fonts, green. | `kvadrant-ui/CLAUDE.md` (B-37) |
| AGP must be declared in the **root** build file, `apply false`, or the Compose plugin cannot see its classes. | `kvadrant-ui/build.gradle.kts` |

## 2. Decisions

### D1. `Oldge` in every identifier

`OldgeTheme`, `OldgeSkin.Toxic`, `OldgeButton`, package `io.github.youndie.oldge`. The design
system's `og-` class names and `window.OldgeUI` are web artefacts and are not copied. *Rejected:*
the bare Material-like names (`Button`, `Card`) — they collide with foundation and Material imports
in every consumer that has both on the classpath.

### D2. The core depends on no Material artefact

The design system follows Material's **inventory** (its own table maps each Material component to
an oldge-ui one) and none of Material's rendering: no ripple, no elevation tonal overlay, no state
layers. The core builds on `foundation`. *Rejected:* wrapping Material 3 and restyling it — M3 in
the CMP 1.12 line is an alpha (kvadrant-ui research §1.2), and every M3 behaviour the design
replaces would have to be switched off one by one.

### D3. References are rendered from the design system's previews by this repository

`scripts/design-references.mjs` (B-03) compiles `tokens.json` into `tokens.css` per the type's
grammar, wraps each `preview.html` with the bundle, and renders it in headless Chrome at 390 px wide
and the content's measured height, with reduced motion, device scale 1 and the repository's bundled
fonts, once per skin: `<Name>_<Skin>.png` in `oldge-core/src/desktopTest/snapshots/design/`, plus a
`manifest.json` recording the design-system version, the Chrome version and each size.

*Rejected:* screenshots of the artifact page — it needs a signed-in session and draws the cards
inside its own chrome; and hand-drawn artboards — a reference nobody rendered from the source is a
reference to somebody's reading of it. **This is a deviation from the design-to-compose pipeline**,
which expects a Claude Design canvas with static artboards: here the "artboards" are the design
system's live previews, and the wrapper is what makes them static.

### D4. Tokens are generated from `tokens.json`, never typed

A generator (B-05) reads `reference/design-system/tokens.json` and writes the Kotlin token layer:
`OldgeColors` per skin, spacing, radii, shadows parsed into bevel/drop-shadow specs, durations,
easings as `CubicBezierEasing`, type styles. A test regenerates and compares, so the vendored file
and the code cannot drift. *Rejected:* typing 46 × 3 colours by hand; kvadrant-ui's D12 made the
same call for the same reason.

### D5. 1 CSS px = 1 dp; 1 rem = 16 sp

Chrome renders the references at device scale 1 and viddik renders at density 1, so a px is a dp
with no conversion — the same rule the design-to-compose skill states. Type is in sp because the
design system's type is in rem and must follow the system font setting (§1.7).

### D6. Four families, six bundled faces — *a deviation from the brief*

| Family | The design says | Bundled | Why |
|---|---|---|---|
| `ui` 400/700 | Tahoma → Verdana → Segoe UI → DejaVu Sans | **DejaVu Sans Condensed** 400, 700 | Tahoma's x-height, cap and width within 3.5 %; static; full Cyrillic; already in the design's own stack. |
| `title` 700 | Trebuchet MS → Tahoma → Segoe UI | **Fira Sans** 700 | Within 2.3 % on Russian; static; OFL. |
| `lcd` 400 | Share Tech Mono → Lucida Console → ui-monospace | **Share Tech Mono** + **PT Mono** for Cyrillic | The design's face where it can draw; a companion with the same vertical metrics where it cannot. |
| `pixel` 400 | Silkscreen → Tahoma | **Silkscreen** + **Tiny5** for Cyrillic | Same; Tiny5 has Silkscreen's cap height. |

The companion is joined **per script run** — Cyrillic runs of a string get the companion's
`FontFamily`, everything else the design's face — rather than left to font fallback, because
whether Compose falls back per glyph is unverified (§1.6) and a fallback to a system face is the
failure that makes a golden stable on one machine only. Measured cost, accepted: PT Mono is 11 %
wider per glyph than Share Tech Mono, so a mixed-script readout is uneven; Tiny5 is 25 % narrower
than Silkscreen. The subsetting (Latin + Cyrillic + the punctuation the design uses), the static
instances and the licence files are B-02.

**As built (B-02).** Seven files for the six faces (DejaVu in two weights), ~980 KB; only DejaVu
is subset (to Latin-1, Latin Extended-A, Cyrillic and UI punctuation, 56 + 51 KB), because the
Bitstream Vera licence asks for a rename only when a modified font's name contains "Bitstream" or
"Vera" (`oldge-core/src/commonMain/composeResources/files/DejaVu-LICENSE.txt`, lines 22–26); the OFL
faces ship unmodified. The companion decision is **coverage read from the design face's cmap**
(`FontCoverage.kt`, generated, held to the files by `FontCoverageTest`), not "is it Cyrillic".
viddik's `glyphCheck` was evaluated and **not enabled**: it checks a fixture's text against one font
file, and a fixture here draws in up to seven; `DesignStringCoverageTest` holds every family to every
string the previews and the fixtures render instead.

*Rejected:* Ubuntu Bold for `title` — metrically closer, but a second licence family (UFL) for one
weight; PT Mono or Tiny5 for the whole family — it would replace the design's named face even for
the Latin and digits it draws correctly; leaving Cyrillic to fallback — above.

**This is the most visible deviation from the brief, and the design system itself has the bug it
works around:** its Cyrillic pixel tags and readouts are not drawn in the faces it names, on any
machine. That is reported to the design system's owner rather than fixed here (the vendored
design system is not edited).

### D7. Reduced motion and texture are theme properties

`OldgeTheme(skin, reducedMotion, texture, pressFlash)`: the platform's reduced-motion setting is
the default, and a fixture sets it explicitly. The design system's `data-og-texture="off"` and
`data-og-press="none"` become the other two. *Rejected:* each component reading the platform
setting itself — it cannot then be forced in a fixture, and the references are rendered with reduced
motion on (§1.2).

### D8. The grain is a generated tile, not a runtime shader

A tile generated once per (skin, density) into an `ImageBitmap` and drawn through an `ImageShader`
with repeat tiling: common code, deterministic, and the "pre-generated tile with density taken into
account" the README allows. **The tile is the SVG turbulence itself** — B-04 measured the port
against Chrome (§1.3), so parity fixtures and references both render with texture on. *Rejected:*
SkSL/AGSL — no common runtime-shader API in CMP 1.12 (§1.4), and a shader layer needs
`viddikStableGlyphs()` around any text inside it.

### D9. viddik 0.6.0, with the one ordering line its bug needs

0.6.0's record-only-what-fails and glyph check are worth having from the first golden. Its #44 bug
is worked around in `oldge-core/build.gradle.kts` by ordering the per-target KSP tasks after
`kspCommonMainKotlinMetadata` — what the plugin itself does when `showroomTargets` is on — with a
comment naming the issue, removed when 0.6.1 ships (B-35).

*Amended in B-01:* ordering the per-target KSP tasks was not enough. Every task that reads commonMain
fails the same way — the compile tasks, and then ktlint's commonMain check, which is not a
`SourceTask` — so the workaround orders KSP, compilation, `SourceTask`s and ktlint's tasks by name.
It is wider than the plugin's own ordering because the plugin only needs to cover the tasks that
run when the directory is real; here it is real for nobody and read by everything. *Rejected:* 0.5.0 — it would have to be
re-recorded against on the bump anyway, and it lacks #30, so every record rewrites every golden.

### D10. Targets: desktop, Android, iOS, wasm — and the suite is desktop

The design system is a mobile one, so Android and iOS are first-class; wasm is for a catalogue page
later. viddik renders on the JVM only, so **a green `check` says nothing about Android**, the same
as in kvadrant-ui. The fixtures live in `desktopTest`; `showroomTargets` stays off because viddik
has no wasm artefact.

## 3. Risks and open questions

| Risk | What it would cost | Mitigation machinery | Item |
|---|---|---|---|
| The reference and the Compose render differ by font rendering alone above parity's default 5 % | Every number is noise; a real gap hides in it | The floor is measured on one component a person declares done, with the same bundled fonts on both sides; every later number is read against it | B-08 |
| Grain noise cannot be matched | A low-amplitude diff over every body pixel | Port `feTurbulence` from the SVG spec; fallback: texture off in parity, a golden for the texture | B-04 |
| A Cyrillic glyph falls back to a system font in one family | A golden that is stable here and differs elsewhere, silently | viddik `glyphCheck` against each bundled family; a coverage test over every string in every fixture | B-02, B-09 |
| `innerShadow` does not draw a crisp 1 px bevel | Every raised and sunken surface is off by a pixel ring | Draw the bevel edges directly; measured against the reference on Panel and Button | B-07 |
| The design system changes after it was vendored | References and code describe an old version | The manifest records the version; re-vendoring is an explicit item, never a silent overwrite | B-36 |
| viddik's #44 workaround outlives the fix | A build line nobody can explain | The comment names the issue; B-35 removes it on the 0.6.1 bump | B-35 |

## 4. What happens next

[backlog.md](../../backlog.md): stage 0 answers the four questions above that can change how every
component is built (B-02…B-04, then the floor in B-08), stage 1 builds the theme and its materials, stage 2 the components in
the design system's own groups, stage 3 the screens that stress them.

## Code anchors

| Kind | Path |
|---|---|
| The brief | `reference/design-system/README.md` |
| Tokens | `reference/design-system/tokens.json` |
| The web implementation | `reference/design-system/components/bundle.css` |
| Component index | `reference/design-system/components/index.d.ts` |
| viddik parity | `youndie/viddik@v0.6.0!/README.md` |
| viddik #44 fix | `youndie/viddik@10f128b!/viddik-gradle-plugin/src/main/kotlin/io/github/youndie/viddik/gradle/ViddikPlugin.kt` |
| Compose shadows | `ui-metadata-1.12.0.jar!/commonMain/default/linkdata/package_androidx.compose.ui.draw/7_draw.knm` |
| Precedent | `kvadrant-ui/CLAUDE.md` |
