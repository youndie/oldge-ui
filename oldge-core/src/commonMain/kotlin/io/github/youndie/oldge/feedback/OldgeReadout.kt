package io.github.youndie.oldge.feedback

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.material.lcdGlass
import io.github.youndie.oldge.material.lcdGlow
import io.github.youndie.oldge.theme.OldgeTheme

/** A readout's size: [Large] (the `readout` style) or [Small]. */
public enum class OldgeReadoutSize { Large, Small }

/**
 * A readout — the design system's `Readout`: a number on LCD glass, the digits glowing over unlit
 * «8»s, a pixel [label] above and a [unit] beside.
 *
 * Its README's rules: for timers, counters and large metrics, one to three on a screen; each skin has
 * its own glass — a green neon in Toxic, a cold blue in Media, a grey-green liquid crystal with no
 * glow in Crystal (the `shadow-lcd-glow` token is `none` there). A screen reader hears the label, the
 * value and the unit as one. When the value changes the digits flicker, as an LCD's do.
 */
@Composable
public fun OldgeReadout(
    value: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    unit: String? = null,
    size: OldgeReadoutSize = OldgeReadoutSize.Large,
) {
    val c = OldgeTheme.colors
    val type = OldgeTheme.type
    val digits =
        when (size) {
            OldgeReadoutSize.Large -> type.readout

            // `.og-readout--sm .og-readout__val { font-size: 1.375rem; line-height: 1.5rem }`, the
            // readout's own tracking kept.
            OldgeReadoutSize.Small -> type.readout.copy(fontSize = SMALL_SIZE, lineHeight = SMALL_LINE)
        }
    Column(
        modifier
            .lcdGlass()
            .clearAndSetSemantics { contentDescription = listOfNotNull(label, value, unit).joinToString(" ") }
            .padding(horizontal = BORDER + OldgeTheme.spacing.space3, vertical = BORDER + OldgeTheme.spacing.space2),
        verticalArrangement = Arrangement.spacedBy(GAP),
    ) {
        if (label != null) LcdTag(label)
        Row(horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2)) {
            Box(Modifier.alignByBaseline()) {
                // `.og-readout__ghost`: every digit as an unlit 8, the rest as it is, under the value.
                LcdText(value.map { if (it.isDigit()) '8' else it }.joinToString(""), digits.copy(color = c.lcdGhost))
                Flicker(value) { LcdText(value, digits.copy(color = c.lcdInk).lcdGlow()) }
            }
            if (unit != null) {
                LcdText(
                    unit,
                    type.readout.copy(
                        fontSize = UNIT_SIZE,
                        lineHeight = UNIT_LINE,
                        letterSpacing = 0.sp,
                        color = c.lcdDim,
                    ),
                    Modifier.alignByBaseline(),
                )
            }
        }
    }
}

/** `og-flicker … steps(4)` over `dur-base` when [key] changes: .15, 1, .35, 1. */
@Composable
internal fun Flicker(
    key: Any,
    content: @Composable () -> Unit,
) {
    val motion = OldgeTheme.motion
    val p = remember { Animatable(1f) }
    LaunchedEffect(key, motion.reduced) {
        if (!motion.reduced) {
            p.snapTo(0f)
            p.animateTo(1f, tween(motion.base, easing = LinearEasing))
        }
    }
    Box(
        Modifier.graphicsLayer {
            // The keyframes 0 % .15, 30 % 1, 45 % .35, 60 % 1, sampled in four steps as CSS's steps(4).
            val step = (p.value * STEPS).toInt().coerceAtMost(STEPS - 1) / STEPS.toFloat()
            alpha =
                when {
                    p.value >= 1f -> 1f
                    step < FLICKER_UP -> FLICKER_LOW
                    step < FLICKER_DIP -> 1f
                    step < FLICKER_BACK -> FLICKER_MID
                    else -> 1f
                }
        },
    ) { content() }
}

private const val STEPS = 4
private const val FLICKER_UP = 0.3f
private const val FLICKER_DIP = 0.45f
private const val FLICKER_BACK = 0.6f
private const val FLICKER_LOW = 0.15f
private const val FLICKER_MID = 0.35f
private val BORDER = 1.dp
private val GAP = 2.dp // css literal: bundle.css `.og-readout { gap: 2px }`
private val SMALL_SIZE = 22.sp // css literal: bundle.css `.og-readout--sm .og-readout__val { font-size: 1.375rem }`
private val SMALL_LINE = 24.sp // css literal: bundle.css `.og-readout--sm .og-readout__val { line-height: 1.5rem }`
private val UNIT_SIZE = 14.sp // css literal: bundle.css `.og-readout__unit { font-size: 0.875rem }`
private val UNIT_LINE = 16.sp // css literal: bundle.css `.og-readout__unit { line-height: 1rem }`
