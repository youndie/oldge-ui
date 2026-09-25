package io.github.youndie.oldge.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.drawGlossCap
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.tokens.OldgeShadow
import io.github.youndie.oldge.type.OldgeText
import kotlin.math.sqrt

/** A spinner's disc: [Small] 24 dp, [Medium] 48 dp, [Large] 72 dp. */
public enum class OldgeSpinnerSize(
    internal val disc: Dp,
) {
    Small(24.dp), // css literal: bundle.css `.og-spinner--sm .og-spinner__disc { --s: 24px }`
    Medium(48.dp), // css literal: bundle.css `.og-spinner__disc { --s: 48px }`
    Large(72.dp), // css literal: bundle.css `.og-spinner--lg .og-spinner__disc { --s: 72px }`
}

/**
 * A spinner — the design system's `Spinner`: a glossy disc with a hole, the skin's colours turning
 * round it, and an optional [label] under it that also names it for a screen reader («Загрузка»
 * without one).
 *
 * Its README's rules: for a wait of unknown length — when the progress is known, a ProgressBar; under
 * reduced motion the disc stands still and the label stays.
 */
@Composable
public fun OldgeSpinner(
    modifier: Modifier = Modifier,
    size: OldgeSpinnerSize = OldgeSpinnerSize.Medium,
    label: String? = null,
) {
    val c = OldgeTheme.colors
    val turn = loopPhase(TURN_MS) ?: 0f
    Column(
        modifier.clearAndSetSemantics {
            contentDescription = label ?: "Загрузка"
            progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
        },
        verticalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(size.disc)) {
            // `shadow-orb` round the disc and not inside it: CSS paints an outer shadow only outside its
            // box, where Compose's drop shadow also fills the shape, and that showed through the hole.
            Box(
                Modifier
                    .matchParentSize()
                    .drawWithContent {
                        val disc = Path().apply { addOval(Rect(Offset.Zero, this@drawWithContent.size)) }
                        clipPath(disc, ClipOp.Difference) { this@drawWithContent.drawContent() }
                    }.cssBox(OldgeRadii.pill, CssBackground.Solid(Color.Transparent), shadows = OldgeTheme.shadows.orb),
            )
            Disc(turn, Modifier.matchParentSize())
        }
        if (label != null) OldgeText(label, style = OldgeTheme.type.caption.copy(color = c.inkMuted))
    }
}

/** The disc: the turning conic ring and the gloss cap, over the dark inner ring they cover. */
@Composable
private fun Disc(
    turn: Float,
    modifier: Modifier,
) {
    val c = OldgeTheme.colors
    Box(
        modifier
            .cssBox(OldgeRadii.pill, CssBackground.Solid(Color.Transparent), shadows = DISC_RING)
            .drawBehind {
                // `::before`: the conic sweep, masked to a ring by `radial-gradient(circle, transparent
                // 0 13%, #000 14%)` — the hole is 13.5 % of the farthest-corner radius, the box's
                // half-diagonal. Turned by the loop.
                val r = size.minDimension / 2
                val centre = Offset(r, r)
                val hole = r * sqrt(2f) * HOLE
                val ring =
                    Path().apply {
                        op(
                            Path().apply { addOval(Rect(centre, r)) },
                            Path().apply { addOval(Rect(centre, hole)) },
                            PathOperation.Difference,
                        )
                    }
                val stops =
                    arrayOf(
                        0f to c.chromeHi,
                        0.12f to c.accentHi,
                        0.25f to c.chromeLo,
                        0.38f to c.bezelHi,
                        0.5f to c.chromeHi,
                        0.62f to c.accent,
                        0.75f to c.chromeLo,
                        0.88f to c.pillLo,
                        1f to c.chromeHi,
                    )
                // A conic gradient starts at 12 o'clock; Compose's sweep at 3.
                rotate(turn * FULL_TURN + CONIC_START, centre) {
                    drawPath(ring, Brush.sweepGradient(*stops, center = centre))
                }
                // `::after`: the gloss cap, 14 % in, 4 % down, 40 % high.
                drawGlossCap(c.gloss, CAP_SIDE, CAP_TOP, CAP_HEIGHT)
            },
    )
}

/** `inset 0 0 0 1px rgba(0,0,0,0.35)`, under the sweep, which covers it (`::before` paints over it). */
private val DISC_RING = listOf(OldgeShadow(true, 0.dp, 0.dp, 0.dp, 1.dp, Color(0f, 0f, 0f, 0.35f)))
private const val TURN_MS = 1200
private const val FULL_TURN = 360f
private const val CONIC_START = -90f
private const val HOLE = 0.135f
private const val CAP_SIDE = 0.14f
private const val CAP_TOP = 0.04f
private const val CAP_HEIGHT = 0.4f
