package io.github.youndie.oldge.actions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * `og-actions` as behaviour (B-56): buttons that fit share a line, each grown from its own width by an
 * equal share of what is left; buttons that do not fit wrap, one per line, each the line's width.
 */
@OptIn(ExperimentalTestApi::class)
class ActionsBehaviourTest {
    private fun androidx.compose.ui.test.ComposeUiTest.bounds(tag: String): DpRect =
        onNodeWithTag(tag, useUnmergedTree = true).getUnclippedBoundsInRoot()

    @Test
    fun buttons_that_fit_share_the_line_each_grown_from_its_own_width() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column {
                        // Their own widths, alone.
                        Box { OldgeButton("Отмена", {}, Modifier.testTag("a0")) }
                        Box { OldgeButton("Удалить навсегда", {}, Modifier.testTag("b0")) }
                        OldgeActions(Modifier.width(358.dp)) {
                            OldgeButton("Отмена", {}, Modifier.testTag("a"))
                            OldgeButton("Удалить навсегда", {}, Modifier.testTag("b"))
                        }
                    }
                }
            }
            val a = bounds("a")
            val b = bounds("b")
            assertEquals(a.top, b.top, "the two buttons are not on one line")
            assertTrue(abs((a.right - a.left + 8.dp + b.right - b.left - 358.dp).value) <= 1f, "the line is not filled")
            val growA = (a.right - a.left) - (bounds("a0").right - bounds("a0").left)
            val growB = (b.right - b.left) - (bounds("b0").right - bounds("b0").left)
            assertTrue(abs((growA - growB).value) <= 1f, "not grown by equal shares: $growA and $growB")
            assertTrue(growA > 0.dp, "not grown at all")
        }

    @Test
    fun buttons_that_do_not_fit_wrap_one_per_line_each_the_lines_width() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    OldgeActions(Modifier.width(288.dp)) {
                        OldgeButton("Änderungen verwerfen", {}, Modifier.testTag("a"))
                        OldgeButton("Speichern und fortfahren", {}, Modifier.testTag("b"))
                    }
                }
            }
            val a = bounds("a")
            val b = bounds("b")
            assertTrue(b.top >= a.bottom + 8.dp - 0.5.dp, "the second button did not wrap below the first")
            assertEquals(288.dp, a.right - a.left, "the first line's button is not the line's width")
            assertEquals(288.dp, b.right - b.left, "the second line's button is not the line's width")
        }
}
