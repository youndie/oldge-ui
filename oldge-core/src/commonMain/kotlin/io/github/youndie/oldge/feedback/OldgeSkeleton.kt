package io.github.youndie.oldge.feedback

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.cssRoundRect
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import kotlin.math.sqrt

/** The form a skeleton stands in for. */
public enum class OldgeSkeletonVariant {
    /** Lines of text, a ragged staircase with the last one shorter. */
    Text,

    /** An avatar: a segmented dial. */
    Circle,

    /** Media: an empty frame with a ghost glyph. */
    Rect,

    /** A list row: an avatar and two lines. */
    Row,

    /** A card: media, then an avatar and lines. */
    Card,
}

/**
 * A loading placeholder — the design system's `Skeleton`: empty sunken slots ruled like a scanline,
 * with a scanner — a comet of the accent's XP blocks — swinging back and forth through each, a line
 * out of step with the next so that a block reads as a wave; a circle is a segmented dial with a lit
 * arc stepping round it.
 *
 * Its README's rules: repeat the shape of what is coming — [OldgeSkeletonVariant.Row] for a list row,
 * [OldgeSkeletonVariant.Card] for a Card; show it for a couple of seconds at most, then a Spinner with
 * a label; under reduced motion the scanner and the arc stand still. It says nothing to a screen
 * reader; the container it stands in should say it is busy. [index] shifts the phase when several
 * stand in a row.
 */
