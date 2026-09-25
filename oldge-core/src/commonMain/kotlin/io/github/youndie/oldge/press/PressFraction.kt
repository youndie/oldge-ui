package io.github.youndie.oldge.press

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import io.github.youndie.oldge.theme.OldgeTheme

/**
 * How far into its pressed look a control is, 0…1, already eased: the design system transitions the
 * gloss stops and the shadows over `dur-fast` into `:active` and `dur-base` out of it, on `ease-out`
 * (bundle.css `.og-btn`, `.og-orb__core`). One fraction drives the colours, the bevel and the
 * highlight together, so they cannot drift apart. Instant under reduced motion.
 */
@Composable
internal fun pressFraction(
    interactionSource: InteractionSource,
    enabled: Boolean = true,
): Float {
    val motion = OldgeTheme.motion
    val pressed by interactionSource.collectIsPressedAsState()
    val on = pressed && enabled
    val t by animateFloatAsState(
        targetValue = if (on) 1f else 0f,
        animationSpec = tween(if (on) motion.fast else motion.base, easing = LinearEasing),
        label = "oldge press fraction",
    )
    return motion.out.transform(t)
}
