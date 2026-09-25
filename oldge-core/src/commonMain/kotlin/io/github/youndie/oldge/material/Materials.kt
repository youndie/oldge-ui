package io.github.youndie.oldge.material

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeColors
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.tokens.OldgeShadow
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.graphics.Shadow as TextShadow

// The design system's materials (B-07), each a combination of the CSS box of CssBox.kt with the
// rules bundle.css gives it. Internal: they become public only when a consumer needs one (B-45).

/** `.og-panel`: `surface`, a 1 px `edge` border, `radius-md`, `shadow-raised`. */
@Composable
internal fun Modifier.panelSurface(): Modifier {
    val c = OldgeTheme.colors
    return cssBox(OldgeRadii.md, CssBackground.Solid(c.surface), 1.dp, c.edge, OldgeTheme.shadows.raised)
}

/** `.og-lcd`: `lcd`, a 1 px `edge` border, `radius-xs`, `shadow-sunken`. */
@Composable
internal fun Modifier.lcdGlass(): Modifier {
    val c = OldgeTheme.colors
    return cssBox(OldgeRadii.xs, CssBackground.Solid(c.lcd), 1.dp, c.edge, OldgeTheme.shadows.sunken)
}

/**
 * `.og-bezel`: the skin's frame — `bezel-hi` → `bezel` at 50 % → `bezel-lo`, `shadow-bezel`, and
 * the skin's material over it ([bezelTexture]).
 */
@Composable
internal fun Modifier.bezel(
    radius: Dp,
    corners: CssCorners = CssCorners.All,
): Modifier {
    val c = OldgeTheme.colors
    return cssBox(
        radius,
        CssBackground.Linear(listOf(0f to c.bezelHi, 0.5f to c.bezel, 1f to c.bezelLo)),
        shadows = OldgeTheme.shadows.bezel,
        corners = corners,
    ).bezelTexture(radius, corners)
}

/**
 * The frame's material: a 6 px diamond weave of `bezel-weave` (Toxic) and the 128 px speckle of
 * `bezel-speckle` (Crystal); a skin without one makes the token transparent and nothing is drawn.
 */
@Composable
internal fun Modifier.bezelTexture(
    radius: Dp,
    corners: CssCorners = CssCorners.All,
): Modifier {
    if (!OldgeTheme.texture) return this
    val c = OldgeTheme.colors
    val weave = remember(c.bezelWeave) { if (c.bezelWeave.alpha > 0f) weaveTile(c.bezelWeave) else null }
    val speckle =
        remember(c.bezelSpeckle) {
            if (c.bezelSpeckle.alpha >
                0f
            ) {
                noiseTile(SPECKLE, c.bezelSpeckle)
            } else {
                null
            }
        }
    if (weave == null && speckle == null) return this
    return drawWithCache {
        val clip =
            Path().apply {
                addRoundRect(
                    cssRoundRect(Offset.Zero, size, radius.toPx(), corners.radii(radius.toPx())),
                )
            }
        onDrawBehind {
            clipPath(clip) {
                weave?.let { drawRect(tiled(it)) }
                speckle?.let { drawRect(tiled(it)) }
            }
        }
    }
}

/**
 * The screen body (`body`, `.og-body`): `body-hi` to `ground` over the top 70 %, the `glow` as an
 * ellipse 120 % × 70 % of the box centred on its bottom-right corner fading out at 60 %, and the
 * grain over everything when texture is on.
 */
@Composable
internal fun Modifier.skinBody(): Modifier {
    val c = OldgeTheme.colors
    val grain = if (OldgeTheme.texture) remember(c.grain) { noiseTile(GRAIN, c.grain) } else null
    return drawBehind { drawSkinBody(c, grain) }
}

internal fun DrawScope.drawSkinBody(
    c: OldgeColors,
    grain: ImageBitmap?,
) {
    drawRect(c.ground)
    drawRect(Brush.verticalGradient(0f to c.bodyHi, BODY_GROUND_STOP to c.ground, startY = 0f, endY = size.height))
    // An elliptical radial gradient: a circle of radius rx, squashed vertically by ry / rx.
    val rx = size.width * GLOW_RX
    val ry = size.height * GLOW_RY
    withTransform({ scale(1f, ry / rx, pivot = Offset(size.width, size.height)) }) {
        drawRect(
            Brush.radialGradient(
                0f to c.glow,
                GLOW_FADE to c.glow.copy(alpha = 0f),
                center = Offset(size.width, size.height),
                radius = rx,
            ),
            topLeft = Offset(0f, size.height - size.height * rx / ry),
            size = Size(size.width, size.height * rx / ry),
        )
    }
    grain?.let { drawRect(tiled(it)) }
}

