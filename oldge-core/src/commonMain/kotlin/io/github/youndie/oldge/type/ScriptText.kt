package io.github.youndie.oldge.type

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import kotlin.math.roundToInt

/**
 * One line of text in a face with a companion (research D6), laid out the way CSS lays it out.
 *
 * CSS builds a line box from the element's own font — its "strut" — and aligns an inline run in
 * another font on the same baseline, so a line of «ИЛИ» set in Silkscreen with a Tiny5 fallback is
 * still exactly its `line-height` tall. Compose lets the companion's metrics grow the line: measured
 * in B-08, the pixel tag's 12 sp line became 13 px with its top 1 px down, which moved the Divider's
 * label line and everything below it by a pixel. Here the line is measured as the design's face
 * alone would be, and the joined text is placed on that face's baseline.
 *
 * Single line only: every place the design sets lcd or pixel text — tags, readouts, labels — is one.
 */
@Composable
internal fun OldgeScriptText(
    text: String,
    style: TextStyle,
    companion: FontFamily,
    primaryCoverage: IntArray,
    modifier: Modifier = Modifier,
) {
    val measurer = rememberTextMeasurer()
    val strut = remember(style, measurer) { measurer.measure(STRUT_SAMPLE, style, softWrap = false, maxLines = 1) }
    val joined = remember(text, companion, primaryCoverage) { oldgeTextOf(text, companion, primaryCoverage) }
    BasicText(
        joined,
        modifier.layout { measurable, constraints ->
            val placeable = measurable.measure(constraints.copy(minHeight = 0, maxHeight = Int.MAX_VALUE))
            val lineHeight = strut.size.height
            val shift = (strut.firstBaseline - placeable[FirstBaseline]).roundToInt()
            layout(placeable.width, lineHeight) { placeable.place(0, shift) }
        },
        style = style,
        softWrap = false,
        maxLines = 1,
    )
}

/** Only the design's face covers this, so its line is the strut: Latin capitals and a digit. */
private const val STRUT_SAMPLE = "H0"
