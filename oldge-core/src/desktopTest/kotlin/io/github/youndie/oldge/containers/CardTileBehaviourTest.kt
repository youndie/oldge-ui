package io.github.youndie.oldge.containers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** The rules in the Card and ActionTile READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class CardTileBehaviourTest {
    private val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
    private val clickable = SemanticsMatcher.keyIsDefined(SemanticsActions.OnClick)

    @Test
    fun a_card_pressed_as_a_whole_is_one_button() =
        runComposeUiTest {
            var taps by mutableIntStateOf(0)
            setContent { OldgeTheme { OldgeCard(title = "Отпуск 2006", subtitle = "148 фото", onClick = { taps++ }) } }
            onNodeWithText("Отпуск 2006").assert(isButton).performClick()
            assertEquals(1, taps)
        }

    @Test
    fun a_card_without_a_press_is_not_a_button() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeCard(title = "Журнал", sunken = true) } }
            onNodeWithText("Журнал").assert(!clickable)
        }

    @Test
    fun a_card_pressed_as_a_whole_takes_no_actions() {
        assertFailsWith<IllegalArgumentException> {
            runComposeUiTest {
                setContent { OldgeTheme { OldgeCard(title = "Два слоя", onClick = {}) { OldgeButton("Открыть", {}) } } }
            }
        }
    }

    @Test
    fun a_card_image_is_named_by_its_description() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    OldgeCard(
                        image = ColorPainter(Color.Gray),
                        imageDescription = "Пляж в Будве",
                        title = "Отпуск",
                    )
                }
            }
            onNodeWithContentDescription("Пляж в Будве").assertExists()
        }

    @Test
    fun an_action_tile_is_a_named_64_px_button() =
        runComposeUiTest {
            var taps by mutableIntStateOf(0)
            setContent { OldgeTheme { OldgeActionTile("Фото", OldgeIcons.Image, { taps++ }) } }
            onNodeWithText("Фото").assert(isButton).assertHeightIsAtLeast(64.dp).performClick()
            assertEquals(1, taps)
        }
}
