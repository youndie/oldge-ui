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
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.harness.DemoText
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/** The rules in the CategoryTabs and Tabs READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class TabsBehaviourTest {
    /**
     * The hook's leg stands under the current glyph's centre, the tab's centre line (B-69; the design
     * has it 6 px in). The label's text starts after the 2 px leg and its 6 px pad, so the leg's
     * centre is 7 dp left of the text. Checked on the first tab and on a later one.
     */
    @Test
    fun the_hooks_leg_stands_under_the_current_glyphs_centre() {
        for (current in listOf(categories.first().id, categories[2].id)) {
            runComposeUiTest {
                setContent { OldgeTheme { OldgeCategoryTabs(categories, current, {}) } }
                val tab = onNode(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true)).getBoundsInRoot()
                val name = categories.first { it.id == current }.label
                val text = onNodeWithText(name, useUnmergedTree = true).getBoundsInRoot()
                val leg = text.left - LEG_TO_TEXT
                val centre = (tab.left + tab.right) / 2
                assertTrue(
                    abs((leg - centre).value) <= HALF_PX,
                    "the hook's leg is at $leg, the glyph's centre at $centre",
                )
            }
        }
    }

    /**
     * A current last tab's label hangs past its tab by more than the row's end padding. The row
     * grows to take it, as CSS's scroll overflow does with an absolutely placed child, instead of
     * cutting it off (B-69).
     */
    @Test
    fun a_current_last_tabs_label_is_inside_the_row() =
        runComposeUiTest {
            val four = categories.take(4)
            setContent { OldgeTheme { OldgeCategoryTabs(four, four.last().id, {}) } }
            val row = onNode(hasContentDescription("Категории")).getBoundsInRoot()
            val text = onNodeWithText(four.last().label, useUnmergedTree = true).getBoundsInRoot()
            assertTrue(
                text.right + LABEL_END_PAD <= row.right,
                "the label ends at ${text.right}, the row at ${row.right}",
            )
        }

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

// The 2 px leg's centre is 1 dp in, and the 6 px pad follows the leg: the text starts 7 dp after it.
private val LEG_TO_TEXT = 7.dp
private const val HALF_PX = 0.5f

// `.og-cat__label { padding: 0 12px 3px 6px }`: the stroke under the label runs 12 dp past its text.
private val LABEL_END_PAD = 12.dp
