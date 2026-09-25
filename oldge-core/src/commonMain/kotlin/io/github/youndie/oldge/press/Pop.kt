package io.github.youndie.oldge.press

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import io.github.youndie.oldge.theme.OldgeTheme

/**
 * `@keyframes og-pop` over `dur-base` on the spring, run once when the element enters the
 * composition: from nothing turned −25°, over to 1.25 at +6° at 70 %, to rest — each keyframe
 * interval eased on its own, as CSS does. The chip's check and the checkbox's check and radio dot
 * pop in this way. Under reduced motion the element is simply there.
 */
@Composable
internal fun Modifier.oldgePopIn(): Modifier {
    val motion = OldgeTheme.motion
    val pop = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) { pop.animateTo(1f, tween(motion.base, easing = LinearEasing)) }
    val ease = motion.spring
    return graphicsLayer {
        val p = pop.value
        val (scale, turn) =
            if (p < PEAK) {
                val k = ease.transform(p / PEAK)
                OVER * k to FROM_TURN + (OVER_TURN - FROM_TURN) * k
            } else {
                val k = ease.transform((p - PEAK) / (1 - PEAK))
                OVER + (1 - OVER) * k to OVER_TURN * (1 - k)
            }
        scaleX = scale
        scaleY = scale
        rotationZ = turn
    }
}

private const val PEAK = 0.7f
private const val OVER = 1.25f
private const val FROM_TURN = -25f
private const val OVER_TURN = 6f
