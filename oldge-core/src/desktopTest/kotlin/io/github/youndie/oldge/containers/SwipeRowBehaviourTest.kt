package io.github.youndie.oldge.containers

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** The rules in the SwipeRow README that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class SwipeRowBehaviourTest {
    private val log = mutableListOf<String>()
    private val actions =
        listOf(
            OldgeSwipeAction(OldgeIcons.Folder, "Архив", { log += "archive" }),
            OldgeSwipeAction(OldgeIcons.Trash, "Удалить", { log += "delete" }, OldgeSwipeTone.Danger),
        )

    /** The platform's touch slop, in px, read from the composition. */
    private var slop = 0f

    private fun androidx.compose.ui.test.ComposeUiTest.row(defaultOpen: Boolean = false) =
        setContent {
            slop = LocalViewConfiguration.current.touchSlop
            OldgeTheme {
                OldgeList(Modifier.width(362.dp)) {
                    row {
                        OldgeSwipeRow(actions, defaultOpen = defaultOpen) {
                            OldgeListItem("Анна Ким", icon = OldgeIcons.Mail, onClick = { log += "row" })
                        }
                    }
                }
            }
        }

    /** How far the row has moved: its title's left edge against where it rests. */
    private fun androidx.compose.ui.test.ComposeUiTest.shift(): Dp {
        waitForIdle()
        return onNodeWithText("Анна Ким", useUnmergedTree = true).getUnclippedBoundsInRoot().left - REST
    }

    @Test
    fun a_left_swipe_opens_the_row_by_its_actions_width() =
        runComposeUiTest {
            row()
            assertEquals(0.dp, shift())
            onNodeWithText("Анна Ким").performTouchInput { swipeLeft() }
            assertEquals(-OPEN, shift())
            onNodeWithText("Анна Ким").performTouchInput { swipeRight() }
            assertEquals(0.dp, shift())
        }

    /**
     * Holds the row dragged [by] past the touch slop (`draggable` starts counting there), in one
     * gesture of small steps, and returns where it is held.
     */
    private fun androidx.compose.ui.test.ComposeUiTest.holdAt(by: Dp): Dp {
        onNodeWithText("Анна Ким").performTouchInput {
            down(center)
            val total = slop + by.toPx()
            repeat(STEPS) { moveBy(Offset(-total / STEPS, 0f)) }
        }
        return shift()
    }

    @Test
    fun let_go_short_of_half_the_row_shuts_and_past_half_it_opens() =
        runComposeUiTest {
            row()
            // Between a quarter and a half of the 144 dp: shut.
            val short = holdAt(55.dp)
            assert(short in -60.dp..-50.dp) { "held at $short" }
            onNodeWithText("Анна Ким").performTouchInput { up() }
            assertEquals(0.dp, shift())
            // Past half: open.
            val far = holdAt(90.dp)
            assert(far in -95.dp..-85.dp) { "held at $far" }
            onNodeWithText("Анна Ким").performTouchInput { up() }
            assertEquals(-OPEN, shift())
        }

    @Test
    fun past_its_ends_the_row_resists_and_comes_back() =
        runComposeUiTest {
            mainClock.autoAdvance = false
            row()
            mainClock.advanceTimeByFrame()
            // A right drag of 80 on a shut row moves it a quarter as far while the finger is down.
            onNodeWithText("Анна Ким").performTouchInput {
                down(center)
                moveBy(
                    androidx.compose.ui.geometry
                        .Offset(90f, 0f),
                )
                moveBy(
                    androidx.compose.ui.geometry
                        .Offset(0.1f, 0f),
                )
            }
            mainClock.advanceTimeByFrame()
            val held = onNodeWithText("Анна Ким", useUnmergedTree = true).getUnclippedBoundsInRoot().left - REST
            assert(held > 15.dp && held < 30.dp) { "held at $held" }
            onNodeWithText("Анна Ким").performTouchInput { up() }
            mainClock.autoAdvance = true
            assertEquals(0.dp, shift())
        }

    @Test
    fun a_vertical_drag_does_not_move_the_row() =
        runComposeUiTest {
            row()
            // Checked while the finger is still down: let go, a short move would settle shut anyway.
            onNodeWithText("Анна Ким").performTouchInput {
                down(center)
                repeat(STEPS) { moveBy(Offset(0f, -(slop + 80.dp.toPx()) / STEPS)) }
            }
            assertEquals(0.dp, shift(), "the row moved on a vertical drag")
            onNodeWithText("Анна Ким").performTouchInput { up() }
        }

    @Test
    fun a_tap_on_an_open_row_shuts_it_without_pressing_it() =
        runComposeUiTest {
            row(defaultOpen = true)
            assertEquals(-OPEN, shift())
            onNodeWithText("Анна Ким").performClick()
            assertEquals(0.dp, shift())
            assertEquals(emptyList(), log)
            onNodeWithText("Анна Ким").performClick()
            assertEquals(listOf("row"), log)
        }

    @Test
    fun an_action_runs_and_shuts_the_row() =
        runComposeUiTest {
            row(defaultOpen = true)
            onNodeWithText("Удалить").performClick()
            assertEquals(listOf("delete"), log)
            assertEquals(0.dp, shift())
        }

    @Test
    fun focusing_an_action_opens_the_row() =
        runComposeUiTest {
            row()
            onNodeWithText("Архив").requestFocus()
            assertEquals(-OPEN, shift())
        }

    @Test
    fun the_actions_are_also_the_rows_accessibility_actions() =
        runComposeUiTest {
            row()
            onNode(SemanticsMatcher.keyIsDefined(SemanticsActions.CustomActions))
                .assert(
                    SemanticsMatcher("archive and delete") { n ->
                        n.config[SemanticsActions.CustomActions].map { it.label } == listOf("Архив", "Удалить")
                    },
                )
            val archive =
                onNode(SemanticsMatcher.keyIsDefined(SemanticsActions.CustomActions))
                    .fetchSemanticsNode()
                    .config[SemanticsActions.CustomActions]
                    .first()
            runOnIdle { archive.action() }
            assertEquals(listOf("archive"), log)
        }

    @Test
    fun a_row_takes_one_to_three_actions_with_danger_last() {
        val four = List(4) { OldgeSwipeAction(OldgeIcons.Folder, "Архив $it", {}) }
        val dangerFirst = actions.reversed()
        for ((what, list) in listOf("none" to emptyList(), "four" to four, "danger first" to dangerFirst)) {
            assertFailsWith<IllegalArgumentException>("$what was accepted") {
                runComposeUiTest { setContent { OldgeTheme { OldgeSwipeRow(list) { OldgeListItem("Строка") } } } }
            }
        }
    }
}

/** The title's resting left edge: 1 px of list frame, 12 px of padding, the 22 px icon and 12 px gap. */
private val REST = 47.dp
private val OPEN = 144.dp
private const val STEPS = 20
