package io.github.youndie.oldge.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText

/** Where a balloon's tail points: out of the [Bottom] edge (the default), the [Top], or [None]. */
public enum class OldgeBalloonTail { Bottom, Top, None }

/**
 * The XP tray balloon — the design system's `Balloon`: a yellow, non-blocking notice with an [icon]
 * mark, a [title], its [text] and a tail. The same in every skin (its README; the tokens are the same
 * in all three, and `BalloonBehaviourTest` holds the three renderings identical).
 *
 * Its README's rules: the title is a fact («Копия создана»), the text what happens next; a balloon is
 * not for decisions — that is a Dialog. With [onClose] it has a close button («Закрыть»). It is a
 * polite live region, so a screen reader announces it when it appears. It inflates from its tail.
 */
@Composable
public fun OldgeBalloon(
    title: String,
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: ImageVector = OldgeIcons.Info,
    tail: OldgeBalloonTail = OldgeBalloonTail.Bottom,
    onClose: (() -> Unit)? = null,
) {
    val c = OldgeTheme.colors
    val type = OldgeTheme.type
    val motion = OldgeTheme.motion
    val inflate = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) { inflate.animateTo(1f, tween(motion.slow, easing = LinearEasing)) }
    val spring = motion.spring
    Box(
        modifier
            .fillMaxWidth()
            .padding(
                top =
                    if (tail ==
                        OldgeBalloonTail.Top
                    ) {
                        TAIL
                    } else {
                        0.dp
                    },
                bottom = if (tail == OldgeBalloonTail.Bottom) TAIL else 0.dp,
            ).graphicsLayer {
                // `og-balloon-in` from the tail: scale .4 turned −6° and clear, over to 1.05 at +1.5°
                // at 60 %, to rest; each keyframe interval eased on its own.
                val p = inflate.value
                val (s, turn, a) =
                    if (p < INFLATE_PEAK) {
                        val k = spring.transform(p / INFLATE_PEAK)
                        Triple(
                            INFLATE_FROM + (INFLATE_OVER - INFLATE_FROM) * k,
                            INFLATE_TURN + (INFLATE_OVER_TURN - INFLATE_TURN) * k,
                            k,
                        )
                    } else {
                        val k = spring.transform((p - INFLATE_PEAK) / (1 - INFLATE_PEAK))
                        Triple(INFLATE_OVER + (1 - INFLATE_OVER) * k, INFLATE_OVER_TURN * (1 - k), 1f)
                    }
                scaleX = s
                scaleY = s
                rotationZ = turn
                alpha = a.coerceIn(0f, 1f)
                val originX = 1f - ORIGIN_FROM_RIGHT.toPx() / size.width
                transformOrigin = TransformOrigin(originX, if (tail == OldgeBalloonTail.Top) 0f else 1f)
            }.semantics { liveRegion = LiveRegionMode.Polite },
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .cssBox(OldgeRadii.md, CssBackground.Solid(c.balloon), BORDER, c.onBalloon, OldgeTheme.shadows.balloon)
                .drawBehind { if (tail != OldgeBalloonTail.None) drawTail(tail, c.onBalloon, c.balloon) }
                .padding(start = BORDER + OldgeTheme.spacing.space3, end = BORDER + OldgeTheme.spacing.space2)
                .padding(vertical = BORDER + OldgeTheme.spacing.space3),
            horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
        ) {
            // `.og-balloon__mark { color: balloon-mark; padding-top: 1px }`.
            OldgeIcon(
                icon,
                contentDescription = null,
                Modifier.padding(top = MARK_TOP),
                size = MARK,
                tint = c.balloonMark,
            )
            Column(Modifier.weight(1f)) {
                OldgeText(title, style = type.bodyStrong.copy(color = c.onBalloon))
                if (text != null) {
                    OldgeText(
                        text,
                        Modifier.padding(top = BODY_GAP),
                        style =
                            type.label.copy(
                                fontWeight = type.body.fontWeight,
                                lineHeight = BODY_LINE,
                                color = c.onBalloon,
                            ),
                    )
                }
            }
            if (onClose != null) Close(onClose)
        }
    }
}

/**
 * `.og-balloon__close`: a 44 dp target pulled 12 dp up and 6 dp right into the padding
 * (`margin: -12px -6px 0 0`), so it takes 32 of the row's height and sits 2 dp from the edge.
 */
