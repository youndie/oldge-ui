package io.github.youndie.oldge.sample

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
}

private fun hasClickAction() =
    androidx.compose.ui.test
        .hasClickAction()
