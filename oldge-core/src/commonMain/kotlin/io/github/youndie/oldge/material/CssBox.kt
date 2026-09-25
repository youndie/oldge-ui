package io.github.youndie.oldge.material

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.tokens.OldgeShadow
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * A CSS box, painted the way a browser paints one — the one primitive every material here is made
 * of (B-07). The design system's surfaces are CSS boxes: a background under a 1 px border, inset
 * shadows inside the border, outer shadows and spread rings outside it, and on glossy ones a
 * highlight band. Painting that model once, with the browser's rules, is what lets a Panel, an LCD
 * well, a button and a frame match their references without each re-deriving the geometry.
 *
 * The rules implemented, each from CSS Backgrounds and Borders 3:
 * - corner radii shrink proportionally when adjacent ones would overlap ([cssRoundRect]);
 * - the padding box's radius is the border box's minus the border width;
 * - an inset shadow is painted inside the padding box: the padding box minus itself moved by the
 *   offset and shrunk by the spread; an outer spread ring is the border box grown by the spread,
 *   minus the border box.
 *
 * A [borderBackground] is CSS's two-layer `background: <fill> padding-box, <rim> border-box`: the
 * fill clipped to and laid out on the padding box, the rim on the border box and seen only through a
 * transparent border (the Fab's chrome frame).
 *
 * Inset shadows are drawn as that path difference, not with `Modifier.innerShadow`: on the Panel
 * probe the difference is 0.04–0.07 % away from Chrome and `innerShadow` 3.3–4.1 %, off along the
 * whole outline (research §1.4, measured in B-07). Blurred outer shadows do use `dropShadow`.
 */
internal fun Modifier.cssBox(
    radius: Dp,
    background: CssBackground,
    border: Dp = 0.dp,
    borderColor: Color = Color.Transparent,
    shadows: List<OldgeShadow> = emptyList(),
    extraOuter: List<OldgeShadow> = emptyList(),
    gloss: Color? = null,
    borderBackground: CssBackground? = null,
): Modifier {
    val all = shadows + extraOuter
    val blurred = all.filter { !it.inset && it.blur > 0.dp }
    val blurredInsets = all.filter { it.inset && it.blur > 0.dp }
    val shape =
        androidx.compose.foundation.shape
            .RoundedCornerShape(radius)
    var modifier = this
    for (drop in blurred) {
        modifier =
            modifier.dropShadow(
                shape,
                Shadow(
                    radius = drop.blur,
                    color = drop.color,
                    spread = drop.spread,
                    offset = DpOffset(drop.offsetX, drop.offsetY),
                ),
            )
    }
    modifier =
        modifier.drawWithCache {
            val r = radius.toPx()
            val b = border.toPx()
            val outer = cssRoundRect(Offset.Zero, size, r)
            val padding =
                cssRoundRect(Offset(b, b), Size(size.width - 2 * b, size.height - 2 * b), (r - b).coerceAtLeast(0f))
            val rings = all.filter { !it.inset && it.blur == 0.dp }
            val insets = all.filter { it.inset && it.blur == 0.dp }
            // Blink's `kBackgroundBleedClipLayer`: a rounded box with a visible border is painted
            // into one layer that is clipped to the border box once. Painted one over the other, the
            // background and the border would each be antialiased along the same outer edge and
            // that edge's pixels covered twice — too opaque by up to a quarter (measured in B-13 on
            // Segmented's pill ends).
            val layered = b > 0f && borderBackground == null && borderColor.alpha > 0f && r > 0f
            val square = RoundRect(outer.left, outer.top, outer.right, outer.bottom)
            // Everything outside the border box, erased with DstOut: a DstIn through the box itself
            // would leave the pixels the path does not reach — the layer's square corners. The erased
            // area reaches a pixel past the layer: ending on its edge, it covered that edge only in
            // part under a press's scale, and the square under the corners showed as a faint frame
            // (found in B-14 on the pressed chips).
            val outside =
                Path().apply {
                    op(
                        Path().apply { addRect(Rect(Offset.Zero, size).inflate(1f)) },
                        Path().apply { addRoundRect(outer) },
                        PathOperation.Difference,
                    )
                }
            onDrawBehind {
                for (ring in rings) drawSpreadRing(outer, r, ring)
                if (layered) drawContext.canvas.saveLayer(Rect(Offset.Zero, size), Paint())
                if (borderBackground == null) {
                    drawBackground(background, if (layered) square else outer, padding)
                } else {
                    drawBackground(borderBackground, outer)
                    drawBackground(background, padding)
                }
                if (gloss != null) drawGloss(gloss, padding, r)
                for (inset in insets) drawInset(padding, inset)
                if (b > 0f && borderBackground == null) drawBorder(if (layered) square else outer, padding, borderColor)
                if (layered) {
                    drawPath(outside, Color.Black, blendMode = BlendMode.DstOut)
                    drawContext.canvas.restore()
                }
            }
        }
    // A blurred inset (an orb's pressed core) cannot be drawn as a path difference; innerShadow
    // draws it. It lost the 1 px bevel measurement (research §1.4), and no reference shows a pressed
    // state to measure this one against.
    for (inset in blurredInsets) {
        modifier =
            modifier.innerShadow(
                shape,
                Shadow(
                    radius = inset.blur,
                    color = inset.color,
                    spread = inset.spread,
                    offset = DpOffset(inset.offsetX, inset.offsetY),
                ),
            )
    }
    return modifier
}

/** A CSS background: a colour, or a `linear-gradient` at a CSS angle (180° is "to bottom"). */
internal sealed interface CssBackground {
    data class Solid(
        val color: Color,
    ) : CssBackground

    data class Linear(
        val stops: List<Pair<Float, Color>>,
        val angleDegrees: Float = 180f,
    ) : CssBackground

    /**
     * `radial-gradient(circle at <x> <y>, …)`: a circle centred at [centreX], [centreY] (fractions
     * of the box) reaching the farthest corner, CSS's default size; [stops] are fractions of that
     * radius.
     */
    data class Radial(
        val stops: List<Pair<Float, Color>>,
        val centreX: Float,
        val centreY: Float,
    ) : CssBackground
}

/** The brush a [CssBackground.Radial] paints with, over a box at [origin] of [size]. */
internal fun CssBackground.Radial.brush(
    size: Size,
    origin: Offset = Offset.Zero,
): Brush {
    val centre = origin + Offset(size.width * centreX, size.height * centreY)
    val dx = maxOf(size.width * centreX, size.width * (1 - centreX))
    val dy = maxOf(size.height * centreY, size.height * (1 - centreY))
    return Brush.radialGradient(
        colorStops = stops.toTypedArray(),
        center = centre,
        radius = kotlin.math.hypot(dx, dy),
    )
}

/** A two- or three-stop vertical gloss: `hi`, `mid` at 55 % when there is one, `lo`. */
internal fun glossGradient(
    hi: Color,
    mid: Color?,
    lo: Color,
): CssBackground.Linear =
    CssBackground.Linear(
        if (mid ==
            null
        ) {
            listOf(0f to hi, 1f to lo)
        } else {
            listOf(0f to hi, GLOSS_MID to mid, 1f to lo)
        },
    )

private const val GLOSS_MID = 0.55f

/** The top 36 % of a glossy element carries the `gloss` highlight (bundle.css `.og-gloss::before`). */
private const val GLOSS_HEIGHT = 0.36f

/**
 * A CSS `linear-gradient(<angle>, …)` as a Compose brush for a box of [size]: the gradient line runs
 * through the centre at the angle (0° up, clockwise), and is as long as the box's projection on it.
 */
internal fun cssLinearGradient(
    stops: List<Pair<Float, Color>>,
    angleDegrees: Float,
    size: Size,
    origin: Offset = Offset.Zero,
): Brush {
    val a = angleDegrees * PI.toFloat() / 180f
    val dx = sin(a)
    val dy = -cos(a)
    val half = (abs(size.width * dx) + abs(size.height * dy)) / 2
    val centre = origin + Offset(size.width / 2, size.height / 2)
    return Brush.linearGradient(
        colorStops = stops.toTypedArray(),
        start = centre - Offset(dx * half, dy * half),
        end = centre + Offset(dx * half, dy * half),
    )
}

/**
 * A rounded rectangle with CSS's radius rule: every radius is [radius] unless two on one side would
 * overlap, in which case all shrink by the same factor (CSS Backgrounds 3, §5.5).
 */
internal fun cssRoundRect(
    topLeft: Offset,
    size: Size,
    radius: Float,
    radii: CssRadii = CssRadii.all(radius, radius),
): RoundRect {
    val w = size.width
    val h = size.height
    val f =
        listOf(
            w / (radii.tl.x + radii.tr.x),
            w / (radii.bl.x + radii.br.x),
            h / (radii.tl.y + radii.bl.y),
            h / (radii.tr.y + radii.br.y),
        ).filter { it.isFinite() }.fold(1f, ::min)

    fun c(r: CornerRadius) = CornerRadius(r.x * f, r.y * f)
    return RoundRect(
        topLeft.x,
        topLeft.y,
        topLeft.x + w,
        topLeft.y + h,
        c(radii.tl),
        c(radii.tr),
        c(radii.br),
        c(radii.bl),
    )
}

/** Four elliptical corner radii, in px. */
internal data class CssRadii(
    val tl: CornerRadius,
    val tr: CornerRadius,
    val br: CornerRadius,
    val bl: CornerRadius,
) {
    companion object {
        fun all(
            x: Float,
            y: Float,
        ) = CornerRadius(x, y).let { CssRadii(it, it, it, it) }
    }
}

/**
 * Fills [box] (the border box: `background-clip` defaults to it) with a gradient laid out over
 * [origin] (the padding box: `background-origin` defaults to it), as CSS does.
 */
internal fun DrawScope.drawBackground(
    background: CssBackground,
    box: RoundRect,
    origin: RoundRect = box,
) {
    val path = Path().apply { addRoundRect(box) }
    when (background) {
        is CssBackground.Solid -> {
            drawPath(path, background.color)
        }

        is CssBackground.Linear -> {
            drawPath(
                path,
                cssLinearGradient(
                    background.stops,
                    background.angleDegrees,
                    Size(origin.width, origin.height),
                    Offset(origin.left, origin.top),
                ),
            )
        }

        is CssBackground.Radial -> {
            drawPath(path, background.brush(Size(origin.width, origin.height), Offset(origin.left, origin.top)))
        }
    }
}

private fun DrawScope.drawBorder(
    outer: RoundRect,
    inner: RoundRect,
    color: Color,
) {
    val ring =
        Path().apply {
            op(Path().apply { addRoundRect(outer) }, Path().apply { addRoundRect(inner) }, PathOperation.Difference)
        }
    drawPath(ring, color)
}

/** An inset shadow with no blur: the padding box minus itself moved by the offset, shrunk by the spread. */
private fun DrawScope.drawInset(
    padding: RoundRect,
    shadow: OldgeShadow,
) {
    val spread = shadow.spread.toPx()
    val dx = shadow.offsetX.toPx()
    val dy = shadow.offsetY.toPx()
    val hole =
        cssRoundRect(
            Offset(padding.left + dx + spread, padding.top + dy + spread),
            Size(padding.width - 2 * spread, padding.height - 2 * spread),
            (padding.topLeftCornerRadius.x - spread).coerceAtLeast(0f),
        )
    val box = Path().apply { addRoundRect(padding) }
    val band = Path().apply { op(box, Path().apply { addRoundRect(hole) }, PathOperation.Difference) }
    clipPath(box) { drawPath(band, shadow.color) }
}

/** An outer shadow with no blur and no offset: a ring of the spread's width outside the border box. */
private fun DrawScope.drawSpreadRing(
    outer: RoundRect,
    radius: Float,
    shadow: OldgeShadow,
) {
    val s = shadow.spread.toPx()
    val grown =
        cssRoundRect(
            Offset(outer.left - s, outer.top - s),
            Size(outer.width + 2 * s, outer.height + 2 * s),
            radius + s,
        )
    val ring =
        Path().apply {
            op(Path().apply { addRoundRect(grown) }, Path().apply { addRoundRect(outer) }, PathOperation.Difference)
        }
    drawPath(ring, shadow.color)
}

/**
 * `.og-gloss::before`: 1 px in from the padding box on the left, right and top, 36 % of the padding
 * box's height, the element's own radius on the top corners and none at the bottom.
 */
private fun DrawScope.drawGloss(
    gloss: Color,
    padding: RoundRect,
    radius: Float,
) {
    val inset = 1f
    val top = Offset(padding.left + inset, padding.top + inset)
    val size = Size(padding.width - 2 * inset, padding.height * GLOSS_HEIGHT)
    val zero = CornerRadius.Zero
    val round = CornerRadius(radius, radius)
    val band = cssRoundRect(top, size, radius, CssRadii(round, round, zero, zero))
    drawPath(Path().apply { addRoundRect(band) }, gloss)
}
