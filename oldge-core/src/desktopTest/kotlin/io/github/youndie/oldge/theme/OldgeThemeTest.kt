package io.github.youndie.oldge.theme

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import io.github.youndie.oldge.tokens.OldgeColors
import io.github.youndie.oldge.tokens.OldgeDurations
import io.github.youndie.oldge.tokens.OldgeShadow
import io.github.youndie.oldge.tokens.OldgeShadows
import io.github.youndie.oldge.tokens.OldgeSpacing
import io.github.youndie.oldge.tokens.OldgeTypeStyles
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class OldgeThemeTest {
    @Test
    fun switching_the_skin_in_one_composition_changes_the_colours_and_the_shadows() =
        runComposeUiTest {
            var skin by mutableStateOf(OldgeSkin.Toxic)
            var accent = Color.Unspecified
            var orb = emptyList<OldgeShadow>()
            var body = TextStyle.Default
            setContent {
                OldgeTheme(skin = skin) {
                    accent = OldgeTheme.colors.accent
                    orb = OldgeTheme.shadows.orb
                    body = OldgeTheme.type.body
                }
            }
            waitForIdle()
            assertEquals(OldgeColors.Toxic.accent, accent)
            assertEquals(OldgeShadows.Toxic.orb, orb)
            val toxicBody = body
            skin = OldgeSkin.Crystal
            waitForIdle()
            assertEquals(OldgeColors.Crystal.accent, accent)
            assertEquals(OldgeShadows.Crystal.orb, orb)
            assertNotEquals(
                OldgeColors.Toxic.accent,
                accent,
                "toxic and crystal share an accent, so this proves nothing",
            )
            assertEquals(toxicBody, body, "a skin change moved a type style, which no skin defines")
        }

    @Test
    fun reduced_motion_makes_every_duration_zero_and_keeps_the_curves() {
        val reduced = OldgeMotion(reduced = true)
        val normal = OldgeMotion(reduced = false)
        assertEquals(listOf(0, 0, 0, 0, 0, 0), reduced.durations())
        assertEquals(
            listOf(
                OldgeDurations.instant,
                OldgeDurations.fast,
                OldgeDurations.base,
                OldgeDurations.slow,
                OldgeDurations.press,
                OldgeDurations.loop,
            ),
            normal.durations(),
        )
        assertEquals(normal.spring, reduced.spring)
    }

    @Test
    fun the_theme_passes_its_switches_down() =
        runComposeUiTest {
            var seen = Triple(false, true, true)
            setContent {
                OldgeTheme(reducedMotion = true, texture = false, pressFlash = false) {
                    seen = Triple(OldgeTheme.motion.reduced, OldgeTheme.texture, OldgeTheme.pressFlash)
                }
            }
            waitForIdle()
            assertEquals(Triple(true, false, false), seen)
        }

    /** EdgeScale's precondition (research §1.7): text follows the font scale, geometry does not. */
    @Test
    fun a_font_scale_of_two_doubles_every_text_style_and_no_length() =
        runComposeUiTest {
            val sizes = mutableListOf<Pair<Float, Float>>()
            var space = 0f
            setContent {
                OldgeTheme {
                    CompositionLocalProvider(LocalDensity provides Density(1f, fontScale = 2f)) {
                        val density = LocalDensity.current
                        with(density) {
                            sizes += OldgeTheme.type.all().map { it.fontSize.toPx() to it.lineHeight.toPx() }
                            space = OldgeSpacing.space4.toPx()
                        }
                    }
                }
            }
            waitForIdle()
            val expected = allTypeStyles().map { it.sizeRem * 16 * 2 to it.lineHeightRem * 16 * 2 }
            assertEquals(expected, sizes.take(expected.size))
            assertEquals(16f, space, "a dp moved with the font scale")
            assertTrue(expected.size == 11)
        }

    private fun OldgeMotion.durations() = listOf(instant, fast, base, slow, press, loop)

    private fun OldgeTypography.all() =
        listOf(display, heading, title, body, bodyStrong, button, label, caption, readout, readoutSm, pixelTag)

    private fun allTypeStyles() =
        with(OldgeTypeStyles) {
            listOf(display, heading, title, body, bodyStrong, button, label, caption, readout, readoutSm, pixelTag)
        }
}
