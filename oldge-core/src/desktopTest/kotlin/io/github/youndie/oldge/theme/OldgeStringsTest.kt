package io.github.youndie.oldge.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.containers.OldgeBottomSheet
import io.github.youndie.oldge.containers.OldgeDialog
import io.github.youndie.oldge.feedback.OldgeProgressBar
import io.github.youndie.oldge.feedback.OldgeSnackbar
import io.github.youndie.oldge.forms.OldgeDatePicker
import io.github.youndie.oldge.forms.OldgeSearchBar
import io.github.youndie.oldge.forms.OldgeTextField
import io.github.youndie.oldge.harness.DemoText
import io.github.youndie.oldge.navigation.OldgeBalloon
import io.github.youndie.oldge.navigation.OldgeNavDrawer
import io.github.youndie.oldge.navigation.OldgePageDots
import io.github.youndie.oldge.navigation.OldgeStepper
import io.github.youndie.oldge.navigation.drawerEntries
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The library's own accessible names come from [OldgeStrings] (B-71): an app in English hears its
 * own words on every orb and action the caller does not label, and none of the Russian defaults.
 */
@OptIn(ExperimentalTestApi::class)
class OldgeStringsTest {
    private val english =
        OldgeStrings(
            close = "Close",
            clear = "Clear",
            showPassword = "Show password",
            hidePassword = "Hide password",
            progress = "Progress",
            stepDone = "done",
            page = { n, count -> "Page $n of $count" },
            day = { d -> "${d.month.name.lowercase()} ${d.day}, ${d.year}" },
        )

    private fun ComposeUiTest.inEnglish(content: @Composable () -> Unit) =
        setContent { OldgeTheme(strings = english) { Box(Modifier.width(390.dp).height(600.dp)) { content() } } }

    private fun ComposeUiTest.noRussianClose() = onNodeWithContentDescription("Закрыть").assertDoesNotExist()

    @Test
    fun the_dialogs_close_orb_says_the_apps_word() =
        runComposeUiTest {
            inEnglish { OldgeDialog("Remove?", onClose = {}) { DemoText("Text") } }
            onNodeWithContentDescription("Close").assertExists()
            noRussianClose()
        }

    @Test
    fun the_bottom_sheets_close_orb_says_the_apps_word() =
        runComposeUiTest {
            inEnglish { OldgeBottomSheet("Sort", onClose = {}, inline = true) { DemoText("Text") } }
            onNodeWithContentDescription("Close").assertExists()
            noRussianClose()
        }

    @Test
    fun the_snackbars_close_orb_says_the_apps_word() =
        runComposeUiTest {
            inEnglish { OldgeSnackbar("Saved", onClose = {}, inline = true) }
            onNodeWithContentDescription("Close").assertExists()
            noRussianClose()
        }

    @Test
    fun the_balloons_close_button_says_the_apps_word() =
        runComposeUiTest {
            inEnglish { OldgeBalloon("Copied", onClose = {}) }
            onNodeWithContentDescription("Close").assertExists()
            noRussianClose()
        }

    @Test
    fun the_drawers_scrim_says_the_apps_word() =
        runComposeUiTest {
            inEnglish { OldgeNavDrawer(drawerEntries, "home", {}, title = "Pavel") }
            onNode(
                hasContentDescription("Close") and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button),
            ).assertExists()
            noRussianClose()
        }

    @Test
    fun the_search_bars_clear_orb_says_the_apps_word() =
        runComposeUiTest {
            inEnglish { OldgeSearchBar(defaultValue = "kotlin") }
            onNodeWithContentDescription("Clear").assertExists()
            onNodeWithContentDescription("Очистить").assertDoesNotExist()
        }

    @Test
    fun the_reveal_orb_says_the_apps_words_both_ways() =
        runComposeUiTest {
            inEnglish { OldgeTextField("Password", "secret", {}, reveal = true) }
            onNodeWithContentDescription("Show password").performClick()
            onNodeWithContentDescription("Hide password").assertExists()
            onNodeWithContentDescription("Показать пароль").assertDoesNotExist()
            onNodeWithContentDescription("Скрыть пароль").assertDoesNotExist()
        }

    @Test
    fun an_unlabelled_progress_bar_says_the_apps_word() =
        runComposeUiTest {
            inEnglish { OldgeProgressBar(0.5f) }
            onNodeWithContentDescription("Progress").assertExists()
            onNodeWithContentDescription("Выполнение").assertDoesNotExist()
        }

    @Test
    fun a_done_steps_tick_says_the_apps_word() =
        runComposeUiTest {
            inEnglish { OldgeStepper(listOf("Cart", "Address", "Pay"), current = 2) }
            onAllNodesWithContentDescription("done", useUnmergedTree = true).assertCountEquals(2)
            onAllNodesWithContentDescription("готово", useUnmergedTree = true).assertCountEquals(0)
        }

    @Test
    fun a_page_dot_says_the_apps_words() =
        runComposeUiTest {
            inEnglish { OldgePageDots(3, 0, {}) }
            onNodeWithContentDescription("Page 2 of 3").assertExists()
            onNodeWithContentDescription("Страница 2 из 3").assertDoesNotExist()
        }

    @Test
    fun a_calendar_day_says_the_apps_words() =
        runComposeUiTest {
            val today = LocalDate(2026, 11, 1)
            inEnglish { OldgeDatePicker(null, {}, today = today) }
            onNodeWithContentDescription("november 12, 2026").assertExists()
            onNodeWithContentDescription("12 ноябрь 2026").assertDoesNotExist()
        }

    @Test
    fun the_default_words_are_the_ones_said_before() {
        val ru = OldgeStrings()
        assertEquals("Выполнение", ru.progress)
        assertEquals("готово", ru.stepDone)
        assertEquals("Страница 2 из 3", ru.page(2, 3))
        assertEquals("12 ноябрь 2026", ru.day(LocalDate(2026, 11, 12)))
    }

    @Test
    fun a_subtree_can_take_other_words_without_another_theme() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    CompositionLocalProvider(LocalOldgeStrings provides english) {
                        OldgeSnackbar("Saved", onClose = {}, inline = true)
                    }
                }
            }
            onNodeWithContentDescription("Close").assertExists()
        }
}
