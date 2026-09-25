package io.github.youndie.oldge.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.CssCornerRadii
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.press.oldgeInflateIn
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText

/** Who a chat message is from: [Me] (right, the skin's pill) or [Them] (left, an inset panel). */
public enum class OldgeChatSide { Me, Them }

/** A sent message's state: [Sent] (one tick) or [Read] (two). */
public enum class OldgeMessageStatus { Sent, Read }

/**
 * A chat message — the design system's `ChatBubble`: [text] in a glossy pill of the skin's colour on
 * the right for [OldgeChatSide.Me], in an inset panel on the left with an [avatar] for
 * [OldgeChatSide.Them]; [author] names the sender in a group chat, and [time] and [status] (mine
 * only) sit at the bubble's bottom end.
 *
 * Its README's rules:
 * - a run of messages from one author is grouped: the time, the status, the avatar and the tail
 *   ([tail]) are on the last of the run only — the caller passes them there;
 * - long words and links wrap, and a bubble is at most 86 % of the lane;
 * - it appears inflating from the corner with the tail.
 */
@Composable
public fun OldgeChatBubble(
    text: String,
    from: OldgeChatSide,
    modifier: Modifier = Modifier,
    time: String? = null,
    status: OldgeMessageStatus? = null,
    avatar: (@Composable () -> Unit)? = null,
    author: String? = null,
    tail: Boolean = true,
) {
    val c = OldgeTheme.colors
    val me = from == OldgeChatSide.Me
    Lane(me, tail, avatar.takeIf { !me }, modifier) {
        if (author != null && !me) {
            OldgeText(author, style = OldgeTheme.type.caption.copy(fontWeight = FontWeight.Bold, color = c.accentInk))
        }
        // `overflow-wrap: anywhere`: a word longer than the bubble breaks inside it, as Compose's does.
        OldgeText(text, style = OldgeTheme.type.body.copy(color = if (me) c.onPill else c.ink))
        if (time != null || (me && status != null)) {
            Meta(time, status.takeIf { me }, me, Modifier.align(Alignment.End))
        }
    }
}

/**
 * «Печатает…» — the design system's `TypingIndicator`: their bubble with three hopping LCD dots,
 * announced as «[name] печатает».
 *
 * Its README's rule: put it last in the lane, and take it away as soon as a message arrives.
 */
@Composable
public fun OldgeTypingIndicator(
    modifier: Modifier = Modifier,
    name: String? = null,
    avatar: (@Composable () -> Unit)? = null,
) {
    val c = OldgeTheme.colors
    val phase = loopPhase(TYPING_MS)
    Lane(
        me = false,
        tail = true,
        avatar = avatar,
        modifier =
            modifier.clearAndSetSemantics {
                contentDescription = listOfNotNull(name, "печатает").joinToString(" ")
                liveRegion = LiveRegionMode.Polite
            },
    ) {
        // `.og-typing__dots`: 5 px apart, padded 6 2; each 8 px, 2 px round, in `accent-ink`.
        Row(
            Modifier.padding(horizontal = DOTS_SIDE, vertical = DOTS_TOP),
            horizontalArrangement = Arrangement.spacedBy(DOT_GAP),
        ) {
            for (i in 0 until DOTS) {
                val dot = phase?.let { typing(it - i * DOT_DELAY) }
                val accent = c.accentInk
                Box(
                    Modifier
                        .graphicsLayer {
                            translationY = (dot?.first ?: 0f) * density
                            alpha = dot?.second ?: 1f
                        }.size(DOT)
                        .drawBehind {
                            drawRoundRect(
                                accent,
                                cornerRadius =
                                    androidx.compose.ui.geometry
                                        .CornerRadius(DOT_RADIUS.toPx()),
                            )
                        },
                )
            }
        }
    }
}

/**
 * `@keyframes og-typing` at [phase] of its 1.1 s: at rest and 0.35 opaque at 0, 60 and 100 %, up
 * 6 px and opaque at 30 %, each interval on `ease-out`. Returns the rise in px and the opacity.
 */
@Composable
private fun typing(phase: Float): Pair<Float, Float> {
    val p = ((phase % 1f) + 1f) % 1f
    val out = OldgeTheme.motion.out
    return when {
        p < TYPING_PEAK -> {
            val k = out.transform(p / TYPING_PEAK)
            -TYPING_RISE * k to (TYPING_DIM + (1 - TYPING_DIM) * k)
        }

        p < TYPING_BACK -> {
            val k = out.transform((p - TYPING_PEAK) / (TYPING_BACK - TYPING_PEAK))
            -TYPING_RISE * (1 - k) to (1 - (1 - TYPING_DIM) * k)
        }

        else -> {
            0f to TYPING_DIM
        }
    }
}

/**
 * `.og-bubble`: the row of the avatar and the body, 8 px apart, at most 86 % of the lane, at its
 * start for theirs and at its end for mine; it inflates (`og-balloon-in`) from the tail's corner.
 */