/** The grain alone, over what is drawn before it: `.og-drawer::before` and the like. */
@Composable
internal fun Modifier.grain(): Modifier {
    if (!OldgeTheme.texture) return this
    val c = OldgeTheme.colors
    val grain = remember(c.grain) { noiseTile(GRAIN, c.grain) }
    return drawBehind { drawRect(tiled(grain)) }
}

private const val BODY_GROUND_STOP = 0.7f
private const val GLOW_RX = 1.2f
private const val GLOW_RY = 0.7f
private const val GLOW_FADE = 0.6f

/**
 * A round orb (`.og-orb`): the chrome rim — `linear-gradient(160deg, chrome-hi, chrome-lo 60%,
 * chrome-hi)` with `shadow-orb` — around a glossy core in [core]'s colours, with a 1 px dark inner
 * ring and the `gloss` highlight on its upper 42 %.
 */
@Composable
internal fun OrbMaterial(
    core: CssBackground,
    size: Dp,
    rim: Dp,
    modifier: Modifier = Modifier,
    coreShadows: List<OldgeShadow> = listOf(CORE_RING),
    highlight: Float = 1f,
    coreModifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    val c = OldgeTheme.colors
    val pill = OldgeRadii.pill
    Box(
        modifier
            .size(size)
            .cssBox(
                pill,
                CssBackground.Linear(
                    listOf(0f to c.chromeHi, RIM_MID to c.chromeLo, 1f to c.chromeHi),
                    angleDegrees = RIM_ANGLE,
                ),
                shadows = OldgeTheme.shadows.orb,
            ).padding(rim)
            .cssBox(pill, core, shadows = coreShadows)
            .drawBehind { drawOrbHighlight(c.gloss.copy(alpha = c.gloss.alpha * highlight)) }
            .then(coreModifier),
        // `.og-orb__core { display: grid; place-items: center }`.
        contentAlignment = Alignment.Center,
    ) { content() }
}

private const val RIM_ANGLE = 160f
private const val RIM_MID = 0.6f

/** `box-shadow: inset 0 0 0 1px rgba(0,0,0,0.35)` on the core. */
private val CORE_RING =
    OldgeShadow(
        inset = true,
        offsetX = 0.dp,
        offsetY = 0.dp,
        blur = 0.dp,
        spread = 1.dp,
        color = Color(0f, 0f, 0f, 0.35f),
    )

/**
 * `.og-orb__core::before`: left and right 14 %, top 3 %, 42 % high, radius `999px 999px 50% 50%` —
 * which CSS's radius rule shrinks until the two top radii fit the width, leaving a round cap over
 * nearly square bottom corners.
 */
private fun DrawScope.drawOrbHighlight(gloss: Color) =
    drawGlossCap(gloss, ORB_HIGHLIGHT_SIDE, ORB_HIGHLIGHT_TOP, ORB_HIGHLIGHT_HEIGHT)

/**
 * A round element's `::before` highlight: [side] in from the left and right and [top] down, as
 * fractions of the box, [height] of it high, radius `999px 999px 50% 50%` — a round cap over nearly
 * square bottom corners once CSS shrinks the radii to fit. The orb's is 14 % / 3 % / 42 %, the
 * ActionTile badge's 12 % / 3 % / 44 %.
 */
internal fun DrawScope.drawGlossCap(
    gloss: Color,
    side: Float,
    top: Float,
    height: Float,
) {
    val w = size.width * (1 - 2 * side)
    val h = size.height * height
    val cap = CornerRadius(PILL_PX, PILL_PX)
    val bottom = CornerRadius(w / 2, h / 2)
    val rect =
        cssRoundRect(Offset(size.width * side, size.height * top), Size(w, h), 0f, CssRadii(cap, cap, bottom, bottom))
    drawPath(Path().apply { addRoundRect(rect) }, gloss)
}

private const val ORB_HIGHLIGHT_SIDE = 0.14f
private const val ORB_HIGHLIGHT_TOP = 0.03f
private const val ORB_HIGHLIGHT_HEIGHT = 0.42f
private const val PILL_PX = 999f

/**
 * `text-shadow: var(--shadow-lcd-glow)` on an LCD value: the skin's glow, none in Crystal, where
 * liquid crystal does not glow (the design system's README, typography).
 */
