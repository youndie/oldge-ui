---
id: B-31
title: "Badge and Avatar"
status: done
priority: P1
size: S
stage: stage-2-components
blocked_by: [B-08, B-09, B-10, B-11]
---

# B-31 — Badge and Avatar

Badge: neutral silver, accent, count, and success/warning/danger as sunken plates with coloured text and an icon. Avatar: a round face in a chrome ring, initials or image or icon, status lamp (online pulses) — the status doubles as a word in the accessible name.

- **References**: `Badge_{Toxic,Media,Crystal}`, `Avatar_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
- **Read first**: the component's README (the rules), its `preview.html` (the demo to port), and
  its `.og-` block in `reference/design-system/components/bundle.css` (the exact numbers). The skill is `design-to-compose`
  from Step 2, since the references already exist (B-03).

- AC: each component is a public composable in `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/` taking every value from the theme
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
  - `reference/design-system/components/Badge/README.md`, `reference/design-system/components/Badge/preview.html`
  - `reference/design-system/components/Avatar/README.md`, `reference/design-system/components/Avatar/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/`

## Findings

- Parity, `summary.txt` in the squash commit:
  - Avatar is 2.70 / 2.92 / 2.78 %, what is left being glyph edges of the initials and the rim's antialiasing.
  - Badge is 5.77 / 5.89 / 5.92 %. The cause is research §1.10's rule and nothing new, measured in
    Chrome: all six badge widths are fractional (78.39, 56.03, 28.05, 77.75, 109.20, 86.38 px) and each
    of ours is its ceiling. So each badge is up to a pixel wider, and the first row drifts +1, +2, +3.
    The `_DIFF` is red on the glyphs and the vertical edges of the drifted badges only.
- **The count's `min-width: 22px` does nothing at the default size.** A digit of the lcd face at
  13 px already fills the 22 px. Its first mutant survived a test at the default size. The test now
  holds it with the system font at 0.75, where the rule is what keeps the circle.
- The initials are sized in px from the avatar (`calc(var(--s) * .36)`), so they are converted from
  dp and do not follow the font scale. A test holds that at a font scale of 2.
- The online lamp's `og-led` is eased `ease-in-out` on each half of its ping-pong. It runs on
  `loopPhase`, so reduced motion stops it and `AvatarStates` freezes it at the peak.
- The count's `og-bump` runs when the number changes, not when the badge first appears. In CSS it
  is an animation on a keyed element, which a changed number re-creates.
- The READMEs' rules, in the API or `BadgeAvatarBehaviourTest`:
  - the three states carry an icon beside the word, and a count takes none;
  - the avatar is one image named with its status word, and the initials are not read separately;
  - the initials are the first letters of the first two words.
- Mutations through `scripts/mutate.py`: 9 of 9 killed by their aimed tests. Goldens by name: the
  sunken plate's shadow (`Badge`) and the rim's 160° gradient (`Avatar`).
- `AvatarStates` covers what the preview does not show: a picture (`src`) and the lamp at the peak of
  its pulse.
- Values the tokens do not hold, marked `// css literal:`:
  - Badge: min height 22, gap 4, count padding 6, icon 14.
  - Avatar:
    - sizes 32/2, 44/2, 64/3 and icon 20;
    - the lamp at least 10, its ring 2, set 1 out, its glow 6 spreading 2.
