package io.github.youndie.oldge.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.forms.OldgeCheckbox
import io.github.youndie.oldge.forms.OldgeSelect
import io.github.youndie.oldge.forms.OldgeSelectOption
import io.github.youndie.oldge.forms.OldgeSlider
import io.github.youndie.oldge.forms.OldgeSwitch
import io.github.youndie.oldge.forms.OldgeTextField
import io.github.youndie.oldge.navigation.OldgeBalloon
import io.github.youndie.oldge.navigation.OldgeMenu
import io.github.youndie.oldge.navigation.fileMenu
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * B-83: the controls B-80 left at `hit-min` take their pointer sizes under [OldgeDensity.Pointer],
 * with no outer modifier, and keep the design system's under [OldgeDensity.Touch].
 */
@OptIn(ExperimentalTestApi::class)
class OldgeDensityControlsTest {
    private fun SemanticsNodeInteraction.height(): Dp = getBoundsInRoot().let { it.bottom - it.top }

    @Test
    fun the_form_controls_take_their_pointer_sizes_and_touch_keeps_the_design_systems() =
        runComposeUiTest {
            var density by mutableStateOf(OldgeDensity.Pointer)
            setContent {
                OldgeTheme(density = density) {
                    Column(Modifier.width(360.dp)) {
                        OldgeTextField("Папка", "/home", {}, Modifier.testTag("field"), showLabel = false)
                        OldgeSelect(
                            "Шифрование",
                            listOf(OldgeSelectOption(1, "Да"), OldgeSelectOption(2, "Нет")),
                            1,
                            {},
                        )
                        OldgeSwitch("Запуск", true, {})
                        OldgeCheckbox("Скорость", true, {})
                        OldgeSlider(0.4f, {}, Modifier.testTag("slider"))
                        OldgeBalloon("Сохранено", onClose = {})
                    }
                }
            }

            fun all(): List<Dp> =
                listOf(
                    onNodeWithTag("field").height(),
                    onNodeWithContentDescription("Шифрование").height(),
                    onNodeWithText("Запуск").height(),
                    onNodeWithText("Скорость").height(),
                    onNodeWithTag("slider").height(),
                    onNodeWithContentDescription(OldgeStrings().close).height(),
                )
            // field, select, switch (its 30 dp track), checkbox, slider, balloon's close
            assertEquals(listOf(32.dp, 32.dp, 30.dp, 28.dp, 28.dp, 28.dp), all(), "pointer")
            density = OldgeDensity.Touch
            waitForIdle()
            assertEquals(listOf(44.dp, 44.dp, 44.dp, 44.dp, 44.dp, 44.dp), all(), "touch")
        }

    @Test
    fun a_menu_item_is_28_dp_for_a_pointer_and_44_for_a_finger() =
        runComposeUiTest {
            var density by mutableStateOf(OldgeDensity.Pointer)
            setContent {
                OldgeTheme(density = density) {
                    OldgeMenu(true, {}, fileMenu, {}) { toggle -> OldgeButton("Файл", toggle) }
                }
            }
            assertEquals(28.dp, onNodeWithText("Открыть").height(), "pointer")
            density = OldgeDensity.Touch
            waitForIdle()
            assertEquals(44.dp, onNodeWithText("Открыть").height(), "touch")
        }
}
