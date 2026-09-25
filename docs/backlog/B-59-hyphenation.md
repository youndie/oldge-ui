---
id: B-59
title: "Hyphenate where the CSS says hyphens: auto"
status: open
priority: P3
size: S
stage: stage-2-components
blocked_by: [B-20]
---

# B-59 — Hyphenate where the CSS says `hyphens: auto`

Found in B-39, on EdgeNarrow. Chrome breaks the German list title as «Personalisierte Wer- / bung
anzeigen», and here it breaks between words: «Personalisierte / Werbung anzeigen».

`bundle.css` sets `hyphens: auto` on four texts:

- `.og-item__title` and `.og-item__value` (ListItem);
- `.og-tile__text` (ActionTile);
- `.og-seg__opt` (Segmented).

Chrome hyphenates by the element's `lang`, which the page sets to `de`. The library sets no
hyphenation anywhere.

- **First, what the platform can do.** Compose's `TextStyle(hyphens = Hyphens.Auto)` with a
  `LocaleList` is honoured on Android. Whether the desktop's Skia paragraph hyphenates at all is
  not known. A probe in B-39 was inconclusive, because at its width neither engine needed a
  hyphen. The item starts with a probe that forces one: a long German compound in a column
  narrower than it.
- **The decision and its reason, if it hyphenates.** Pass `Hyphens.Auto` on those four texts, and
  let the locale come from the platform (or from a `localeList` in the style), as `lang` does for
  Chrome.
- **If desktop Skia does not hyphenate,** the item records it as a platform limit in research, with
  the probe's evidence. It is then done for Android and iOS, where the platform does hyphenate.
  The EdgeNarrow difference stays, named in B-39's numbers.
- AC: the probe's result recorded in research §1.10. If it hyphenates, EdgeNarrow's title breaks
  where Chrome's does, and a test holds a hyphenated break in a narrow ListItem. With a mutant.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/containers/OldgeList.kt`,
  `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/containers/OldgeActionTile.kt`,
  `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/OldgeSegmented.kt`.
