package io.github.youndie.oldge.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The rules in the TextField README that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class TextFieldBehaviourTest {
    @Test
    fun the_label_names_the_field_and_typing_changes_its_value() =
        runComposeUiTest {
            var v by mutableStateOf("")
            setContent { OldgeTheme { OldgeTextField("Имя профиля", v, { v = it }) } }
            onNodeWithContentDescription("Имя профиля").performTextInput("Павел")
            assertEquals("Павел", v)
        }

    @Test
    fun an_error_marks_the_field_invalid_with_its_message() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    OldgeTextField(
                        "Эл. почта",
                        "pavel@",
                        {},
                        error = "Адрес неполный: нет домена",
                    )
                }
            }
            onNodeWithContentDescription("Эл. почта")
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Адрес неполный: нет домена"))
        }

    @Test
    fun the_reveal_button_says_what_it_will_do() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeTextField("Пароль", "secret", {}, reveal = true) } }
            onNodeWithContentDescription("Показать пароль").performClick()
            onNodeWithContentDescription("Скрыть пароль").performClick()
            onNodeWithContentDescription("Показать пароль").assertExists()
        }

    @Test
    fun a_disabled_field_takes_no_input() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeTextField("Недоступно", "Только чтение", {}, enabled = false) } }
            onNodeWithContentDescription("Недоступно").assertIsNotEnabled()
        }

    @Test
    fun the_height_is_a_minimum_and_grows_with_the_system_font() =
        runComposeUiTest {
            var scale by mutableStateOf(1f)
            setContent {
                val base = LocalDensity.current
                CompositionLocalProvider(LocalDensity provides Density(base.density, scale)) {
                    OldgeTheme { OldgeTextField("Имя профиля", "Павел", {}) }
                }
            }
            val normal = onNodeWithContentDescription("Имя профиля").getBoundsInRoot().let { it.bottom - it.top }
            scale = 2f
            waitForIdle()
            val large = onNodeWithContentDescription("Имя профиля").getBoundsInRoot().let { it.bottom - it.top }
            // The field is its 20 px line between the input's 11 px paddings.
            assertEquals(42.dp, normal, "one 20 px line at 100 %")
            assertTrue(large >= 62.dp, "a 40 px line at 200 %, got $large")
        }

    /**
     * The field's box is laid out as CSS lays `.og-field__box` out: its content inside the 1 px
     * border, at least `hit-min` in all (B-53). Measured in Chrome with `scripts/probes/FieldProbe`: 44 px
     * with a value or empty, 46 round the reveal orb's 44 px box, 84 for the default three rows.
     */
    @Test
    fun the_box_is_44_empty_or_not_46_round_the_reveal_orb_and_84_with_three_lines() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column {
                        OldgeTextField(
                            "Имя",
                            "Павел",
                            {},
                            androidx.compose.ui.Modifier
                                .testTag("value"),
                        )
                        OldgeTextField(
                            "Поиск",
                            "",
                            {},
                            androidx.compose.ui.Modifier
                                .testTag("empty"),
                            placeholder = "Файлы",
                        )
                        OldgeTextField(
                            "Пароль",
                            "secret",
                            {},
                            androidx.compose.ui.Modifier
                                .testTag("reveal"),
                            reveal = true,
                        )
                        OldgeTextField(
                            "Комментарий",
                            "",
                            {},
                            androidx.compose.ui.Modifier
                                .testTag("lines"),
                            lines = 3,
                        )
                    }
                }
            }

            // The whole field is the label's 16 px line, the 6 px gap and the box.
            fun box(tag: String) = onNodeWithTag(tag).getBoundsInRoot().let { it.bottom - it.top } - 22.dp
            assertEquals(
                listOf(44.dp, 44.dp, 46.dp, 84.dp),
                listOf(box("value"), box("empty"), box("reveal"), box("lines")),
                "value, empty, reveal, three lines",
            )
        }
}
