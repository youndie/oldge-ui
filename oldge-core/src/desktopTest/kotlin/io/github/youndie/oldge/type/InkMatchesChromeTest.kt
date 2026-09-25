package io.github.youndie.oldge.type

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.harness.PortableTextStyle
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Compose's side of `scripts/research/ink-probe.mjs` (B-46, B-49): the ink of each case's text in its
 * line box lands where Chrome's does. The centre of the ink, weighted by coverage, is held to
 * Chrome's within a quarter of a pixel: the faults this guards were a whole pixel — `OldgeText` at
 * 13 px and in the 17 px titles, and `OldgeScriptText`'s lcd digit at 14 px in a 14 px line.
 */
@OptIn(ExperimentalTestApi::class)
class InkMatchesChromeTest {
    @Test
    fun text_ink_lands_where_chromes_does_at_every_size_the_tokens_use() {
        val off = mutableListOf<String>()
        for (case in CASES) {
            runComposeUiTest {
                setContent {
                    OldgeTheme(platformTextStyle = PortableTextStyle) {
                        val t = OldgeTheme.type
                        val base =
                            when (case.family) {
                                "lcd" -> t.readoutSm
                                "pixel" -> t.pixelTag
                                "title" -> t.body.copy(fontFamily = t.families.title)
                                else -> t.body.copy(fontFamily = t.families.ui)
                            }
                        val style =
                            base.copy(
                                fontWeight = FontWeight(case.weight),
                                fontSize = case.size.sp,
                                lineHeight = case.line.sp,
                                letterSpacing = 0.sp,
                                color = Color.Black,
                            )
                        Box(
                            Modifier
                                .testTag("c")
                                .width(120.dp)
                                .height(case.line.dp)
                                .background(Color.White),
                        ) {
                            when (case.family) {
                                "lcd" -> {
                                    OldgeScriptText(
                                        case.text,
                                        style,
                                        t.families.lcdCompanion,
                                        FontCoverage.shareTechMono,
                                    )
                                }

                                "pixel" -> {
                                    OldgeScriptText(
                                        case.text,
                                        style,
                                        t.families.pixelCompanion,
                                        FontCoverage.silkscreen,
                                    )
                                }

                                else -> {
                                    OldgeText(case.text, style = style, softWrap = false)
                                }
                            }
                        }
                    }
                }
                val m = onNodeWithTag("c").captureToImage().toPixelMap()
                val rows =
                    (0 until m.height).map { y ->
                        (0 until m.width).maxOf { x -> ((1 - m[x, y].red) * FULL).toInt() }
                    }
                val centre = rows.withIndex().sumOf { (y, v) -> y * v }.toFloat() / rows.sum()
                if (abs(centre - case.chrome) > TOLERANCE) {
                    off +=
                        "${case.family} ${case.weight} ${case.size}px/${case.line}px: ink centre $centre, Chrome's ${case.chrome}"
                }
            }
        }
        assertEquals(emptyList(), off)
    }
}

private const val TOLERANCE = 0.25f
private const val FULL = 255

/** A style's face, weight, size and line, and the centre of Chrome's ink in the line box, in px. */
private data class Case(
    val family: String,
    val weight: Int,
    val size: Int,
    val line: Int,
    val chrome: Float,
    val text: String = "НН",
)

// Chrome/153 through the reference wrapper: `node scripts/research/ink-probe.mjs`.
private val CASES =
    listOf(
        Case("ui", 400, 12, 16, 7.11f),
        Case("ui", 700, 12, 16, 7.11f),
        Case("ui", 700, 13, 16, 6.75f),
        Case("ui", 400, 13, 18, 7.75f),
        Case("ui", 700, 13, 20, 8.75f),
        Case("ui", 400, 14, 18, 8.39f),
        Case("ui", 400, 15, 20, 9.03f),
        Case("ui", 700, 15, 20, 9.03f),
        Case("ui", 400, 16, 20, 8.66f),
        Case("title", 700, 17, 22, 9.6f),
        Case("title", 700, 20, 24, 11.56f),
        Case("title", 700, 28, 32, 14.79f),
        // The lcd and pixel faces through OldgeScriptText (B-49), Latin in the face itself and
        // Cyrillic from its companion.
        Case("lcd", 400, 12, 16, 7.29f, "HH"),
        Case("lcd", 400, 13, 16, 6.95f, "HH"),
        Case("lcd", 400, 14, 14, 5.5f, "HH"),
        Case("lcd", 400, 16, 20, 8.89f, "HH"),
        Case("lcd", 400, 20, 20, 8.5f, "HH"),
        Case("lcd", 400, 24, 24, 10.1f, "HH"),
        Case("lcd", 400, 28, 28, 12.69f, "HH"),
        Case("lcd", 400, 32, 32, 14.29f, "HH"),
        Case("lcd", 400, 34, 34, 15.6f, "HH"),
        Case("pixel", 400, 8, 10, 5f, "HH"),
        Case("pixel", 400, 10, 12, 5.36f, "HH"),
        Case("lcd", 400, 14, 14, 5.64f, "ГБ"),
        Case("lcd", 400, 16, 20, 9.07f, "ГБ"),
        Case("pixel", 400, 10, 12, 5.36f, "ИЛИ"),
    )
