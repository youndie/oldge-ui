package io.github.youndie.oldge.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * B-72: a read-only field is a current value, not an unavailable one; the label can be left out of
 * the drawing; and a screen reader hears the label once, as the field's name.
 */
@OptIn(ExperimentalTestApi::class)
class TextFieldReadOnlyLabelTest {
    private val folder = "/home/pavel/Downloads"

    @Test
    fun a_read_only_field_is_drawn_as_an_enabled_one() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column(Modifier.width(320.dp)) {
                        OldgeTextField("Папка", folder, {}, Modifier.testTag("live"))
                        OldgeTextField("Папка", folder, {}, Modifier.testTag("read"), readOnly = true)
                    }
                }
            }
            val live = onNodeWithTag("live").captureToImage().toPixelMap()
            val read = onNodeWithTag("read").captureToImage().toPixelMap()
            assertEquals(live.width to live.height, read.width to read.height)
            for (x in 0 until live.width) {
                for (y in 0 until live.height) {
                    assertEquals(live[x, y], read[x, y], "pixel ($x, $y)")
                }
            }
        }

    @Test
    fun a_read_only_field_is_enabled_focusable_and_selectable_and_takes_no_typing() =
        runComposeUiTest {
            var v by mutableStateOf(folder)
            setContent { OldgeTheme { OldgeTextField("Папка", v, { v = it }, readOnly = true) } }
            val field = onNodeWithContentDescription("Папка")
            field.assertIsEnabled()
            field.assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Disabled))
            field.requestFocus().assertIsFocused()
            field.performTextInputSelection(TextRange(0, 5))
            field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(0, 5)))
            // Its text can be copied, and there is no way to type into it: the node has no SetText at all.
            field.assert(SemanticsMatcher.keyIsDefined(SemanticsActions.CopyText))
            field.assert(SemanticsMatcher.keyNotDefined(SemanticsActions.SetText))
            field.assert(SemanticsMatcher.expectValue(SemanticsProperties.IsEditable, false))
            assertEquals(folder, v)
        }

    @Test
    fun a_visible_label_is_heard_once_as_the_fields_name() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeTextField("Имя профиля", "Павел", {}) } }
            onNodeWithContentDescription("Имя профиля").assertExists()
            onAllNodesWithText("Имя профиля").assertCountEquals(0)
            onAllNodes(hasContentDescription("Имя профиля") or hasText("Имя профиля"), useUnmergedTree = true)
                .assertCountEquals(1)
        }

    @Test
    fun without_its_label_the_field_is_only_the_well_and_still_named() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column {
                        OldgeTextField("Имя", "Павел", {}, Modifier.testTag("shown"))
                        OldgeTextField("Имя2", "Павел", {}, Modifier.testTag("hidden"), showLabel = false)
                    }
                }
            }
            onNodeWithContentDescription("Имя2").assertExists()

            fun height(tag: String) = onNodeWithTag(tag).getBoundsInRoot().let { it.bottom - it.top }
            // The label's 16 px line and the 6 px gap are gone; the 44 px box is what is left.
            assertEquals(44.dp, height("hidden"))
            assertEquals(height("shown") - 22.dp, height("hidden"))
        }
}
