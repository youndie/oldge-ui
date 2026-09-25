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
import androidx.compose.ui.unit.isSpecified
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.math.roundToInt

/**
 * One line of text in a face with a companion (research D6), laid out the way CSS lays it out.
 *
 * CSS builds a line box from the element's own font — its "strut" — and aligns an inline run in
 * another font on the same baseline, so a line of «ИЛИ» set in Silkscreen with a Tiny5 fallback is
 * still exactly its `line-height` tall. Compose lets the companion's metrics grow the line: measured
 * in B-08, the pixel tag's 12 sp line became 13 px with its top 1 px down, which moved the Divider's
 * label line and everything below it by a pixel. Here the line is measured as the design's face
 * alone would be, and the joined text is placed on the baseline CSS computes for that face
 * ([cssBaseline], B-11).
 *
 * Single line only: every place the design sets lcd or pixel text — tags, readouts, labels — is one.
 */
@Composable
internal fun OldgeScriptText(
    text: String,
    given: TextStyle,
    companion: FontFamily,
    primaryCoverage: IntArray,
    modifier: Modifier = Modifier,
) {
    val style = given.withHonouredLine()
    val measurer = rememberTextMeasurer()
    val metrics = OldgeTheme.type.metricsOf(style)
    val strut = remember(style, measurer) { measurer.measure(STRUT_SAMPLE, style, softWrap = false, maxLines = 1) }
    val joined = remember(text, companion, primaryCoverage) { oldgeTextOf(text, companion, primaryCoverage) }
    BasicText(
        joined,
        modifier.layout { measurable, constraints ->
            val placeable = measurable.measure(constraints.copy(minHeight = 0, maxHeight = Int.MAX_VALUE))
            // The line box is `line-height` whenever the style sets one, as CSS's is: a face taller
            // than its line (Share Tech Mono at 32 px on a 32 px line is 36 px of ascent and descent)
            // measures taller than the line in Compose, and a cell centring it put the digits 2 px
            // high (B-18, CodeInput).
            val lineHeight = if (style.lineHeight.isSpecified) style.lineHeight.roundToPx() else strut.size.height
            // Shifted by the pixel Compose draws the joined line's baseline on, not by the baseline it
            // reports: a face taller than its line (Share Tech Mono at 14 px in 14) was reported
            // where it is not drawn, and the Stepper's digit sat a pixel high (B-49, as B-46 for OldgeText).
            // The line is the primary face's alone: each companion's ascent and descent sit inside its
            // primary's (CompanionMetricsTest), so a Cyrillic run never makes the line taller.
            val shift =
                if (metrics != null && style.lineHeight.isSpecified) {
                    val size = style.fontSize.toPx()
                    val line = style.lineHeight.toPx()
                    (cssBaseline(metrics, size, line) - composeBaseline(metrics, size, line)).roundToInt()
                } else {
                    (strut.firstBaseline - placeable[FirstBaseline]).roundToInt()
                }
            layout(placeable.width, lineHeight) { placeable.place(0, shift) }
        },
        style = style,
        softWrap = false,
        maxLines = 1,
    )
}

/** Only the design's face covers this, so its line is the strut: Latin capitals and a digit. */
private const val STRUT_SAMPLE = "H0"
