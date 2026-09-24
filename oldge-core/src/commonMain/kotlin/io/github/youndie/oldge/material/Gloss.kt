package io.github.youndie.oldge.material

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import io.github.youndie.oldge.theme.OldgeTheme

/**
 * The three stops of a glossy fill: `hi`, `mid` at 55 % (a two-stop gloss has none), `lo`.
 * bundle.css keeps them in three registered colour properties, `--og-g1…3`, so that a press or a
 * selection interpolates the gradient instead of swapping it (research §1.5).
 */
@Immutable
internal data class GlossStops(
    val hi: Color,
    val mid: Color?,
    val lo: Color,
) {
    fun background(): CssBackground.Linear = glossGradient(hi, mid, lo)

    /** The mid stop a two-stop gloss has implicitly: its gradient's own value at 55 %. */
    val midOrImplied: Color get() = mid ?: premultipliedLerp(hi, lo, MID)

    private companion object {
        const val MID = 0.55f
    }
}

/**
 * [target], transitioned over [durationMillis] on `ease-out` the way CSS transitions the three
 * colour properties — in premultiplied sRGB, which is how a browser interpolates legacy `#hex` and
 * `rgba()` colours. Not `animateColorAsState`: Compose interpolates in Oklab, and every mid-state of
 * a press would differ from the design's.
 *
 * With [durationMillis] zero (reduced motion) the target is returned at once, in the same frame.
 */
@Composable
internal fun animateGloss(
    target: GlossStops,
    durationMillis: Int,
): GlossStops {
    var from by remember { mutableStateOf(target) }
    var to by remember { mutableStateOf(target) }
    val progress = remember { Animatable(1f) }
    val easing = OldgeTheme.motion.out
    if (target != to) {
        // Start from wherever the running transition is now, as CSS does when a transition is
        // interrupted by a new one.
        from = blend(from, to, easing.transform(progress.value))
        to = target
    }
    LaunchedEffect(target, durationMillis) {
        if (durationMillis == 0) {
            progress.snapTo(1f)
        } else {
            progress.snapTo(0f)
            // Linear progress: the curve is applied once, in blend(), as the design's ease-out.
            progress.animateTo(1f, tween(durationMillis, easing = LinearEasing))
        }
    }
    if (durationMillis == 0) return target
    return blend(from, to, easing.transform(progress.value))
}

private fun blend(
    a: GlossStops,
    b: GlossStops,
    t: Float,
): GlossStops =
    GlossStops(
        hi = premultipliedLerp(a.hi, b.hi, t),
        mid = if (a.mid == null && b.mid == null) null else premultipliedLerp(a.midOrImplied, b.midOrImplied, t),
        lo = premultipliedLerp(a.lo, b.lo, t),
    )

/** Colour interpolation as CSS does it for legacy sRGB colours: premultiplied, in sRGB. */
internal fun premultipliedLerp(
    a: Color,
    b: Color,
    t: Float,
): Color {
    val alpha = a.alpha + (b.alpha - a.alpha) * t
    if (alpha <= 0f) return Color.Transparent

    fun channel(
        x: Float,
        y: Float,
    ) = ((x * a.alpha) + ((y * b.alpha) - (x * a.alpha)) * t) / alpha
    return Color(channel(a.red, b.red), channel(a.green, b.green), channel(a.blue, b.blue), alpha)
}
