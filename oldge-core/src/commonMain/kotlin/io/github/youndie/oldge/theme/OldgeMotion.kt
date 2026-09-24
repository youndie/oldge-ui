package io.github.youndie.oldge.theme

import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Immutable
import io.github.youndie.oldge.tokens.OldgeDurations
import io.github.youndie.oldge.tokens.OldgeEasings

/**
 * The design system's durations and curves, and whether motion is reduced.
 *
 * Under [reduced] every duration is zero — the design system's `prefers-reduced-motion` rule makes
 * everything instant, and the parity references are rendered that way (research §1.2). A component
 * reads its durations here rather than from `OldgeDurations`, so that one switch reaches all of
 * them; loops (Spinner, the pulse, the scanner) check [reduced] and stay still.
 */
@Immutable
public class OldgeMotion(
    public val reduced: Boolean,
) {
    /** A press: the squash in. */
    public val instant: Int get() = ms(OldgeDurations.instant)

    /** Switches, checkboxes, a tab change; a press's background in. */
    public val fast: Int get() = ms(OldgeDurations.fast)

    /** Menus, tooltips, chips; a press's background out and the spring back. */
    public val base: Int get() = ms(OldgeDurations.base)

    /** Sheets, dialogs, the drawer. */
    public val slow: Int get() = ms(OldgeDurations.slow)

    /** The gloss flash of a touch. */
    public val press: Int get() = ms(OldgeDurations.press)

    /** One turn of a loop: Spinner, the pulse. */
    public val loop: Int get() = ms(OldgeDurations.loop)

    public val spring: Easing get() = OldgeEasings.spring
    public val bounce: Easing get() = OldgeEasings.bounce
    public val out: Easing get() = OldgeEasings.out
    public val linear: Easing get() = OldgeEasings.linear

    private fun ms(value: Int): Int = if (reduced) 0 else value

    override fun equals(other: Any?): Boolean = other is OldgeMotion && other.reduced == reduced

    override fun hashCode(): Int = reduced.hashCode()
}
