---
id: B-21
title: "Card and ActionTile"
status: done
priority: P1
size: M
stage: stage-2-components
blocked_by: [B-12]
---

# B-21 — Card and ActionTile

Card: raised or sunken inset surface, optional bar header in the skin's frame, media zone (an icon on the frame gradient when there is no image), actions, whole-card press. ActionTile: the launcher-style action — round glossy icon, bold label, `row` or `stack`, tone chrome/accent/bezel.

- **References**: `Card_{Toxic,Media,Crystal}`, `ActionTile_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
- **Read first**: the component's README (the rules), its `preview.html` (the demo to port), and
  its `.og-` block in `reference/design-system/components/bundle.css` (the exact numbers). The skill is `design-to-compose`
  from Step 2, since the references already exist (B-03).

- AC: each component is a public composable in `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/containers/` taking every value from the theme
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
  - `reference/design-system/components/Card/README.md`, `reference/design-system/components/Card/preview.html`
  - `reference/design-system/components/ActionTile/README.md`, `reference/design-system/components/ActionTile/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/containers/`

## Findings (2026-09-25)

- Parity (±16, floor 0.72 %):
  - ActionTile: 0.94 / 0.98 / 0.99 % (Toxic / Media / Crystal), on the first run.
  - Card: 2.48 / 2.55 / 2.60 %, from 4.94 / 5.14 / 7.15 % after one cause. The media zone's glow,
    an ellipse centred on the zone's bottom edge, was not clipped to the zone and spilled into the
    card body; a CSS background is painted inside its box. What is left:
    - the titles, the `title` face, a pixel low (B-46, which now records that it is more than the
      13 px `ui` style);
    - the action buttons' label widths rounded up (research §1.10, B-12);
    - glyph edges.
- **A contradiction in the design system:** Card's preview makes its first card pressable *and*
  gives it two action buttons, which its README forbids ("two pressable layers confuse"). It is
  also invalid HTML, buttons nested in a button. `OldgeCard` enforces the README (`require`), and
  the fixture ports the card without the press, which draws the same at rest.
- The orb's highlight is now a shared `drawGlossCap(side, top, height)`; the ActionTile badge's cap
  is 12 % / 3 % / 44 %. OrbButton and the orb probe are unchanged by the move.
- The media icon's `drop-shadow(0 3px 3px rgba(0,0,0,.4))` is the glyph again, dark, 3 px down,
  under a `BlurEffect` of radius √3, since Compose's radius is `(σ − 0.5) / 0.577` and CSS's 3 px blur
  is σ 1.5. `Card`'s media golden holds it.
- The READMEs' rules, in the API or `CardTileBehaviourTest`:
  - a card pressed as a whole is one button and takes no actions;
  - a card without a press is not clickable;
  - an image is named by its description;
  - an ActionTile is a named button at least 64 dp high;
  - «a two-column title is up to 12 characters a line» is a KDoc line.

  The 64 dp minimum is never the binding size (badge and padding make 70), so no mutant is claimed
  for it.
- Mutations, through `scripts/mutate.py` (B-48):
  - `card-no-actions`, `card-role`, `image-name`, `tile-role`: 4 of 4 killed by their aimed tests;
  - goldens, by name: the glow clip and the glow stop (Card, CardStates), the pressed tile's panel
    (CardStates), the badge cap (ActionTile, CardStates).
- Values the tokens do not hold, marked `// css literal:`:
  - Card: bar 40 high, bar icon 18, bar text 0.9375rem / 1.25rem, media 132 high, media icon 56,
    the icon shadow 3 / 1.73, body gap 2;
  - ActionTile: min height 64, badge 52, icon 28, text gap 2.
