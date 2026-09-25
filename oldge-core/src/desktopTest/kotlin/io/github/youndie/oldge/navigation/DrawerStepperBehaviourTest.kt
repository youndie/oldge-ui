package io.github.youndie.oldge.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/** The rules in the NavDrawer, Stepper and PageDots READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class DrawerStepperBehaviourTest {
    @Test
    fun the_current_destination_is_selected_and_a_tap_changes_it() =
        runComposeUiTest {
            var value by mutableStateOf("files")
            setContent {
                OldgeTheme {
                    OldgeNavDrawer(
                        drawerEntries,
                        value,
                        { value = it },
                        title = "Павел",
                        inline = true,
                    )
                }
            }
            onNode(hasContentDescription("Меню")).assertExists()
            onNode(
                hasText("Файлы", substring = true),
            ).assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
            onNodeWithText("Настройки")
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, false))
                .assertHeightIsAtLeast(48.dp)
                .performClick()
            assertEquals("set", value)
            // A section title is only a title.
            onNodeWithText(
                "СЕРВИС",
                useUnmergedTree = true,
            ).assert(SemanticsMatcher.keyNotDefined(SemanticsActions.OnClick))
        }

    @Test
    fun a_drawer_is_304_dp_wide_or_86_percent_of_its_parent_if_less() {
        for ((parent, drawer) in listOf(390.dp to 304.dp, 300.dp to 258.dp)) {
            runComposeUiTest {
                setContent {
                    OldgeTheme {
                        Box(Modifier.width(parent).height(400.dp)) {
                            OldgeNavDrawer(
                                drawerEntries,
                                "home",
                                {},
                                title = "Павел",
                                modifier = Modifier.testTag("d"),
                                inline = true,
                            )
                        }
                    }
                }
                onNode(hasContentDescription("Меню")).assertWidthIsEqualTo(drawer)
            }
        }
    }

    @Test
    fun over_a_screen_the_scrim_closes_it_and_a_closed_one_is_not_there() =
        runComposeUiTest {
            var open by mutableStateOf(true)
            setContent {
                OldgeTheme {
                    Box(Modifier.width(390.dp).height(400.dp).testTag("screen")) {
                        OldgeNavDrawer(
                            drawerEntries,
                            "home",
                            {},
                            title = "Павел",
                            open = open,
                            onClose = { open = false },
                        )
                    }
                }
            }
            // Right of the 304 dp drawer is the scrim.
            onNodeWithTag("screen").performTouchInputAt(360f, 200f)
            assertEquals(false, open)
            onNode(hasContentDescription("Меню")).assertDoesNotExist()
        }

    @Test
    fun the_items_come_in_one_after_another() =
        runComposeUiTest {
            mainClock.autoAdvance = false
            setContent {
                OldgeTheme(reducedMotion = false) {
                    OldgeNavDrawer(drawerEntries, "home", {}, title = "Павел", inline = true)
                }
            }
            mainClock.advanceTimeByFrame()

            fun look(label: String): List<Color> {
                val m = onNodeWithText(label).captureToImage().toPixelMap()
                val middle = m.height / 2
                return (0 until m.width).map { m[it, middle] }
            }
            val firstEarly = look("Главная")
            val lastEarly = look("Справка")
            // 150 ms in: the first item (80 ms) has started, the sixth (80 + 5 × 35) has not.
            mainClock.advanceTimeBy(150)
            assertNotEquals(firstEarly, look("Главная"), "the first item had not started")
            assertEquals(lastEarly, look("Справка"), "the sixth item started with the first")
            mainClock.advanceTimeBy(1000)
            assertNotEquals(lastEarly, look("Справка"), "the sixth item never came")
        }

    @Test
    fun the_current_step_is_selected_and_done_steps_are_ticked() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeStepper(wizard, 2) } }
            onNode(hasContentDescription("Шаги")).assertExists()
            onNode(hasText("Проверка")).assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
            onNode(hasText("Файлы")).assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, false))
            assertEquals(
                2,
                onAllNodes(hasContentDescription("готово"), useUnmergedTree = true).fetchSemanticsNodes().size,
            )
        }

    @Test
    fun the_trough_fills_up_to_the_current_step() =
        runComposeUiTest {
            setContent { OldgeTheme { Box(Modifier.width(400.dp).testTag("s")) { OldgeStepper(wizard, 2) } } }
            val m = onNodeWithTag("s").captureToImage().toPixelMap()
            // The joins lie between the columns' centres, at the fill's middle row, 17 px down.
            val col = m.width / 4f
            val y = (17 * density.density).toInt()

            fun lit(join: Int): Boolean {
                val c: Color = m[(col * join).toInt(), y]
                return c.green > c.red + 0.2f && c.green > c.blue + 0.2f
            }
            assertTrue(lit(1) && lit(2), "the joins to the current step are not filled")
            assertTrue(!lit(3), "the join past the current step is filled")
        }

    @Test
    fun the_current_ring_pulses_and_stands_still_under_reduced_motion() {
        fun frames(reduced: Boolean): Pair<List<Any>, List<Any>> {
            var pair: Pair<List<Any>, List<Any>> = emptyList<Any>() to emptyList()
            runComposeUiTest {
                mainClock.autoAdvance = false
                setContent {
                    OldgeTheme(
                        reducedMotion = reduced,
                    ) { Box(Modifier.testTag("s")) { OldgeStepper(wizard, 1) } }
                }
                // After the done step's tick has popped in (`dur-base`), so only the ring can move;
                // at 100 ms the pop alone told two frames apart (B-26's `step-ring-still`).
                mainClock.advanceTimeBy(1000)
                val a = grab("s")
                mainClock.advanceTimeBy(400)
                pair = a to grab("s")
            }
            return pair
        }
        frames(reduced = false).let { (a, b) -> assertNotEquals(a, b, "the ring stood still") }
        frames(reduced = true).let { (a, b) -> assertEquals(a, b, "the ring pulsed under reduced motion") }
    }

    /**
     * B-58: on a photo the current dot is the stretched capsule in the dark dots' grey, lit only by its
     * glow, as the CSS cascade has it; on the body it is filled with the accent.
     */
    @Test
    fun on_a_photo_the_current_dot_is_grey_and_on_the_body_it_is_the_accent() {
        fun centre(onDark: Boolean): Color {
            var colour = Color.Unspecified
            runComposeUiTest {
                setContent {
                    OldgeTheme {
                        Box(
                            Modifier
                                .testTag(
                                    "d",
                                ).width(DOTS_WIDTH)
                                .background(if (onDark) Color.Black else Color.Unspecified),
                        ) {
                            OldgePageDots(5, 0, {}, onDark = onDark)
                        }
                    }
                }
                val m = onNodeWithTag("d").captureToImage().toPixelMap()
                // The row is exactly the dots' width, so the current one, the first, is 0 to 36 dp.
                colour = m[(18 * density.density).toInt(), m.height / 2]
            }
            return colour
        }

        fun lime(c: Color) = c.green > c.red + 0.15f && c.green > c.blue + 0.15f
        val dark = centre(onDark = true)
        // Grey and lighter than the black under it: the dark dots' 35 % white, not the background.
        assertTrue(
            !lime(dark) && abs(dark.red - dark.green) < 0.05f && dark.red > 0.2f,
            "on a photo the current dot is not grey: $dark",
        )
        assertTrue(lime(centre(onDark = false)), "on the body the current dot is not the accent")
    }

    @Test
    fun steppers_take_three_to_five_steps_and_dots_seven_at_most() {
        for (n in listOf(2, 6)) {
            assertFailsWith<IllegalArgumentException>("$n steps were accepted") {
                runComposeUiTest { setContent { OldgeTheme { OldgeStepper(List(n) { "Шаг" }, 0) } } }
            }
        }
        assertFailsWith<IllegalArgumentException>("eight dots were accepted") {
            runComposeUiTest { setContent { OldgeTheme { OldgePageDots(8, 0, {}) } } }
        }
    }

    @Test
    fun every_dot_is_a_named_44_px_button_and_the_current_one_is_wider() =
        runComposeUiTest {
            var page by mutableIntStateOf(2)
            setContent { OldgeTheme { OldgePageDots(5, page, { page = it }) } }
            onNode(hasContentDescription("Страницы")).assertExists()
            onNodeWithContentDescription("Страница 3 из 5")
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
                .assertWidthIsEqualTo(36.dp)
                .assertHeightIsEqualTo(44.dp)
            onNodeWithContentDescription("Страница 5 из 5")
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
                .assertWidthIsEqualTo(24.dp)
                .performClick()
            assertEquals(4, page)
        }
}

@OptIn(ExperimentalTestApi::class)
private fun androidx.compose.ui.test.ComposeUiTest.grab(tag: String): List<Any> {
    val m = onNodeWithTag(tag).captureToImage().toPixelMap()
    return (0 until m.height).flatMap { y -> (0 until m.width).map { x -> m[x, y] } }
}

@OptIn(ExperimentalTestApi::class)
private fun androidx.compose.ui.test.SemanticsNodeInteraction.performTouchInputAt(
    x: Float,
    y: Float,
) = performTouchInput {
    click(
        androidx.compose.ui.geometry
            .Offset(x * density, y * density),
    )
}

/** Five dots: the current one's 36 dp, four of 24, and four 2 dp gaps. */
private val DOTS_WIDTH = 140.dp
