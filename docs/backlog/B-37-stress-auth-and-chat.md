---
id: B-37
title: "Stress screens: sign-in and chat"
status: open
priority: P2
size: L
stage: stage-3-screens
blocked_by: [B-12, B-13, B-15, B-17, B-18, B-24, B-29, B-31, B-34, B-50, B-51]
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

Stopped on a second library gap: `OldgeText`, the only way the library sets plain text, is
internal. It is filed as [B-51](B-51-public-text.md), which now blocks this item and B-38 to B-40.

The groundwork waits on the branch `feat/b-37-stress-auth-and-chat`, whose findings say what it
holds: `sample` wired for parity, the references rendered into it with identical hashes, and a
draft of the sign-in screen.
