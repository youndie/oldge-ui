---
id: B-55
title: "List sections stick inside a pull-to-refresh, as the design system's do"
status: open
priority: P2
size: M
stage: stage-2-components
blocked_by: [B-20, B-33]
---

# B-55 — List sections stick inside a pull-to-refresh, as the design system's do

Found in B-38. The InboxScreen page puts three `ListSection`s straight into a `PullRefresh`.
PullRefresh's `og-pull__scroll` is the scroll container, and each section's header is
`position: sticky; top: 0` in it (`bundle.css`, `.og-lsec__head`). So the headers stick while the
list refreshes and while it scrolls.

Here the two cannot be combined:

- `oldgeListSections` is a `LazyListScope` extension, whose sticky headers need a `LazyColumn`
  (B-20).
- `OldgePullRefresh` scrolls its own content, a plain column (B-33), and says "the content is
  scrolled by the component, so it goes in as it is, not in a scroll of its own".

A screen gets pull-to-refresh or sticky sections, not both. The inbox, the design system's own
stress page for the combination, cannot be built.

- **The decision and its reason.** To be settled in the item, between:
  - **(a)** PullRefresh with a lazy form: its content a `LazyListScope` block, in its own
    `LazyColumn`. The sections go in unchanged, and a long list stays lazy.
  - **(b)** Sections for a plain scrolling column, sticky by offset.

  (a) is preferred: an inbox is exactly the long list for which laziness matters, and
  `oldgeListSections`' stickiness is already measured (B-20).
- **Not covered:** changing what either component does on its own. Their existing behaviour
  tests stay green.

- AC: a behaviour test pulls the lazy form past its threshold and gets `onRefresh`. It also
  scrolls it and finds a section header held at the top while its section is under it.
  Mutants for both.
- AC: PullRefresh's and ListSection's goldens are unchanged.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/OldgePullRefresh.kt`,
  `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/containers/OldgeListSection.kt`.
