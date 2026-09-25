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
import io.github.youndie.oldge.harness.PortableText
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Compose's side of `scripts/research/ink-probe.mjs` (B-46): the ink of «НН» in each line box lands
 * where Chrome's does. The centre of the ink, weighted by coverage, is held to Chrome's within a
 * quarter of a pixel: the fault this guards was a whole pixel, at 13 px and in the 17 px titles.
 */
@OptIn(ExperimentalTestApi::class)
class InkMatchesChromeTest {
    @Test
    fun text_ink_lands_where_chromes_does_at_every_size_the_tokens_use() {
        val off = mutableListOf<String>()
        for (case in CASES) {
            runComposeUiTest {
                setContent {
                    OldgeTheme {
                        PortableText {
                            val t = OldgeTheme.type
                            val face = if (case.family == "ui") t.families.ui else t.families.title
                            val style =
                                t.body.copy(
                                    fontFamily = face,
                                    fontWeight = FontWeight(case.weight),
                                    fontSize = case.size.sp,
                                    lineHeight = case.line.sp,
                                    color = Color.Black,
                                )
                            Box(
                                Modifier
                                    .testTag("c")
                                    .width(120.dp)
                                    .height(case.line.dp)
                                    .background(Color.White),
                            ) {
                                OldgeText("НН", style = style, softWrap = false)
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
    )
