package io.github.youndie.oldge.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.AnnotatedString
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * B-86: a choice's description is part of the choice — a press on it toggles the box or picks the
 * option, and a screen reader hears it after the label, in the control's own node.
 */
@OptIn(ExperimentalTestApi::class)
class CheckDescriptionTest {
    private fun heard(vararg lines: String) =
        SemanticsMatcher.expectValue(SemanticsProperties.Text, lines.map { AnnotatedString(it) })

    @Test
    fun a_checkbox_description_is_heard_after_the_label_and_a_press_on_it_toggles() =
        runComposeUiTest {
            var on by mutableStateOf(false)
            setContent {
                OldgeTheme {
                    OldgeCheckbox("Удалить скачанные данные", on, { on = it }, description = "~/Downloads")
                }
            }
            onNode(hasText("Удалить скачанные данные")).assert(heard("Удалить скачанные данные", "~/Downloads"))
            onNodeWithText("~/Downloads", useUnmergedTree = true).performClick()
            onNode(hasText("Удалить скачанные данные")).assertIsOn()
        }

    @Test
    fun a_radio_description_is_heard_after_the_label_and_a_press_on_it_picks_the_option() =
        runComposeUiTest {
            var picked by mutableStateOf("any")
            setContent {
                OldgeTheme {
                    Column {
                        OldgeRadioGroup(
                            "Порядок",
                            listOf(
                                OldgeRadioOption("any", "Как удобнее"),
                                OldgeRadioOption("seq", "По порядку", description = "Раздаче хуже"),
                            ),
                            picked,
                            { picked = it },
                        )
                    }
                }
            }
            onNode(hasText("По порядку")).assert(heard("По порядку", "Раздаче хуже"))
            onNodeWithText("Раздаче хуже", useUnmergedTree = true).performClick()
            assertEquals("seq", picked)
        }
}
