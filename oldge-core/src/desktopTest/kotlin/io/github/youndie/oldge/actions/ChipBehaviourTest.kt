package io.github.youndie.oldge.actions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals

/** The rules in the Chip README that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class ChipBehaviourTest {
    private fun role(role: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

    @Test
    fun a_chip_is_laid_out_34_high_and_touched_46_high() =
        runComposeUiTest {
            // A tag on the chip itself would merge into its clickable's node; the box around it has
            // the size the chip takes in a row.
            setContent { OldgeTheme { Box(Modifier.testTag("slot")) { OldgeAssistChip("Фото", {}) } } }
            onNodeWithTag("slot").assertHeightIsEqualTo(34.dp)
            onNodeWithText("Фото").assertHeightIsEqualTo(46.dp).assert(role(Role.Button))
        }

    @Test
    fun a_touch_just_outside_the_drawn_chip_still_presses_it() =
        runComposeUiTest {
            var taps by mutableIntStateOf(0)
            setContent {
                OldgeTheme {
                    Column(Modifier.padding(20.dp).testTag("row")) {
                        OldgeAssistChip("Фото", { taps++ })
                    }
                }
            }
            // 4 px above the drawn top edge: inside `::after { inset: -6px -2px }`, outside the box.
            onNodeWithTag("row").performTouchInput {
                down(Offset(30f, -4f))
                up()
            }
            assertEquals(1, taps)
        }

    @Test
    fun a_filter_chip_toggles() =
        runComposeUiTest {
            var on by mutableStateOf(false)
            setContent { OldgeTheme { OldgeFilterChip("Документы", on, { on = it }) } }
            onNodeWithText("Документы").assertIsOff().performClick()
            onNodeWithText("Документы").assertIsOn()
        }

    @Test
    fun an_input_chip_is_36_high_and_its_remove_button_is_a_labelled_44_px_target() =
        runComposeUiTest {
            var removed by mutableIntStateOf(0)
            setContent {
                OldgeTheme {
                    OldgeInputChip(
                        "Работа",
                        { removed++ },
                        "Убрать Работа",
                        Modifier.testTag("chip"),
                    )
                }
            }
            onNodeWithTag("chip").assertHeightIsEqualTo(36.dp)
            onNodeWithContentDescription("Убрать Работа")
                .assertWidthIsEqualTo(44.dp)
                .assertHeightIsEqualTo(44.dp)
                .assert(role(Role.Button))
                .performClick()
            assertEquals(1, removed)
        }
}
