package io.github.youndie.oldge.feedback

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals

/** B-79: the compact well, at its modifier's height, in the row's own colour. */
@OptIn(ExperimentalTestApi::class)
class ProgressWellBehaviourTest {
    @Test
    fun the_well_is_10_dp_unless_its_modifier_says_otherwise_and_the_bars_is_20() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column(Modifier.width(300.dp)) {
                        OldgeProgressWell(0.5f, "a", Modifier.testTag("compact"))
                        OldgeProgressWell(0.5f, "b", Modifier.height(14.dp).testTag("taller"))
                        OldgeProgressBar(0.5f, label = "Загрузка")
                    }
                }
            }
            onNodeWithTag("compact").assertHeightIsEqualTo(10.dp)
            onNodeWithTag("taller").assertHeightIsEqualTo(14.dp)
            onNodeWithContentDescription("Загрузка").assertHeightIsEqualTo(20.dp)
        }

    @Test
    fun the_well_is_a_progress_bar_to_a_screen_reader() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column {
                        OldgeProgressWell(0.45f, "Ubuntu ISO")
                        OldgeProgressWell(null, "Linux Mint")
                    }
                }
            }
            onNodeWithContentDescription("Ubuntu ISO").assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.ProgressBarRangeInfo,
                    ProgressBarRangeInfo(
                        0.45f,
                        0f..1f,
                    ),
                ),
            )
            onNodeWithContentDescription("Linux Mint").assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.ProgressBarRangeInfo,
                    ProgressBarRangeInfo.Indeterminate,
                ),
            )
        }

    /** A block's middle, low in the well and clear of the top gloss line, is the caller's colour. */
    @Test
    fun a_callers_fill_colours_the_blocks() =
        runComposeUiTest {
            val red = Color(0xFFC42B1C)
            setContent {
                OldgeTheme(reducedMotion = true) {
                    OldgeProgressWell(1f, "c", Modifier.width(200.dp).height(20.dp).testTag("w"), fill = red)
                }
            }
            val m = onNodeWithTag("w").captureToImage().toPixelMap()
            // 1 px border, 2 px padding, then the first 8 px block: its middle at x 7, low at y 14.
            val px = with(density) { 7.dp.roundToPx() } to with(density) { 14.dp.roundToPx() }
            assertEquals(red, m[px.first, px.second])
        }
}
