---
id: B-65
title: "The sample app's feed refreshes by pull, and by a button"
status: done
priority: P3
size: XS
stage: stage-3-screens
blocked_by: [B-64]
---

# B-65 — The sample app's feed refreshes by pull, and by a button

Asked by the owner after B-64: the feed should have pull-to-refresh too.

- **The decision.** With `more = true` (the app's feed), the page's scrolling column goes inside
  `OldgePullRefresh`, which scrolls it. A refresh runs for a moment and puts a new post at the top.
  The PullRefresh README says "offer the refresh as a button or a menu item too", since the gesture
  cannot be made from a keyboard, so the feed's bar gets an «Обновить» action that does the same.
  The design's page, with `more = false`, keeps its plain scrolling column, and its parity does not
  move.
- AC: in the app, a pull down past the threshold refreshes, with the disc heard as «Обновляю», and
  a new post appears at the top. The bar's «Обновить» does the same. FeedScreen's goldens and
  parity are unchanged. With a mutant.
- Anchors: `sample/src/commonMain/kotlin/io/github/youndie/oldge/sample/ShowcaseScreens.kt`.

## Findings (2026-09-25)

- `Page` takes an optional `Refresh`. Without one it lays out the same scrolling column as
  before, so the design's pages do not change. With one, the column goes into `OldgePullRefresh`
  without a scroll of its own, since the PullRefresh scrolls it. The app's feed refreshes for
  1.2 s, then puts «Свежий пост № n» at the top. The bar gets «Обновить» before «События».
- **Test:** `SampleAppBehaviourTest.the_apps_feed_refreshes_by_a_pull_and_by_the_bars_button`.
  - A 400 px pull from the top of the first card shows the disc, heard as «Обновляю», and then
    post № 1.
  - The bar's «Обновить» then brings post № 2.
  - The design's page has no «Обновить».
- **Mutants:** 3 of 3 killed: no PullRefresh at all, a pull that does not refresh, and a bar button
  that does nothing.
