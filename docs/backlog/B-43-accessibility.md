---
id: B-43
title: "Accessibility: roles, 44 dp targets, colour never alone"
status: done
priority: P2
size: M
stage: stage-4-product
blocked_by: [B-42]
---

# B-43 — Accessibility: roles, 44 dp targets, colour never alone

The design system states three rules (README "Фокус и доступность", "Отступы и касание"): every
control is at least `hit-min` (44 px) to touch; the focus ring is visible on every background
in every skin; colour is never the only carrier of meaning (an error is a colour, an icon and a
word; ON/OFF is written in the switch).

- **The decision and its reason.** Tests over the catalogue registry (B-42), not per component:
  every node with a click action has a touch target ≥ 44 dp; every such node has a role and a
  label; every status-coloured element in the registry has a text or content description.
  Contrast is the design system's claim (≥ 4.5:1 for text, ≥ 3:1 for `edge` and `focus`); a test
  computes it from the generated tokens for the pairs the README names, per skin — a failing pair
  is kept and reported, as the design system's own checklist says, not silently re-coloured.
- Rejected: auditing by eye.

- AC: the three registry-wide tests and the contrast test green, or each failure an item.
- Anchors: `oldge-core/src/desktopTest/kotlin/io/github/youndie/oldge/behaviour/`, `reference/design-system/README.md`.

## Findings (2026-09-25)

- **The registry is viddik's**, as for B-42: `AccessibilityTest` renders one skin of each of the
  fixture groups (a skin changes colours, not semantics) and reads every node while the
  composition is alive. Each fixture is read, with every root, since a Dialog, a sheet or a menu is
  a window of its own.
- **Controls: a role and a label.** Every node with a click action has a role and a word: a
  content description, a text, or a state description. A text field is identified by its editable
  text, since Compose gives it no role.
  - **Found:** the scrims of Dialog, BottomSheet and NavDrawer, and SwipeRow's "tap to shut"
    overlay, were `clickable`s with neither. A screen reader announced each as an unnamed button
    covering the screen.
  - **Fixed:** they are now pointer-only (`oldgeScrimTaps`), as the design system's scrims are plain
    `div`s with a click handler. That leaves the drawer with no accessible close, filed as the
    question [B-61](B-61-drawer-accessible-close.md).
- **States in words.** Every node carrying an error, an on/off, a selection or progress has a word.
  None was missing.
  - The Switch's ON/OFF lamp is now kept out of the semantics (`clearAndSetSemantics`). It is the
    word for the eye that the colour rule asks for. To a screen reader it repeated the switch's own
    state, and it stood in for a missing name: with the list-row switch's hidden label removed, the
    test still counted the switch as named by «ON».
- **Touch: 44 dp.** *Amended in B-60: this paragraph measured the wrong axis.* Compose extends
  a small pointer target to its 48 dp `minimumTouchTargetSize` for touch, so the design system's
  rule holds on touch through the platform. The 35 sizes below are what a mouse reaches.
  `touchBoundsInRoot` "could never fall short" because the platform guarantees exactly the rule, and
  that was the right measure for touch. The check is now a test of that guarantee, and B-60 was
  dropped. What was written:
  - The test reads each control's own laid-out size, which `oldgeHitArea` enlarges. It first read
    `touchBoundsInRoot`, which is at least the platform's minimum target by definition: the
    hit-area mutant survived it, and could never have failed it.
  - Clipped bounds were rejected too: a chip at a scroll's edge and a pressed button's squash
    showed as small targets.
  - **What remains is the design system's own CSS**, 35 fixture sizes in `KNOWN_TOUCH`: the small
    Button at 36, Segmented and Tabs and the calendar's days at 40, the dots 24 wide, the
    Composer's field 38, the text input 42. Filed as [B-60](B-60-hit-min-everywhere.md): widen
    them without changing what is drawn.
- **Contrast** (`ContrastTest`). Every pair the README and `tokens.json` claim, 32 pairs in three
  skins, is computed with WCAG 2's ratio from the generated tokens. Every one holds, and `KNOWN` is
  empty.
- **The sets are compared whole**: a new shortfall fails the test, and so does a known one that has
  been fixed.
- **Mutants:** 4 of 4 killed.
  - A sheet scrim back to a `clickable` (unnamed).
  - The hit area's height gone: the chips fall to 32 dp.
  - The list-row switch's hidden label gone.
  - `ink-muted` darkened in Toxic (the contrast test).

  The hit area's horizontal half was first chosen and survived. It is equivalent: the chips'
  2 dp horizontal overhang is on a target already far wider than 44.
