---
id: B-32
title: "Banner, Snackbar and EmptyState"
status: done
priority: P1
size: M
stage: stage-2-components
blocked_by: [B-12]
---

# B-32 — Banner, Snackbar and EmptyState

Banner: the yellow XP information strip, actions chrome-only (skin colours do not read on yellow). Snackbar: a plate in the frame, sliding in. EmptyState: the big glossy orb for empty, error and offline.

- **References**: `Banner_{Toxic,Media,Crystal}`, `Snackbar_{Toxic,Media,Crystal}`, `EmptyState_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
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
  - `reference/design-system/components/Banner/README.md`, `reference/design-system/components/Banner/preview.html`
  - `reference/design-system/components/Snackbar/README.md`, `reference/design-system/components/Snackbar/preview.html`
  - `reference/design-system/components/EmptyState/README.md`, `reference/design-system/components/EmptyState/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/`

## Findings

- Parity, `summary.txt` in the squash commit:
  - Banner is 4.27 / 4.29 / 4.38 %. The 13 px body lines and the buttons' labels are a pixel low
    (B-46), and the end-aligned buttons are 2 px left of Chrome's (research §1.10's whole-pixel
    widths). Title and icon match.
  - Snackbar is 2.46 / 2.56 / 2.53 %.
  - EmptyState is 1.80 / 1.84 / 1.91 %.
- **Banner's first run had the body text invisible.** The fixture's `DemoText` forced `ink`, light
  on the Media skin and unreadable on the yellow, where a preview's text inherits `on-balloon`. It
  now inherits the colour its container gives. Every container before this one gave `ink`, and no
  other golden moved.
- **The Banner draws its own actions**, from `OldgeBannerAction(label, onClick)`: the README's rule
  that they are chrome only (the skin's colours do not read on the yellow) is then the API's.
- **EmptyState's `max-width: 30ch` is 30 widths of «0», 0.572 em** in the bundled ui face, which
  the references render with too. It was read from the font file, after a draft that assumed ½ em.
- The EmptyState orb is `OrbMaterial` (the same 160° chrome rim and 0.35 ring), which gained
  `highlightTop`: the EmptyState's gloss cap starts 4 % down, the orbs' 3 %. The golden mutation
  moving it back to 3 % fails both EmptyState goldens, so the parameter is visible and stays.
- An error needs an action (`require`), as the README says. Error and offline are announced
  assertively (`role="alert"`), and a plain empty state is not.
- Over a screen the Snackbar fills its parent, 16 dp above the bottom and the system inset, with no
  scrim: the screen under it stays in reach. `og-snack-in`'s four keys, each interval eased on the
  spring, are kept, including the sway.
- The READMEs' rules, in the API or `NoticeBehaviourTest`:
  - a banner is a polite status, with its actions as buttons;
  - closed notices draw nothing;
  - a snackbar goes by itself after its duration, offers its action and its orb, and leaves the
    screen in reach, 16 dp from the bottom;
  - an error needs an action;
  - errors and offline are announced, and an empty state is not;
  - the sentence is at most 30ch.
- Mutations through `scripts/mutate.py`: 8 of 8 killed by their aimed tests. Goldens by name:
  - the banner's mark colour and the snackbar's window shadow;
  - the orb's highlight top, and the error core's sunken inset. The inset needed two retries: a
    `sed` that did not match the reformatted line, then an `emptyList()` the compiler could not
    type.
- The States goldens:
  - the Banner's full-width band, with a title and action and bare;
  - a Snackbar over a screen with a long message;
  - the offline EmptyState, and a bare one.
- Values the tokens do not hold, marked `// css literal:`:
  - banner icon 20, lines 18, title 14;
  - snackbar 52 high, 4 edges, message 14 / 18;
  - orb 88 with a 4 rim, icon 36.
