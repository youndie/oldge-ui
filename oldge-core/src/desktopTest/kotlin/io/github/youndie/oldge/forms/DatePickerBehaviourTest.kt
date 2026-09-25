package io.github.youndie.oldge.forms

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.youndie.oldge.theme.OldgeTheme
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The rules in the DatePicker README that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class DatePickerBehaviourTest {
    private val today = LocalDate(2026, 9, 24)

    @Test
    fun each_day_is_named_in_full_and_choosing_one_reports_it() =
        runComposeUiTest {
            var picked by mutableStateOf<LocalDate?>(today)
            setContent { OldgeTheme { OldgeDatePicker(picked, { picked = it }, today = today) } }
            onNodeWithContentDescription("24 сентябрь 2026").assertIsSelected()
            onNodeWithContentDescription("3 сентябрь 2026").assertIsNotSelected().performClick()
            assertEquals(LocalDate(2026, 9, 3), picked)
            onNodeWithContentDescription("3 сентябрь 2026").assertIsSelected()
        }

    @Test
    fun the_week_starts_on_monday() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeDatePicker(null, {}, today = today) } }
            // 1 September 2026 is a Tuesday: the second column, right of Monday the 7th.
            val tuesday = onNodeWithContentDescription("1 сентябрь 2026").getBoundsInRoot()
            val monday = onNodeWithContentDescription("7 сентябрь 2026").getBoundsInRoot()
            val sunday = onNodeWithContentDescription("6 сентябрь 2026").getBoundsInRoot()
            assertTrue(
                monday.left < tuesday.left && tuesday.left < sunday.left,
                "Mon $monday, Tue $tuesday, Sun $sunday",
            )
            assertTrue(
                sunday.top == tuesday.top && monday.top > tuesday.top,
                "the 6th ends the first week, the 7th starts the second",
            )
        }

    @Test
    fun the_arrows_turn_the_month_and_are_named() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeDatePicker(today, {}, today = today) } }
            onNodeWithContentDescription("Следующий месяц").performClick()
            onNodeWithContentDescription("1 октябрь 2026").assertExists()
            onNodeWithContentDescription("Предыдущий месяц").performClick()
            onNodeWithContentDescription("Предыдущий месяц").performClick()
            onNodeWithContentDescription("31 август 2026").assertExists()
        }
}
