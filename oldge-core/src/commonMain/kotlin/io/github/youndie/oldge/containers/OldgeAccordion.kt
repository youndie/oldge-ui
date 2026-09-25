package io.github.youndie.oldge.containers

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.CssCorners
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.lerpShadows
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.theme.LocalOldgeTextStyle
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText

/**
 * A folding section — the design system's `Accordion`, open to begin with ([defaultOpen]) and then
 * its own: the pill header with the round [icon] badge and a chrome chevron, and the inset panel of
 * [content] under it.
 *
 * Its README's rules: a `List(bare = true)` of dense rows is what usually goes inside; sections stack
 * `space-3` apart; the [title] is a noun («Приложения», «Инструменты»).
 */
@Composable
public fun OldgeAccordion(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = OldgeIcons.Star,
    defaultOpen: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    var open by rememberSaveable { mutableStateOf(defaultOpen) }
    OldgeAccordion(title, open, { open = it }, modifier, icon, interactionSource, content)
}

/**
 * The controlled `Accordion`: [open] is the caller's, and a tap on the header asks for the other
 * state through [onToggle].
 *
 * The body unfolds on the spring and the badge makes a full turn; the chevron turns with a bounce,
 * and under reduced motion it does not animate (research §1.2).
 */
@Composable
public fun OldgeAccordion(
    title: String,
    open: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = OldgeIcons.Star,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    // `.og-acc__head:active { box-shadow: shadow-sunken }`: in over `dur-fast`, out over `dur-base`.
    val sunk by animateFloatAsState(
        if (pressed) 1f else 0f,
        tween(if (pressed) motion.fast else motion.base, easing = motion.out),
        label = "oldge accordion press",
    )
    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier
                // The header is positioned, so it paints over the body tucked 6 px under it.
                .zIndex(1f)
                .fillMaxWidth()
                .defaultMinSize(minHeight = HEAD_HEIGHT)
                .cssBox(
                    OldgeRadii.lg,
                    CssBackground.Linear(listOf(0f to c.pillHi, 1f to c.pillLo)),
                    BORDER,
                    c.edge,
                    lerpShadows(OldgeTheme.shadows.raised, OldgeTheme.shadows.sunken, sunk),
                    gloss = c.gloss,
                ).clickable(source, OldgeIndication(RoundedCornerShape(OldgeRadii.lg)), role = Role.Button) {
                    onToggle(!open)
                }.semantics {
                    if (open) {
                        collapse {
                            onToggle(false)
                            true
                        }
                    } else {
                        expand {
                            onToggle(true)
                            true
                        }
                    }
                }.padding(horizontal = BORDER + HEAD_PADDING),
            horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Badge(icon, open, pressed)
            OldgeText(
                title,
                Modifier.weight(1f),
                style = OldgeTheme.type.bodyStrong.copy(color = c.onPill),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
            Chevron(open)
        }
        if (open) Body(content)
    }
}

/**
 * `.og-acc__badge`: a 34 px chrome ring (160°) with `shadow-orb`, the accent disc 2 px inside it and
 * the 16 px icon in `on-accent`. Open, it has turned a full 360° on the spring over `dur-slow`;
 * pressed, it squashes to 0.85 and turns −20° over `dur-instant`.
 */
@Composable
private fun Badge(
    icon: ImageVector,
    open: Boolean,
    pressed: Boolean,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val turn by animateFloatAsState(
        when {
            pressed -> PRESSED_TURN
            open -> FULL_TURN
            else -> 0f
        },
        tween(if (pressed) motion.instant else motion.slow, easing = motion.spring),
        label = "oldge accordion badge",
    )
    val squash by animateFloatAsState(
        if (pressed) PRESSED_SCALE else 1f,
        tween(if (pressed) motion.instant else motion.slow, easing = motion.spring),
        label = "oldge accordion badge squash",
    )
    Box(
        Modifier
            .graphicsLayer {
                rotationZ = turn
                scaleX = squash
                scaleY = squash
            }.size(BADGE)
            .cssBox(
                OldgeRadii.pill,
                CssBackground.Linear(listOf(0f to c.chromeHi, 1f to c.chromeLo), BADGE_ANGLE),
                shadows = OldgeTheme.shadows.orb,
            ).padding(BADGE_RIM)
            .cssBox(OldgeRadii.pill, CssBackground.Linear(listOf(0f to c.accentHi, 1f to c.accentLo))),
        contentAlignment = Alignment.Center,
    ) { OldgeIcon(icon, contentDescription = null, size = ICON, tint = c.onAccent) }
}

