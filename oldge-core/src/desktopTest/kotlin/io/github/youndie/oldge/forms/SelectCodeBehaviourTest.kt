package io.github.youndie.oldge.forms

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals

/** The rules in the Select and CodeInput READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class SelectCodeBehaviourTest {
    private val networks =
        listOf(
            OldgeSelectOption("wifi", "Только Wi-Fi"),
            OldgeSelectOption("any", "Любую сеть"),
            OldgeSelectOption("off", "Вручную"),
        )

    @Test
    fun a_select_is_a_named_drop_down_list_that_reads_its_choice_and_opens_to_change_it() =
        runComposeUiTest {
            var picked by mutableStateOf("wifi")
            setContent { OldgeTheme { OldgeSelect("Синхронизировать через", networks, picked, { picked = it }) } }
            onNodeWithContentDescription("Синхронизировать через")
                .assertHeightIsAtLeast(44.dp)
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.DropdownList))
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Только Wi-Fi"))
                .performClick()
            onNodeWithText("Вручную").performClick()
            assertEquals("off", picked)
            onNodeWithContentDescription("Синхронизировать через")
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Вручную"))
        }

    @Test
    fun the_code_field_is_one_real_field_for_a_one_time_code() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeCodeInput("Код из СМС", "", {}, error = "Код устарел — запросите новый") } }
            onNodeWithContentDescription("Код из СМС")
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentType, ContentType.SmsOtpCode))
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Код устарел — запросите новый"))
        }

    @Test
    fun only_digits_up_to_the_length_are_taken_and_the_last_one_completes_the_code() =
        runComposeUiTest {
            var code by mutableStateOf("")
            var completed by mutableIntStateOf(0)
            var last = ""
            setContent {
                OldgeTheme {
                    OldgeCodeInput("Код", code, { code = it }, length = 4, onComplete = {
                        completed++
                        last = it
                    })
                }
            }
            onNodeWithContentDescription("Код").performTextInput("1a2-3")
            assertEquals("123", code)
            assertEquals(0, completed)
            onNodeWithContentDescription("Код").performTextInput("45")
            assertEquals("1234", code)
            assertEquals(1, completed)
            assertEquals("1234", last)
        }
}
