package io.github.youndie.oldge.feedback

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.harness.DemoText
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/** The rules in the Banner, Snackbar and EmptyState READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class NoticeBehaviourTest {
    private fun live(mode: LiveRegionMode) = SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, mode)

    @Test
    fun a_banner_is_a_polite_status_with_its_actions_as_buttons() =
        runComposeUiTest {
            var later by mutableIntStateOf(0)
            setContent {
                OldgeTheme {
                    OldgeBanner(title = "Мало места", actions = listOf(OldgeBannerAction("Позже", { later++ }))) {
                        DemoText("Осталось 1,2 ГБ.")
                    }
                }
            }
            onNode(live(LiveRegionMode.Polite)).assertExists()
            onNodeWithText("Позже").performClick()
            assertEquals(1, later)
        }

    @Test
    fun closed_notices_draw_nothing() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column {
                        OldgeBanner(title = "Баннер", open = false)
                        OldgeSnackbar("Снэкбар", inline = true, open = false)
                    }
                }
            }
            onNodeWithText("Баннер").assertDoesNotExist()
            onNodeWithText("Снэкбар").assertDoesNotExist()
        }

    @Test
    fun a_snackbar_goes_by_itself_after_its_duration() =
        runComposeUiTest {
            mainClock.autoAdvance = false
            var closed by mutableIntStateOf(0)
            setContent {
                OldgeTheme {
                    OldgeSnackbar(
                        "Файл перемещён",
                        onClose = { closed++ },
                        durationMillis = 4000,
                        inline = true,
                    )
                }
            }
            mainClock.advanceTimeBy(3900)
            assertEquals(0, closed)
            mainClock.advanceTimeBy(200)
            assertEquals(1, closed)
        }

    @Test
    fun a_snackbar_offers_its_action_and_its_close_orb() =
        runComposeUiTest {
            var undone by mutableIntStateOf(0)
            var closed by mutableIntStateOf(0)
            setContent {
                OldgeTheme {
                    OldgeSnackbar("Файл перемещён", actionLabel = "Отменить", onAction = {
                        undone++
                    }, onClose = { closed++ }, inline = true)
                }
            }
            onNode(live(LiveRegionMode.Polite)).assertExists()
            onNodeWithText("Отменить").performClick()
            onNodeWithContentDescription("Закрыть").performClick()
            assertEquals(1 to 1, undone to closed)
        }

    @Test
    fun over_a_screen_a_snackbar_leaves_the_screen_in_reach() =
        runComposeUiTest {
            var pressed by mutableIntStateOf(0)
            setContent {
                OldgeTheme {
                    Box(Modifier.width(390.dp).height(300.dp)) {
                        Column { OldgeButton("Под снэкбаром", { pressed++ }) }
                        OldgeSnackbar("Файл перемещён")
                    }
                }
            }
            onNodeWithText("Под снэкбаром").performClick()
            assertEquals(1, pressed)
            // And it keeps to the bottom, 16 dp above it.
            assertEquals(284.dp, onNode(live(LiveRegionMode.Polite)).getUnclippedBoundsInRoot().bottom)
        }

    @Test
    fun an_error_needs_an_action() {
        assertFailsWith<IllegalArgumentException> {
            runComposeUiTest {
                setContent {
                    OldgeTheme {
                        OldgeEmptyState(
                            "Не удалось загрузить",
                            tone = OldgeEmptyTone.Error,
                        )
                    }
                }
            }
        }
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    OldgeEmptyState(
                        "Не удалось загрузить",
                        tone = OldgeEmptyTone.Error,
                        action = { OldgeButton("Повторить", {}) },
                    )
                }
            }
            onNodeWithText("Повторить").assertExists()
        }
    }

    @Test
    fun errors_and_offline_are_announced_and_an_empty_state_is_not() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column {
                        OldgeEmptyState("Всё прочитано")
                        OldgeEmptyState("Нет связи", tone = OldgeEmptyTone.Offline)
                    }
                }
            }
            onNode(
                hasText("Всё прочитано") and SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading),
            ).assertExists()
            assertEquals(1, onAllNodes(live(LiveRegionMode.Assertive)).fetchSemanticsNodes().size)
            onNode(live(LiveRegionMode.Assertive) and hasAnyDescendant(hasText("Нет связи"))).assertExists()
        }

    @Test
    fun an_empty_states_sentence_is_at_most_30ch_wide() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    OldgeEmptyState(
                        "Пусто",
                        Modifier.width(390.dp),
                        text = "Здесь появятся письма, как только кто-нибудь напишет вам хоть что-нибудь.",
                    )
                }
            }
            val text = onNodeWithText("Здесь", substring = true, useUnmergedTree = true).getUnclippedBoundsInRoot()
            // 30 × 0.572 em of 15 px: 257.5 dp, which a constraint rounds to a whole pixel.
            assertTrue(text.right - text.left <= 258.dp, "the sentence is ${text.right - text.left} wide")
            assertTrue(text.right - text.left > 200.dp, "the sentence is ${text.right - text.left} wide")
        }
}
