package io.github.youndie.oldge.type

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import io.github.youndie.oldge.theme.LocalOldgeTextStyle
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.theme.OldgeTypography
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * Text placed in its line the way a browser places it (B-11).
 *
 * CSS puts a line's baseline at `floor((line-height − (ascent + descent)) / 2) + ascent`, with the
 * face's ascent and descent each **rounded to whole pixels** first (Blink's font metrics). Compose
 * centres the same text using the unrounded values, which for most sizes lands on the same pixel
 * and for some does not: Silkscreen at 10 px in a 12 px line has ascent 10.3 and descent 2.5, so
 * CSS rounds to 10 + 3, overflows the line by one, floors the half-leading to −1 and puts the
 * baseline at 9 where Compose puts it at 9.9 — every pixel label a pixel low, measured on the Icon
 * preview. The text here is laid out by `BasicText` and moved onto the CSS baseline; its size is
 * unchanged.
 *
 * Styles from [OldgeTheme.type] are recognised by their face; a style in some other face is left
 * where Compose puts it.
 */
@Composable
internal fun OldgeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalOldgeTextStyle.current,
    maxLines: Int = Int.MAX_VALUE,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val metrics = OldgeTheme.type.metricsOf(style)
    BasicText(
        text,
        if (metrics == null) modifier else modifier.onCssBaseline(metrics, style),
        style = style,
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
        val target = cssBaseline(metrics, style.fontSize.toPx(), style.lineHeight.toPx())
        val shift = (target - placeable[FirstBaseline]).roundToInt()
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
