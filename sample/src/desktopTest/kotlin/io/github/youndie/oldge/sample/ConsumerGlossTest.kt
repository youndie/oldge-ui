package io.github.youndie.oldge.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.material.OldgeGloss
import io.github.youndie.oldge.material.animateOldgeGloss
import io.github.youndie.oldge.material.oldgeGloss
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * A glossy element of a consumer's own, through the public gloss API only (B-45). The design
 * system's README invites it ("Свои глянцевые элементы стройте так же"): three stops from one token
 * family, a gradient with its middle stop at 55 %, and a transition between states rather than a
 * swap. `sample` sees nothing internal, so this is what a consumer can actually build.
 */
@OptIn(ExperimentalTestApi::class)
class ConsumerGlossTest {
    @Test
    fun a_consumers_gloss_paints_its_familys_three_stops_top_middle_and_bottom() {
        lateinit var accent: OldgeGloss
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    val c = OldgeTheme.colors
                    accent = OldgeGloss(c.accentHi, c.accent, c.accentLo)
                    Box(Modifier.testTag("gloss").size(20.dp, HEIGHT.dp).oldgeGloss(accent))
                }
            }
            val m = onNodeWithTag("gloss").captureToImage().toPixelMap()
            close(accent.hi, m[10, 0], "the top")
            close(accent.mid!!, m[10, (HEIGHT * MID).toInt()], "55 %")
            close(accent.lo, m[10, HEIGHT - 1], "the bottom")
        }
    }

    @Test
    fun a_press_transitions_the_gloss_along_a_straight_line_in_srgb_and_reduced_motion_swaps_it() {
        // Halfway through, every stop lies on the straight line between its two ends in sRGB, as
        // a browser interpolates `--og-g1…3`; Oklab, which `animateColorAsState` uses, bends off it.
        val (from, to, halfway) = press(reduced = false)
        assertNotEquals(from, halfway, "nothing moved halfway through the press")
        assertNotEquals(to, halfway, "the press had already landed halfway through")
        for ((stop, ends) in listOf("hi" to (from.hi to to.hi), "lo" to (from.lo to to.lo))) {
            val (a, b) = ends
            val got = if (stop == "hi") halfway.hi else halfway.lo
            val t = spread(a, b, got)
            assertTrue(t.max() - t.min() <= STRAIGHT, "$stop: $got is off the sRGB line from $a to $b (t = $t)")
            // The control: Compose's own `lerp`, in Oklab, at the same point is off the line, or this
            // check could not tell the two interpolations apart.
            val oklab = spread(a, b, lerp(a, b, t.average().toFloat()))
            assertTrue(
                oklab.max() - oklab.min() > STRAIGHT,
                "$stop: an Oklab mid-point reads as straight too (t = $oklab)",
            )
        }
        val (_, target, first) = press(reduced = true)
        assertEquals(target, first, "under reduced motion the pressed gloss is there in the first frame")
    }

    /** The resting gloss, the pressed one, and what [animateOldgeGloss] returns 80 ms into the press. */
    private fun press(reduced: Boolean): Triple<OldgeGloss, OldgeGloss, OldgeGloss> {
        lateinit var rest: OldgeGloss
        lateinit var pressed: OldgeGloss
        lateinit var now: OldgeGloss
        runComposeUiTest {
            mainClock.autoAdvance = false
            val down = mutableStateOf(false)
            setContent {
                OldgeTheme(reducedMotion = reduced) {
                    val c = OldgeTheme.colors
                    rest = OldgeGloss(c.chromeHi, null, c.chromeLo)
                    pressed = OldgeGloss(c.accentHi, null, c.accentLo)
                    now = animateOldgeGloss(if (down.value) pressed else rest, OldgeTheme.motion.fast)
                    Box(Modifier.size(20.dp).oldgeGloss(now))
                }
            }
            mainClock.advanceTimeBy(SETTLE)
            down.value = true
            mainClock.advanceTimeByFrame()
            if (!reduced) mainClock.advanceTimeBy(HALF_A_PRESS)
        }
        return Triple(rest, pressed, now)
    }

    private fun close(
        want: Color,
        got: Color,
        where: String,
    ) {
        val off = maxOf(abs(want.red - got.red), abs(want.green - got.green), abs(want.blue - got.blue))
        assertTrue(off * FULL <= CHANNEL, "at $where the gloss is $got, the family's stop $want")
    }

    /** For each sRGB channel that changes enough between [a] and [b], the t at which [mid] sits on it. */
    private fun spread(
        a: Color,
        b: Color,
        mid: Color,
    ): List<Float> {
        val ts =
            listOf(a.red to b.red to mid.red, a.green to b.green to mid.green, a.blue to b.blue to mid.blue)
                .filter { (ends, _) -> abs(ends.second - ends.first) * FULL >= WIDE }
                .map { (ends, m) -> (m - ends.first) / (ends.second - ends.first) }
        assertTrue(ts.size >= 2, "fewer than two channels change enough between $a and $b to tell")
        return ts
    }
}

private const val HEIGHT = 100
private const val MID = 0.55f
private const val SETTLE = 32L
private const val HALF_A_PRESS = 80L
private const val FULL = 255
private const val CHANNEL = 3
private const val WIDE = 20
private const val STRAIGHT = 0.02f
