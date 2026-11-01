package io.github.youndie.oldge.type

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * B-87: an [AnnotatedString] goes through [OldgeText]'s baseline placement as a plain string does,
 * and keeps its spans. The size is 13 px in a 16 px line, where `BasicText` lands a pixel off the
 * CSS baseline (B-46), so the control below can tell placed text from unplaced.
 */
@OptIn(ExperimentalTestApi::class)
class OldgeTextAnnotatedTest {
    private fun images(content: @Composable (tag: String) -> Unit): Map<String, PixelMap> {
        val tags = listOf("plain", "annotated", "basic")
        lateinit var out: Map<String, PixelMap>
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column { for (t in tags) content(t) }
                }
            }
            out = tags.associateWith { onNodeWithTag(it).captureToImage().toPixelMap() }
        }
        return out
    }

    private fun same(
        a: PixelMap,
        b: PixelMap,
    ): Boolean =
        a.width == b.width && a.height == b.height &&
            (0 until a.width).all { x -> (0 until a.height).all { y -> a[x, y] == b[x, y] } }

    @Test
    fun an_annotated_text_lands_where_the_plain_one_does_and_not_where_basic_text_does() {
        val px =
            images { tag ->
                val style =
                    OldgeTheme.type.label.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 0.sp,
                        color = Color.Black,
                    )
                Box(
                    Modifier
                        .testTag(tag)
                        .width(120.dp)
                        .height(16.dp)
                        .background(Color.White),
                ) {
                    when (tag) {
                        "plain" -> OldgeText("НН", style = style)
                        "annotated" -> OldgeText(AnnotatedString("НН"), style = style)
                        else -> BasicText(AnnotatedString("НН"), style = style)
                    }
                }
            }
        assertTrue(same(px.getValue("plain"), px.getValue("annotated")), "the annotated text is drawn elsewhere")
        // The control: unplaced, the same text sits a pixel off, or this test could not tell the two.
        assertTrue(!same(px.getValue("annotated"), px.getValue("basic")), "the annotated text was not moved")
    }

    @Test
    fun a_span_keeps_its_own_style() =
        runComposeUiTest {
            val text =
                buildAnnotatedString {
                    append("НН")
                    withStyle(SpanStyle(color = Color.Red)) { append("НН") }
                }
            setContent {
                OldgeTheme {
                    Box(Modifier.testTag("t").background(Color.White)) {
                        OldgeText(text, style = OldgeTheme.type.body.copy(color = Color.Black))
                    }
                }
            }
            val m = onNodeWithTag("t").captureToImage().toPixelMap()
            var red = 0
            for (x in 0 until m.width) {
                for (y in 0 until m.height) {
                    val p = m[x, y]
                    if (p.red > 0.8f && p.green < 0.3f && p.blue < 0.3f) red++
                }
            }
            assertTrue(red > 0, "no pixel of the red span was drawn")
            assertEquals(Color.White, m[0, 0])
        }
}
