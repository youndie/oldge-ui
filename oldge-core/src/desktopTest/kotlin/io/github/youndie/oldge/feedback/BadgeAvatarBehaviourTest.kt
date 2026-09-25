package io.github.youndie.oldge.feedback

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeDurations
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/** The rules in the Badge and Avatar READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class BadgeAvatarBehaviourTest {
    @Test
    fun the_three_states_carry_an_icon_and_a_count_or_a_plain_badge_does_not() =
        runComposeUiTest {
            // An icon adds its 14 px and the 4 px gap; the tones share their border and padding.
            setContent {
                OldgeTheme {
                    Column {
                        OldgeBadge("Синхр.", Modifier.testTag("accent"), tone = OldgeBadgeTone.Accent)
                        OldgeBadge("Синхр.", Modifier.testTag("neutral"))
                        for (tone in listOf(OldgeBadgeTone.Success, OldgeBadgeTone.Warning, OldgeBadgeTone.Danger)) {
                            OldgeBadge("Синхр.", Modifier.testTag("$tone"), tone = tone)
                            OldgeBadge("12", Modifier.testTag("$tone count"), tone = tone, count = true)
                        }
                        OldgeBadge("12", Modifier.testTag("accent count"), tone = OldgeBadgeTone.Accent, count = true)
                    }
                }
            }

            fun width(tag: String) = onNodeWithTag(tag).getUnclippedBoundsInRoot().let { it.right - it.left }
            val plain = width("accent")
            assertEquals(plain, width("neutral"))
            for (tone in listOf("Success", "Warning", "Danger")) {
                assertEquals(plain + 18.dp, width(tone), "$tone should add an icon")
                assertEquals(width("accent count"), width("$tone count"), "a $tone count should take no icon")
            }
        }

    @Test
    fun a_one_digit_count_stays_a_22_px_circle_when_the_system_font_shrinks() =
        runComposeUiTest {
            // At the default size a digit fills the 22 px by itself; `min-width` is what holds a smaller one.
            setContent {
                OldgeTheme {
                    val d = LocalDensity.current
                    CompositionLocalProvider(LocalDensity provides Density(d.density, SMALL_FONT)) {
                        OldgeBadge("1", Modifier.testTag("n"), count = true)
                    }
                }
            }
            onNodeWithTag("n").assertWidthIsEqualTo(22.dp).assertHeightIsEqualTo(22.dp)
        }

    @Test
    fun a_count_bumps_when_it_changes_and_not_when_it_appears() =
        runComposeUiTest {
            mainClock.autoAdvance = false
            var n by mutableStateOf("12")
            setContent {
                OldgeTheme(reducedMotion = false) {
                    Box(Modifier.testTag("n")) { OldgeBadge(n, tone = OldgeBadgeTone.Accent, count = true) }
                }
            }
            mainClock.advanceTimeByFrame()
            val rest = grab("n")
            mainClock.advanceTimeBy(OldgeDurations.base * 2L / 5)
            assertEquals(rest, grab("n"), "the count bumped on its first appearance")
            n = "13"
            mainClock.advanceTimeByFrame()
            mainClock.advanceTimeBy(OldgeDurations.base * 2L / 5)
            val peak = grab("n")
            mainClock.advanceTimeBy(OldgeDurations.base * 2L)
            assertNotEquals(grab("n"), peak, "the count did not bump when it changed")
        }

    @Test
    fun an_avatar_is_one_image_whose_name_says_its_status_in_words() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column {
                        OldgeAvatar(name = "Павел Вотяков", status = OldgeAvatarStatus.Online)
                        OldgeAvatar(name = "Иван Ли", status = OldgeAvatarStatus.Busy)
                        OldgeAvatar(name = "Мария Орлова", status = OldgeAvatarStatus.Away)
                        OldgeAvatar(name = "Анна Ким")
                    }
                }
            }
            for (label in listOf("Павел Вотяков, в сети", "Иван Ли, занят", "Мария Орлова, отошёл", "Анна Ким")) {
                onNodeWithContentDescription(
                    label,
                ).assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Image))
            }
            // The initials are the face, not a second thing to read.
            onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text)).assertDoesNotExist()
        }

    @Test
    fun initials_are_the_first_letters_of_the_first_two_words() {
        assertEquals("ПВ", oldgeInitials("Павел Вотяков"))
        assertEquals("МО", oldgeInitials("  мария   орлова  петровна "))
        assertEquals("А", oldgeInitials("Анна"))
        assertEquals("", oldgeInitials(null))
    }

    @Test
    fun the_initials_keep_their_size_when_the_system_font_grows() {
        fun face(fontScale: Float): List<Any> {
            var pixels: List<Any> = emptyList()
            runComposeUiTest {
                setContent {
                    OldgeTheme {
                        val d = LocalDensity.current
                        CompositionLocalProvider(LocalDensity provides Density(d.density, fontScale)) {
                            OldgeAvatar(Modifier.testTag("a"), name = "Анна Ким", size = OldgeAvatarSize.Large)
                        }
                    }
                }
                pixels = grab("a")
            }
            return pixels
        }
        assertEquals(face(1f), face(2f), "the initials grew with the font scale")
    }

    @Test
    fun an_online_lamp_pulses_and_stands_still_under_reduced_motion() {
        fun frames(reduced: Boolean): Pair<List<Any>, List<Any>> {
            var pair: Pair<List<Any>, List<Any>> = emptyList<Any>() to emptyList()
            runComposeUiTest {
                mainClock.autoAdvance = false
                setContent {
                    OldgeTheme(reducedMotion = reduced) {
                        OldgeAvatar(Modifier.testTag("a"), name = "Павел Вотяков", status = OldgeAvatarStatus.Online)
                    }
                }
                mainClock.advanceTimeBy(100)
                val first = grab("a")
                mainClock.advanceTimeBy(500)
                pair = first to grab("a")
            }
            return pair
        }
        frames(reduced = false).let { (a, b) -> assertNotEquals(a, b, "the online lamp stood still") }
        frames(reduced = true).let { (a, b) -> assertEquals(a, b, "the lamp pulsed under reduced motion") }
    }
}

private const val SMALL_FONT = 0.75f

@OptIn(ExperimentalTestApi::class)
private fun androidx.compose.ui.test.ComposeUiTest.grab(tag: String): List<Any> {
    val m = onNodeWithTag(tag).captureToImage().toPixelMap()
    return (0 until m.height).flatMap { y -> (0 until m.width).map { x -> m[x, y] } }
}
