---
id: B-22
title: "Accordion"
status: done
priority: P1
size: S
stage: stage-2-components
blocked_by: [B-20]
---

# B-22 — Accordion

A pill header with a round badge on the left and a chrome chevron button on the right, an inset panel below; open/closed controlled or not; the body unfolds on a spring, the badge turns once. Under reduced motion the chevron does not animate (research §1.2).

- **References**: `Accordion_{Toxic,Media,Crystal}` in `oldge-core/src/desktopTest/snapshots/design/`.
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
  - `reference/design-system/components/Accordion/README.md`, `reference/design-system/components/Accordion/preview.html`
  - `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/containers/`

## Findings

- Parity, `summary.txt` in the squash commit: Accordion is 0.78 / 0.73 / 0.95 %, at the floor. What
  is left is glyph edges and the chevron's antialiasing; no round was needed.
- **The body's open top is not observable, so it is drawn closed.** The CSS frame has
  `border-top: 0`, and a `cssBox(openTop)` was written for it. Its golden mutation (closing the
  frame) changed no pixel: the body's top 6 px, border and inset shadow included, lie under the
  header at rest and while unfolding. The option was taken out of the shared painter again rather
  than kept unverified. The body keeps CSS's content position, 12 px below its top.
- The header paints over the body (`zIndex`), as the CSS header does by being positioned. The
  body's −6 px top margin is a `layout` that places it 6 px up and reports it 6 px shorter.
- The badge follows CSS's cascade: pressed wins over open, as `:active` does over `[aria-expanded]`
  (same specificity, later rule). So a press of an open header turns it from 360° to −20° and back.
- The accordion comes in two forms: uncontrolled with `defaultOpen` (open unless told), which keeps
  its state in `rememberSaveable`; and controlled with `open` and `onToggle`. The header is a
  button and offers `expand` or `collapse` to accessibility services.
- The README's rules, in the API or `AccordionBehaviourTest`:
  - open by default, and the header folds it;
  - a closed one hides its body;
  - a controlled one only asks;
  - expand and collapse are offered by state;
  - the chevron turns with motion and not under reduced motion (research §1.2).
- Mutations through `scripts/mutate.py`: 7 of 7 killed by their aimed tests.
  - Two were rewritten before their first run so they would compile: a class the file does not
    import, and a constant condition under `-Werror`.
  - `acc-chevron-still` first survived: the sampled box took the header's rounded bottom corner,
    where the unfolding body shows. It now samples only the chevron's rows.
  - Goldens by name: the body's 6 px tuck and the header's gloss (`Accordion`, `AccordionStates`).
- `AccordionStates` covers a pressed header (the badge squashed and turned, the shadow sunken), a
  title cut with an ellipsis, and a plain-text body.
- Values the tokens do not hold, marked `// css literal:`:
  - the header 44 padded 4, the badge 34 with its 2 px rim, icon 16;
  - the chevron 26, 4 from the end;
  - the body tucked 6 and 10 in, padded 12 4 4, unfolding from 8 up.
