package io.github.youndie.oldge.press

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class OldgeIndicationTest {
    private val touch = Offset(30f, 20f)

    /** Lit pixels (green channel) of a black 120 × 60 clickable pressed at [touch], a few frames in. */
    private fun pressedPixels(pressFlash: Boolean): List<Pair<Int, Int>> {
        var lit = emptyList<Pair<Int, Int>>()
        runComposeUiTest {
            mainClock.autoAdvance = false
            setContent {
                OldgeTheme(reducedMotion = false, pressFlash = pressFlash) {
                    Box(
                        Modifier
                            .size(120.dp, 60.dp)
                            .background(Color.Black)
                            .testTag(TAG)
                            .clickable {},
                    )
                }
            }
            mainClock.advanceTimeByFrame()
            onNodeWithTag(TAG).performTouchInput { down(touch) }
            repeat(4) { mainClock.advanceTimeByFrame() }
            val map = onNodeWithTag(TAG).captureToImage().toPixelMap()
            lit =
                (0 until map.height).flatMap { y ->
                    (0 until map.width).filter { x -> map[x, y].green > 0.08f }.map { x ->
                        x to
                            y
                    }
                }
        }
        return lit
    }

    @Test
    fun the_flash_is_centred_on_the_press() {
        val lit = pressedPixels(pressFlash = true)
        assertTrue(lit.size > 50, "the press drew ${lit.size} lit pixels")
        val cx = lit.sumOf { it.first }.toDouble() / lit.size + 0.5
        val cy = lit.sumOf { it.second }.toDouble() / lit.size + 0.5
        assertTrue(abs(cx - touch.x) < 1.0 && abs(cy - touch.y) < 1.0, "flash centred at ($cx, $cy), pressed at $touch")
    }

    @Test
    fun nothing_is_drawn_with_the_flash_off() = assertEquals(emptyList(), pressedPixels(pressFlash = false))

    /**
     * A focused clickable draws no ring while the input mode is touch, and one when it is keyboard —
     * the same control, the same focus, only the mode changed.
     * The focus is asserted before the absence of the ring: without that, this passes for a control
     * that was never focusable (kvadrant-ui's lesson).
     */
    @Test
    fun the_ring_is_gated_on_the_keyboard() =
        runComposeUiTest {
            val focus = FocusRequester()
            lateinit var modes: InputModeManager
            setContent {
                modes = LocalInputModeManager.current
                OldgeTheme {
                    Box(Modifier.size(140.dp, 80.dp).background(Color.Black).testTag(FRAME)) {
                        Box(
                            Modifier
                                .padding(20.dp)
                                .size(100.dp, 40.dp)
                                .background(Color.Black)
                                .testTag(TAG)
                                .focusRequester(focus)
                                .clickable(remember { MutableInteractionSource() }, OldgeIndication()) {},
                        )
                    }
                }
            }
            // Focus is taken in keyboard mode, as Tab takes it: in touch mode Compose focuses a
            // clickable without emitting a FocusInteraction at all (measured in B-10), so there is
            // nothing for the ring to follow and the check below would pass vacuously.
            runOnIdle { assertTrue(modes.requestInputMode(InputMode.Keyboard), "keyboard mode refused") }
            runOnIdle { focus.requestFocus() }
            waitForIdle()
            runOnIdle { assertTrue(modes.requestInputMode(InputMode.Touch), "touch mode refused") }
            onNodeWithTag(TAG).assertIsFocused()
            assertEquals(0, ringPixels(), "a ring was drawn in touch mode")
            runOnIdle { assertTrue(modes.requestInputMode(InputMode.Keyboard), "keyboard mode refused") }
            waitForIdle()
            onNodeWithTag(TAG).assertIsFocused()
            assertTrue(ringPixels() > 200, "no ring in keyboard mode")
        }

    private fun androidx.compose.ui.test.ComposeUiTest.ringPixels(): Int {
        val map = onNodeWithTag(FRAME).captureToImage().toPixelMap()
        return (0 until map.height).sumOf { y -> (0 until map.width).count { x -> map[x, y].red > 0.3f } }
    }

    private companion object {
        const val TAG = "target"
        const val FRAME = "frame"
    }
}