@Composable
private fun Lane(
    me: Boolean,
    tail: Boolean,
    avatar: (@Composable () -> Unit)?,
    modifier: Modifier,
    body: @Composable ColumnScope.() -> Unit,
) {
    BoxWithConstraints(
        modifier.fillMaxWidth(),
        contentAlignment = if (me) Alignment.BottomEnd else Alignment.BottomStart,
    ) {
        Row(
            Modifier
                .widthIn(max = maxWidth * LANE_SHARE)
                .oldgeInflateIn(if (me) TransformOrigin(1f, 1f) else TransformOrigin(0f, 1f)),
            horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
            verticalAlignment = Alignment.Bottom,
        ) {
            if (avatar != null) avatar()
            Body(me, tail, body)
        }
    }
}

/**
 * `.og-bubble__body`: `radius-lg` but 4 px at the tail's corner (bottom left for theirs, bottom right
 * for mine; none without a tail), a 1 px `edge` frame, `shadow-raised`, padded 8 12, lines 2 px
 * apart. Theirs is `surface` in `ink`; mine the `pill-hi` to `pill-lo` pill in `on-pill`.
 */
@Composable
private fun Body(
    me: Boolean,
    tail: Boolean,
    content: @Composable ColumnScope.() -> Unit,
) {
    val c = OldgeTheme.colors
    val s = OldgeTheme.spacing
    val lg = OldgeRadii.lg
    val corners =
        when {
            !tail -> CssCornerRadii(lg, lg, lg, lg)
            me -> CssCornerRadii(lg, lg, TAIL, lg)
            else -> CssCornerRadii(lg, lg, lg, TAIL)
        }
    Column(
        Modifier
            .cssBox(
                lg,
                if (me) {
                    CssBackground.Linear(
                        listOf(0f to c.pillHi, 1f to c.pillLo),
                    )
                } else {
                    CssBackground.Solid(c.surface)
                },
                BORDER,
                c.edge,
                OldgeTheme.shadows.raised,
                cornerRadii = corners,
            ).padding(BORDER)
            .padding(horizontal = s.space3, vertical = s.space2),
        verticalArrangement = Arrangement.spacedBy(LINE_GAP),
        content = content,
    )
}

/**
 * `.og-bubble__meta`: the time in the lcd face at 0.75rem / 1rem and, for mine, the ticks — one
 * `check` sent, two overlapped by 9 px read, named «отправлено» or «прочитано»; mine at 0.8 opacity,
 * theirs in `ink-muted`.
 */
@Composable
private fun Meta(
    time: String?,
    status: OldgeMessageStatus?,
    me: Boolean,
    modifier: Modifier,
) {
    val c = OldgeTheme.colors
    val ink = if (me) c.onPill.copy(alpha = c.onPill.alpha * MINE_META) else c.inkMuted
    val type = OldgeTheme.type
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(META_GAP),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (time !=
            null
        ) {
            LcdText(
                time,
                type.readoutSm.copy(
                    fontSize = type.caption.fontSize,
                    lineHeight = type.caption.lineHeight,
                    color = ink,
                ),
            )
        }
        if (status != null) {
            val read = status == OldgeMessageStatus.Read
            // The second tick overlaps the first by 9 px, and the span is as wide as the two drawn.
            Box(
                Modifier
                    .width(if (read) TICK * 2 - TICK_OVERLAP else TICK)
                    .semantics { contentDescription = if (read) "прочитано" else "отправлено" },
            ) {
                OldgeIcon(OldgeIcons.Check, contentDescription = null, size = TICK, tint = ink)
                if (read) {
                    OldgeIcon(
                        OldgeIcons.Check,
                        contentDescription = null,
                        modifier =
                            Modifier.offset(
                                x =
                                    TICK - TICK_OVERLAP,
                            ),
                        size = TICK,
                        tint = ink,
                    )
                }
            }
        }
    }
}

private const val LANE_SHARE = 0.86f
private const val MINE_META = 0.8f
private const val DOTS = 3
private const val TYPING_MS = 1100 // css literal: bundle.css `.og-typing__dots i { animation: og-typing 1.1s }`
private const val DOT_DELAY = 0.16f / 1.1f
private const val TYPING_PEAK = 0.3f
private const val TYPING_BACK = 0.6f
private const val TYPING_DIM = 0.35f
private const val TYPING_RISE = 6f
private val BORDER = 1.dp
private val TAIL = 4.dp // css literal: bundle.css `.og-bubble--me .og-bubble__body { border-bottom-right-radius: 4px }`
private val LINE_GAP = 2.dp // css literal: bundle.css `.og-bubble__body { gap: 2px }`
private val META_GAP = 2.dp // css literal: bundle.css `.og-bubble__meta { gap: 2px }`
private val TICK = 14.dp // css literal: bundle.js `ChatBubble`, tick `Icon size: 14`
private val TICK_OVERLAP = 9.dp // css literal: bundle.css `.og-bubble__tick .og-icon + .og-icon { margin-left: -9px }`
private val DOT = 8.dp // css literal: bundle.css `.og-typing__dots i { width: 8px; height: 8px }`
private val DOT_RADIUS = 2.dp // css literal: bundle.css `.og-typing__dots i { border-radius: 2px }`
private val DOT_GAP = 5.dp // css literal: bundle.css `.og-typing__dots { gap: 5px }`
private val DOTS_TOP = 6.dp // css literal: bundle.css `.og-typing__dots { padding: 6px 2px }`
private val DOTS_SIDE = 2.dp // css literal: bundle.css `.og-typing__dots { padding: 6px 2px }`
