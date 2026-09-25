---
id: B-18
title: "Select and CodeInput"
status: done
priority: P1
size: M
stage: stage-2-components
blocked_by: [B-17]
---

# B-18 — Select and CodeInput

Select: a chrome frame with the value and a chevron, opening a Menu-like list (reuse B-28's popup if it is merged, otherwise build the minimum and note it). CodeInput: one-time code on LCD cells, digits glowing, one hidden text field behind the cells.

- **References**: `Select_{Toxic,Media,Crystal}`, `CodeInput_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
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
  - `reference/design-system/components/Select/README.md`, `reference/design-system/components/Select/preview.html`
  - `reference/design-system/components/CodeInput/README.md`, `reference/design-system/components/CodeInput/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/`

## Findings (2026-09-25)

- Parity (±16, floor 0.72 %):
  - Select: 2.40 / 2.41 / 2.51 % (Toxic / Media / Crystal), on the first run. The frame, the
    arrow and the value are aligned; about 85 % of the diff is the label, the 13 px style
    (B-46).
  - CodeInput: 1.97 / 1.93 / 1.88 %, from 3.30 / 3.25 / 3.04 % after one cause, which was in the
    shared `OldgeScriptText`. It sized its line by Compose's strut, taller than CSS's line box for
    a face taller than its line, so the 32 px digits sat 2 px high in their cells. The line box is
    now `line-height`. The measurements, with the Chrome probe and the components it did not move,
    are in research §1.10 ("B-18"). What is left is the two labels (B-46) and the glowing digits'
    edges; the glow's energy matches Chrome's to 0.1 %.
- Both a code cell and the Select arrow are `span`s, content-box, measured so in the reference:
  a cell is 48 × 58 with its border (46 × 56 inside), and the arrow is 36 wide (34 inside) and 34
  tall, stretched.
- Select opens a **minimal stand-in list** under the frame, as the item allowed, because Menu
  (B-28) is not built. The list takes the frame's width and marks the chosen option in
  `accent-ink`. B-28 carries a note to replace it. No golden shows the open list; the behaviour
  test opens it and picks.
- CodeInput is one real `BasicTextField` under the cells, invisible, as its README requires:
  - numeric, marked `ContentType.SmsOtpCode` for autofill;
  - non-digits dropped, input capped at `length`;
  - `onComplete` once, when the last digit arrives.
- The field's label and help line are one internal pair (`Field.kt`), used by TextField, Select and
  CodeInput. The TextField goldens are unchanged by the move.
- The READMEs' rules, in the API or `SelectCodeBehaviourTest`:
  - Select is a named drop-down list that reads its choice and changes it;
  - CodeInput is one field for a one-time code, digits only, and completes;
  - errors are colour, icon and word (`FieldHelp`) and the error semantics;
  - «2–4 options want Segmented or RadioGroup» is a KDoc line.
- Mutations, each failing, restored:
  - in `SelectCodeBehaviourTest`: the digit filter, the completion condition, the OTP content
    type, the list role, the state description;
  - in the goldens: the script-text line box (6), the cell's content-box width, the arrow's
    content-box width.
- Values the tokens do not hold, marked `// css literal:`:
  - Select: icon 18, chevron 16, arrow 34 with margin 4, radius 3, dip 2, highlight inset 1, the
    list's gap 4;
  - CodeInput: gap 6, cell 48 × 56 + border, digits 2rem, dash 1.25rem, focus ring 2, cursor 3
    at 9 from the bottom.
