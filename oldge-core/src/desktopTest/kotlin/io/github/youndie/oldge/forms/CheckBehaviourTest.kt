package io.github.youndie.oldge.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** The rules in the Checkbox and RadioGroup READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class CheckBehaviourTest {
    private fun role(role: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

    @Test
    fun the_whole_checkbox_row_is_a_44_px_checkbox_and_its_label_toggles_it() =
        runComposeUiTest {
            var on by mutableStateOf(false)
            setContent { OldgeTheme { OldgeCheckbox("Присылать сводку по почте", on, { on = it }) } }
            onNodeWithText("Присылать сводку по почте")
                .assertHeightIsAtLeast(44.dp)
                .assert(role(Role.Checkbox))
                .assertIsOff()
                .performClick()
            onNodeWithText("Присылать сводку по почте").assertIsOn()
        }

    @Test
    fun a_disabled_checkbox_or_radio_ignores_taps() =
        runComposeUiTest {
            var on by mutableStateOf(true)
            var picked by mutableStateOf("a")
            setContent {
                OldgeTheme {
                    Column {
                        OldgeCheckbox("Шифрование", on, { on = it }, enabled = false)
                        OldgeRadioGroup(
                            "Группа",
                            listOf(OldgeRadioOption("a", "Первый"), OldgeRadioOption("b", "Второй", enabled = false)),
                            picked,
                            { picked = it },
                        )
                    }
                }
            }
            onNodeWithText("Шифрование").assertIsNotEnabled().performClick()
            onNodeWithText("Второй").assertIsNotEnabled().performClick()
            assertEquals(true, on)
            assertEquals("a", picked)
        }

    @Test
    fun a_radio_row_is_a_44_px_radio_button_and_choosing_it_reports_its_value() =
        runComposeUiTest {
            var picked by mutableStateOf("hi")
            setContent {
                OldgeTheme {
                    OldgeRadioGroup(
                        "Качество загрузки",
                        listOf(OldgeRadioOption("orig", "Оригинал"), OldgeRadioOption("hi", "Высокое")),
                        picked,
                        { picked = it },
                    )
                }
            }
            onNodeWithText("Высокое").assertIsSelected()
            onNodeWithText("Оригинал").assertHeightIsAtLeast(44.dp).assert(role(Role.RadioButton)).performClick()
            assertEquals("orig", picked)
        }

    @Test
    fun a_radio_group_takes_two_to_six_options() {
        for (count in listOf(1, 7)) {
            assertFailsWith<IllegalArgumentException>("$count options") {
                runComposeUiTest {
                    setContent {
                        OldgeTheme { OldgeRadioGroup("N", (1..count).map { OldgeRadioOption(it, "$it") }, 1, {}) }
                    }
                }
            }
        }
    }
}
