---
id: B-28
title: "Menu, Tooltip and Balloon"
status: done
priority: P1
size: M
stage: stage-2-components
blocked_by: [B-12]
---

# B-28 — Menu, Tooltip and Balloon

Menu: the XP menu — icon strip, blue selection. Tooltip: the small yellow plate. Balloon: the XP tray balloon, identical in every skin (the README says so — a test asserts the three skins' Balloon fixtures are pixel-identical), with a tail top, bottom or none.

- **References**: `Menu_{Toxic,Media,Crystal}`, `Tooltip_{Toxic,Media,Crystal}`, `Balloon_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
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
  - `reference/design-system/components/Menu/README.md`, `reference/design-system/components/Menu/preview.html`
  - `reference/design-system/components/Tooltip/README.md`, `reference/design-system/components/Tooltip/preview.html`
  - `reference/design-system/components/Balloon/README.md`, `reference/design-system/components/Balloon/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/`

- *Added by B-18:* `OldgeSelect` opens a minimal stand-in list (`OptionList` in
  `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeSelect.kt`), 4 px under the
  frame as `.og-menu` opens. When Menu exists, Select opens Menu's popup instead, and the stand-in
  goes.

## Findings (2026-09-25)

- Parity (±16, floor 0.72 %):
  - Menu: 0.95 / 1.03 / 1.11 % (Toxic / Media / Crystal), from 2.54–2.70 % after one cause. The
    item's `grid-template-columns: 40px …` column is the item's own first 40 px, which start 3 px in
    from the menu's edge, so the icons sit 3 px right of the strip. I had taken the column as the
    strip.
  - Tooltip: 1.67 / 1.92 / 1.93 %. The plate lines up with Chrome's; what is left is its label's
    sub-pixel edges.
  - Balloon: 3.72 / 3.73 / 3.76 %. Box, border and tail line up; the body text, 13 px in 18, is a
    pixel low. That is B-46's cause, added there.
- **`cssBox` paints a hard offset shadow**: the balloon's `2px 3px 0`. An outer shadow with no blur
  had been drawn as a spread ring that ignored its offset. Every earlier ring has a zero offset, and
  a mutation of the offset moves only the Balloon and Tooltip goldens.
- Balloon is the same in every skin, as its README says: `balloon`, `on-balloon` and `balloon-mark`
  are one colour in all three skins. `a_balloon_is_the_same_in_every_skin` renders it on the same
  ground in each skin and compares the pixels.
- **Tooltip reads "focus" as `:focus-visible`.** The design system's JSX shows the plate on any
  `onFocus`, and a tap focuses a button, which would show it at once on every tap, against the
  README's own rule for touch (450 ms, then 1.5 s). Focus brought by a press does not count until
  the focus is lost. The plate is a `Popup` centred 6 px above or below.
- Menu is a `Popup` 4 px under its trigger, from the start or end corner. It takes the focus when it
  opens, so Escape reaches it; a tap outside and a choice close it too. **Select now opens its
  options as an `OldgeMenu`**, as B-18's note asked, and its stand-in list is gone. Select's
  parity (2.40–2.51 %) and B-18's five behaviour mutants are unchanged.
- The READMEs' rules, in the API or `MenuTooltipBalloonBehaviourTest` (7 tests):
  - Balloon: the same in every skin; announced (a polite live region); a named 44 dp close button.
  - Tooltip: shows on hover, on keyboard focus, and after a 450 ms press; goes 1.5 s after the
    touch.
  - Menu: opens from its trigger; a choice closes it; Escape closes it.
  - KDoc lines: «a destructive item last, after a divider» and «a balloon is not for decisions».
- Mutations through `scripts/mutate.py`: 10 of 10 behaviour mutants killed by their aimed tests.
  Goldens by name: the tail, the shadow offset, the strip and the icon column.
- Values the tokens do not hold, marked `// css literal:`:
  - Balloon: tail 14, the triangles 18 × 14 and 15 × 12 at 36 / 37 from the right, mark 20 with
    1 on top, body gap 2 and line 1.125rem, close icon 16 pulled 12 / 6, the 44 px transform
    origin.
  - Tooltip: gap 6, padding 3 × 8.
  - Menu: gap 4, min width 220, strip 40, padding 3, row inset and radius 3, icon 18, divider inset
    44, the 6 and 14 px drops.
