package io.github.youndie.oldge.feedback

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.material.lcdGlass
import io.github.youndie.oldge.material.lcdGlow
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.math.roundToInt

/**
 * A segment meter — the design system's `Meter`: a level on LCD glass, [segments] cells lit up to
 * [value] (0…1), under a pixel [label] and the [valueText].
 *
 * Its README's rules: a cell's colour is fixed by where it sits, not by the value — up to 60 % of the
 * scale `meter-low`, up to 85 % `meter-mid`, past that `meter-peak`; and the value is always given in
 * words too, which is why [valueText] is required. A screen reader hears the label, the level and
 * the words. The cells light up one after another when the meter appears.
 */
@Composable
public fun OldgeMeter(
    value: Float,
    label: String,
    valueText: String,
    modifier: Modifier = Modifier,
    segments: Int = DEFAULT_SEGMENTS,
) {
    require(segments >= 1) { "a meter needs at least one segment, got $segments" }
    val c = OldgeTheme.colors
    val type = OldgeTheme.type
    val v = value.coerceIn(0f, 1f)
    val lit = (v * segments).roundToInt()
    val motion = OldgeTheme.motion
    val appear = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) { appear.animateTo(1f, tween(motion.fast + STAGGER_MS * segments, easing = motion.out)) }
    Column(
        modifier
            .fillMaxWidth()
            .lcdGlass()
            .clearAndSetSemantics {
                contentDescription = label
                stateDescription = valueText
                progressBarRangeInfo = ProgressBarRangeInfo(v, 0f..1f)
            }.padding(horizontal = BORDER + OldgeTheme.spacing.space3, vertical = BORDER + OldgeTheme.spacing.space2),
        verticalArrangement = Arrangement.spacedBy(GAP),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            LcdTag(label, Modifier.alignByBaseline())
            LcdText(
                valueText,
                type.readout
                    .copy(
                        fontSize = VALUE_SIZE,
                        lineHeight = VALUE_LINE,
                        letterSpacing = 0.sp,
                        color = c.lcdInk,
                    ).lcdGlow(),
                Modifier.alignByBaseline(),
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(BAR)
                .drawBehind {
                    // `flex: 1` cells 2 px apart: fractional, and each edge rounded to a pixel as Chrome
                    // snaps a painted box.
                    val gap = SEGMENT_GAP.toPx()
                    val w = (size.width - gap * (segments - 1)) / segments
                    val r = CornerRadius(SEGMENT_RADIUS.toPx())
                    val total = (motion.fast + STAGGER_MS * segments).toFloat()
                    for (i in 0 until segments) {
                        val left = (i * (w + gap)).roundToInt().toFloat()
                        val right = (i * (w + gap) + w).roundToInt().toFloat()
                        val position = (i + 1f) / segments
                        val colour =
                            when {
                                i >= lit -> c.lcdGhost
                                position > PEAK -> c.meterPeak
                                position > MID -> c.meterMid
                                else -> c.meterLow
                            }
                        // `og-seg-in`, `28ms` apart: from 0.2 high and clear, grown from the bottom.
                        val start = STAGGER_MS * i / total
                        val k = ((appear.value - start) * total / motion.fast.coerceAtLeast(1)).coerceIn(0f, 1f)
                        val h = size.height * (SEG_FROM + (1 - SEG_FROM) * k)
                        drawRoundRect(colour, Offset(left, size.height - h), Size(right - left, h), r, alpha = k)
                    }
                },
        ) {}
    }
}

private const val DEFAULT_SEGMENTS = 20
private const val MID = 0.6f
private const val PEAK = 0.85f
private const val STAGGER_MS = 28
private const val SEG_FROM = 0.2f
private val BORDER = 1.dp
private val GAP = 6.dp // css literal: bundle.css `.og-meter { gap: 6px }`
private val BAR = 12.dp // css literal: bundle.css `.og-meter__bar { height: 12px }`
private val SEGMENT_GAP = 2.dp // css literal: bundle.css `.og-meter__bar { gap: 2px }`
private val SEGMENT_RADIUS = 1.dp // css literal: bundle.css `.og-meter__seg { border-radius: 1px }`
private val VALUE_SIZE = 15.sp // css literal: bundle.css `.og-meter__val { font-size: 0.9375rem }`
private val VALUE_LINE = 18.sp // css literal: bundle.css `.og-meter__val { line-height: 1.125rem }`
