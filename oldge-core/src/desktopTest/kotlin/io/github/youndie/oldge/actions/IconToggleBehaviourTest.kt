package io.github.youndie.oldge.actions

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** B-78: an icon button with `selected` is a toggle a screen reader hears, and on is drawn as on. */
@OptIn(ExperimentalTestApi::class)
class IconToggleBehaviourTest {
    private fun state(s: ToggleableState) = SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, s)

    @Test
    fun a_toggle_is_heard_on_or_off_and_a_plain_icon_button_is_not_a_toggle() =
        runComposeUiTest {
            var shown by mutableStateOf(false)
            setContent {
                OldgeTheme {
                    Row {
                        OldgeIconButton(OldgeIcons.Search, "Поиск", {})
                        OldgeIconButton(OldgeIcons.Info, "Подробности", { shown = !shown }, selected = shown)
                    }
                }
            }
            onNodeWithContentDescription("Поиск")
                .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ToggleableState))
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            val toggle = onNodeWithContentDescription("Подробности")
            toggle
                .assert(
                    state(ToggleableState.Off),
                ).assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
            toggle.performClick()
            assertEquals(true, shown)
            toggle.assert(state(ToggleableState.On))
        }

    @Test
    fun on_is_drawn_unlike_off_and_off_like_a_plain_icon_button() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Row {
                        OldgeIconButton(OldgeIcons.Info, "a", {}, Modifier.testTag("plain"))
                        OldgeIconButton(OldgeIcons.Info, "b", {}, Modifier.testTag("off"), selected = false)
                        OldgeIconButton(OldgeIcons.Info, "c", {}, Modifier.testTag("on"), selected = true)
                    }
                }
            }
            val plain = onNodeWithTag("plain").captureToImage().toPixelMap()
            val off = onNodeWithTag("off").captureToImage().toPixelMap()
            val on = onNodeWithTag("on").captureToImage().toPixelMap()
            var offDiffers = 0
            var onDiffers = 0
            for (x in 0 until plain.width) {
                for (y in 0 until plain.height) {
                    if (plain[x, y] != off[x, y]) offDiffers++
                    if (off[x, y] != on[x, y]) onDiffers++
                }
            }
            assertEquals(0, offDiffers, "off is drawn as a plain icon button")
            assertTrue(onDiffers > plain.width * plain.height / 2, "on is drawn unlike off: $onDiffers pixels differ")
        }
}
