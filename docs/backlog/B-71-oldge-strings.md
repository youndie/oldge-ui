---
id: B-71
title: "OldgeStrings: the library's accessible names, provided through the theme"
status: wip
priority: P1
size: S
stage: stage-5-consumers
blocked_by: []
---

# B-71 — OldgeStrings: the library's accessible names, provided through the theme

Filed as #28 and #18 by the owner, from kachok's English window. Six controls draw an accessible
name in Russian that the caller cannot change, so an app in another language gets Russian words in
its semantics tree:

- `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/containers/OldgeDialog.kt` — `WindowBar`'s close orb «Закрыть», shared by `OldgeDialog` and
  `OldgeBottomSheet`;
- `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/feedback/OldgeSnackbar.kt` — the close orb «Закрыть»;
- `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/OldgeBalloon.kt` — `contentDescription = "Закрыть"`;
- `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/navigation/OldgeNavDrawer.kt` — `CLOSE = "Закрыть"`, the scrim's action;
- `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeTextField.kt` — the reveal orb «Показать пароль» / «Скрыть пароль»;
- `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeSearchBar.kt` — the clear orb «Очистить» (#18).

kachok's tests press the search bar's clear button as «Очистить», and its dialogs give up the close
orb and end with *Cancel* instead.

- **The decision: one `OldgeStrings` class, provided by a `LocalOldgeStrings` composition local that
  `OldgeTheme` takes** (`strings: OldgeStrings = OldgeStrings()`), with the Russian defaults. An app
  sets its language once, at its root, and every control follows.
- **Rejected: a parameter on each component**, as #18 proposed for `clearLabel`. There are six names
  already, a new control would add a seventh, and an app would have to pass every one at every call
  site to stay consistent.
- Not covered: the visible words of the design system's demos (the fixtures stay as the previews say
  them).

- AC: with `OldgeTheme(strings = OldgeStrings(close = "Close", …))`, each of the six controls is found
  by its English name in a semantics test; with the default theme every name is the Russian one it is
  today, and no golden moves. With a mutant.
- AC: `oldge-core/api/` gains `OldgeStrings` and `LocalOldgeStrings`, and nothing else changes.
- Closes #28 and #18.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/theme/OldgeTheme.kt`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeSearchBar.kt`, `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeTextField.kt`,
  `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/containers/OldgeDialog.kt`.
