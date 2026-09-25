package io.github.youndie.oldge.sample

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals

/** What the two pages demonstrate that is interaction rather than look (B-37). */
@OptIn(ExperimentalTestApi::class)
class ScreenBehaviourTest {
    @Test
    fun sign_in_switches_from_the_password_to_an_sms_code_and_back() =
        runComposeUiTest {
            setContent { OldgeTheme(reducedMotion = true) { AuthScreen(Modifier.size(390.dp, 760.dp)) } }
            onNode(hasSetTextAction() and hasContentDescription("Эл. почта", substring = true)).assertExists()
            onNodeWithText("Код из СМС", useUnmergedTree = true).assertDoesNotExist()

            onNodeWithText("Код", useUnmergedTree = true).performClick()
            onNodeWithText("Код из СМС", useUnmergedTree = true).assertExists()
            onNodeWithText("Отправить ещё раз", useUnmergedTree = true).assertExists()
            onNode(hasText("Отправить ещё раз") and hasClickAction()).assertIsNotEnabled()
            onNode(hasSetTextAction() and hasContentDescription("Эл. почта", substring = true)).assertDoesNotExist()

            onNodeWithText("Пароль", useUnmergedTree = true).performClick()
            onNode(hasSetTextAction() and hasContentDescription("Эл. почта", substring = true)).assertExists()
        }

    @Test
    fun a_sent_message_joins_my_run_and_takes_the_runs_status() =
        runComposeUiTest {
            setContent { OldgeTheme(reducedMotion = true) { ChatScreen(Modifier.size(390.dp, 760.dp)) } }

            fun count(status: String) =
                onAllNodes(hasContentDescription(status), useUnmergedTree = true).fetchSemanticsNodes().size
            // The preview's run of mine ends in one «прочитано».
            assertEquals(1, count("прочитано"))
            assertEquals(0, count("отправлено"))

            onNode(hasSetTextAction()).performTextInput("Отлично")
            onNodeWithContentDescription("Отправить").performClick()

            onNodeWithText("Отлично", useUnmergedTree = true).assertExists()
            // The new message is the last of the run now: the status is on it, and only on it.
            assertEquals(0, count("прочитано"))
            assertEquals(1, count("отправлено"))
        }

    @Test
    fun the_inbox_filters_show_the_empty_state_and_the_error_and_the_refresh_ends() =
        runComposeUiTest {
            setContent {
                OldgeTheme(
                    reducedMotion = true,
                ) { InboxScreen(Modifier.size(390.dp, 760.dp), refreshMillis = 1600) }
            }
            // It opens refreshing, as the page does, and stops when the refresh is done.
            onNode(hasContentDescription("Обновляю")).assertExists()
            mainClock.advanceTimeBy(2000)
            onNode(hasContentDescription("Обновляю")).assertDoesNotExist()
            onNodeWithText("Иван Ли", useUnmergedTree = true).assertExists()

            onNodeWithText("Непрочитанные", useUnmergedTree = true).performClick()
            onNodeWithText("Всё прочитано", useUnmergedTree = true).assertExists()
            onNodeWithText("Иван Ли", useUnmergedTree = true).assertDoesNotExist()

            onNode(
                hasText("Архив") and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox),
            ).performClick()
            onNodeWithText("Архив не загрузился", useUnmergedTree = true).assertExists()
            onNode(hasText("Повторить") and hasClickAction()).assertExists()

            onNodeWithText("Все", useUnmergedTree = true).performClick()
            onNodeWithText("Иван Ли", useUnmergedTree = true).assertExists()
        }

    @Test
    fun the_photo_turns_on_a_swipe_hides_the_interface_on_a_tap_and_the_heart_toggles() =
        runComposeUiTest {
            setContent {
                OldgeTheme(
                    reducedMotion = true,
                ) { MediaScreen(Modifier.size(390.dp, 760.dp).testTag("media")) }
            }
            onNodeWithText("2 из 148", useUnmergedTree = true).assertExists()
            onNodeWithText("Серпантин", useUnmergedTree = true).assertExists()

            // A swipe left past 40 dp turns to the next photograph, one of less does not.
            onNodeWithTag("media").performTouchInput { swipeLeft(startX = centerX + 20.dp.toPx(), endX = centerX) }
            onNodeWithText("2 из 148", useUnmergedTree = true).assertExists()
            onNodeWithTag("media").performTouchInput {
                swipeLeft(
                    startX = centerX + 100.dp.toPx(),
                    endX =
                        centerX - 100.dp.toPx(),
                )
            }
            onNodeWithText("3 из 148", useUnmergedTree = true).assertExists()
            onNodeWithText("Старый город", useUnmergedTree = true).assertExists()

            onNodeWithContentDescription("В избранное").performClick()
            onNodeWithContentDescription("Убрать из избранного").assertExists()

            onNodeWithTag("media").performTouchInput { click(center) }
            onNodeWithText("3 из 148", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("media").performTouchInput { click(center) }
            onNodeWithText("3 из 148", useUnmergedTree = true).assertExists()
        }
}
