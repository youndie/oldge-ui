package io.github.youndie.oldge.feedback

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.cssLinearGradient
import io.github.youndie.oldge.press.oldgePopSoftIn
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeColors
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText
import kotlin.math.roundToInt

/** A progress bar's outcome: still going ([None]), finished ([Done]), or failed ([Error]). */
public enum class OldgeProgressStatus { None, Done, Error }

/**
 * An XP progress bar — the design system's `ProgressBar`: accent blocks in a sunken well, the
 * [label] and the percentage above, a [detail] line below.
 *
 * [value] from 0 to 1; null is the indeterminate bar, blocks running through the well — and under
 * reduced motion, where they stop, a full bar at half opacity, as the design system's own
 * reduced-motion rule draws it (research §1.2). Its README's rule: [OldgeProgressStatus.Done] and
 * [OldgeProgressStatus.Error] add an icon and a word, so the outcome is never told by colour alone.
 */
@Composable
public fun OldgeProgressBar(
    value: Float?,
    modifier: Modifier = Modifier,
    label: String? = null,
    detail: String? = null,
    status: OldgeProgressStatus = OldgeProgressStatus.None,
) {
    val c = OldgeTheme.colors
    val type = OldgeTheme.type
    val motion = OldgeTheme.motion
    val pct = value?.let { (it.coerceIn(0f, 1f) * 100).roundToInt() }
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space1)) {
        if (label != null || pct != null) {
            // `.og-progress__head`: 0.8125rem / 1rem, the label bold, the percentage in the lcd face.
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2)) {
                OldgeText(label.orEmpty(), Modifier.weight(1f), style = type.label.copy(color = c.ink))
                if (pct != null) LcdText("$pct%", type.label.copy(fontWeight = FontWeight.Normal, color = c.ink))
            }
        }
        val width by animateFloatAsState(
            (pct ?: 0) / 100f,
            tween(motion.slow, easing = motion.out),
            label = "oldge progress",
        )
        val slide =
            if (pct == null && !motion.reduced) {
                val t = rememberInfiniteTransition(label = "oldge progress run")
                t
                    .animateFloat(
                        0f,
                        1f,
                        infiniteRepeatable(tween(RUN_MS, easing = LinearEasing), RepeatMode.Restart),
                        label = "run",
                    ).value
            } else {
                0f
            }
        Box(
            Modifier
                .fillMaxWidth()
                .height(WELL)
                .cssBox(OldgeRadii.sm, CssBackground.Solid(c.well), BORDER, c.edge, OldgeTheme.shadows.sunken)
                .clip(RoundedCornerShape(OldgeRadii.sm - BORDER))
                .clearAndSetSemantics {
                    contentDescription = label ?: "Выполнение"
                    progressBarRangeInfo =
                        if (pct ==
                            null
                        ) {
                            ProgressBarRangeInfo.Indeterminate
                        } else {
                            ProgressBarRangeInfo(pct / 100f, 0f..1f)
                        }
                }.drawBehind {
                    val inset = (BORDER + WELL_PADDING).toPx()
                    val inner = Size(size.width - 2 * inset, size.height - 2 * inset)
                    when {
                        pct != null -> {
                            drawFill(Offset(inset, inset), Size(inner.width * width, inner.height), 1f, c)
                        }

                        motion.reduced -> {
                            drawFill(Offset(inset, inset), inner, INDETERMINATE_STILL, c)
                        }

                        else -> {
                            // `og-slide`: a 30 % block from −100 % to 340 % of its own width.
                            val w = inner.width * RUN_WIDTH
                            val x = inset + w * (RUN_FROM + (RUN_TO - RUN_FROM) * slide)
                            clipRect(inset, inset, inset + inner.width, inset + inner.height) {
                                drawFill(Offset(x, inset), Size(w, inner.height), 1f, c)
                            }
                        }
                    }
                },
        )
        if (status != OldgeProgressStatus.None || detail != null) Detail(status, detail)
    }
}

/**
 * `.og-progress__fill`: `linear-gradient(accent-hi, accent 50%, accent-lo)` under
 * `repeating-linear-gradient(90deg, transparent 0 8px, well 8px 10px)` — 8 px blocks with 2 px of the
 * well between — its box snapped to whole pixels.
 */
private fun DrawScope.drawFill(
    at: Offset,
    size: Size,
    alpha: Float,
    c: OldgeColors,
) {
    val left = at.x.roundToInt().toFloat()
    val right = (at.x + size.width).roundToInt().toFloat()
    if (right <= left) return
    val box = Size(right - left, size.height)
    // `opacity: .5` is the element's: the gradient and its blocks fade as one layer, not one by one.
    val layered = alpha < 1f
    if (layered) {
        drawContext.canvas.saveLayer(
            Rect(left, at.y, right, at.y + size.height),
            Paint().apply {
                this.alpha =
                    alpha
            },
        )
    }
    drawRoundRect(
        cssLinearGradient(listOf(0f to c.accentHi, HALF to c.accent, 1f to c.accentLo), DOWN, box, Offset(left, at.y)),
        Offset(left, at.y),
        box,
        CornerRadius(FILL_RADIUS.toPx()),
    )
    val block = BLOCK.toPx()
    val gap = BLOCK_GAP.toPx()
    var x = left + block
    clipRect(left, at.y, right, at.y + size.height) {
        while (x < right) {
            drawRect(c.well, Offset(x, at.y), Size(gap, size.height))
            x += block + gap
        }
    }
    if (layered) drawContext.canvas.restore()
}

/** `.og-progress__detail`: 0.75rem / 1rem in `ink-muted`; done and error bold, coloured, with their icon, popping in. */
@Composable
private fun Detail(
    status: OldgeProgressStatus,
    detail: String?,
) {
    val c = OldgeTheme.colors
    val type = OldgeTheme.type
    val (tone, icon, word) =
        when (status) {
            OldgeProgressStatus.Done -> Triple(c.success, OldgeIcons.Ok, detail ?: "Готово")
            OldgeProgressStatus.Error -> Triple(c.danger, OldgeIcons.Error, detail ?: "Ошибка")
            OldgeProgressStatus.None -> Triple(c.inkMuted, null, detail.orEmpty())
        }
    Row(
        if (icon != null) Modifier.oldgePopSoftIn() else Modifier,
        horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) OldgeIcon(icon, contentDescription = null, size = ICON, tint = tone)
        OldgeText(
            word,
            style =
                type.caption.copy(
                    color = tone,
                    fontWeight =
                        if (icon !=
                            null
                        ) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                ),
        )
    }
}

private const val HALF = 0.5f
private const val DOWN = 180f
private const val RUN_MS = 1400
private const val RUN_WIDTH = 0.3f
private const val RUN_FROM = -1f
private const val RUN_TO = 3.4f
private const val INDETERMINATE_STILL = 0.5f
private val BORDER = 1.dp
private val WELL = 20.dp // css literal: bundle.css `.og-progress__well { height: 20px }`
private val WELL_PADDING = 2.dp // css literal: bundle.css `.og-progress__well { padding: 2px }`
private val FILL_RADIUS = 1.dp // css literal: bundle.css `.og-progress__fill { border-radius: 1px }`
private val BLOCK = 8.dp // css literal: bundle.css `.og-progress__fill { repeating-linear-gradient(… 0 8px, …) }`
private val BLOCK_GAP = 2.dp // css literal: bundle.css `.og-progress__fill { … var(--well) 8px 10px) }`
private val ICON = 16.dp // css literal: bundle.js `ProgressBar`, status `Icon size: 16`
