package io.github.youndie.oldge.material

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeColors
import io.github.youndie.oldge.tokens.OldgeDurations
import io.github.youndie.oldge.tokens.OldgeEasings
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** B-07: a gloss interpolates between states on the clock, and snaps under reduced motion. */
@OptIn(ExperimentalTestApi::class)
class GlossTransitionTest {
    private val c = OldgeColors.Toxic
    private val rest = GlossStops(c.chromeHi, null, c.chromeLo)
    private val pressed = GlossStops(c.chromeLo, null, c.chromeHi)

    @Test
    fun half_way_through_the_transition_the_stops_are_half_way_along_the_curve() =
        runComposeUiTest {
            mainClock.autoAdvance = false
            var target by mutableStateOf(rest)
            var seen = rest
            setContent {
                OldgeTheme(reducedMotion = false) {
                    seen = animateGloss(target, OldgeDurations.base)
                }
            }
            mainClock.advanceTimeByFrame()
            target = pressed
            mainClock.advanceTimeByFrame()
            val start = mainClock.currentTime
            mainClock.advanceTimeBy(OldgeDurations.base / 2L)
            waitForIdle()
            val elapsed = (mainClock.currentTime - start).toFloat() / OldgeDurations.base
            // The clock moves in frames, so the expected point is bracketed by one frame either side.
            val frame = 16f / OldgeDurations.base
            val low = OldgeEasings.out.transform((elapsed - 2 * frame).coerceIn(0f, 1f))
            val high = OldgeEasings.out.transform((elapsed + frame).coerceIn(0f, 1f))
            assertBetween(
                premultipliedLerp(rest.hi, pressed.hi, low),
                premultipliedLerp(rest.hi, pressed.hi, high),
                seen.hi,
                "hi",
            )
            assertBetween(
                premultipliedLerp(rest.lo, pressed.lo, low),
                premultipliedLerp(rest.lo, pressed.lo, high),
                seen.lo,
                "lo",
            )
            assertTrue(seen.hi != rest.hi && seen.hi != pressed.hi, "hi is at an end, not in between: ${seen.hi}")
            mainClock.advanceTimeBy(OldgeDurations.base.toLong())
            waitForIdle()
            assertEquals(pressed, seen)
        }

    @Test
    fun under_reduced_motion_the_target_is_there_in_the_same_frame() =
        runComposeUiTest {
            mainClock.autoAdvance = false
            var target by mutableStateOf(rest)
            var seen = rest
            setContent {
                OldgeTheme(reducedMotion = true) {
                    seen = animateGloss(target, OldgeTheme.motion.base)
                }
            }
            mainClock.advanceTimeByFrame()
            target = pressed
            mainClock.advanceTimeByFrame()
            assertEquals(pressed, seen)
        }

    @Test
    fun interpolation_is_premultiplied_srgb_not_oklab() {
        // Transparent to a colour keeps the colour's hue all the way, as CSS does; Oklab or an
        // unpremultiplied lerp would drag it through black.
        val half = premultipliedLerp(Color.Transparent, c.accent, 0.5f)
        assertEquals(c.accent.copy(alpha = 0.5f), half)
    }

    private fun assertBetween(
        a: Color,
        b: Color,
        actual: Color,
        name: String,
    ) {
        for ((x, y, v) in listOf(
            Triple(a.red, b.red, actual.red),
            Triple(a.green, b.green, actual.green),
            Triple(a.blue, b.blue, actual.blue),
        )) {
            val lo = minOf(x, y) - 1e-3f
            val hi = maxOf(x, y) + 1e-3f
            assertTrue(v in lo..hi, "$name: $v outside [$lo, $hi] (a=$a b=$b actual=$actual)")
        }
        assertTrue(abs(actual.alpha - 1f) < 1e-3f)
    }
}
