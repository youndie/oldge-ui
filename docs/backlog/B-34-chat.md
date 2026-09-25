---
id: B-34
title: "ChatBubble, TypingIndicator and Composer"
status: done
priority: P1
size: M
stage: stage-2-components
blocked_by: [B-17, B-31]
---

# B-34 — ChatBubble, TypingIndicator and Composer

Own messages a glossy pill in the skin's colour on the right, theirs an inset panel on the left with an avatar; grouping (time, status, avatar and tail on the last of a run only); long words wrap, a bubble is at most 86 % of the lane. TypingIndicator's three hopping dots. Composer: the message field in the frame.

- **References**: `ChatBubble_{Toxic,Media,Crystal}`, `TypingIndicator_{Toxic,Media,Crystal}`, `Composer_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
- **Read first**: the component's README (the rules), its `preview.html` (the demo to port), and
  its `.og-` block in `reference/design-system/components/bundle.css` (the exact numbers). The skill is `design-to-compose`
  from Step 2, since the references already exist (B-03).

- AC: each component is a public composable in `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/chat/` taking every value from the theme
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
  - `reference/design-system/components/ChatBubble/README.md`, `reference/design-system/components/ChatBubble/preview.html`
  - `reference/design-system/components/TypingIndicator/README.md`, `reference/design-system/components/TypingIndicator/preview.html`
  - `reference/design-system/components/Composer/README.md`, `reference/design-system/components/Composer/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/chat/`

## Findings

- Parity, `summary.txt` in the squash commit:
  - ChatBubble is 1.63 / 1.73 / 1.92 %.
  - TypingIndicator is 0.54 / 0.60 / 0.63 %.
  - **Composer is 18.08 / 29.65 / 29.37 %, a deviation (research D11).** The design's script sizes
    the textarea with its padding counted twice: an empty field is 56 px, not 38 (measured in
    Chrome). The component draws the field the CSS and the README describe. Against the page with
    that height corrected, it differs on 3.7 % of pixels. The reference is left as it is; the
    script's fix is the design system's.
- **`cssBox` takes a radius per corner** (`CssCornerRadii`): the bubble is `radius-lg` but 4 px at
  its tail's corner. `CssCorners` is now a way of building those radii, and every earlier golden
  stayed green through the change. A spread ring round a square corner now keeps it square, as CSS
  does; no golden had depended on the other way.
- `og-balloon-in` is now `oldgeInflateIn` in `press/Pop.kt`, which the bubble uses from its tail's
  corner. The Balloon and the Tooltip keep their own copies, since they were not the item's to
  touch; `oldgeInflateIn` is where they should come to.
- Grouping is the caller's, as the README puts it («`tail` false on all but the last»): the time,
  status, avatar and tail are passed on the last of a run. A tail-less message of theirs without an
  avatar sits at the lane's start, as bundle.js has it.
- The typing dots run `og-typing` on the loop phase, each 0.16 s behind the one before, and stand
  still, fully opaque, under reduced motion, the base style the design's 1 ms run ends on.
- The Composer's Enter sends and Shift+Enter breaks the line, both on the field's key events. The
  send orb is a chrome, disabled one on a blank field and a lime one that pops in (`og-pop-soft`)
  once there is text. A blank field sends nothing, however it is asked.
- The READMEs' rules, in the API or `ChatBehaviourTest`:
  - a bubble is at most 86 % of the lane, mine at its end and theirs at its start;
  - my status is said, and theirs has none;
  - the typing indicator says who types, and its dots hop, but not under reduced motion;
  - send is off on a blank field, sends, and clears it;
  - Enter sends and Shift+Enter breaks the line;
  - the attach orb is there unless it is turned off;
  - the field grows to 120 px and no further.
- Mutations through `scripts/mutate.py`: 10 of 10 killed by their aimed tests.
  - `composer-blank` first survived: the disabled orb kept the test from reaching `send` at all. The
    test now presses Enter on a blank field.
  - `composer-attach` first did not compile (a smart cast), and was rewritten.
  - Goldens by name: the tail corner, the dots' colour and the field's radius.
- The States goldens:
  - a group chat's author, a link too long to fit, their tail-less run;
  - the dots frozen mid-hop;
  - a composer with text (the send orb lit), and three lines without the attach orb.
- Values the tokens do not hold, marked `// css literal:`:
  - tail 4, gaps 2, ticks 14 overlapping by 9;
  - dots 8 with radius 2, 5 apart, padded 6 2, over 1.1 s;
  - composer edges 4 and 6, field radius 20, height 40, margin 2, padding 9, text 1rem, at most 120.
