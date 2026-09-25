---
id: B-37
title: "Stress screens: sign-in and chat"
status: done
priority: P2
size: L
stage: stage-3-screens
blocked_by: [B-12, B-13, B-15, B-17, B-18, B-24, B-29, B-31, B-34, B-50, B-51, B-52]
---

# B-37 — Stress screens: sign-in and chat

Sign-in: password with reveal and an error, the switch to an SMS code (CodeInput and a resend timer), passkey and e-mail link. Chat: an avatar in the WindowBar, grouped bubbles, statuses, typing, Composer in place of the bottom navigation.

- **The decision and its reason.** The page previews are the design system's own proof that its
  components compose; rebuilt from this library's components with no screen-specific drawing,
  they are the same proof for the Compose side. A screen that needs a drawing the library does
  not offer is a finding: a missing component or parameter becomes a `B-<next free>` item, not
  a private composable in the screen.
- The screens live in `sample` (not in the library) with their fixtures in `sample`'s
  `desktopTest`, which therefore gets viddik too; the references for them are copied from
  B-03's output into `sample/src/desktopTest/snapshots/design/`, or B-03's renderer learns a
  per-module output — say which.
- **References**: `AuthScreen_{Toxic,Media,Crystal}`, `ChatScreen_{Toxic,Media,Crystal}`. The fixture size is the page's root size from the manifest.
- Interaction the preview demonstrates (the chat's send, the auth mode switch, the inbox swipe)
  is a Compose UI test, not a golden.

- AC: parity per skin read against the floor; `summary.txt` in the commit body; every
  component gap found here is a new item, listed in the commit body.
- AC: `./gradlew check` and `make check` green.
- Anchors:
  - `reference/design-system/components/AuthScreen/README.md`, `reference/design-system/components/AuthScreen/preview.html`
  - `reference/design-system/components/ChatScreen/README.md`, `reference/design-system/components/ChatScreen/preview.html`
  - `sample/src/commonMain/kotlin/io/github/youndie/oldge/sample/`

## Iteration 1 (2026-09-25)

The screens found a missing library surface before any screen code was written, and it became
[B-50](B-50-screen-body.md), now a blocker of this item and of B-38 to B-40.

- **The gap.** Every page preview stands on `.og-body` (the `.phone` rule is the same in all nine),
  and the library's painter for it, `skinBody()`, is internal.
- **Read for the next iteration:**
  - The references can be rendered straight into the sample module:
    `node scripts/design-references.mjs --only AuthScreen --out sample/src/desktopTest/snapshots/design`,
    and the same for ChatScreen. Compare each `sha256` with the one in oldge-core's manifest to
    show the copy is the same render.
  - `sample` has no viddik, KSP or `desktopTest` yet. It needs the plugin, the KSP ordering
    workaround from `oldge-core/build.gradle.kts` (youndie/viddik#44, until B-35), ui-test, and a
    copy of `PortableText`. The harness is test-only in oldge-core and cannot be shared.
  - The phone mock-up (the `radius-xl` clip, `shadow-window`, and the skin's body outside the
    corners) is harness. The reference's corner pixels are the page body's colour: Toxic
    (25, 41, 18) at (0, 0).
  - The chat's `og-composer--dock` rounds the Composer's bottom corners to `radius-xl`. That is
    the same curve the phone clips to, so the clip alone may be enough; measure it before
    calling it a gap.
  - Everything else both screens use exists: WindowBar, Segmented, TextField (reveal, error),
    Checkbox, Button, CodeInput, Readout, Divider, ChatBubble, TypingIndicator, Composer, Avatar.

## Iteration 2 (2026-09-25)

B-50 landed, and the branch `feat/b-37-stress-auth-and-chat` now holds verified groundwork:

- `sample` has viddik, KSP and a `desktopTest`, and `./gradlew :sample:build :sample:viddikVerify`
  is green. The #44 workaround is not needed there: with one target there is no
  `kspCommonMainKotlinMetadata`, and copying the block failed the build on the missing task.
- The six references were rendered with `--only '<Name>_*' --out
  sample/src/desktopTest/snapshots/design`. Each `sha256` equals the one in oldge-core's manifest,
  so they are the same render, not a second one. The glob matches the stem, so `--only AuthScreen`
  renders nothing.
- A draft of `AuthScreen`.

**Stopped on a second gap: `OldgeText` is internal.** The screens set a heading and paragraphs of
plain text, and an app cannot set text in the skin's type with the drawn-baseline placement of
B-46 and B-49. Filed as [B-51](B-51-public-text.md), which now blocks this item. The draft does not
compile until it lands.

**A deviation to record when this closes** (refuted in iteration 3, below). The preview's
`<h2 class="display">` has no rule in any vendored stylesheet. Chrome draws it with the browser's h2 default: 1.5em of the 15 px body in
DejaVu bold, on the inherited 20 px line. The design system's rule is the `display` style for a
screen title (Fira Sans 28/32), and the draft follows the rule. Its parity cost goes into research
as a deviation, as D11 did for the Composer.

## Iteration 3 (2026-09-25): done

- **The screens.** `AuthScreen` and `ChatScreen` are in `sample`, built from public components
  only, on `OldgeScreenBody` (B-50) with `OldgeText` (B-51). The fixtures pin text through the
  theme (B-52).
- **Parity, raw, against the floor of 0.72 %:**

  | Page | Toxic | Media | Crystal |
  |---|---|---|---|
  | AuthScreen | 7.10 % | 7.24 % | 7.14 % |
  | ChatScreen | 16.76 % | 18.11 % | 20.96 % |

  Both are dominated by one vertical shift each. A band-by-band alignment found where each one
  starts.
  - **AuthScreen: −2 px from the password field down.** The reveal field is 46 px in Chrome and 44
    here, which is filed as [B-53](B-53-reveal-field-height.md).
  - **ChatScreen: +14 px over the whole lane.** The docked Composer is taller in the preview than
    in the CSS. That is D11, the deviation already recorded: the preview's script double-counts
    the textarea's padding, and the library follows the CSS.
  - **With the shifted band moved back** (a diagnostic, not the reported number), the residual is
    2.44 / 2.57 / 2.50 % and 3.78 / 3.84 / 4.28 %. That is text-heavy components' own residual
    (Button 4.0, Chip 4.5 %) plus the Composer bar's own height.
- **Refuted: the `display` deviation of iteration 2.** The reference's title is Fira Sans at
  28/32, the same as ours. `scripts/tokens-css.mjs` compiles every type style into a class of its
  name (`.display { font: … }`), and the reference wrapper loads that `tokens.css`. The class has
  a rule; it is not in `bundle.css`, which is where iteration 2 looked. There is no deviation.
- **Interaction, as Compose UI tests** (`ScreenBehaviourTest`):
  - Sign-in switches from the password to the SMS code (with the disabled resend) and back.
  - A sent message joins my run and takes the run's status: «отправлено» on it, and the previous
    last one's «прочитано» gone.
- **Mutants:** 3 of 3 killed.
  - The mode never switching.
  - Every message the last of its run.
  - Send dropping the message.
- **Golden mutation:** the avatar stand-in 32 → 40 dp. `viddikVerify` named ChatScreen ×3
  (0.80–0.83 %).
- **Component gaps found here:** B-53 only. The Composer's height is D11, not a gap.
