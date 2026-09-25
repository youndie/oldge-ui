package io.github.youndie.oldge.actions

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals

/** The rules in the Button and OrbButton READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class ButtonBehaviourTest {
    private val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)

    @Test
    fun the_three_sizes_are_36_44_and_52_high() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column {
                        OldgeButton("sm", {}, size = OldgeButtonSize.Small)
                        OldgeButton("md", {})
                        OldgeButton("lg", {}, size = OldgeButtonSize.Large)
                    }
                }
            }
            onNodeWithText("sm").assertHeightIsEqualTo(36.dp)
            onNodeWithText("md").assertHeightIsEqualTo(44.dp)
            onNodeWithText("lg").assertHeightIsEqualTo(52.dp)
        }

    @Test
    fun an_icon_only_button_is_a_labelled_44_px_target() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeIconButton(OldgeIcons.Search, "Поиск", {}, size = OldgeButtonSize.Small) } }
            onNodeWithContentDescription("Поиск")
                .assertWidthIsEqualTo(44.dp)
                .assert(isButton)
        }

    @Test
    fun a_small_orb_is_30_px_drawn_in_a_44_px_target() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeOrbButton(OldgeIcons.Close, "Закрыть", {}, size = OldgeOrbSize.Small) } }
            onNodeWithContentDescription("Закрыть")
                .assertWidthIsEqualTo(44.dp)
                .assertHeightIsEqualTo(44.dp)
                .assert(isButton)
        }

    @Test
    fun a_large_orb_keeps_its_own_size() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeOrbButton(OldgeIcons.Plus, "Создать", {}, size = OldgeOrbSize.Large) } }
            onNodeWithContentDescription("Создать").assertWidthIsEqualTo(60.dp)
        }

    @Test
    fun a_short_label_still_makes_a_44_px_wide_target() =
        runComposeUiTest {
            // One character: its padding and text come to about 34 dp, so only the minimum makes it 44.
            // «OK» was 46 dp by itself and passed with the minimum removed (found by B-48's runner).
            setContent { OldgeTheme { OldgeButton("1", {}, size = OldgeButtonSize.Small) } }
            onNodeWithText("1").assertWidthIsEqualTo(44.dp)
        }

    @Test
    fun a_disabled_button_ignores_taps_and_an_enabled_one_does_not() =
        runComposeUiTest {
            var taps by mutableIntStateOf(0)
            setContent {
                OldgeTheme {
                    Column {
                        OldgeButton("Недоступна", { taps++ }, enabled = false)
                        OldgeOrbButton(OldgeIcons.Close, "Закрыть", { taps += 10 }, enabled = false)
                        OldgeButton("Отправить", { taps += 100 })
                    }
                }
            }
            onNodeWithText("Недоступна").assertIsNotEnabled().performClick()
            onNodeWithContentDescription("Закрыть").assertIsNotEnabled().performClick()
            assertEquals(0, taps)
            onNodeWithText("Отправить").performClick()
            assertEquals(100, taps)
        }

    /**
     * The glint runs only while the primary is held (`:active::after`). A run of quick presses must
     * not queue a sweep per press: 700 ms after the last release, the button is at rest (B-62).
     */
    @Test
    fun quick_presses_leave_no_glints_queued_after_the_last_release() =
        runComposeUiTest {
            mainClock.autoAdvance = false
            setContent {
                OldgeTheme(reducedMotion = false) {
                    OldgeButton("Войти", {}, Modifier.testTag("go"), variant = OldgeButtonVariant.Primary)
                }
            }
            mainClock.advanceTimeBy(SETTLE)
            val rest = onNodeWithTag("go").captureToImage().toPixelMap()
            repeat(PRESSES) {
                onNodeWithTag("go").performTouchInput { down(center) }
                mainClock.advanceTimeBy(HELD)
                onNodeWithTag("go").performTouchInput { up() }
                mainClock.advanceTimeBy(BETWEEN)
            }
            mainClock.advanceTimeBy(AFTER)
            val later = onNodeWithTag("go").captureToImage().toPixelMap()
            var off = 0
            for (y in 0 until rest.height) for (x in 0 until rest.width) if (rest[x, y] != later[x, y]) off++
            assertEquals(0, off, "pixels still off the resting button ${AFTER + BETWEEN} ms after the last release")
        }
}

private const val SETTLE = 100L
private const val PRESSES = 5
private const val HELD = 50L
private const val BETWEEN = 50L
private const val AFTER = 650L
