package io.github.youndie.oldge.type

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.theme.LocalOldgeTextStyle
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.theme.OldgeTypography
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * Plain text in the skin's type — what a screen sets between the components: a screen title in
 * [OldgeTypography.display], a paragraph in `body`, a muted note in `ink-muted`. [style] defaults to
 * the text style the container provides (`body` in `ink` on an
 * [io.github.youndie.oldge.containers.OldgeScreenBody]).
 *
 * What it adds over `BasicText` with the same style is where the glyphs land. It lays the text out
 * with `BasicText`, then moves it so that it sits in its line as a browser sets it, and the design
 * lines up.
 *
 * - **The CSS baseline (B-11).** CSS puts a line's baseline at
 *   `floor((line-height − (ascent + descent)) / 2) + ascent`, with the ascent and descent each
 *   rounded to whole pixels first (Blink's font metrics). Compose centres the same text on the
 *   unrounded values. For most sizes that is the same pixel, and for some it is not: Silkscreen at
 *   10 px in a 12 px line is a pixel low in Compose.
 * - **The drawn baseline (B-46).** The move is measured against where Compose draws the glyphs,
 *   not against the `firstBaseline` it reports. For 13 px in a 16 px line the report is 12.43 and
 *   the glyphs are drawn at 13, so a shift read from the report left every 13 px label and the
 *   17 px titles a pixel low.
 * - **A line as tall as its font (B-49).** Compose lays such a line out as if no line height were
 *   set, on the face's own taller line. The line is nudged so that it is honoured.
 *
 * `InkMatchesChromeTest` holds the result to Chrome's ink, per size the tokens use. Styles from
 * [OldgeTheme.type] are recognised by their face; a style in some other face is left where Compose
 * puts it.
 *
 * Set the lcd and pixel faces (`readout`, `readoutSm`, `pixelTag`) through the components that use
 * them: `OldgeReadout`, `OldgeMeter`, `OldgeStepper` and the rest. Those faces have no Cyrillic of
 * their own, and the components join each with its companion face (research D6). Here, Cyrillic
 * in them falls back to whatever the platform has.
 */
@Composable
public fun OldgeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalOldgeTextStyle.current,
    maxLines: Int = Int.MAX_VALUE,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val metrics = OldgeTheme.type.metricsOf(style)
    val laid = style.withHonouredLine()
    BasicText(
        text,
        if (metrics == null) modifier else modifier.onCssBaseline(metrics, laid),
        style = laid,
        maxLines = maxLines,
        softWrap = softWrap,
        overflow = overflow,
    )
}

/** Moves laid-out text so that its first baseline is where CSS puts it for [metrics] and [style]. */
internal fun Modifier.onCssBaseline(
    metrics: FontVerticalMetrics,
    style: TextStyle,
): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val size = style.fontSize.toPx()
        val line = style.lineHeight.toPx()
        val shift = (cssBaseline(metrics, size, line) - composeBaseline(metrics, size, line)).roundToInt()
        layout(placeable.width, placeable.height) { placeable.place(0, shift) }
    }

/** The baseline, in px from the line's top, that Blink computes for a face at a size in a line. */
internal fun cssBaseline(
    metrics: FontVerticalMetrics,
    fontSizePx: Float,
    lineHeightPx: Float,
): Float {
    val ascent = floor(metrics.ascent * fontSizePx / metrics.unitsPerEm + 0.5f)
    val descent = floor(metrics.descent * fontSizePx / metrics.unitsPerEm + 0.5f)
    return floor((lineHeightPx - (ascent + descent)) / 2) + ascent
}

/**
 * The pixel Compose draws the first baseline on, from its line's top, for a face at a size in a line
 * with the typography's `LineHeightStyle(Center, Trim.None)`: the centred baseline from the unrounded
 * ascent and descent, rounded half up (B-46, measured over every size the tokens use).
 */
internal fun composeBaseline(
    metrics: FontVerticalMetrics,
    fontSizePx: Float,
    lineHeightPx: Float,
): Float {
    val ascent = metrics.ascent * fontSizePx / metrics.unitsPerEm
    val descent = metrics.descent * fontSizePx / metrics.unitsPerEm
    return floor(ascent + (lineHeightPx - (ascent + descent)) / 2 + 0.5f)
}

/**
 * [style] with a line that Compose honours. A line exactly as tall as the font size (a height
 * multiplier of 1) is laid out as if no line height were set: the face's own, taller line, with the
 * baseline at its ascent. The lcd readouts drew 2 px low that way, and a node taller than its line
 * sat off-centre in the Avatar's face (B-49). A hair more line keeps the multiplier off 1, and the
 * text is laid out and drawn as every other line is.
 */
internal fun TextStyle.withHonouredLine(): TextStyle =
    if (lineHeight.isSp && fontSize.isSp && lineHeight.value == fontSize.value) {
        copy(lineHeight = (lineHeight.value * SAME_SIZE_NUDGE).sp)
    } else {
        this
    }

/** The bundled face a style of this typography is set in, by its family and weight. */
internal fun OldgeTypography.metricsOf(style: TextStyle): FontVerticalMetrics? {
    val bold = (style.fontWeight ?: FontWeight.Normal) >= FontWeight.Bold
    return when (style.fontFamily) {
        families.ui -> if (bold) BundledFontMetrics.dejavuSansCondensedBold else BundledFontMetrics.dejavuSansCondensed
        families.title -> BundledFontMetrics.firaSansBold
        families.lcd -> BundledFontMetrics.shareTechMono
        families.lcdCompanion -> BundledFontMetrics.ptMono
        families.pixel -> BundledFontMetrics.silkscreen
        families.pixelCompanion -> BundledFontMetrics.tiny5
        else -> null
    }
}

private const val SAME_SIZE_NUDGE = 1.0001f
