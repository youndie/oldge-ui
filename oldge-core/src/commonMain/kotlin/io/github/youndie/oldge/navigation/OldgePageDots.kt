package io.github.youndie.oldge.navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.premultipliedLerp
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.tokens.OldgeShadow

/**
 * A page indicator for carousels and pagers — the design system's `PageDots`: LCD capsules, the
 * [index] one stretched and lit. A tap calls [onChange] with the page's index; [onDark] is the look
 * over a photograph.
 *
 * Its README's rules: every dot is a button with a 44 px target, named «Страница N из M»; past seven
 * pages show a counter («12 из 148») instead of dots, so [count] is at most seven (`require`).
 */
@Composable
public fun OldgePageDots(
    count: Int,
    index: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onDark: Boolean = false,
    label: String = "Страницы",
) {
    require(count in 1..MAX_DOTS) { "page dots show 1 to $MAX_DOTS pages, got $count; past that, a counter" }
    // `.og-dots` is a block: the row spans its parent and centres the dots.
    Row(
        modifier.fillMaxWidth().semantics { contentDescription = label },
        horizontalArrangement = Arrangement.spacedBy(DOT_GAP, Alignment.CenterHorizontally),
    ) {
        for (i in 0 until count) Dot(i, count, i == index, onDark) { onChange(i) }
    }
}

/**
 * `.og-dots__dot`: a 24 × 44 target (36 wide when current) round an 8 px capsule of `lcd-ghost` in a
 * 1 px `edge` frame; current, the capsule is 24 wide, `accent` in `accent-rim`, glowing `0 0 6px
 * accent`. Widths move on the spring over `dur-base`, the colour on `ease-out`.
 */
@Composable
private fun Dot(
    i: Int,
    count: Int,
    current: Boolean,
    onDark: Boolean,
    onClick: () -> Unit,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val target by animateDpAsState(
        if (current) TARGET_CURRENT else TARGET,
        tween(motion.base, easing = motion.spring),
        label = "oldge dot",
    )
    val pill by animateDpAsState(
        if (current) PILL_CURRENT else PILL,
        tween(motion.base, easing = motion.spring),
        label = "oldge dot pill",
    )
    val on by animateFloatAsState(
        if (current) 1f else 0f,
        tween(motion.base, easing = motion.out),
        label = "oldge dot colour",
    )
    val rest = if (onDark) DARK_GROUND else c.lcdGhost
    val restEdge = if (onDark) DARK_EDGE else c.edge
    Box(
        Modifier
            .width(target)
            .height(TARGET_HEIGHT)
            .selectable(
                current,
                remember { MutableInteractionSource() },
                OldgeIndication(RoundedCornerShape(FOCUS_RADIUS), ringOffset = -FOCUS_INSET),
                role = Role.Button,
                onClick = onClick,
            ).semantics { contentDescription = "Страница ${i + 1} из $count" },
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.size(pill, PILL)) {
            // The glow outside the pill only, as CSS paints an outer shadow, and apart from the fill:
            // on a photo the fill is translucent, and a glow drawn under it showed through as green.
            Box(
                Modifier
                    .matchParentSize()
                    .drawWithContent {
                        val shape =
                            Path().apply {
                                addRoundRect(RoundRect(Rect(Offset.Zero, size), CornerRadius(size.height / 2)))
                            }
                        clipPath(shape, ClipOp.Difference) { this@drawWithContent.drawContent() }
                    }.cssBox(
                        OldgeRadii.pill,
                        CssBackground.Solid(Color.Transparent),
                        shadows =
                            listOf(
                                OldgeShadow(
                                    false,
                                    0.dp,
                                    0.dp,
                                    GLOW,
                                    0.dp,
                                    c.accent.copy(
                                        alpha =
                                            c.accent.alpha * on,
                                    ),
                                ),
                            ),
                    ),
            )
            Box(
                Modifier
                    .matchParentSize()
                    .cssBox(
                        OldgeRadii.pill,
                        // On a photo the current dot keeps the dark dots' fill and edge, and only its width and
                        // glow say it is current: `.og-dots--dark .og-dots__dot > span` comes after the current
                        // dot's rule with the same specificity, and wins for those two (B-58).
                        CssBackground.Solid(if (onDark) rest else premultipliedLerp(rest, c.accent, on)),
                        BORDER,
                        if (onDark) restEdge else premultipliedLerp(restEdge, c.accentRim, on),
                    ),
            )
        }
    }
}

private const val MAX_DOTS = 7
private val DARK_GROUND = Color(1f, 1f, 1f, 0.35f)
private val DARK_EDGE = Color(0f, 0f, 0f, 0.5f)
private val BORDER = 1.dp
private val DOT_GAP = 2.dp // css literal: bundle.css `.og-dots { gap: 2px }`
private val TARGET = 24.dp // css literal: bundle.css `.og-dots__dot { width: 24px }`
private val TARGET_CURRENT = 36.dp // css literal: bundle.css `.og-dots__dot[aria-current="true"] { width: 36px }`
private val TARGET_HEIGHT = 44.dp // css literal: bundle.css `.og-dots__dot { height: 44px }`
private val PILL = 8.dp // css literal: bundle.css `.og-dots__dot > span { width: 8px; height: 8px }`
private val PILL_CURRENT = 24.dp // css literal: bundle.css `.og-dots__dot[aria-current="true"] > span { width: 24px }`
private val GLOW = 6.dp // css literal: bundle.css `… > span { box-shadow: 0 0 6px var(--accent) }`
private val FOCUS_RADIUS = 8.dp // css literal: bundle.css `.og-dots__dot:focus-visible { border-radius: 8px }`
private val FOCUS_INSET = 8.dp // css literal: bundle.css `.og-dots__dot:focus-visible { outline-offset: -8px }`