@Composable
internal fun TextStyle.lcdGlow(): TextStyle {
    val glow = OldgeTheme.shadows.lcdGlow.firstOrNull() ?: return this
    return copy(
        shadow =
            TextShadow(
                glow.color,
                Offset(glow.offsetX.value, glow.offsetY.value),
                glow.blur.value * CSS_BLUR_TO_TEXT_SHADOW,
            ),
    )
}

/**
 * CSS's text-shadow blur and Compose's text-shadow `blurRadius` are different lengths for the same
 * halo. Measured on the LCD glow probe (B-07), isolating the glow as the left half (with it) minus
 * the right half (without it) in each renderer: at 0.5 the halo's total energy matches Chrome's to
 * 0.2 % in Toxic (71,498 against 71,632) and Media (94,296 against 94,530); 1.0 overshoots by 9–13 %.
 */
internal const val CSS_BLUR_TO_TEXT_SHADOW = 0.5f

// Tiles.

private fun tiled(tile: ImageBitmap): Brush = ShaderBrush(ImageShader(tile, TileMode.Repeated, TileMode.Repeated))

/** The grain and the speckle: the Chrome-exact noise of [turbulenceAlpha] as [color]'s alpha. */
private class Noise(
    val size: Int,
    val baseFrequency: Double,
    val slope: Double,
    val intercept: Double,
)

// Read out of bundle.css (--og-grain-mask, --og-speckle-mask); TurbulenceTileTest holds these
// numbers to the SVGs rendered from that file.
private val GRAIN = Noise(size = 160, baseFrequency = 0.85, slope = 1.6, intercept = -0.4)
private val SPECKLE = Noise(size = 128, baseFrequency = 1.3, slope = 9.0, intercept = -6.2)
private const val NOISE_OCTAVES = 2

private fun noiseTile(
    noise: Noise,
    color: Color,
): ImageBitmap {
    val alpha = turbulenceAlpha(noise.size, noise.baseFrequency, NOISE_OCTAVES, noise.slope, noise.intercept)
    return pixelTile(noise.size) { x, y -> color.copy(alpha = color.alpha * alpha[y * noise.size + x] / 255f) }
}

/**
 * The weave: two layers of `linear-gradient(45deg, c 25%, transparent 25% 75%, c 75%)` in a 6 px
 * tile, the second shifted by 3 px, evaluated per pixel centre as a browser evaluates hard stops.
 */
private fun weaveTile(color: Color): ImageBitmap {
    val n = WEAVE_TILE

    fun layer(
        x: Int,
        y: Int,
    ): Boolean {
        val a = WEAVE_ANGLE * PI / 180
        val dx = sin(a)
        val dy = -cos(a)
        val length = abs(n * dx) + abs(n * dy)
        val t = ((x + 0.5 - n / 2.0) * dx + (y + 0.5 - n / 2.0) * dy) / length + 0.5
        return t < WEAVE_STOP || t >= 1 - WEAVE_STOP
    }
    return pixelTile(n) { x, y ->
        val first = layer(x, y)
        val second = layer((x - n / 2 + n) % n, (y - n / 2 + n) % n)
        val layers = (if (first) 1 else 0) + (if (second) 1 else 0)
        // Two translucent layers of one colour composite as 1 - (1 - a)².
        if (layers ==
            0
        ) {
            Color.Transparent
        } else {
            color.copy(
                alpha =
                    1 - (1 - color.alpha).let { r -> if (layers == 2) r * r else r },
            )
        }
    }
}

private const val WEAVE_TILE = 6
private const val WEAVE_ANGLE = 45.0
private const val WEAVE_STOP = 0.25

/** A small bitmap drawn pixel by pixel, grouped by colour so that it is one draw call per colour. */
private fun pixelTile(
    size: Int,
    pixel: (Int, Int) -> Color,
): ImageBitmap {
    val bitmap = ImageBitmap(size, size)
    val canvas = Canvas(bitmap)
    val groups = HashMap<Color, MutableList<Offset>>()
    for (y in 0 until size) {
        for (x in 0 until size) {
            val c = pixel(x, y)
            if (c.alpha > 0f) groups.getOrPut(c) { mutableListOf() } += Offset(x + 0.5f, y + 0.5f)
        }
    }
    val paint =
        Paint().apply {
            isAntiAlias = false
            strokeWidth = 1f
            strokeCap = StrokeCap.Square
        }
    for ((c, points) in groups) {
        paint.color = c
        canvas.drawPoints(PointMode.Points, points, paint)
    }
    return bitmap
}
