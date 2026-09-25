---
id: B-38
title: "Stress screens: the long list and full-screen media"
status: done
priority: P2
size: L
stage: stage-3-screens
blocked_by: [B-20, B-23, B-24, B-26, B-30, B-31, B-32, B-33, B-50, B-51, B-52, B-54, B-55]
---

# B-38 — Stress screens: the long list and full-screen media

Inbox: sticky sections, swipe rows, pull to refresh, a long scrolling list — the content between the bar and the navigation must scroll (`min-height: 0` in the CSS), or at a large font the navigation leaves the screen. Media: no frame, a transparent WindowBar over the image, PageDots.

- **The decision and its reason.** The page previews are the design system's own proof that its
  components compose; rebuilt from this library's components with no screen-specific drawing,
  they are the same proof for the Compose side. A screen that needs a drawing the library does
  not offer is a finding: a missing component or parameter becomes a `B-<next free>` item, not
  a private composable in the screen.
- The screens live in `sample` (not in the library) with their fixtures in `sample`'s
  `desktopTest`, which therefore gets viddik too; the references for them are copied from
  B-03's output into `sample/src/desktopTest/snapshots/design/`, or B-03's renderer learns a
  per-module output — say which.
- **References**: `InboxScreen_{Toxic,Media,Crystal}`, `MediaScreen_{Toxic,Media,Crystal}`. The fixture size is the page's root size from the manifest.
- Interaction the preview demonstrates (the chat's send, the auth mode switch, the inbox swipe)
  is a Compose UI test, not a golden.

- AC: parity per skin read against the floor; `summary.txt` in the commit body; every
  component gap found here is a new item, listed in the commit body.
- AC: `./gradlew check` and `make check` green.
- Anchors:
  - `reference/design-system/components/InboxScreen/README.md`, `reference/design-system/components/InboxScreen/preview.html`
  - `reference/design-system/components/MediaScreen/README.md`, `reference/design-system/components/MediaScreen/preview.html`
  - `sample/src/commonMain/kotlin/io/github/youndie/oldge/sample/`

## Iteration 1 (2026-09-25)

Both previews were surveyed against the library's public API before any screen code was written.

- **Inbox finds two component gaps**, filed as blockers of this item:
  - [B-54](B-54-list-item-lead-slot.md): ListItem's icon is an `ImageVector` only, and the rows
    lead with an Avatar.
  - [B-55](B-55-sticky-sections-in-pull-refresh.md): list sections stick only in a `LazyColumn`,
    and PullRefresh scrolls a plain column. The page puts the sections inside the refresh.
- **Media needs nothing new.**
  - WindowBar `overlay`, PageDots `onDark` and the three OrbButtons exist.
  - The photo stand-in, the bottom scrim and the caption are the page's own `<style>`
    (`.media`, `.media__bottom`, `.media__cap`), not a design-system component. They are drawn in
    `sample` as the page's content, from theme colours, with `css literal` markers.
  - The like orb's `aria-pressed` is the page's attribute. It is a `semantics` modifier on the
    orb, not an orb parameter.
- **Inbox's PullRefresh is refreshing in the reference.** The preview starts with `busy` true and
  a 1.6 s timeout, and the render is photographed about 100 ms in. The fixture must show the disc
  spinning under reduced motion, which is still.
- **None of the later pages (B-39, B-40) use** a ListItem with an element icon, a PullRefresh or
  a ListSection. The two gaps block only this item.

## Iteration 2 (2026-09-25): done

- **The screens.** `InboxScreen` and `MediaScreen` are in `sample`, from public components only.
  - The inbox's body is `OldgeLazyPullRefresh { oldgeListSections { … } }` (B-55). Its rows lead
    with an Avatar (B-54).
  - The media page's photograph, scrim and caption are the page's own `<style>`, drawn in
    `sample` from theme colours with `css literal` markers, as iteration 1 decided.
- **References** were rendered into `sample` with `--out`. Each `sha256` equals oldge-core's.
- **Parity, raw, against the floor of 0.72 %:**

  | Page | Toxic | Media | Crystal |
  |---|---|---|---|
  | InboxScreen | 1.86 % | 2.00 % | 2.26 % |
  | MediaScreen | 1.19 % | 1.29 % | 1.21 % |

  The inbox matches the reference as rendered: the refresh disc spinning above «Сегодня» (the
  page opens refreshing for 1.6 s, and both sides are photographed inside it), and the second
  row swiped open onto «Архив» and «Удалить». What is left is the text-heavy rows' own residual.
- **Component gaps found here**, both first measured against Chrome on this page, since no preview
  shows either:
  - [B-57](B-57-overlay-bar-height.md): the overlay WindowBar is 72 px in Chrome and 64 here
    (`content-box`), so its content sits 4 px high.
  - [B-58](B-58-page-dots-on-dark.md): PageDots on dark. The CSS cascade gives the current dot
    the dark grey with the accent glow, and here it is filled with the accent.
- **Interaction, as Compose UI tests** (`ScreenBehaviourTest`):
  - The inbox opens refreshing and stops after its 1.6 s.
  - «Непрочитанные» shows the empty state, and «Архив» the error with «Повторить».
  - The media page turns on a swipe of more than 40 dp and not on less, hides its interface on a
    tap and shows it again, and the heart toggles its label.
- **Mutants:** 5 of 5 killed.
  - The refresh never ending.
  - The unread filter dead.
  - The tap doing nothing.
  - The swipe threshold gone.
  - The heart stuck.
- **Golden mutation:** the light's centre moved from 30 % to 50 %. `viddikVerify` named
  MediaScreen ×3.