@Composable
private fun Close(onClose: () -> Unit) {
    val c = OldgeTheme.colors
    val source = remember { MutableInteractionSource() }
    Box(
        Modifier
            .layout { measurable, constraints ->
                val p = measurable.measure(constraints)
                val up = CLOSE_PULL_UP.roundToPx()
                val right = CLOSE_PULL_RIGHT.roundToPx()
                layout(p.width - right, p.height - up) { p.place(0, -up) }
            }.size(OldgeTheme.spacing.hitMin)
            .semantics { contentDescription = "Закрыть" }
            .clickable(
                source,
                OldgeIndication(RoundedCornerShape(OldgeRadii.sm), flash = false),
                role = Role.Button,
                onClick = onClose,
            ),
        contentAlignment = Alignment.Center,
    ) { OldgeIcon(OldgeIcons.Close, contentDescription = null, size = CLOSE_ICON, tint = c.onBalloon) }
}

/**
 * `::before` and `::after`: two right triangles made of a border, the outline 18 × 14 in
 * `on-balloon` and the fill 15 × 12 in `balloon` over it, 36 and 37 px from the padding box's right
 * edge, starting at its bottom (or top) edge — over the balloon's border, so the tail's opening has
 * no line across it.
 */
private fun DrawScope.drawTail(
    tail: OldgeBalloonTail,
    outline: Color,
    fill: Color,
) {
    val b = BORDER.toPx()

    fun triangle(
        fromRight: Float,
        w: Float,
        h: Float,
    ): Path {
        val right = size.width - b - fromRight
        val left = right - w
        return Path().apply {
            if (tail == OldgeBalloonTail.Bottom) {
                val top = size.height - b
                moveTo(left, top)
                lineTo(right, top)
                lineTo(right, top + h)
            } else {
                val bottom = b
                moveTo(left, bottom)
                lineTo(right, bottom)
                lineTo(right, bottom - h)
            }
            close()
        }
    }
    drawPath(triangle(OUTLINE_RIGHT.toPx(), OUTLINE_W.toPx(), OUTLINE_H.toPx()), outline)
    drawPath(triangle(FILL_RIGHT.toPx(), FILL_W.toPx(), FILL_H.toPx()), fill)
}

private val BORDER = 1.dp
private val TAIL = 14.dp // css literal: bundle.css `.og-balloon { margin-bottom: 14px }`
private val MARK = 20.dp // css literal: bundle.js `Balloon`, mark `Icon size: 20`
private val MARK_TOP = 1.dp // css literal: bundle.css `.og-balloon__mark { padding-top: 1px }`
private val BODY_GAP = 2.dp // css literal: bundle.css `.og-balloon__body { margin: 2px 0 0 }`
private val BODY_LINE = 18.sp // css literal: bundle.css `.og-balloon__body { line-height: 1.125rem }`
private val CLOSE_ICON = 16.dp // css literal: bundle.js `Balloon`, close `Icon size: 16`
private val CLOSE_PULL_UP = 12.dp // css literal: bundle.css `.og-balloon__close { margin: -12px -6px 0 0 }`
private val CLOSE_PULL_RIGHT = 6.dp // css literal: bundle.css `.og-balloon__close { margin: -12px -6px 0 0 }`
private val OUTLINE_RIGHT = 36.dp // css literal: bundle.css `.og-balloon::before { right: 36px }`
private val OUTLINE_W = 18.dp // css literal: bundle.css `.og-balloon::before { border-width: 14px 0 0 18px }`
private val OUTLINE_H = 14.dp // css literal: bundle.css `.og-balloon::before { border-width: 14px 0 0 18px }`
private val FILL_RIGHT = 37.dp // css literal: bundle.css `.og-balloon::after { right: 37px }`
private val FILL_W = 15.dp // css literal: bundle.css `.og-balloon::after { border-width: 12px 0 0 15px }`
private val FILL_H = 12.dp // css literal: bundle.css `.og-balloon::after { border-width: 12px 0 0 15px }`
private val ORIGIN_FROM_RIGHT = 44.dp // css literal: bundle.css `.og-balloon { transform-origin: calc(100% - 44px) }`
private const val INFLATE_PEAK = 0.6f
private const val INFLATE_FROM = 0.4f
private const val INFLATE_OVER = 1.05f
private const val INFLATE_TURN = -6f
private const val INFLATE_OVER_TURN = 1.5f
