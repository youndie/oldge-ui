---
id: B-54
title: "ListItem's icon takes an element, as the design system's does"
status: done
priority: P2
size: S
stage: stage-2-components
blocked_by: [B-20]
---

# B-54 — ListItem's icon takes an element, as the design system's does

Found in B-38. The InboxScreen page puts an Avatar where a list row's icon goes:
`ListItem({ icon: h(B.Avatar, { size: 'sm', name: … }) })`. The design system's `ListItem` accepts
either kind (`bundle.js`, `ListItem`): a string is an `Icon` at 22 px, and anything else is placed
in `og-item__lead` as it is.

`OldgeListItem`'s `icon` is an `ImageVector?` only, so a row cannot lead with an Avatar, a
thumbnail or a Badge. The inbox cannot be built from the library.

- **The decision and its reason.** A composable `lead` slot next to `icon`, placed in the same
  `og-item__lead` box with the same gap, taking the element as the design system does. `icon`
  stays for the common case: an icon at the design system's 22 dp in the row's colour.
- **Rejected: turning `icon` into a composable.** Every existing caller would then have to spell
  `OldgeIcon(…, size = 22.dp)` and get the size right. The string form is the common one in the
  design system for that reason.
- **Rejected: an `OldgeAvatarListItem`.** The design system has no such component. Its ListItem
  takes any element.
- **Not covered:** ListItem's `trailing`, which is already a slot.

- AC: `OldgeListItem(title, lead = { OldgeAvatar(…) })` lays the Avatar out where the icon goes.
  The ListItem preview's rows and goldens are unchanged, and a new ListItem state golden shows a
  row with an Avatar lead.
- AC: `icon` and `lead` together is refused (`require`), since one row has one lead.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/containers/OldgeList.kt`.

## Findings (2026-09-25)

- **`OldgeListItem(lead = { … })`** places any element in the lead's box.
  - It is centred, `space-3` from the text, and the element reads the row's ink through
    `LocalOldgeContentColor` (`on-select` when the row is selected). That is `.og-item__lead`'s
    `color: var(--ink)` and its selected rule.
  - `icon` stays the common case.
  - The two together are refused with `require`.
- **The narrow list's indent is unchanged.** The value under the title is indented by
  `calc(22px + space-3)` in the CSS (`@container og-list (max-width: 20em)`), a fixed icon column,
  not the lead's width. So a row with a 32 dp Avatar keeps the design's 34 dp indent.
- **Goldens.** The new `ListItemStates / Avatar lead` shows InboxScreen's rows with an Avatar,
  subtitle and value, and looks as the page does. `viddikRecord` wrote only those three: no
  existing golden moved.
- **Behaviour** (`PanelListBehaviourTest`):
  - The lead sits 12 dp from the row's start and 12 dp before the title, in the row's ink, and in
    `on-select` when selected.
  - An icon and a lead together throw.
- **Mutants:** 3 of 3 killed (the lead dropped, its colour not the row's, both allowed), and
  B-20's fifteen all die.
- **An editing slip, caught before commit.** A script that appended the new tests sliced
  `PanelListBehaviourTest` at its last brace and dropped the file-level `HEAD_HEIGHT` after it. The
  compile error named it, and it was restored. The same kind of append in B-53 lost nothing
  (checked against its base).
