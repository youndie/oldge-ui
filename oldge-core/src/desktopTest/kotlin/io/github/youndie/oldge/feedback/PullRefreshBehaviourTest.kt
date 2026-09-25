package io.github.youndie.oldge.feedback

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.containers.OldgeListItem
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The rules in the PullRefresh README that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class PullRefreshBehaviourTest {
    private var refreshes = 0
    private var slop = 0f

    private fun androidx.compose.ui.test.ComposeUiTest.pullable(rows: Int = 3) {
        setContent {
            slop = LocalViewConfiguration.current.touchSlop
            OldgeTheme {
                Box(Modifier.width(360.dp).testTag("pull")) {
                    OldgePullRefresh(false, { refreshes++ }, height = 164.dp) {
                        Column { repeat(rows) { OldgeListItem("Строка $it") } }
                    }
                }
            }
        }
    }

    /** Drags down by [pull] of the disc's way (the finger goes 1 / 0.55 as far) past the slop, in one gesture. */
    private fun androidx.compose.ui.test.ComposeUiTest.drag(
        pull: Dp,
        release: Boolean = true,
    ) {
        onNodeWithTag("pull").performTouchInput {
            down(Offset(centerX, 20f))
            val total = slop + pull.toPx() / 0.55f
            repeat(STEPS) { moveBy(Offset(0f, total / STEPS)) }
            if (release) up()
        }
        waitForIdle()
    }

    @Test
    fun let_go_past_64_px_it_refreshes_and_short_of_it_it_does_not() =
        runComposeUiTest {
            pullable()
            drag(50.dp)
            assertEquals(0, refreshes)
            drag(80.dp)
            assertEquals(1, refreshes)
        }

    @Test
    fun the_pull_starts_only_at_the_top_of_the_scroll() =
        runComposeUiTest {
            pullable(rows = 20)
            // Scrolled 200 dp down, a drag of 80 dp of pull brings the list back first.
            onNodeWithTag("pull").performTouchInput {
                down(Offset(centerX, 150.dp.toPx()))
                repeat(STEPS) { moveBy(Offset(0f, -(slop + 200.dp.toPx()) / STEPS)) }
                up()
            }
            waitForIdle()
            drag(80.dp)
            assertEquals(0, refreshes)
        }

    @Test
    fun past_the_threshold_the_disc_lights_with_the_accent() =
        runComposeUiTest {
            pullable()

            // 18 dp right of the disc's centre is outside its 16 dp: only the ready ring draws there.
            fun ring(): Color {
                val m = onNodeWithTag("pull").captureToImage().toPixelMap()
                val px = density.density
                val indicator = (m.height - 164 * px)
                return m[(m.width / 2 + 18 * px).toInt(), (indicator / 2).toInt()]
            }

            fun lime(c: Color) = c.green > c.red + 0.15f && c.green > c.blue + 0.15f
            drag(58.dp, release = false)
            assertTrue(!lime(ring()), "the disc lit short of the threshold: ${ring()}")
            onNodeWithTag("pull").performTouchInput { up() }
            waitForIdle()
            drag(80.dp, release = false)
            assertTrue(lime(ring()), "the disc did not light past the threshold: ${ring()}")
            onNodeWithTag("pull").performTouchInput { up() }
        }

    @Test
    fun while_it_refreshes_a_screen_reader_hears_it() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgePullRefresh(true, {}, height = 164.dp) { OldgeListItem("Строка") } } }
            onNode(
                hasContentDescription("Обновляю") and
                    SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite),
            ).assertExists()
        }
}

private const val STEPS = 20
