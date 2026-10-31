---
id: B-77
title: "OldgeButton: a Danger variant for an irreversible confirm"
status: question
priority: P2
size: S
stage: stage-5-consumers
blocked_by: []
---

# B-77 — OldgeButton: a Danger variant for an irreversible confirm

Filed as #19 by the owner. `OldgeButtonVariant` has `Primary`, `Secondary` and `Plain`. For an
irreversible *Delete* a consumer chooses between the lime "go" of `Primary` and a `Secondary` that no
longer marks the action as different; kachok's remove dialog uses `Secondary`.

- **The decision, the owner's: a deviation from the design system,** which has no such button. A
  `Danger` variant drawn from the skin's `danger` token, with `Primary`'s gloss, bevel and press, in
  every skin. The KDoc says when: the confirm of an action that cannot be undone, one per dialog.
- **No reference exists for it**, so it closes on its own goldens, looked at in three skins, not on a
  parity number. If the owner adds it to the design system artifact, re-vendoring (B-36) brings a
  reference.
- Rejected: a `colors` override on `Primary`. It gives every caller a way to break the one-accent rule.

- AC: `OldgeButton(variant = Danger)` is drawn in `danger` with `Primary`'s materials (goldens in three
  skins, both sizes, pressed and disabled), and its label passes the contrast check the other variants
  pass. With a mutant.
- Closes #19.
- Anchors: `oldge-core/src/commonMain/kotlin/io/github/youndie/oldge/actions/OldgeButton.kt`.

## Question for the owner (2026-11-01)

**The design system already decides this, and decides it the other way.** Two of its own sentences:

- `reference/design-system/components/Dialog/README.md`: «Деструктивное действие — primary с иконкой
  `trash` и глаголом «Удалить», не «OK»». A destructive confirm is the lime Primary, told apart by the
  `trash` icon and the verb.
- `reference/design-system/README.md` and `tokens.json`: `danger` is «Текст на фонах тела; всегда со
  словом и иконкой», and «`danger` — сбой, а не акцент». It is a text colour on the body, for a
  failure. There is no `danger-hi`/`danger-lo` for a gloss, and no `on-danger` for a label.

So #19's premise, that Primary is "the wrong colour for a destructive confirm", is the opposite of
the brief, and the item's "deviation, the owner's" was written before this was read.

**What the tokens allow, measured** (WCAG contrast, from `tokens.json`):

| | Toxic | Media | Crystal |
|---|---|---|---|
| `danger` label on `chrome-hi` | 2.26 | 1.67 | 5.66 |
| `danger` label on `chrome-lo` | 1.07 | 1.11 | 3.66 |
| `on-accent` (dark) label on a `danger` fill | 7.46 | 10.48 | 3.33 |
| white label on a `danger` fill | 2.53 | 1.80 | 5.66 |

A Secondary with a danger label fails in the two dark skins. A danger fill needs a dark label in Toxic
and Media and a white one in Crystal, and no token says which; the library may not type a colour
(research D4).

**The choices:**

1. **Follow the brief; no code.** A destructive confirm is
   `OldgeButton("Удалить", variant = Primary, icon = OldgeIcons.Trash)`. kachok's remove dialog moves
   from Secondary to that, and #19 is closed as answered by the design system. This is the cheapest
   choice, and the one consistent with everything else here.
2. **Change the brief, then build.** The owner adds a destructive button to the design system artifact
   (`danger-hi`, `danger-lo`, `on-danger` per skin, a `.og-btn--danger` and its README line, and the
   Dialog README's rule rewritten), re-vendored through B-36. B-77 then builds `Danger` against a real
   reference and closes on a parity number.
3. **Deviate in the library only.** Not recommended: it needs colours the tokens do not have, so it
   breaks D4, and the library would disagree with its own brief.

The loop does not pick one: whether a destructive action is lime or red is the design system's call.
