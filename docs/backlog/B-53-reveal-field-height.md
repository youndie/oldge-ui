---
id: B-53
title: "A password field with reveal is 46 px in Chrome and 44 here"
status: open
priority: P2
size: XS
stage: stage-2-components
blocked_by: [B-17]
---

# B-53 — A password field with reveal is 46 px in Chrome and 44 here

Found in B-37, on the sign-in screen: the first reference with a reveal field in it. The TextField
preview has none, so B-17 never measured one.

- **Chrome.** The reveal button is `og-orb og-orb--sm`, whose box is `max(var(--orb),
  var(--hit-min))`, so 44 px square, not 30. The field box is `min-height: var(--hit-min)` with a
  1 px border in `border-box`, so its content is 42 px. A 44 px flex item grows it to 46.
- **Here.** `OldgeTextField` keeps the reveal field at 44. The field's inside measured 41 px here
  against 43 in the reference.
- **The result on the page.** Everything below the password field on AuthScreen sits 2 px high.
  That is most of the page's raw 7.1–7.2 %: with that band shifted back, it is 2.4–2.6 %.

- **The decision and its reason.** The field grows round its reveal orb as the flex box does. The
  number comes from the CSS, not from this measurement: the orb's 44 px box plus the border.
- AC: a behaviour test holds a reveal field to 46 dp and a plain one to 44, with a mutant.
- AC: AuthScreen's parity drops, and TextFieldStates' golden is re-recorded and looked at.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/forms/OldgeTextField.kt`.
