package io.github.youndie.oldge.navigation

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeDurations
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

/** The rules in the WindowBar and BottomNav READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class BarBehaviourTest {
    @Test
    fun a_bar_title_is_a_heading_and_its_orbs_are_named_buttons() =
        runComposeUiTest {
            var back by mutableIntStateOf(0)
            var help by mutableIntStateOf(0)
            setContent {
                OldgeTheme {
                    OldgeWindowBar(
                        "Настройки",
                        onBack = { back++ },
                        actions = listOf(OldgeWindowAction(OldgeIcons.Help, "Справка", { help++ })),
                    )
                }
            }
            onNode(hasText("Настройки") and SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading)).assertExists()
            onNodeWithContentDescription(
                "Назад",
            ).assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)).performClick()
            onNodeWithContentDescription("Справка").performClick()
            assertEquals(1 to 1, back to help)
        }

    @Test
    fun a_bar_takes_at_most_three_actions() {
        val four = List(4) { OldgeWindowAction(OldgeIcons.Help, "Справка $it", {}) }
        assertFailsWith<IllegalArgumentException> {
            runComposeUiTest { setContent { OldgeTheme { OldgeWindowBar("Файлы", actions = four) } } }
        }
        runComposeUiTest { setContent { OldgeTheme { OldgeWindowBar("Файлы", actions = four.take(3)) } } }
    }

    @Test
    fun a_bottom_nav_has_three_to_five_sections() {
        for (n in listOf(2, 6)) {
            val items = List(n) { OldgeNavItem("s$it", OldgeIcons.Home, "Раздел") }
            assertFailsWith<IllegalArgumentException>("$n sections were accepted") {
                runComposeUiTest { setContent { OldgeTheme { OldgeBottomNav(items, "s0", {}) } } }
            }
        }
    }

    @Test
    fun the_current_section_is_selected_and_a_tap_changes_it() =
        runComposeUiTest {
            var value by mutableStateOf("home")
            setContent { OldgeTheme { OldgeBottomNav(sections, value, { value = it }) } }
            onNode(hasContentDescription("Разделы")).assertExists()
            onNodeWithText("Главная").assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
            onNodeWithText("Поиск")
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, false))
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
                .assertHeightIsAtLeast(56.dp)
                .performClick()
            assertEquals("search", value)
            onNodeWithText("Поиск").assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
        }

    @Test
    fun the_capsule_pops_when_a_section_becomes_current_and_not_under_reduced_motion() {
        // The new capsule's edge: 19 dp left of the bell's centre is chrome at rest and bezel while
        // the capsule is still small; the icon's hop lives inside its 22 dp and never reaches it.
        fun edge(reduced: Boolean): Pair<Color, Color> {
            var pair = Color.Unspecified to Color.Unspecified
            runComposeUiTest {
                mainClock.autoAdvance = false
                var value by mutableStateOf("home")
                setContent {
                    OldgeTheme(reducedMotion = reduced) {
                        Box(Modifier.testTag("nav").width(NAV)) { OldgeBottomNav(sections, value, { value = it }) }
                    }
                }
                mainClock.advanceTimeBy(1000)
                value = "bell"
                mainClock.advanceTimeByFrame()
                mainClock.advanceTimeByFrame()

                fun at(): Color {
                    val m = onNodeWithTag("nav").captureToImage().toPixelMap()
                    // Four 90.5 dp slots from 8 dp in, 4 dp apart; the capsule's middle 23 dp down.
                    val centre = with(density) { (8.dp + (90.5.dp + 4.dp) * 2 + 45.25.dp).toPx() }
                    val x = centre - with(density) { 19.dp.toPx() }
                    return m[x.toInt(), with(density) { 23.dp.roundToPx() }]
                }
                val mid = at()
                mainClock.advanceTimeBy(1000)
                pair = mid to at()
            }
            return pair
        }
        edge(reduced = false).let { (mid, rest) -> assertNotEquals(rest, mid, "the new capsule did not pop") }
        edge(reduced = true).let { (mid, rest) -> assertEquals(rest, mid, "the capsule popped under reduced motion") }
    }

    /**
     * B-57: `.og-winbar` is `content-box`, so the overlay's 8 px under its 64 px minimum adds to it. The
     * overlay bar is 72 dp in all, as Chrome lays out MediaScreen's; the plain bar is its 56.
     */
    @Test
    fun the_overlay_bar_is_72_dp_and_the_plain_one_56() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    androidx.compose.foundation.layout.Column {
                        OldgeWindowBar("Отпуск 2006", Modifier.testTag("overlay"), onBack = {}, overlay = true)
                        OldgeWindowBar("Настройки", Modifier.testTag("plain"), onBack = {})
                    }
                }
            }
            onNodeWithTag("overlay").assertHeightIsEqualTo(72.dp)
            onNodeWithTag("plain").assertHeightIsEqualTo(56.dp)
        }
}

private val NAV = 390.dp
