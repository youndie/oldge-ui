---
id: B-59
title: "Hyphenate where the CSS says hyphens: auto"
status: done
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

## Findings (2026-09-25)

- **The probe forced a break.** It laid out a German compound in a 90 dp column, in eight
  combinations:
  - `Hyphens.None` and `Hyphens.Auto`;
  - `LineBreak.Simple` and `LineBreak.Paragraph`;
  - the locales `de` and `en-US`.

  All eight gave the same eight lines, broken at letters with no hyphen. Skiko's `ParagraphStyle`,
  on both the JVM and iOS, has no hyphenation setting. On desktop, Compose reads `Hyphens` nowhere
  past the style classes. Android's `AndroidParagraph` does read it. The evidence, with its
  addresses, is in research §1.10.
- **Deviation:** the item expected iOS to hyphenate. It draws with the same Skiko paragraph, so
  it does not. Hyphenation is real on Android only.
- **Done as the item's branch "Skia does not hyphenate".** `Hyphens.Auto` is passed on ListItem's
  title and value, on ActionTile's title and description (`.og-tile__text` is inherited by both),
  and on Segmented's option. The locale is left to the platform, as `lang` is for Chrome.
  EdgeNarrow is unchanged at 2.97 / 3.09 / 3.16 %: the gap is the platform limit, and B-39's
  number stands.
- **Not verified on a device.** Seeing a hyphen on Android needs an emulator or a device, which is
  B-41's open question. What this item can hold is that the request reaches the layout.
- **Tests:** `HyphenationTest`.
  - The request test reads each text's laid-out style through `GetTextLayoutResult`. Its control
    is ListItem's subtitle, which does not hyphenate and reads `Unspecified`.
  - The canary requires `Auto` and `None` to break the desktop lines identically. It checks
    first that the column forces breaks at all, since two layouts with no breaks could only agree.
- **Mutants:** 4 of 4 killed, removing `Hyphens.Auto` from the ListItem title, the ListItem value,
  the ActionTile description and the Segmented option, one at a time.
