---
id: B-19
title: "DatePicker"
status: done
priority: P1
size: M
stage: stage-2-components
blocked_by: [B-12]
---

# B-19 — DatePicker

A month calendar with a pill header; the day cells have a minimum, not a fixed, height. Everything drawn from the clock takes a `today` parameter with a fixed default in the fixture, or the reference is right on one day only (design-to-compose, Step 3).

- **References**: `DatePicker_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
- **Read first**: the component's README (the rules), its `preview.html` (the demo to port), and
  its `.og-` block in `reference/design-system/components/bundle.css` (the exact numbers). The skill is `design-to-compose`
  from Step 2, since the references already exist (B-03).

- AC: each component is a public composable in `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/` taking every value from the theme
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
  - `reference/design-system/components/DatePicker/README.md`, `reference/design-system/components/DatePicker/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/`

## Findings (2026-09-25)

- Parity (±16, floor 0.72 %): DatePicker 1.59 / 1.61 / 1.67 % (Toxic / Media / Crystal), on the
  first run. Every row and the header line up with Chrome's. What is left is glyph edges in a
  preview that is 30 numbers, a title and seven weekday tags, plus a pixel of column rounding:
  Chrome's `1fr` columns are ≈ 46.9 px, and cells are placed at the rounded position. A second round
  moved each cell to its exact x in a layer. It was worse (1.77–1.84 %), because the chosen day's
  ball blurred where Chrome snaps it, and it was reverted.
- `today` is a parameter, defaulting to the system clock's date. The fixture pins it to 2026-09-24,
  the day the reference renderer pins Chrome's clock to (B-03), and the preview's value is that same
  day. The one cell is both, and the chosen ball's ring replaces today's, as bundle.css's
  specificity has it. `DatePickerStates` has them apart in June 2026, which starts on a Monday.
- **kotlinx-datetime** (`wip`'s 0.8.0) is now an `api` dependency: the value is a `LocalDate`,
  months are `YearMonth`. The alternative, a date type of the library's own, would make every
  consumer convert.
- **A vacuous assertion, found by a mutant that survived.** The arrows test ended in a bare
  `onNodeWithContentDescription("1 октябрь 2026")`, and Compose's finders are lazy, so reversing the
  month direction passed. The ad-hoc mutation loop had reported that run as `exit=1`, and it was
  re-run with test names. The fix is `assertExists()`. A grep for the same pattern found one more
  bare line, B-17's reveal test, and fixed it. Because B-12…B-18 read mutation kills the same way,
  **B-48** (P0) builds a runner that names the failing test and re-checks them.
- The README's rules, in the API or `DatePickerBehaviourTest`:
  - the week starts on Monday;
  - the names are Russian and every day is named in full («24 сентябрь 2026», as bundle.js
    spells it);
  - choosing a day reports it and marks it selected;
  - the arrows are named and turn the month.
- The rules that are look, in the goldens:
  - today's `accent-ink` ring;
  - the chosen ball with its `accent-rim`;
  - minimum-height cells.

  The slide of a month change and the ball's pop are motion, not shown under reduced motion.
- Mutations, each failing on the test it was aimed at (names read from the log):
  - in `DatePickerBehaviourTest`: a Sunday-first week, the day label's case, the arrows'
    direction, the selected state;
  - in the goldens: today's ring, the ball's stop, the grid gap.
- Values the tokens do not hold, marked `// css literal:`:
  - the head's padding 2 / 4 and gap 4, the grid gap 2;
  - a day at most 44 wide and at least 40 high, rings of 2;
  - the weekday padding 4, the 24 px slide;
  - the title at 1rem / 1.25rem, the weekdays at 0.625rem / 0.75rem.
