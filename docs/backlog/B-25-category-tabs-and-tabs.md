---
id: B-25
title: "CategoryTabs and Tabs"
status: done
priority: P1
size: M
stage: stage-2-components
blocked_by: [B-12, B-31]
---

# B-25 — CategoryTabs and Tabs

CategoryTabs: a scrolling row of big glyphs, only the active one labelled, the label underlined by the 'hook' that draws left to right. Tabs: the XP property-sheet tabs.

- **References**: `CategoryTabs_{Toxic,Media,Crystal}`, `Tabs_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
- **Read first**: the component's README (the rules), its `preview.html` (the demo to port), and
  its `.og-` block in `reference/design-system/components/bundle.css` (the exact numbers). The skill is `design-to-compose`
  from Step 2, since the references already exist (B-03).

- AC: each component is a public composable in `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/` taking every value from the theme
  (research D4) — `grep -nE "Color\(0x|[0-9]+\.dp|[0-9]+\.sp" ` over the new files finds only
  token-layer references, and any exception is named in the commit body with its reason.
- AC: the design system's preview is ported as **one fixture per skin**, string for string and in
  the same order — `@ViddikScreenshot(group = "<Name>", name = "Toxic" | "Media" | "Crystal")` at the
  size `design/manifest.json` records for `<Name>_<Skin>.png`, inside the `OldgeDemo` harness (B-08).
- AC: `./gradlew :oldge-core:viddikDesignParity --component "<Name>*"` run for every component here;
  each fixture's `mismatchPercent` read against the floor B-08 measured, every non-`MATCH` fixture
  read as reference / `_ACTUAL` / `_DIFF` (at most five rounds per component, design-to-compose
  Step 5), and `summary.txt` for these fixtures in the squash commit's body with the token table.
- AC: the states the preview does not show but the component's README names (pressed, disabled,
  selected, focused, error — whichever apply) each have a golden fixture in group `<Name>States`;
  those are goldens, not parity, and are recorded for these fixtures only (`git status` shows no
  other PNG).
- AC: the rules in each component's README that are behaviour rather than look (an icon-only
  control needs a label, a minimum touch target, what a disabled control ignores) are encoded in
  the API or in a test, and the KDoc names the README rule it came from.
- AC: `ScreenshotSuiteTest` (B-09) no longer lists these components as missing; `./gradlew check`
  and `make check` green.

- Anchors:
  - `reference/design-system/components/CategoryTabs/README.md`, `reference/design-system/components/CategoryTabs/preview.html`
  - `reference/design-system/components/Tabs/README.md`, `reference/design-system/components/Tabs/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/`

## Findings

- Parity, `summary.txt` in the squash commit:
  - CategoryTabs is 0.48 / 0.50 / 1.01 %.
  - Tabs is 1.76 / 1.82 / 1.87 %, after two rounds. The rest is research §1.10's rule: Chrome's tab
    widths are fractional (76.81, 101.5, 81.14 px), measured on the reference.
- **Tabs' first run was 6.3 %:** the whole block was a pixel high. The resting tabs'
  `margin-top: 4px` is what makes the list 44 px inside its border, and without it the list was 43.
  The current tab also starts 1 px down, since its `margin-bottom: -1px` reaches over the border.
  Both are a `layout` on the tab. The quirk is in the service document.
- **Tabs' second run was 3 %:** the labels sat a pixel left and half a pixel high. The tab's 12 px
  padding is inside its 1 px frame (`border-box`), as B-12 already found for the Button. The
  quirk is written down now, since it came back.
- The tab's frame is open at the bottom and the panel's at the top. Neither is a `cssBox`, which
  paints a closed ring, so both are drawn as two rounded rectangles, the edge and the fill inset on
  three sides.
- The CategoryTabs label hangs outside its 56 × 52 button, as `position: absolute` puts it. The
  category is a `Layout` that reports the button's size and places the label below. The selection
  is on the whole layout, so the current category's name is its visible label; the others take
  `aria-label`, as bundle.js does.
- The glyph's `drop-shadow(0 2px 2px)` is the glyph again, blurred to σ 1 (Compose radius 0.87), as
  OldgeCard's media glyph does with σ 1.5.
- **An addition to the design: ← and → move between tabs**, which the ARIA tabs pattern expects.
  bundle.js keeps a roving `tabIndex`, so only the current tab is in the Tab order, but gives no
  arrow keys, which leaves the other tabs unreachable from the keyboard. The focus follows once
  the new tab is current, since only a current tab can take it.
- The READMEs' rules, in the API or `TabsBehaviourTest`:
  - only the current category shows its label, and the others are named;
  - four to eight categories and two to five tabs (both `require`);
  - a tab shows its page and a tap changes it;
  - the arrow keys move, wrapping, and take focus;
  - only the current tab can be focused.
- Mutations through `scripts/mutate.py`: 12 of 12 killed by their aimed tests. The first
  `tabs-panel` mutant did not compile (warnings are errors), so it was rewritten. Goldens by name:
  - the hook (`CategoryTabs`, `CategoryTabsStates`) and the glyph's shadow;
  - the accent strip and the list's border (`Tabs`, `TabsStates`).
- The States goldens: the first category current and the last of four current; five tabs with
  icons and a badge, the middle one current. Five tabs overflow 390 dp and scroll, as
  `overflow-x: auto` does.
- Values the tokens do not hold, marked `// css literal:`:
  - category 56 × 52, glyph 36, its shadow 2 down at σ 1;
  - the label 6 in and 4 down, padded 0 12 3 6, hook 2, the row's 16 of reserved room;
  - tab 40 / 44, 4 down, corners 6, strip 3, gaps 2 and 6, icon 16, text 0.875rem / 1.125rem.
