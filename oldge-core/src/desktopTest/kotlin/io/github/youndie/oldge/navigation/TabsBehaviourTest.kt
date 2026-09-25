package io.github.youndie.oldge.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.youndie.oldge.harness.DemoText
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** The rules in the CategoryTabs and Tabs READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class TabsBehaviourTest {
    @Test
    fun only_the_current_category_shows_its_label_and_the_others_are_named() =
        runComposeUiTest {
            var value by mutableStateOf("doc")
            setContent { OldgeTheme { OldgeCategoryTabs(categories, value, { value = it }) } }
            onNode(hasContentDescription("Категории")).assertExists()
            // The current tab is named by its visible label; the rest by `aria-label` alone.
            onNodeWithText("Документы").assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
            onNode(hasText("Музыка"), useUnmergedTree = true).assertDoesNotExist()
            onNodeWithContentDescription("Музыка")
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, false))
                .performClick()
            assertEquals("music", value)
            onNodeWithText("Музыка").assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
            onNode(hasText("Документы"), useUnmergedTree = true).assertDoesNotExist()
        }

    @Test
    fun category_tabs_take_four_to_eight_and_tabs_two_to_five() {
        for (n in listOf(3, 9)) {
            val items = List(n) { categories[it % categories.size].copy(id = "c$it") }
            assertFailsWith<IllegalArgumentException>("$n categories were accepted") {
                runComposeUiTest { setContent { OldgeTheme { OldgeCategoryTabs(items, "c0", {}) } } }
            }
        }
        for (n in listOf(1, 6)) {
            val items = List(n) { OldgeTab("t$it", "Вкладка") }
            assertFailsWith<IllegalArgumentException>("$n tabs were accepted") {
                runComposeUiTest { setContent { OldgeTheme { OldgeTabs(items, "t0", {}, label = "Свойства") } } }
            }
        }
    }

    @Test
    fun a_tab_shows_its_page_and_a_tap_changes_it() =
        runComposeUiTest {
            var value by mutableStateOf("gen")
            setContent {
                OldgeTheme {
                    OldgeTabs(folderTabs, value, { value = it }, label = "Свойства папки") {
                        DemoText(if (value == "gen") "Имя" else "Кому")
                    }
                }
            }
            onNode(hasContentDescription("Свойства папки")).assertExists()
            onNodeWithText("Общие").assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
            onNodeWithText("Имя").assertExists()
            onNode(hasText("Доступ", substring = true))
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
                .performClick()
            assertEquals("share", value)
            onNodeWithText("Кому").assertExists()
        }

    @Test
    fun the_arrow_keys_move_to_the_next_tab_and_take_focus_with_them() =
        runComposeUiTest {
            var value by mutableStateOf("gen")
            setContent { OldgeTheme { OldgeTabs(folderTabs, value, { value = it }, label = "Свойства папки") } }
            onNodeWithText("Общие").requestFocus().assertIsFocused()
            onNodeWithText("Общие").performKeyInput { pressKey(Key.DirectionRight) }
            waitForIdle()
            assertEquals("share", value)
            onNode(hasText("Доступ", substring = true)).assertIsFocused()
            onNode(hasText("Доступ", substring = true)).performKeyInput { pressKey(Key.DirectionLeft) }
            waitForIdle()
            assertEquals("gen", value)
            onNodeWithText("Общие").performKeyInput { pressKey(Key.DirectionLeft) }
            waitForIdle()
            // From the first, ← wraps to the last.
            assertEquals("hist", value)
        }

    @Test
    fun only_the_current_tab_can_take_focus() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeTabs(folderTabs, "gen", {}, label = "Свойства папки") } }
            onNodeWithText("История").assert(SemanticsMatcher.expectValue(SemanticsProperties.Focused, false))
            onNodeWithText("История").requestFocus()
            onNodeWithText("История").assert(SemanticsMatcher.expectValue(SemanticsProperties.Focused, false))
        }
}
