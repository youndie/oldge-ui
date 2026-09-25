---
id: B-24
title: "WindowBar and BottomNav"
status: done
priority: P1
size: M
stage: stage-2-components
blocked_by: [B-12, B-31]
---

# B-24 — WindowBar and BottomNav

The frame of every screen: WindowBar in the skin's bezel (top corners `radius-xl`, round window buttons, avatar lead, transparent-over-media mode) and BottomNav, its pair at the bottom (active icon in a chrome capsule that pops).

- **References**: `WindowBar_{Toxic,Media,Crystal}`, `BottomNav_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
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
  - `reference/design-system/components/WindowBar/README.md`, `reference/design-system/components/WindowBar/preview.html`
  - `reference/design-system/components/BottomNav/README.md`, `reference/design-system/components/BottomNav/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/`

## Findings

- Parity, `summary.txt` in the squash commit:
  - WindowBar is 1.98 / 2.31 / 2.19 %: the 17 px title a pixel low (B-46), and the orbs' edges.
  - BottomNav is 2.13 / 2.18 / 2.13 %. The current section matches Chrome. The others sit a pixel
    left, because four slots of 82.5 px are laid out on whole pixels (research §1.10, amended).
- **`cssBox` rounds only some corners now** (`CssCorners.Top` and `Bottom`). This is
  `border-radius: xl xl 0 0` for the WindowBar and its mirror for the BottomNav. The corners run
  through the border box, the padding box, the spread rings, the inset hole (now per corner, not the
  top-left radius for all) and the bezel texture's clip. Every earlier golden stays green with the
  default of `All`, which is the check that the shared painter did not move.
- The design's negative margins, the back orb's −8 and each action's −6, are a `layout` that places
  the child earlier and reports it that much narrower. A plain `offset` would have kept the space.
- `og-hop` is `oldgeHopIn` in `press/Pop.kt`, beside `og-pop` and `og-pop-soft`, with each keyframe
  interval eased on the spring.
- The READMEs' rules, in the API or `BarBehaviourTest`:
  - the title is a heading, and the orbs are named buttons (the back orb «Назад»);
  - a bar takes at most three actions, and a dock three to five sections (both `require`);
  - the current section is selected, and a tap reports its id;
  - a section is at least 56 dp;
  - the new capsule pops, and not under reduced motion.
  The nine-letter label is not enforced: a longer one is cut with an ellipsis, as
  `BottomNavStates` shows.
- Mutations through `scripts/mutate.py`: 9 of 9 killed by their aimed tests.
  - `nav-no-pop` first survived: the test saw the icon's hop move and called it the pop. It now
    samples the capsule's edge, which only the pop changes.
  - `hop-ignores-reduced` was equivalent and was dropped. Under reduced motion every duration is
    zero, so the guard it removed cannot be seen; the quirk is in the service document.
  - Goldens by name: the bar's top-only corners (`WindowBar`, `WindowBarStates`), the capsule's
    gloss (`BottomNav`, `BottomNavStates`) and the overlay's veil (`WindowBarStates`). The capsule
    mutation first failed only because it did not compile, and was redone as a transparent gloss.
- The States goldens cover what the previews do not show: an overlay bar over media, a square bar
  with an Avatar lead, a title cut with an ellipsis, and a square dock of five with a long label.
- Values the tokens do not hold, marked `// css literal:`:
  - bar 56 (overlay 64), brand icon 24, action overlap 6;
  - section 56 with 2 px sides and gap, capsule 44 × 28, icon 22, gloss cap 4 in.
