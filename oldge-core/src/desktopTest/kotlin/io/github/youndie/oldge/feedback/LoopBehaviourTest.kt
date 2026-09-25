package io.github.youndie.oldge.feedback

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/** The rules in the Spinner and Skeleton READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class LoopBehaviourTest {
    private val indeterminate =
        SemanticsMatcher.expectValue(
            SemanticsProperties.ProgressBarRangeInfo,
            ProgressBarRangeInfo.Indeterminate,
        )

    @Test
    fun a_spinner_is_an_indeterminate_progress_named_by_its_label_or_as_loading() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column {
                        OldgeSpinner(label = "Загружаю…")
                        OldgeSpinner(size = OldgeSpinnerSize.Small)
                    }
                }
            }
            onNodeWithContentDescription("Загружаю…").assert(indeterminate)
            onNodeWithContentDescription("Загрузка").assert(indeterminate)
        }

    @Test
    fun a_skeleton_says_nothing_to_a_screen_reader() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeSkeleton(variant = OldgeSkeletonVariant.Card) } }
            val spoken =
                onAllNodes(
                    SemanticsMatcher("says something") {
                        it.config.contains(SemanticsProperties.Text) ||
                            it.config.contains(SemanticsProperties.ContentDescription)
                    },
                ).fetchSemanticsNodes()
            assertEquals(0, spoken.size)
        }

    @Test
    fun a_dial_lights_its_first_100_degrees_from_twelve_o_clock() =
        runComposeUiTest {
            setContent {
                OldgeTheme(reducedMotion = true) {
                    OldgeSkeleton(Modifier.testTag("dial"), variant = OldgeSkeletonVariant.Circle, width = 88.dp)
                }
            }
            val m = onNodeWithTag("dial").captureToImage().toPixelMap()

            // Sample the ring midway between its inner edge (53 % of the farthest-corner radius) and
            // the rim, at the centre of a segment: 10°, 40°, 70° and 95° are lit; 130° and 190° are not.
            fun at(deg: Double): Color {
                val c = m.width / 2.0
                val d = m.width - 2 * 4.0
                val r = (d / 2 * sqrt(2.0) * 0.53 + d / 2) / 2
                val a = Math.toRadians(deg - 90)
                return m[(c + r * cos(a)).toInt(), (c + r * sin(a)).toInt()]
            }

            fun lit(color: Color) = color.green > color.red + 0.15f
            for (deg in listOf(10.0, 40.0, 70.0, 95.0)) assertTrue(lit(at(deg)), "$deg° should be lit: ${at(deg)}")
            for (deg in listOf(130.0, 190.0)) assertTrue(!lit(at(deg)), "$deg° should be dark: ${at(deg)}")
        }

    /** Two frames of a spinner and a skeleton [ms] apart, as pixels. */
    private fun twoFrames(
        reduced: Boolean,
        ms: Long,
    ): Pair<List<Any>, List<Any>> {
        var first: List<Any> = emptyList()
        var second: List<Any> = emptyList()
        runComposeUiTest {
            mainClock.autoAdvance = false
            setContent {
                OldgeTheme(reducedMotion = reduced) {
                    Column(Modifier.testTag("loops")) {
                        OldgeSpinner()
                        OldgeSkeleton(variant = OldgeSkeletonVariant.Row)
                    }
                }
            }
            mainClock.advanceTimeBy(100)

            fun grab(): List<Any> {
                val m = onNodeWithTag("loops").captureToImage().toPixelMap()
                return (0 until m.height).flatMap { y -> (0 until m.width).map { x -> m[x, y] } }
            }
            first = grab()
            mainClock.advanceTimeBy(ms)
            second = grab()
        }
        return first to second
    }

    @Test
    fun under_reduced_motion_the_disc_the_scanner_and_the_arc_stand_still() {
        val (a, b) = twoFrames(reduced = true, ms = 400)
        assertEquals(a, b, "a frame 400 ms later differs under reduced motion")
    }

    @Test
    fun without_reduced_motion_they_move() {
        val (a, b) = twoFrames(reduced = false, ms = 400)
        assertNotEquals(a, b, "nothing moved in 400 ms")
    }
}
