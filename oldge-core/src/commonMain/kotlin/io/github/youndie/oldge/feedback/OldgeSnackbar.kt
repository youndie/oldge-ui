package io.github.youndie.oldge.feedback

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.actions.OldgeButtonSize
import io.github.youndie.oldge.actions.OldgeOrbButton
import io.github.youndie.oldge.actions.OldgeOrbSize
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.bezel
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText
import kotlinx.coroutines.delay

/**
 * A snackbar — the design system's `Snackbar`, Material's: a short [message] on a plate in the
 * skin's frame by the bottom edge, with one action ([actionLabel], usually «Отменить») and a
 * «Закрыть» orb. With [durationMillis] it calls [onClose] by itself after that long.
 *
 * [inline] shows it in place. Otherwise it fills its parent and sits 16 dp above its bottom and
 * the system's inset, 16 dp in from the sides, and lets taps through to the screen. Not [open],
 * nothing is drawn.
 *
 * Its README's rules: it rises from below and sways like a hanging sign; one message at a time, and
 * what must not be missed is a Banner's or a Dialog's.
 */
@Composable
public fun OldgeSnackbar(
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
    onClose: (() -> Unit)? = null,
    durationMillis: Long? = null,
    inline: Boolean = false,
    open: Boolean = true,
) {
    if (!open) return
    val close by rememberUpdatedState(onClose)
    if (durationMillis != null) {
        LaunchedEffect(durationMillis) {
            delay(durationMillis)
            close?.invoke()
        }
    }
    if (inline) {
        Plate(message, actionLabel, onAction, onClose, modifier)
    } else {
        val s = OldgeTheme.spacing
        // No scrim and no pointer input of its own: the screen under it stays in reach.
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Plate(
                message,
                actionLabel,
                onAction,
                onClose,
                Modifier
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                    .padding(start = s.space4, end = s.space4, bottom = s.space4),
            )
        }
    }
}

/**
 * `.og-snack`: the bezel with `radius-md`, `shadow-bezel` and `shadow-window`, 52 px at least,
 * padded 4 4 4 16; the message at 0.875rem / 1.125rem. `og-snack-in` brings it up from 130 % of its
 * height, past the rest by 6 px turned −1.2° at 60 %, back turned 0.6° at 80 %, then level — over
 * `dur-slow` on the spring, each keyframe interval eased on its own.
 */
@Composable
private fun Plate(
    message: String,
    actionLabel: String?,
    onAction: () -> Unit,
    onClose: (() -> Unit)?,
    modifier: Modifier,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val rise = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) { rise.animateTo(1f, tween(motion.slow, easing = LinearEasing)) }
    val ease = motion.spring
    Row(
        modifier
            .fillMaxWidth()
            .graphicsLayer {
                val p = rise.value
                val i = SWAY_AT.indexOfLast { it <= p }.coerceAtMost(SWAY_AT.size - 2)
                val k = ease.transform(((p - SWAY_AT[i]) / (SWAY_AT[i + 1] - SWAY_AT[i])).coerceIn(0f, 1f))

                fun lerp(v: FloatArray) = v[i] + (v[i + 1] - v[i]) * k
                // The first key is a share of the plate's height, the others are px.
                translationY =
                    if (i == 0) {
                        size.height * FROM_BELOW * (1 - k) + SWAY_Y[1] * density * k
                    } else {
                        lerp(SWAY_Y) * density
                    }
                rotationZ = lerp(SWAY_TURN)
            }.semantics { liveRegion = LiveRegionMode.Polite }
            .cssBox(OldgeRadii.md, CssBackground.Solid(Color.Transparent), shadows = OldgeTheme.shadows.window)
            .bezel(OldgeRadii.md)
            .defaultMinSize(minHeight = HEIGHT)
            .padding(start = OldgeTheme.spacing.space4, top = EDGE, end = EDGE, bottom = EDGE),
        horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OldgeText(
            message,
            Modifier.weight(1f),
            style = OldgeTheme.type.body.copy(fontSize = MESSAGE_SIZE, lineHeight = MESSAGE_LINE, color = c.onBezel),
        )
        if (actionLabel != null) OldgeButton(actionLabel, onAction, size = OldgeButtonSize.Small)
        if (onClose != null) OldgeOrbButton(OldgeIcons.Close, "Закрыть", onClose, size = OldgeOrbSize.Small)
    }
}

private const val FROM_BELOW = 1.3f
private val SWAY_AT = floatArrayOf(0f, 0.6f, 0.8f, 1f)
private val SWAY_Y = floatArrayOf(0f, -6f, 0f, 0f)
private val SWAY_TURN = floatArrayOf(0f, -1.2f, 0.6f, 0f)
private val HEIGHT = 52.dp // css literal: bundle.css `.og-snack { min-height: 52px }`
private val EDGE = 4.dp // css literal: bundle.css `.og-snack { padding: 4px 4px 4px var(--space-4) }`
private val MESSAGE_SIZE = 14.sp // css literal: bundle.css `.og-snack__msg { font-size: 0.875rem }`
private val MESSAGE_LINE = 18.sp // css literal: bundle.css `.og-snack__msg { line-height: 1.125rem }`