/** `.og-acc__chev`: a 26 px chrome button in a 1 px `edge` frame, 4 px from the end; open, turned 180° on the bounce. */
@Composable
private fun Chevron(open: Boolean) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val turn by animateFloatAsState(
        if (open) HALF_TURN else 0f,
        tween(motion.base, easing = motion.bounce),
        label = "oldge accordion chevron",
    )
    Box(
        Modifier
            .padding(end = CHEVRON_END)
            .graphicsLayer { rotationZ = turn }
            .size(CHEVRON)
            .cssBox(OldgeRadii.pill, CssBackground.Linear(listOf(0f to c.chromeHi, 1f to c.chromeLo)), BORDER, c.edge),
        contentAlignment = Alignment.Center,
    ) { OldgeIcon(OldgeIcons.Chevrons, contentDescription = null, size = ICON, tint = c.onChrome) }
}

/**
 * `.og-acc__body`: `margin: -6px 10px 0` — tucked 6 px under the header and 10 px in from its sides
 * — `surface` in a 1 px `edge` frame, `radius-md` below, `shadow-sunken`, padded 12 4 4. The CSS
 * frame is open at the top (`border-top: 0`). It is drawn closed here, since its top 6 px, border
 * and inset shadow included, lie under the header at rest and while unfolding (a mutation that
 * closed it changed no pixel, B-22). It unfolds (`og-unfold`) from 0.55 high and 8 px up, from the top, over `dur-slow` on the
 * spring.
 */
@Composable
private fun Body(content: @Composable () -> Unit) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val unfold = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) { unfold.animateTo(1f, tween(motion.slow, easing = motion.spring)) }
    Box(
        Modifier
            .fillMaxWidth()
            .layout { measurable, constraints ->
                val tuck = BODY_TUCK.roundToPx()
                val p = measurable.measure(constraints)
                layout(p.width, (p.height - tuck).coerceAtLeast(0)) { p.place(0, -tuck) }
            }.padding(horizontal = BODY_INSET)
            .graphicsLayer {
                val k = unfold.value
                transformOrigin = TransformOrigin(0.5f, 0f)
                scaleY = UNFOLD_FROM + (1 - UNFOLD_FROM) * k
                translationY = -UNFOLD_UP.toPx() * (1 - k)
                alpha = k.coerceIn(0f, 1f)
            }.cssBox(
                OldgeRadii.md,
                CssBackground.Solid(c.surface),
                BORDER,
                c.edge,
                OldgeTheme.shadows.sunken,
                corners = CssCorners.Bottom,
            ).padding(BORDER)
            // The content keeps CSS's place, 12 px below the top of a frame with no top border.
            .padding(start = BODY_SIDE, end = BODY_SIDE, top = BODY_TOP - BORDER, bottom = BODY_SIDE),
    ) {
        CompositionLocalProvider(LocalOldgeTextStyle provides OldgeTheme.type.body.copy(color = c.ink)) { content() }
    }
}

private const val FULL_TURN = 360f
private const val HALF_TURN = 180f
private const val PRESSED_TURN = -20f
private const val PRESSED_SCALE = 0.85f
private const val BADGE_ANGLE = 160f
private const val UNFOLD_FROM = 0.55f
private val BORDER = 1.dp
private val HEAD_HEIGHT = 44.dp // css literal: bundle.css `.og-acc__head { min-height: 44px }`
private val HEAD_PADDING = 4.dp // css literal: bundle.css `.og-acc__head { padding: 0 4px 0 4px }`
private val BADGE = 34.dp // css literal: bundle.css `.og-acc__badge { width: 34px; height: 34px }`
private val BADGE_RIM = 2.dp // css literal: bundle.css `.og-acc__badge { padding: 2px }`
private val ICON = 16.dp // css literal: bundle.js `Accordion`, `Icon size: 16`
private val CHEVRON = 26.dp // css literal: bundle.css `.og-acc__chev { width: 26px; height: 26px }`
private val CHEVRON_END = 4.dp // css literal: bundle.css `.og-acc__chev { margin-right: 4px }`
private val BODY_TUCK = 6.dp // css literal: bundle.css `.og-acc__body { margin: -6px 10px 0 }`
private val BODY_INSET = 10.dp // css literal: bundle.css `.og-acc__body { margin: -6px 10px 0 }`
private val BODY_TOP = 12.dp // css literal: bundle.css `.og-acc__body { padding: 12px 4px 4px }`
private val BODY_SIDE = 4.dp // css literal: bundle.css `.og-acc__body { padding: 12px 4px 4px }`
private val UNFOLD_UP = 8.dp // css literal: bundle.css `@keyframes og-unfold { from { translateY(-8px) } }`
