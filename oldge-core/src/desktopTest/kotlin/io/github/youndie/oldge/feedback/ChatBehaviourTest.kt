package io.github.youndie.oldge.feedback

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.test.withKeyDown
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/** The rules in the ChatBubble, TypingIndicator and Composer READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class ChatBehaviourTest {
    private val long =
        "Очень длинное сообщение, которое никак не уместится в одну строку и займёт всю ширину ленты целиком"

    @Test
    fun a_bubble_is_at_most_86_percent_of_the_lane_mine_at_its_end_theirs_at_its_start() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column(Modifier.width(400.dp).testTag("lane")) {
                        OldgeChatBubble(long, OldgeChatSide.Them, Modifier.testTag("them"))
                        OldgeChatBubble(long, OldgeChatSide.Me, Modifier.testTag("me"))
                    }
                }
            }

            fun text(side: String) =
                onAllNodes(hasText(long), useUnmergedTree = true)[
                    if (side ==
                        "them"
                    ) {
                        0
                    } else {
                        1
                    },
                ].getUnclippedBoundsInRoot()
            val them = text("them")
            val me = text("me")
            // The text inside the 86 % (344 dp) row, less the body's 1 + 12 dp each side.
            assertTrue(them.right - them.left <= 344.dp - 26.dp, "their bubble is too wide")
            assertTrue(them.left < 20.dp, "theirs is not at the lane's start: ${them.left}")
            assertTrue(me.right > 380.dp, "mine is not at the lane's end: ${me.right}")
        }

    @Test
    fun my_status_is_said_and_theirs_has_none() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column {
                        OldgeChatBubble("Смотрю", OldgeChatSide.Me, time = "09:14", status = OldgeMessageStatus.Read)
                        OldgeChatBubble("Можно?", OldgeChatSide.Me, time = "09:14", status = OldgeMessageStatus.Sent)
                        OldgeChatBubble("Да", OldgeChatSide.Them, time = "09:15", status = OldgeMessageStatus.Read)
                    }
                }
            }
            assertEquals(
                1,
                onAllNodes(hasContentDescription("прочитано"), useUnmergedTree = true).fetchSemanticsNodes().size,
            )
            assertEquals(
                1,
                onAllNodes(hasContentDescription("отправлено"), useUnmergedTree = true).fetchSemanticsNodes().size,
            )
        }

    @Test
    fun the_typing_indicator_says_who_types_and_its_dots_hop_but_not_under_reduced_motion() {
        fun frames(reduced: Boolean): Pair<List<Any>, List<Any>> {
            var pair: Pair<List<Any>, List<Any>> = emptyList<Any>() to emptyList()
            runComposeUiTest {
                mainClock.autoAdvance = false
                setContent {
                    OldgeTheme(
                        reducedMotion = reduced,
                    ) { Box(Modifier.testTag("t")) { OldgeTypingIndicator(name = "Анна") } }
                }
                mainClock.advanceTimeBy(1000)
                onNode(
                    hasContentDescription("Анна печатает") and
                        SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite),
                ).assertExists()
                val a = grab("t")
                mainClock.advanceTimeBy(300)
                pair = a to grab("t")
            }
            return pair
        }
        frames(reduced = false).let { (a, b) -> assertNotEquals(a, b, "the dots stood still") }
        frames(reduced = true).let { (a, b) -> assertEquals(a, b, "the dots hopped under reduced motion") }
    }

    @Test
    fun send_is_off_on_an_empty_field_and_sends_and_clears_the_text() =
        runComposeUiTest {
            val sent = mutableListOf<String>()
            setContent { OldgeTheme { OldgeComposer({ sent += it }) } }
            onNodeWithContentDescription("Отправить").assertIsNotEnabled()
            onNode(hasSetTextAction()).performTextInput("   ")
            onNodeWithContentDescription("Отправить").assertIsNotEnabled()
            // Enter reaches the send itself, and a blank field still sends nothing.
            onNode(hasSetTextAction()).performKeyInput { pressKey(Key.Enter) }
            assertEquals(emptyList(), sent)
            onNode(hasSetTextAction()).performTextInput("Привет")
            onNodeWithContentDescription("Отправить").assertIsEnabled().performClick()
            assertEquals(listOf("   Привет"), sent)
            onNode(
                hasSetTextAction(),
            ).assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.EditableText,
                    androidx.compose.ui.text
                        .AnnotatedString(""),
                ),
            )
        }

    @Test
    fun enter_sends_and_shift_enter_breaks_the_line() =
        runComposeUiTest {
            val sent = mutableListOf<String>()
            setContent { OldgeTheme { OldgeComposer({ sent += it }) } }
            val field = onNode(hasSetTextAction())
            field.performTextInput("Раз")
            field.performKeyInput { withKeyDown(Key.ShiftLeft) { pressKey(Key.Enter) } }
            assertEquals(emptyList(), sent)
            field.performKeyInput { pressKey(Key.Enter) }
            assertEquals(1, sent.size)
            assertTrue(
                sent[0].startsWith("Раз") && sent[0].contains("\n"),
                "Shift+Enter did not break the line: ${sent[0]}",
            )
        }

    @Test
    fun the_attach_orb_is_there_unless_it_is_turned_off() =
        runComposeUiTest {
            var attach by mutableStateOf<(() -> Unit)?>({})
            setContent { OldgeTheme { OldgeComposer({}, onAttach = attach) } }
            onNodeWithContentDescription("Прикрепить").assertExists()
            attach = null
            onNodeWithContentDescription("Прикрепить").assertDoesNotExist()
        }

    @Test
    fun the_field_grows_to_120_px_and_no_further() =
        runComposeUiTest {
            var text by mutableStateOf("Строка")
            setContent { OldgeTheme { OldgeComposer({}, value = text, onValueChange = { text = it }) } }

            fun height() = onNode(hasSetTextAction()).getUnclippedBoundsInRoot().let { it.bottom - it.top }
            val one = height()
            text = List(3) { "Строка $it" }.joinToString("\n")
            val three = height()
            text = List(12) { "Строка $it" }.joinToString("\n")
            val twelve = height()
            assertEquals(40.dp, three - one, "three lines are not two lines taller than one")
            // The 9 px padding round a content box of at most 120.
            assertEquals(138.dp, twelve)
        }
}

@OptIn(ExperimentalTestApi::class)
private fun androidx.compose.ui.test.ComposeUiTest.grab(tag: String): List<Any> {
    val m = onNodeWithTag(tag).captureToImage().toPixelMap()
    return (0 until m.height).flatMap { y -> (0 until m.width).map { x -> m[x, y] } }
}
