---
id: B-82
title: "The fixed Russian accessible names B-71 missed: Stepper, PageDots, DatePicker, ProgressBar"
status: done
priority: P2
size: S
stage: stage-5-consumers
blocked_by: []
---

# B-82 — The fixed Russian accessible names B-71 missed

Found in B-79. B-71 moved six names into `OldgeStrings` from #28's list, and #28's list was not the
whole of it. These still say Russian to a screen reader, whatever the app's language:

- `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/OldgeProgressBar.kt` — «Выполнение», the bar's name when it has no label;
- `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/OldgeStepper.kt` — «готово» on a done step;
- `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/OldgePageDots.kt` — «Страница N из M» on each dot;
- `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeDatePicker.kt` — each day's name, «D месяца YYYY», from Russian month names.

- **The decision: the same as B-71's.** Each joins `OldgeStrings`, with the Russian default. The two
  that take numbers are functions, not strings: `page(n, of)` and `day(date)`. A month list alone
  cannot say a date in English word order.
- Not covered: the DatePicker's *visible* month and weekday names. Those are the design system's
  demo, drawn, and a localised calendar is a larger item if anyone asks for one.

- AC: under `OldgeTheme(strings = …)` in English, each of the four is found by its English name and
  none by the Russian; with the default theme nothing changes and no golden moves. Mutant per site.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/theme/OldgeStrings.kt`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/OldgeStepper.kt`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/OldgePageDots.kt`,
  `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeDatePicker.kt`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/OldgeProgressBar.kt`.

## Findings (2026-11-01)

- **`OldgeStrings` gained four values,** with defaults that say exactly what the controls said before
  (`the_default_words_are_the_ones_said_before`):
  - `progress`: «Выполнение»;
  - `stepDone`: «готово»;
  - `page: (number, count) -> String`;
  - `day: (LocalDate) -> String`.
- **The two with numbers are functions**, read from the theme in the composable and called in the
  semantics block, so English can say "november 12, 2026" in its own order. `MONTHS` became
  `internal` so that `day`'s default can reuse the DatePicker's list.
- **The Russian default for a day is kept as it was: «12 ноябрь 2026»,** the nominative, not the
  genitive «ноября». That is a pre-existing slip of grammar, and changing it would change what a
  screen reader says today. Left for the owner to ask for.
- **Still Russian, on purpose:** the caller's parameters `label = "Шаги"`, `"Страницы"`,
  `"Выбор даты"` and NavDrawer's `"Меню"`. A caller can already change each, which is the line B-71
  drew. The drawn calendar's month and weekday names are the design system's demo.
- **Tests:** `OldgeStringsTest`, five new cases. **Mutants:** 4 of 4 killed (`--only B-82`).
  **Goldens:** none moved. **ABI:** four new constructor parameters and getters on `OldgeStrings`.
