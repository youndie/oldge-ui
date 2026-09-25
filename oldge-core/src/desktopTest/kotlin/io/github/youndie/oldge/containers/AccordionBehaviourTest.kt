package io.github.youndie.oldge.containers

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.harness.DemoText
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeDurations
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/** The rules in the Accordion README that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class AccordionBehaviourTest {
    @Test
    fun an_accordion_is_open_by_default_and_its_header_folds_it() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeAccordion("Приложения", icon = OldgeIcons.Grid) { DemoText("Документы") } } }
            onNodeWithText("Документы").assertExists()
            onNodeWithText("Приложения")
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
                .assertHeightIsAtLeast(44.dp)
                .performClick()
            onNodeWithText("Документы").assertDoesNotExist()
            onNodeWithText("Приложения").performClick()
            onNodeWithText("Документы").assertExists()
        }

    @Test
    fun a_closed_one_hides_its_body() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeAccordion("Инструменты", defaultOpen = false) { DemoText("Скрыто") } } }
            onNodeWithText("Скрыто").assertDoesNotExist()
        }

    @Test
    fun a_controlled_one_asks_and_stays_as_its_caller_keeps_it() =
        runComposeUiTest {
            val asked = mutableListOf<Boolean>()
            setContent {
                OldgeTheme {
                    OldgeAccordion(
                        "Справка",
                        open = false,
                        onToggle = { asked += it },
                    ) { DemoText("Скрыто") }
                }
            }
            onNodeWithText("Справка").performClick()
            assertEquals(listOf(true), asked)
            onNodeWithText("Скрыто").assertDoesNotExist()
        }

    @Test
    fun the_header_offers_expand_when_closed_and_collapse_when_open() =
        runComposeUiTest {
            var open by mutableStateOf(false)
            setContent {
                OldgeTheme {
                    OldgeAccordion(
                        "Справка",
                        open = open,
                        onToggle = { open = it },
                    ) { DemoText("Тело") }
                }
            }
            onNodeWithText("Справка")
                .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.Expand))
                .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.Collapse))
                .performSemanticsAction(SemanticsActions.Expand)
            assertEquals(true, open)
            onNodeWithText("Справка")
                .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.Collapse))
                .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.Expand))
                .performSemanticsAction(SemanticsActions.Collapse)
            assertEquals(false, open)
        }

    @Test
    fun the_chevron_turns_with_a_bounce_and_does_not_animate_under_reduced_motion() {
        fun frames(reduced: Boolean): Pair<List<Any>, List<Any>> {
            var pair: Pair<List<Any>, List<Any>> = emptyList<Any>() to emptyList()
            runComposeUiTest {
                mainClock.autoAdvance = false
                var open by mutableStateOf(false)
                setContent {
                    OldgeTheme(reducedMotion = reduced) {
                        // The header alone: the body is not composed while closed.
                        Box(Modifier.testTag("acc")) { OldgeAccordion("Справка", open = open, onToggle = {}) {} }
                    }
                }
                mainClock.advanceTimeBy(1000)
                open = true
                mainClock.advanceTimeByFrame()
                mainClock.advanceTimeBy(OldgeDurations.base / 3L)
                val mid = grab()
                mainClock.advanceTimeBy(2000)
                pair = mid to grab()
            }
            return pair
        }
        frames(reduced = false).let { (mid, rest) -> assertNotEquals(mid, rest, "nothing turned") }
        frames(reduced = true).let { (mid, rest) -> assertEquals(mid, rest, "it animated under reduced motion") }
    }
}

@OptIn(ExperimentalTestApi::class)
private fun androidx.compose.ui.test.ComposeUiTest.grab(): List<Any> {
    // The chevron's box alone: 26 px, 4 px of margin and 5 px of padding and frame from the end,
    // rows 9 to 35 of the 44 px header. Lower down the header's rounded corner lets the unfolding
    // body show, which moved while the chevron stood still (B-22's `acc-chevron-still`).
    val m = onNodeWithTag("acc").captureToImage().toPixelMap()
    val px = density.density
    val right = m.width - (9 * px).toInt()
    val left = m.width - (36 * px).toInt()
    return ((9 * px).toInt() until (36 * px).toInt()).flatMap { y -> (left until right).map { x -> m[x, y] } }
}
