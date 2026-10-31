package io.github.youndie.oldge.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.actions.OldgeButtonSize
import io.github.youndie.oldge.actions.OldgeIconButton
import io.github.youndie.oldge.forms.OldgeSearchBar
import io.github.youndie.oldge.icons.OldgeIcons
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * B-80: under [OldgeDensity.Pointer] the toolbar's controls take their pointer sizes with no outer
 * modifier; under [OldgeDensity.Touch] they are the design system's, as before.
 */
@OptIn(ExperimentalTestApi::class)
class OldgeDensityTest {
    @Test
    fun pointer_sizes_need_no_outer_modifier_and_touch_keeps_the_design_systems() =
        runComposeUiTest {
            var density by mutableStateOf(OldgeDensity.Pointer)
            setContent {
                OldgeTheme(density = density) {
                    Column(Modifier.width(360.dp)) {
                        OldgeIconButton(OldgeIcons.Plus, "medium", {})
                        OldgeIconButton(OldgeIcons.Plus, "small", {}, size = OldgeButtonSize.Small)
                        OldgeButton("Ok", {}, Modifier.testTag("sm"), size = OldgeButtonSize.Small)
                        OldgeButton("Сохранить", {}, Modifier.testTag("md"))
                        OldgeButton("Готово", {}, Modifier.testTag("lg"), size = OldgeButtonSize.Large)
                        OldgeSearchBar(Modifier.testTag("search"), defaultValue = "ubuntu")
                    }
                }
            }

            fun size(tag: String): Pair<Dp, Dp> =
                onNodeWithTag(tag).getBoundsInRoot().let {
                    (it.right - it.left) to
                        (it.bottom - it.top)
                }

            fun named(name: String): Pair<Dp, Dp> =
                onNodeWithContentDescription(
                    name,
                ).getBoundsInRoot().let { (it.right - it.left) to (it.bottom - it.top) }

            assertEquals(32.dp to 32.dp, named("medium"), "pointer: a medium icon button is square")
            assertEquals(28.dp to 28.dp, named("small"), "pointer: a small icon button is square")
            assertEquals(28.dp, size("sm").second, "pointer: small")
            assertEquals(32.dp, size("md").second, "pointer: medium")
            assertEquals(36.dp, size("lg").second, "pointer: large")
            assertEquals(32.dp, size("search").second, "pointer: search bar")
            // Nothing is cut: the clear orb is whole inside the bar.
            val bar = onNodeWithTag("search").getBoundsInRoot()
            val orb = onNodeWithContentDescription(OldgeStrings().clear).getBoundsInRoot()
            assert(
                orb.top >= bar.top && orb.bottom <= bar.bottom,
            ) { "the clear orb sticks out of the bar: $orb in $bar" }

            density = OldgeDensity.Touch
            waitForIdle()
            assertEquals(44.dp to 44.dp, named("medium"), "touch: hit-min")
            assertEquals(44.dp to 36.dp, named("small"), "touch: hit-min wide, 36 high")
            assertEquals(36.dp, size("sm").second, "touch: small")
            assertEquals(44.dp, size("md").second, "touch: medium")
            assertEquals(52.dp, size("lg").second, "touch: large")
            assertEquals(48.dp, size("search").second, "touch: search bar")
        }
}
