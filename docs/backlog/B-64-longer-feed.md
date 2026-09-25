---
id: B-64
title: "The sample app's feed: more posts, so that it scrolls"
status: done
priority: P3
size: XS
stage: stage-3-screens
blocked_by: []
---

# B-64 — The sample app's feed: more posts, so that it scrolls

Asked by the owner after looking at the desktop app: the feed needs more items, and scrolling.
FeedScreen is the design system's page with two cards. Its content column already scrolls, but
two cards leave nothing to scroll.

- **The decision.** `FeedScreen(more = true)` adds a run of posts after the design's two: media
  cards and cards under a bar, like the design's. It also leaves room at the end, so the last post
  scrolls clear of the Snackbar and the Fab. The app turns `more` on. The design's fixture keeps
  `more = false`. *Rejected:* adding the posts to the page itself. The reference shows empty
  ground below the two cards, so the added cards would show in the parity frame, and the page
  would stop being the design's.
- AC: in the app, the feed's last post is out of view and is reached by scrolling. The FeedScreen
  goldens and parity are unchanged. With a mutant.
- Anchors: `sample/src/commonMain/kotlin/io/github/youndie/oldge/sample/ShowcaseScreens.kt`,
  `sample/src/commonMain/kotlin/io/github/youndie/oldge/sample/OldgeSampleApp.kt`.

## Findings (2026-09-25)

- `FeedScreen(more = true)` adds eight posts after the design's two. They alternate between media
  cards and cards under a bar, and end with 80 dp of room: the Snackbar's top is about 136 dp up,
  and the nav is 64. The app passes `more = true`, and every fixture calls `FeedScreen()` as
  before.
- **Looked at:** the app's feed scrolled to the end, captured headless. The posts are the
  library's cards, in the design's two kinds.
- **Test:** `SampleAppBehaviourTest.the_apps_feed_scrolls_to_posts_beyond_the_designs_two`. In the
  app, the last post is not displayed, then scrolled to, then displayed. The design's FeedScreen
  has no such post.
- **Mutants:** 2 of 2 killed: the app's feed back to the design's two posts, and the design's page
  given the posts too.
