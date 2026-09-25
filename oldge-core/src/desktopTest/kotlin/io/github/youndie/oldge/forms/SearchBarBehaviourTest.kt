package io.github.youndie.oldge.forms

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/** The rules in the SearchBar README that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class SearchBarBehaviourTest {
    @Test
    fun the_field_is_named_by_its_label_or_its_placeholder() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeSearchBar(placeholder = "Файлы, папки, теги") } }
            onNode(hasSetTextAction() and hasContentDescription("Файлы, папки, теги")).assertExists()
        }

    @Test
    fun the_clear_button_comes_with_text_is_named_and_empties_the_field() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeSearchBar() } }
            onNodeWithContentDescription("Очистить").assertDoesNotExist()
            onNode(hasSetTextAction()).performTextInput("отчёт")
            onNodeWithContentDescription("Очистить").performClick()
            onNode(
                hasSetTextAction(),
            ).assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString("")))
            onNodeWithContentDescription("Очистить").assertDoesNotExist()
        }

    @Test
    fun enter_submits_the_text() =
        runComposeUiTest {
            val submitted = mutableListOf<String>()
            var text by mutableStateOf("отчёт")
            setContent {
                OldgeTheme {
                    OldgeSearchBar(value = text, onValueChange = { text = it }, onSubmit = {
                        submitted +=
                            it
                    })
                }
            }
            // Typed into first, as a user does: the field has focus when Enter comes.
            onNode(hasSetTextAction()).requestFocus().performKeyInput { pressKey(Key.Enter) }
            assertEquals(listOf("отчёт"), submitted)
        }

    @Test
    fun the_magnifier_wiggles_on_focus_and_not_under_reduced_motion() {
        fun frames(reduced: Boolean): Pair<List<Any>, List<Any>> {
            var pair: Pair<List<Any>, List<Any>> = emptyList<Any>() to emptyList()
            runComposeUiTest {
                mainClock.autoAdvance = false
                val source = MutableInteractionSource()
                setContent {
                    OldgeTheme(reducedMotion = reduced) {
                        Box(Modifier.width(360.dp).testTag("s")) { OldgeSearchBar(interactionSource = source) }
                    }
                }
                mainClock.advanceTimeBy(500)
                runBlocking { source.emit(FocusInteraction.Focus()) }
                mainClock.advanceTimeByFrame()
                mainClock.advanceTimeBy(OLDGE_SLOW / 4)
                val mid = glass()
                mainClock.advanceTimeBy(1000)
                pair = mid to glass()
            }
            return pair
        }
        frames(reduced = false).let { (mid, rest) -> assertNotEquals(rest, mid, "the magnifier stood still") }
        frames(reduced = true).let { (mid, rest) ->
            assertEquals(rest, mid, "the magnifier wiggled under reduced motion")
        }
    }
}

private const val OLDGE_SLOW = 420L

/** The magnifier's 20 px, 13 px in from the bar's start and centred in its 48 px. */
@OptIn(ExperimentalTestApi::class)
private fun androidx.compose.ui.test.ComposeUiTest.glass(): List<Any> {
    val m = onNodeWithTag("s").captureToImage().toPixelMap()
    val px = density.density
    return ((14 * px).toInt() until (34 * px).toInt()).flatMap { y ->
        ((13 * px).toInt() until (33 * px).toInt()).map { x -> m[x, y] }
    }
}
