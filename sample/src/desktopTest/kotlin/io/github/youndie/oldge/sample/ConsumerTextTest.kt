package io.github.youndie.oldge.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.type.OldgeText
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * `OldgeText` from outside the library (B-51): a consumer's text lands where Chrome's does, which
 * `BasicText` with the same style does not. The size is 13 px in a 16 px line, the label size, where
 * Compose's reported baseline and its drawn one fall on either side of .5 (B-46). Chrome's ink
 * centre is `scripts/research/ink-probe.mjs`'s, as in oldge-core's `InkMatchesChromeTest`.
 *
 * The text is rendered as a consumer's is, with the platform's own hinting and smoothing: the
 * library offers no public way to pin them (B-52), which oldge-core's harness does through internals.
 */
@OptIn(ExperimentalTestApi::class)
class ConsumerTextTest {
    @Test
    fun a_consumers_text_lands_on_chromes_ink_where_basic_text_does_not() {
        val oldge = centre(basic = false)
        val basic = centre(basic = true)
        assertTrue(abs(oldge - CHROME) <= TOLERANCE, "OldgeText's ink centre $oldge, Chrome's $CHROME")
        // The control: the same style through BasicText sits off, or this test could not tell the two.
        assertTrue(abs(basic - CHROME) >= MISS, "BasicText's ink centre $basic is as close to Chrome's $CHROME")
    }

    private fun centre(basic: Boolean): Float {
        var centre = 0f
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    run {
                        // `label` is the ui face at 700; 13 px in 16 is its size and line.
                        val style =
                            OldgeTheme.type.label.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                lineHeight = 16.sp,
                                letterSpacing = 0.sp,
                                color = Color.Black,
                            )
                        Box(
                            Modifier
                                .testTag("c")
                                .width(120.dp)
                                .height(16.dp)
                                .background(Color.White),
                        ) {
                            if (basic) BasicText("НН", style = style) else OldgeText("НН", style = style)
                        }
                    }
                }
            }
            val m = onNodeWithTag("c").captureToImage().toPixelMap()
            val rows =
                (0 until m.height).map { y ->
                    (0 until m.width).maxOf { x -> ((1 - m[x, y].red) * FULL).toInt() }
                }
            centre = rows.withIndex().sumOf { (y, v) -> y * v }.toFloat() / rows.sum()
        }
        return centre
    }
}

/** Chrome/153's ink centre for `ui` 700 at 13 px in a 16 px line: oldge-core's `InkMatchesChromeTest`. */
private const val CHROME = 6.75f
private const val TOLERANCE = 0.25f
private const val MISS = 0.5f
private const val FULL = 255
