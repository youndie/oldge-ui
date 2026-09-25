package io.github.youndie.oldge.feedback

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test

/** The rules in the Readout, Meter and ProgressBar READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class LcdBehaviourTest {
    private fun state(text: String) = SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, text)

    private fun range(info: ProgressBarRangeInfo) =
        SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, info)

    @Test
    fun a_readout_is_heard_as_its_label_value_and_unit() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeReadout("21.5", label = "Темп.", unit = "°C") } }
            onNodeWithContentDescription("Темп. 21.5 °C").assertExists()
        }

    @Test
    fun a_meter_is_heard_as_its_level_and_its_words() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeMeter(0.37f, "Хранилище", "23,4 / 64 ГБ") } }
            onNodeWithContentDescription("Хранилище")
                .assert(state("23,4 / 64 ГБ"))
                .assert(range(ProgressBarRangeInfo(0.37f, 0f..1f)))
        }

    @Test
    fun a_progress_bar_reports_its_value_or_that_it_has_none() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column {
                        OldgeProgressBar(0.45f, label = "Загрузка")
                        OldgeProgressBar(null, label = "Подключение…")
                    }
                }
            }
            onNodeWithContentDescription("Загрузка").assert(range(ProgressBarRangeInfo(0.45f, 0f..1f)))
            onNodeWithContentDescription("Подключение…").assert(range(ProgressBarRangeInfo.Indeterminate))
        }

    @Test
    fun done_and_error_say_so_in_a_word_not_only_a_colour() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column {
                        OldgeProgressBar(1f, label = "Проверка", status = OldgeProgressStatus.Done)
                        OldgeProgressBar(0.3f, label = "Отправка", status = OldgeProgressStatus.Error)
                    }
                }
            }
            onNodeWithText("Готово").assertExists()
            onNodeWithText("Ошибка").assertExists()
        }
}