@Composable
public fun OldgeSkeleton(
    modifier: Modifier = Modifier,
    variant: OldgeSkeletonVariant = OldgeSkeletonVariant.Text,
    lines: Int? = null,
    width: Dp? = null,
    height: Dp? = null,
    icon: ImageVector = OldgeIcons.Image,
    index: Int = 0,
) {
    val c = OldgeTheme.colors
    val spacing = OldgeTheme.spacing
    val m = modifier.clearAndSetSemantics {}
    when (variant) {
        OldgeSkeletonVariant.Text -> {
            Lines(lines ?: TEXT_LINES, index, width, m)
        }

        OldgeSkeletonVariant.Circle -> {
            Dial(width ?: height ?: CIRCLE, index, m)
        }

        OldgeSkeletonVariant.Rect -> {
            Slot(height ?: RECT_HEIGHT, index, icon, width, m)
        }

        OldgeSkeletonVariant.Row -> {
            Row(
                m.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.space3),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Dial(CIRCLE, index)
                Lines(lines ?: GROUP_LINES, index + 1, width, Modifier.weight(1f))
            }
        }

        OldgeSkeletonVariant.Card -> {
            Column(
                m
                    .fillMaxWidth()
                    .cssBox(OldgeRadii.lg, CssBackground.Solid(c.surface), BORDER, c.edge, OldgeTheme.shadows.raised)
                    .padding(BORDER + spacing.space3),
                verticalArrangement = Arrangement.spacedBy(spacing.space3),
            ) {
                Slot(height ?: RECT_HEIGHT, index, icon, width)
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.space3),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Dial(CARD_CIRCLE, index + 1)
                    Lines(lines ?: GROUP_LINES, index + 2, width, Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * `.og-skel--text` lines: 14 px slots with 5 px of margin above and below, collapsing between
 * siblings but not through the flex item that holds them; widths from `SKEL_W`, the last of several
 * at 58 %.
 */
@Composable
private fun Lines(
    n: Int,
    from: Int,
    width: Dp?,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val full = maxWidth
        Column(Modifier.padding(vertical = LINE_MARGIN), verticalArrangement = Arrangement.spacedBy(LINE_MARGIN)) {
            for (i in 0 until n) {
                val w =
                    when {
                        i == n - 1 && n > 1 -> full * LAST_LINE
                        width != null -> width
                        else -> full * RAGGED[i % RAGGED.size]
                    }
                Box(Modifier.width(w).height(LINE).slot(from + i, SCAN_W, SCAN_INSET, SCAN_INSET))
            }
        }
    }
}

/** `.og-skel--rect`: a frame [height] high with a ghost glyph in `line`, the scanner along its bottom. */
@Composable
private fun Slot(
    height: Dp,
    index: Int,
    icon: ImageVector,
    width: Dp?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .then(if (width != null) Modifier.width(width) else Modifier.fillMaxWidth())
            .height(height)
            .slot(index, RECT_SCAN_W, bottom = RECT_SCAN_BOTTOM, band = RECT_SCAN_HEIGHT),
        contentAlignment = Alignment.Center,
    ) {
        OldgeIcon(
            icon,
            contentDescription = null,
            Modifier.alpha(GLYPH_ALPHA),
            size = GLYPH,
            tint = OldgeTheme.colors.line,
        )
    }
}

/**
 * `.og-skel`: `line` border, `radius-xs`, `shadow-sunken`, a scanline of `line` every 4 px on `well`,
 * and the scanner: a [scan]-wide comet of 6 px accent blocks 3 px apart, faded in and out at its ends
 * (its `drop-shadow` glow is not drawn: no reference shows it moving), swung from off the left edge to the right edge and back on `ease-in-out` every 1.8 s,
 * 0.24 s ahead per [index]. Under reduced motion it rests off the left edge, as the design's 1 ms run
 * leaves it, and so is not seen.
 */
@Composable
private fun Modifier.slot(
    index: Int,
    scan: Dp,
    top: Dp = 0.dp,
    bottom: Dp = 0.dp,
    band: Dp? = null,
): Modifier {
    val c = OldgeTheme.colors
    val phase = loopPhase(SCAN_MS, reverse = true)
    return this
        .drawBehind {
            // The background, under the bevel and the border as CSS paints it: `well`, and over it
            // `repeating-linear-gradient(180deg, transparent 0 3px, line 3px 4px)` from the padding box.
            val b = BORDER.toPx()
            val r = (OldgeRadii.xs.toPx() - b).coerceAtLeast(0f)
            val padding =
                Path().apply {
                    addRoundRect(
                        cssRoundRect(
                            Offset(b, b),
                            Size(
                                size.width - 2 * b,
                                size.height - 2 * b,
                            ),
                            r,
                        ),
                    )
                }
            clipPath(padding) {
                drawRect(c.well)
                var y = b + SCANLINE_GAP.toPx()
                while (y < size.height - b) {
                    drawRect(c.line, Offset(b, y), Size(size.width - 2 * b, HAIRLINE.toPx()))
                    y += SCANLINE.toPx()
                }
            }
        }.cssBox(OldgeRadii.xs, CssBackground.Solid(Color.Transparent), BORDER, c.line, OldgeTheme.shadows.sunken)
        .drawWithContent {
            drawContent()
            if (phase != null) {
                val b = BORDER.toPx()
                val w = scan.toPx()
                // `animation-delay: calc(var(--i) * -0.24s)`: each line a step ahead, in 1.8 s periods.
                val shifted = ((phase + index * SCAN_DELAY / (SCAN_MS / 1000f)) % 1f + 1f) % 1f
                val eased = EASE_IN_OUT.transform(shifted)
                val left = b + (-w + (size.width - 2 * b + w) * eased)
                // A band anchored at the bottom (the media's), or inset from top and bottom (a line's).
                val bandBottom = size.height - b - bottom.toPx()
                val bandTop = if (band != null) bandBottom - band.toPx() else b + top.toPx()
                clipRect(
                    b,
                    b,
                    size.width - b,
                    size.height - b,
                ) { drawComet(left, bandTop, w, bandBottom - bandTop, c.accent) }
            }
        }
}

/** The comet: 6 px blocks 3 px apart, masked `transparent → #000 35% → #000 65% → transparent`. */
private fun DrawScope.drawComet(
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    accent: Color,
) {
    val block = SCAN_BLOCK.toPx()
    val step = block + SCAN_GAP.toPx()
    var x = 0f
    while (x < width) {
        val centre = (x + minOf(block, width - x) / 2) / width
        val mask =
            when {
                centre < SCAN_FADE -> centre / SCAN_FADE
                centre > 1 - SCAN_FADE -> (1 - centre) / SCAN_FADE
                else -> 1f
            }
        drawRect(accent, Offset(left + x, top), Size(minOf(block, width - x), height), alpha = mask)
        x += step
    }
}

/**
 * `.og-skel--circle`: a [size] disc of `well` in a `line` border, and on it a dial — 12 segments of
 * 20° with 10° between, in a ring from 53 % of the farthest-corner radius out — in `line`, with the
 * first 100° of it lit in the accent; the lit arc steps round in twelve 30° steps every 1.2 s.
 */
@Composable
private fun Dial(
    size: Dp,
    index: Int,
    modifier: Modifier = Modifier,
) {
    val c = OldgeTheme.colors
    val phase = loopPhase(DIAL_MS)
    Box(
        modifier
            .size(size)
            .cssBox(OldgeRadii.pill, CssBackground.Solid(c.well), BORDER, c.line, OldgeTheme.shadows.sunken)
            .drawWithContent {
                drawContent()
                val inset = (BORDER + DIAL_INSET).toPx()
                val d = this.size.minDimension - 2 * inset
                val centre = Offset(this.size.width / 2, this.size.height / 2)
                val outer = d / 2
                val inner = d / 2 * sqrt(2f) * RING_FROM
                val ring =
                    Path().apply {
                        op(
                            Path().apply { addOval(Rect(centre, outer)) },
                            Path().apply { addOval(Rect(centre, inner)) },
                            PathOperation.Difference,
                        )
                    }
                // `steps(12)`, 0.1 s ahead per index.
                val step = phase?.let { ((it + index * DIAL_DELAY / (DIAL_MS / 1000f)) % 1f * DIAL_STEPS).toInt() } ?: 0
                clipPath(ring) {
                    for (s in 0 until DIAL_STEPS) drawSegment(centre, outer, s * SEGMENT_PITCH, SEGMENT, c.line)
                    // `conic-gradient(#000 0 100deg, transparent 100deg)` over the accent segments, the
                    // whole mask turned by the step.
                    rotate(step * SEGMENT_PITCH, centre) {
                        for (s in 0 until DIAL_STEPS) {
                            val from = s * SEGMENT_PITCH
                            val to = minOf(from + SEGMENT, LIT)
                            if (to > from) drawSegment(centre, outer, from, to - from, c.accent)
                        }
                    }
                }
            },
    )
}

/** An annular sector from [fromDeg] (clockwise from 12 o'clock) sweeping [sweepDeg]. */
private fun DrawScope.drawSegment(
    centre: Offset,
    radius: Float,
    fromDeg: Float,
    sweepDeg: Float,
    color: Color,
) = drawArc(
    color,
    fromDeg + CONIC_START,
    sweepDeg,
    useCenter = true,
    topLeft = centre - Offset(radius, radius),
    size =
        Size(radius * 2, radius * 2),
)

private val EASE_IN_OUT = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
private const val SCAN_MS = 1800
private const val SCAN_DELAY = 0.24f
private const val SCAN_FADE = 0.35f
private const val DIAL_MS = 1200
private const val DIAL_DELAY = 0.1f
private const val DIAL_STEPS = 12
private const val SEGMENT = 20f
private const val SEGMENT_PITCH = 30f
private const val LIT = 100f
private const val CONIC_START = -90f
private const val RING_FROM = 0.53f
private const val TEXT_LINES = 3
private const val GROUP_LINES = 2
private const val LAST_LINE = 0.58f
private const val GLYPH_ALPHA = 0.9f
private val RAGGED = listOf(1f, 0.92f, 0.78f, 0.88f, 0.7f)
private val BORDER = 1.dp
private val HAIRLINE = 1.dp
private val LINE = 14.dp // css literal: bundle.css `.og-skel--text { height: 14px }`
private val LINE_MARGIN = 5.dp // css literal: bundle.css `.og-skel--text { margin: 5px 0 }`
private val SCANLINE = 4.dp // css literal: bundle.css `.og-skel`, scanline `line 3px 4px`
private val SCANLINE_GAP = 3.dp // css literal: bundle.css `.og-skel`, scanline `transparent 0 3px`
private val SCAN_W = 54.dp // css literal: bundle.css `.og-skel { --og-scan-w: 54px }`
private val SCAN_INSET = 2.dp // css literal: bundle.css `.og-skel::after { top: 2px; bottom: 2px }`
private val SCAN_BLOCK = 6.dp // css literal: bundle.css `.og-skel::after`, `accent 0 6px`
private val SCAN_GAP = 3.dp // css literal: bundle.css `.og-skel::after { … transparent 6px 9px) }`
private val RECT_HEIGHT = 120.dp // css literal: bundle.js `Skeleton`, `rect(p.height || 120)`
private val RECT_SCAN_W = 72.dp // css literal: bundle.css `.og-skel--rect { --og-scan-w: 72px }`
private val RECT_SCAN_BOTTOM = 8.dp // css literal: bundle.css `.og-skel--rect::after { bottom: 8px; height: 10px }`
private val RECT_SCAN_HEIGHT = 10.dp // css literal: bundle.css `.og-skel--rect::after { bottom: 8px; height: 10px }`
private val GLYPH = 40.dp // css literal: bundle.js `Skeleton`, rect `Icon size: 40`
private val CIRCLE = 44.dp // css literal: bundle.js `Skeleton`, `circle(… 44)`
private val CARD_CIRCLE = 36.dp // css literal: bundle.js `Skeleton`, card `circle(36)`
private val DIAL_INSET = 3.dp // css literal: bundle.css `.og-skel--circle::before { inset: 3px }`
