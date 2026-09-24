package io.github.youndie.oldge.type

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.resources.Res
import io.github.youndie.oldge.resources.silkscreen
import io.github.youndie.oldge.resources.tiny5
import org.jetbrains.compose.resources.Font
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * The per-script join of research D6, measured on the bundled faces rather than assumed.
 *
 * The width of a run inside the joined string is compared with the width of the same run set alone
 * in the face that is supposed to draw it. That is portable — both sides are measured in one run on
 * one machine — and it fails if the join stops happening: the run then comes out at the host face's
 * width, which is what [a_family_list_does_not_fall_back_so_the_host_draws_the_missing_glyphs]
 * shows a lone face produces.
 */
@OptIn(ExperimentalTestApi::class)
class CompanionJoinTest {
    @Test
    fun lcd_cyrillic_runs_are_set_in_pt_mono_and_digits_in_share_tech_mono() =
        assertJoined(
            LCD_TEXT,
            run = "ГБ",
            latinRun = "23,4",
            { it.lcd },
            { it.lcdCompanion },
            FontCoverage.shareTechMono,
        )

    @Test
    fun pixel_cyrillic_runs_are_set_in_tiny5_and_latin_in_silkscreen() =
        assertJoined(PIXEL_TEXT, run = "ХРАНИЛИЩЕ", latinRun = "SYNC", {
            it.pixel
        }, { it.pixelCompanion }, FontCoverage.silkscreen)

    /**
     * Research §1.6's open question, answered: what draws Cyrillic set in Silkscreen alone.
     *
     * Not a tofu box and not nothing: two different letters render as two different shapes with
     * ink, so the glyphs are real — and since Silkscreen has none, they are the host's. And a second
     * face in the same family does not change that, which is why the companion is a separate span.
     */
    @Test
    fun a_family_list_does_not_fall_back_so_the_host_draws_the_missing_glyphs() {
        val kha = pixels({ AnnotatedString("Х") }) { it.pixel }
        val el = pixels({ AnnotatedString("Л") }) { it.pixel }
        assertTrue(kha.count { it } > 0, "Silkscreen alone drew no ink for Х")
        assertNotEquals(
            kha,
            el,
            "Х and Л rendered identically in Silkscreen alone: that is a .notdef box, not a host glyph",
        )

        val alone = pixels({ AnnotatedString(PIXEL_TEXT) }) { it.pixel }
        val listed =
            pixels({ AnnotatedString(PIXEL_TEXT) }) {
                FontFamily(Font(Res.font.silkscreen, FontWeight.Normal), Font(Res.font.tiny5, FontWeight.Normal))
            }
        assertEquals(
            alone,
            listed,
            "a second face in the family changed the render, so Compose now falls back per glyph",
        )

        // The control: the joined text is not what the host draws.
        val joined = pixels({ oldgeTextOf(PIXEL_TEXT, it.pixelCompanion, FontCoverage.silkscreen) }) { it.pixel }
        assertNotEquals(alone, joined, "the joined text renders exactly like the host fallback")
    }

    private fun assertJoined(
        text: String,
        run: String,
        latinRun: String,
        primary: (OldgeFontFamilies) -> FontFamily,
        companion: (OldgeFontFamilies) -> FontFamily,
        coverage: IntArray,
    ) {
        val joined = width(text, { oldgeTextOf(text, companion(it), coverage) }, primary, run)
        val inCompanion = width(run, { AnnotatedString(run) }, companion, run)
        assertClose(inCompanion, joined, "the run «$run» is not set in the companion")
        // Width alone cannot tell the companion from the host: PT Mono and the host's monospace are
        // both 0.6 em, measured at 24.0 px for «ГБ» at 20 sp. The pixels can.
        assertNotEquals(
            pixels({ AnnotatedString(text) }) { primary(it) },
            pixels({ oldgeTextOf(text, companion(it), coverage) }) { primary(it) },
            "the joined text renders exactly like the design's face alone, which the host completes",
        )

        val latinJoined = width(text, { oldgeTextOf(text, companion(it), coverage) }, primary, latinRun)
        val latinAlone = width(latinRun, { AnnotatedString(latinRun) }, primary, latinRun)
        assertClose(latinAlone, latinJoined, "the run «$latinRun» left the design's face")
    }

    private fun assertClose(
        expected: Float,
        actual: Float,
        message: String,
    ) = assertTrue(abs(expected - actual) <= TOLERANCE_PX, "$message: expected $expected px, got $actual px")

    /** The horizontal extent of the first occurrence of [run] in the laid-out text. */
    private fun width(
        text: String,
        content: @Composable (OldgeFontFamilies) -> AnnotatedString,
        family: (OldgeFontFamilies) -> FontFamily,
        run: String,
    ): Float {
        var layout: TextLayoutResult? = null
        runComposeUiTest {
            setContent {
                val families = oldgeFontFamilies()
                BasicText(
                    content(
                        families,
                    ),
                    style = TextStyle(fontFamily = family(families), fontSize = SIZE),
                    onTextLayout = {
                        layout =
                            it
                    },
                )
            }
            waitForIdle()
        }
        val start = text.indexOf(run)
        val result = checkNotNull(layout)
        return result.getHorizontalPosition(start + run.length, true) - result.getHorizontalPosition(start, true)
    }

    private fun pixels(
        content: @Composable (OldgeFontFamilies) -> AnnotatedString,
        family: @Composable (OldgeFontFamilies) -> FontFamily,
    ): List<Boolean> {
        var lit = emptyList<Boolean>()
        runComposeUiTest {
            setContent {
                val families = oldgeFontFamilies()
                Box(Modifier.size(320.dp, 40.dp).background(Color.Black).testTag(TAG)) {
                    BasicText(
                        content(families),
                        style = TextStyle(fontFamily = family(families), fontSize = SIZE, color = Color.White),
                    )
                }
            }
            waitForIdle()
            val map = onNodeWithTag(TAG).captureToImage().toPixelMap()
            lit = List(map.width * map.height) { i -> map[i % map.width, i / map.width].red > 0.5f }
        }
        return lit
    }

    private companion object {
        const val TAG = "text"
        const val LCD_TEXT = "23,4 ГБ из 64"
        const val PIXEL_TEXT = "SYNC ХРАНИЛИЩЕ"
        val SIZE = 20.sp
        const val TOLERANCE_PX = 0.5f
    }
}
