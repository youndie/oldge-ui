package io.github.youndie.oldge.containers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.harness.DemoText
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals

/** The rules in the Dialog and BottomSheet READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class WindowBehaviourTest {
    private val isDialog = SemanticsMatcher.keyIsDefined(SemanticsProperties.IsDialog)

    @Test
    fun a_dialog_title_is_a_heading_and_its_orb_closes_it() =
        runComposeUiTest {
            var closed by mutableIntStateOf(0)
            setContent { OldgeTheme { OldgeDialog("Удалить папку?", onClose = { closed++ }) { DemoText("Текст") } } }
            onNode(
                hasText("Удалить папку?") and SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading),
            ).assertExists()
            onNodeWithContentDescription("Закрыть").performClick()
            assertEquals(1, closed)
        }

    @Test
    fun without_on_close_there_is_no_close_orb() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeDialog("Готово") { DemoText("Текст") } } }
            onNodeWithContentDescription("Закрыть").assertDoesNotExist()
        }

    @Test
    fun only_a_modal_dialog_says_it_is_one_and_names_its_pane() =
        runComposeUiTest {
            var modal by mutableStateOf(false)
            setContent { OldgeTheme { OldgeDialog("Удалить папку?", modal = modal) { DemoText("Текст") } } }
            onNode(isDialog).assertDoesNotExist()
            modal = true
            onNode(
                isDialog and SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, "Удалить папку?"),
            ).assertExists()
        }

    @Test
    fun a_modal_dialog_takes_the_taps_meant_for_the_screen_under_it() =
        runComposeUiTest {
            var under by mutableIntStateOf(0)
            setContent {
                OldgeTheme {
                    Box(Modifier.width(390.dp).height(400.dp).testTag("screen")) {
                        Column { OldgeButton("Под диалогом", { under++ }) }
                        OldgeDialog("Выйти?", modal = true) { DemoText("Текст") }
                    }
                }
            }
            val button = onNodeWithText("Под диалогом").getUnclippedBoundsInRoot()
            onNodeWithTag("screen").performTouchInput {
                click(Offset(((button.left + button.right) / 2).toPx(), ((button.top + button.bottom) / 2).toPx()))
            }
            assertEquals(0, under)
        }

    @Test
    fun a_closed_dialog_and_a_closed_sheet_draw_nothing() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column {
                        OldgeDialog("Диалог", open = false) { DemoText("Текст") }
                        OldgeBottomSheet("Шторка", open = false) { DemoText("Текст") }
                    }
                }
            }
            onNodeWithText("Диалог").assertDoesNotExist()
            onNodeWithText("Шторка").assertDoesNotExist()
        }

    @Test
    fun over_a_screen_the_sheet_is_a_dialog_and_its_scrim_closes_it() =
        runComposeUiTest {
            var closed by mutableIntStateOf(0)
            setContent {
                OldgeTheme {
                    Box(Modifier.width(390.dp).height(400.dp).testTag("screen")) {
                        OldgeBottomSheet("Шторка", onClose = { closed++ }) { DemoText("Текст") }
                    }
                }
            }
            onNode(isDialog and SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, "Шторка")).assertExists()
            // The top of the screen is scrim.
            onNodeWithTag("screen").performTouchInput { click(Offset(centerX, 20.dp.toPx())) }
            assertEquals(1, closed)
            onNodeWithContentDescription("Закрыть").performClick()
            assertEquals(2, closed)
        }

    @Test
    fun an_inline_sheet_is_not_a_dialog() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeBottomSheet("Отправить файл", inline = true) { DemoText("Текст") } } }
            onNodeWithText("Отправить файл").assertExists()
            onNode(isDialog).assertDoesNotExist()
        }

    @Test
    fun over_a_screen_the_sheets_body_is_at_most_70_percent_of_it() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Box(Modifier.width(390.dp).height(400.dp)) {
                        OldgeBottomSheet("Шторка", onClose = {}) {
                            Column(Modifier.testTag("long")) { repeat(30) { DemoText("Строка $it") } }
                        }
                    }
                }
            }
            // Long content: the body stops at 280 dp and scrolls the rest. The sheet is then its 8 + 6
            // + 2 dp tab, the 44 dp bar, that body and its 6 dp frame below.
            val sheet = onNode(isDialog).getUnclippedBoundsInRoot()
            assertEquals(346.dp, sheet.bottom - sheet.top)
        }
}
