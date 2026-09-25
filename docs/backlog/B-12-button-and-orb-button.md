---
id: B-12
title: "Button and OrbButton"
status: done
priority: P1
size: M
stage: stage-2-components
blocked_by: [B-08, B-09, B-10, B-11]
---

# B-12 — Button and OrbButton

The two buttons everything else borrows from: `primary` (accent in its `accent-rim`, dark `on-accent` label — never white), `secondary` (chrome), `plain`; sizes `sm` 36, `md` 44, `lg` 52, `block`; the diagonal glint that runs across a primary on press and fades at the right edge. OrbButton is the round glossy button in a chrome ring: visible 30 px, hit area 44 px.

- **References**: `Button_{Toxic,Media,Crystal}`, `OrbButton_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
- **Read first**: the component's README (the rules), its `preview.html` (the demo to port), and
  its `.og-` block in `reference/design-system/components/bundle.css` (the exact numbers). The skill is `design-to-compose`
  from Step 2, since the references already exist (B-03).

- AC: each component is a public composable in `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/` taking every value from the theme
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
  - `reference/design-system/components/Button/README.md`, `reference/design-system/components/Button/preview.html`
  - `reference/design-system/components/OrbButton/README.md`, `reference/design-system/components/OrbButton/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/`

## Findings (2026-09-25)

- Parity (`viddikDesignParity --component "*Button*"`, ±16, floor 0.72 %): OrbButton 0.36 / 0.57 /
  0.82 % (Toxic / Media / Crystal), at the floor. Button 4.36 / 4.41 / 4.43 %, from 6.71–6.80 %
  after one cause: the content box did not start inside the 1 px border (`box-sizing:
  border-box`), so every button was 2 px narrow. What is left is text laid out on whole pixels
  (a label's width rounded up, the label at a whole x), which drifts a row of labelled buttons by
  up to a pixel per button; the `_DIFF` is red only on glyph edges and on the drifted row's box
  edges. Written into research §1.10 as "As built, B-12", with why it was not fixed here.
- **The pressed goldens photographed the rest state** until the fixture waited a frame before
  pressing: viddik's unconfined dispatcher runs the fixture's effect before the control subscribes
  to its interactions, and the flow keeps no replay. Found because two mutations of the pressed
  look survived; after the fix both fail (ButtonStates 15–16 %, OrbButtonStates 3.9–4.4 %). A quirk
  in `docs/services/oldge-core.md`, since every later state golden can fall into it.
- The READMEs' behaviour rules, in the API or `ButtonBehaviourTest`: an icon-only button takes a
  non-null `contentDescription` and an orb a non-null `label` (the API); sizes 36 / 44 / 52; a 44 px
  target for an icon button, a small orb and a short label; a disabled button or orb ignores taps.
- Mutations, each failing, restored: the orb's hit area as its drawn size, the disabled button
  clickable, the minimum width dropped (`ButtonBehaviourTest`); the 1 px press offset, the orb's
  pressed core shadow, the border in the padding, the empty press (goldens).
- Values the tokens do not hold, marked `// css literal:` with their `bundle.css` rule: icon 20 / 24
  in a button, orb 30 / 48 / 60 with rims 2 / 3 / 4, the pressed core's `0 2px 4px`.
