package io.github.youndie.oldge.press

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import io.github.youndie.oldge.theme.OldgeTheme

/**
 * The squash: [pressed] scale while held, `dur-instant` in and `dur-base` out, both on
 * `ease-spring` — bundle.css transitions `transform` on the spring and shortens the duration while
 * `:active`. The amount is the component's (0.86 an orb's rim, 0.94 a button, 0.95 a segment), which
 * is why this is a modifier and not part of [OldgeIndication]. Under reduced motion the scale still
 * follows the press, instantly, as the design's 1 ms transitions do.
 */
@Composable
internal fun Modifier.oldgePressScale(
    interactionSource: InteractionSource,
    pressed: Float,
): Modifier {
    val motion = OldgeTheme.motion
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressed else 1f,
        animationSpec = tween(if (isPressed) motion.instant else motion.base, easing = motion.spring),
        label = "oldge press scale",
    )
    return graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
