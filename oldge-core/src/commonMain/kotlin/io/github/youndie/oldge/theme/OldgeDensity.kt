package io.github.youndie.oldge.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.tokens.OldgeSpacing

/**
 * How large the controls are drawn: for a finger or for a pointer (B-80).
 *
 * - [Touch], the default, is the design system: every control at least `hit-min`, 44 dp, as its
 *   README asks. Nothing changes under it.
 * - [Pointer] is for a desktop window driven by a mouse, where a 44 dp toolbar is too tall and buys
 *   nothing: Compose extends a touch target to 48 dp by itself, and a pointer is never extended
 *   (B-60). Under it `hit-min` is 28 dp, the buttons are 28, 32 and 36 dp (small, medium, large), an
 *   icon button is square, and the search bar is 32 dp.
 *
 * The design system has no pointer sizes; these are the owner's, from youndie/oldge-ui#21.
 */
public enum class OldgeDensity { Touch, Pointer }

internal val LocalOldgeDensity = staticCompositionLocalOf { OldgeDensity.Touch }

/** The smallest a control is drawn: `hit-min` for a finger, [POINTER_SMALL] for a pointer. */
internal val OldgeDensity.hitMin: Dp
    get() =
        when (this) {
            OldgeDensity.Touch -> OldgeSpacing.hitMin
            OldgeDensity.Pointer -> POINTER_SMALL
        }

internal val POINTER_SMALL = 28.dp
internal val POINTER_MEDIUM = 32.dp // css literal: youndie/oldge-ui#21, the pointer sizes «roughly 28, 32 and 36 dp»
internal val POINTER_LARGE = 36.dp
