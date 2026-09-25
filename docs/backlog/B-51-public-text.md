---
id: B-51
title: "A public OldgeText: plain text in the skin's type, on the drawn baseline"
status: done
priority: P2
size: S
stage: stage-2-components
---

# B-51 — A public `OldgeText`: plain text in the skin's type, on the drawn baseline

Found in B-37, the library's first consumer. The sign-in screen sets a heading
(«С возвращением») and muted paragraphs. The only way the library sets text is `OldgeText`, and it
is `internal`. A consumer is left with `BasicText` and an `OldgeTheme.type` style, which puts the
text where Compose reports its baseline rather than where CSS draws it.

- At 13 px and in the 17 px titles the ink was then a pixel off Chrome's (B-46).
- A line exactly as tall as its font is laid out as if it had no line height at all (B-49).
- Every component's text is corrected. The text between components on a screen is not.

- **The decision and its reason.** Make `OldgeText` public, with `LocalOldgeTextStyle` as its
  default style and a KDoc that says what it adds over `BasicText`: the CSS baseline placement,
  and a same-size line honoured. It is the text an app writes between the components, and the one
  B-37 to B-40 need.
- **Rejected: documenting `BasicText` with `OldgeTheme.type`.** The two faults above are
  invisible until a screen is compared with the design. They would become every consumer's to
  find.
- **Rejected: public text roles** (`OldgeDisplay`, `OldgeMuted`, …). The design system has styles,
  not text components. A style parameter over `OldgeTheme.type` is its shape.
- **Not covered:**
  - The lcd and pixel faces' companion join (`OldgeScriptText`) stays internal, reached through
    `OldgeReadout` and the other components that set those faces.
  - A text in `OldgeTheme.type.readout` or `pixelTag` through the public `OldgeText` is a question
    for the KDoc to answer (route it or refuse it), not a new API.
  - Pinning the ABI is B-45.

- AC: `OldgeText` is public, and its parameters are what a consumer needs and no more. What is
  there today (text, modifier, style, maxLines, softWrap, overflow) is read against the screens of
  B-37 to B-40 before anything is added.
- AC: a behaviour test from outside the library's package holds a consumer's `OldgeText` at 13 px
  in a 16 px line to Chrome's ink centre, as `InkMatchesChromeTest` does inside. Plus a mutant.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/type/OldgeText.kt`.

## Findings (2026-09-25)

- **`OldgeText` is public, with the parameters it had.** Read against B-37's two screens: a title,
  paragraphs, and a note beside a button all fit text, modifier, style, maxLines, softWrap and
  overflow. Alignment is the style's `textAlign`. Nothing was added.
- **The KDoc now speaks to a consumer.**
  - It opens on what the component is for: text between the components.
  - It lists the three corrections it makes over `BasicText` (B-11, B-46, B-49).
  - It answers the lcd and pixel question by refusing it: set those faces through the components,
    which join each face with its Cyrillic companion. Through `OldgeText`, Cyrillic falls back to
    the platform's face.
- **Held from outside the library.** `sample`'s `ConsumerTextTest` compiles against the public API
  only, with a `desktopTest` added to `sample` for it.
  - `label`-sized text (13 px in 16) lands within 0.25 px of Chrome's ink centre (6.75).
  - `BasicText` with the same style is the control: it lands at least half a pixel off, or the
    test could not tell the two apart.
- **Mutant:** 1 of 1 killed. Dropping the CSS-baseline move put the consumer's ink at 7.69, a
  pixel low, as B-46 measured inside.
  - Run with `mutate.py --command "./gradlew :sample:desktopTest --tests {tests} --rerun …"
    --results sample/build/test-results/desktopTest`, since the runner defaults to oldge-core.
- **A further gap, filed as [B-52](B-52-pin-text-rendering.md).** The consumer test renders with
  the platform's hinting, because a consumer cannot pin it. oldge-core's `PortableText` rebuilds
  the typography through internals (`OldgeTypography`'s constructor, `LocalOldgeTypography`).
  - The ink still lands on Chrome's here.
  - But `sample`'s screen fixtures would read parity against a floor measured with pinned text.
  - B-52 blocks B-37 to B-40.
