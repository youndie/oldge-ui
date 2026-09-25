---
id: B-54
title: "ListItem's icon takes an element, as the design system's does"
status: open
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
