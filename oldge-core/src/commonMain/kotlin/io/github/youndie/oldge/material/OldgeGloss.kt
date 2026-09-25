package io.github.youndie.oldge.material

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The three stops of a glossy fill, for a glossy element of the consumer's own — the design
 * system's `README.md`, "Свои глянцевые элементы стройте так же": `--og-g1…3` taken from one token
 * family, painted as `linear-gradient(var(--og-g1), var(--og-g2) 55%, var(--og-g3))`, and
 * transitioned between states rather than swapped. [mid] is null for a two-stop family
 * (`chrome-hi` → `chrome-lo`), as it is for the library's own.
 *
 * The rest of `material/` stays internal (B-45): the CSS box painter, the bezels and the texture
 * are the library's way of matching Chrome, still moving item by item, and pinning them would make
 * every such fix a breaking change.
 */
@Immutable
public class OldgeGloss(
    public val hi: Color,
    public val mid: Color?,
    public val lo: Color,
) {
    internal val stops: GlossStops get() = GlossStops(hi, mid, lo)

    override fun equals(other: Any?): Boolean =
        other is OldgeGloss && other.hi == hi && other.mid == mid && other.lo == lo

    override fun hashCode(): Int = (hi.hashCode() * 31 + mid.hashCode()) * 31 + lo.hashCode()

    override fun toString(): String = "OldgeGloss(hi=$hi, mid=$mid, lo=$lo)"
}

/**
 * [target], transitioned the way the design system transitions `--og-g1…3`: over
 * [durationMillis] on `ease-out`, in premultiplied sRGB as a browser interpolates the colours — not
 * in Oklab, as `animateColorAsState` would, which would put every mid-state off the design's. The
 * README's timing is `OldgeTheme.motion.fast` into a press or a selection and `OldgeTheme.motion.base`
 * out of it; under reduced motion both are zero, and the target is returned in the same frame.
 */
@Composable
public fun animateOldgeGloss(
    target: OldgeGloss,
    durationMillis: Int,
): OldgeGloss {
    val now = animateGloss(target.stops, durationMillis)
    return OldgeGloss(now.hi, now.mid, now.lo)
}

/**
 * [gloss] as the element's background, in a box with [radius] corners: `hi` at the top, `mid` at
 * 55 %, `lo` at the bottom, painted as the library paints its own glossy fills.
 */
public fun Modifier.oldgeGloss(
    gloss: OldgeGloss,
    radius: Dp = 0.dp,
): Modifier = cssBox(radius, gloss.stops.background())
