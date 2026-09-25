package io.github.youndie.oldge.forms

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The rules in the Switch and Slider READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class SwitchSliderBehaviourTest {
    private fun role(role: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

    @Test
    fun a_switch_row_is_a_44_px_switch_that_acts_at_once() =
        runComposeUiTest {
            var on by mutableStateOf(false)
            setContent { OldgeTheme { OldgeSwitch("Тёмный скин ночью", on, { on = it }) } }
            onNodeWithText(
                "Тёмный скин ночью",
            ).assertHeightIsAtLeast(44.dp).assert(role(Role.Switch)).assertIsOff().performClick()
            assertEquals(true, on)
            onNodeWithText("Тёмный скин ночью").assertIsOn()
        }

    @Test
    fun a_disabled_switch_ignores_taps() =
        runComposeUiTest {
            var on by mutableStateOf(false)
            setContent { OldgeTheme { OldgeSwitch("Недоступно", on, { on = it }, enabled = false) } }
            onNodeWithText("Недоступно").assertIsNotEnabled().performClick()
            assertEquals(false, on)
        }

    @Test
    fun a_slider_is_named_reads_its_value_text_and_can_be_set() =
        runComposeUiTest {
            var v by mutableFloatStateOf(0.6f)
            setContent {
                OldgeTheme {
                    OldgeSlider(
                        v,
                        { v = it },
                        label = "Громкость",
                        valueText = "${Math.round(v * 100)}%",
                    )
                }
            }
            onNodeWithContentDescription("Громкость")
                .assertHeightIsEqualTo(44.dp)
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "60%"))
                .assert(
                    SemanticsMatcher.expectValue(
                        SemanticsProperties.ProgressBarRangeInfo,
                        ProgressBarRangeInfo(
                            0.6f,
                            0f..1f,
                            999,
                        ),
                    ),
                ).performSemanticsAction(SemanticsActions.SetProgress) { it(0.25f) }
            assertEquals(0.25f, v)
        }

    @Test
    fun the_keys_of_a_native_range_input_move_the_slider() =
        runComposeUiTest {
            var v by mutableFloatStateOf(0.5f)
            setContent { OldgeTheme { OldgeSlider(v, { v = it }, label = "Громкость") } }
            val node = onNodeWithContentDescription("Громкость")
            node.requestFocus()
            node.performKeyInput { pressKey(Key.DirectionRight) }
            assertTrue(abs(v - 0.501f) < 1e-4f, "right arrow: $v")
            node.performKeyInput { pressKey(Key.PageDown) }
            assertTrue(abs(v - 0.401f) < 1e-4f, "page down: $v")
            node.performKeyInput { pressKey(Key.MoveEnd) }
            assertEquals(1f, v)
        }

    @Test
    fun a_press_moves_the_ball_to_the_finger() =
        runComposeUiTest {
            var v by mutableFloatStateOf(0f)
            setContent { OldgeTheme { OldgeSlider(v, { v = it }, Modifier.width(228.dp), label = "Громкость") } }
            // The ball's centre travels from 14 to 214 px across a 228 px strip (1 px border, 26 px ball).
            onNodeWithContentDescription("Громкость").performTouchInput {
                down(Offset(114f, 22f))
                up()
            }
            assertTrue(abs(v - 0.5f) < 1e-3f, "pressed at the middle: $v")
        }
}
