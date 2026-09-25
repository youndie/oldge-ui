package io.github.youndie.oldge.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.actions.OldgeOrbButton
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The rules in the Menu, Tooltip and Balloon READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class MenuTooltipBalloonBehaviourTest {
    @Test
    fun a_balloon_is_the_same_in_every_skin() {
        // On the same plain ground in each skin: the skin's body differs, the balloon must not.
        val pixels =
            OldgeSkin.entries.map { skin ->
                var map: List<Color> = emptyList()
                runComposeUiTest {
                    setContent {
                        OldgeTheme(skin = skin) {
                            Box(
                                Modifier
                                    .testTag("ground")
                                    .background(Color.White)
                                    .padding(8.dp)
                                    .width(320.dp),
                            ) {
                                OldgeBalloon(
                                    "Копия создана",
                                    text = "Сохранено 1 204 файла.",
                                    icon = OldgeIcons.Ok,
                                    onClose = {},
                                )
                            }
                        }
                    }
                    val m = onNodeWithTag("ground").captureToImage().toPixelMap()
                    map = (0 until m.height).flatMap { y -> (0 until m.width).map { x -> m[x, y] } }
                }
                skin to map
            }
        val (first, reference) = pixels.first()
        for ((skin, map) in pixels.drop(1)) assertEquals(reference, map, "$skin draws the balloon unlike $first")
    }

    @Test
    fun a_balloon_is_announced_and_its_close_button_is_a_named_44_px_target() =
        runComposeUiTest {
            var closed by mutableIntStateOf(0)
            setContent { OldgeTheme { OldgeBalloon("Копия создана", onClose = { closed++ }) } }
            onNodeWithText("Копия создана", useUnmergedTree = true).assertExists()
            onNodeWithContentDescription(
                "Закрыть",
            ).assertWidthIsEqualTo(44.dp).assertHeightIsEqualTo(44.dp).performClick()
            assertEquals(1, closed)
            onNode(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite)).assertExists()
        }

    @Test
    fun a_tooltip_shows_on_hover_and_goes_when_the_pointer_leaves() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Box(
                        Modifier.padding(60.dp),
                    ) { OldgeTooltip("Справка") { OldgeOrbButton(OldgeIcons.Help, "Справка", {}) } }
                }
            }
            onNodeWithText("Справка", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithContentDescription("Справка").performMouseInput { enter(center) }
            onNodeWithText("Справка", useUnmergedTree = true).assertExists()
            onNodeWithContentDescription("Справка").performMouseInput { exit() }
            onNodeWithText("Справка", useUnmergedTree = true).assertDoesNotExist()
        }

    @Test
    fun a_tooltip_shows_after_a_450_ms_press_and_goes_1_5_s_after_the_touch() =
        runComposeUiTest {
            mainClock.autoAdvance = false
            setContent {
                OldgeTheme {
                    Box(
                        Modifier.padding(60.dp),
                    ) { OldgeTooltip("Справка") { OldgeOrbButton(OldgeIcons.Help, "Справка", {}) } }
                }
            }
            mainClock.advanceTimeByFrame()
            onNodeWithContentDescription("Справка").performTouchInput { down(center) }
            mainClock.advanceTimeBy(400)
            onNodeWithText("Справка", useUnmergedTree = true).assertDoesNotExist()
            mainClock.advanceTimeBy(100)
            onNodeWithText("Справка", useUnmergedTree = true).assertExists()
            onNodeWithContentDescription("Справка").performTouchInput { up() }
            mainClock.advanceTimeBy(1400)
            onNodeWithText("Справка", useUnmergedTree = true).assertExists()
            mainClock.advanceTimeBy(200)
            onNodeWithText("Справка", useUnmergedTree = true).assertDoesNotExist()
        }

    @Test
    fun a_tooltip_shows_on_keyboard_focus() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Box(
                        Modifier.padding(60.dp),
                    ) { OldgeTooltip("Справка") { OldgeOrbButton(OldgeIcons.Help, "Справка", {}) } }
                }
            }
            onNodeWithContentDescription("Справка").performKeyInput { pressKey(Key.Tab) }
            onNodeWithContentDescription("Справка").requestFocus()
            onNodeWithText("Справка", useUnmergedTree = true).assertExists()
        }

    @Test
    fun a_menu_opens_from_its_trigger_and_choosing_an_item_closes_it() =
        runComposeUiTest {
            var open by mutableStateOf(false)
            var chosen = ""
            setContent {
                OldgeTheme {
                    OldgeMenu(open, { open = it }, fileMenu, { chosen = it }) { toggle -> OldgeButton("Файл", toggle) }
                }
            }
            onNodeWithText("Файл").performClick()
            assertTrue(open)
            onNodeWithText("Переименовать").performClick()
            assertEquals("ren", chosen)
            assertEquals(false, open)
            onNodeWithText("Переименовать").assertDoesNotExist()
        }

    @Test
    fun escape_closes_a_menu() =
        runComposeUiTest {
            var open by mutableStateOf(true)
            setContent {
                OldgeTheme { OldgeMenu(open, { open = it }, fileMenu, {}) { toggle -> OldgeButton("Файл", toggle) } }
            }
            onNodeWithText("Открыть").performKeyInput { pressKey(Key.Escape) }
            waitForIdle()
            assertEquals(false, open)
        }
}
