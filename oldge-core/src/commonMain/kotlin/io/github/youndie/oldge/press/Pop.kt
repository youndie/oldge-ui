package io.github.youndie.oldge.press

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
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

/**
 * `@keyframes og-pop-soft` over `dur-base` on the spring, once, when the element enters the
 * composition: from 0.6 and 0.3 opacity to rest. Under reduced motion the element is simply there.
 */
@Composable
internal fun Modifier.oldgePopSoftIn(): Modifier {
    val motion = OldgeTheme.motion
    val pop = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) { pop.animateTo(1f, tween(motion.base, easing = motion.spring)) }
    return graphicsLayer {
        val p = pop.value
        val s = SOFT_FROM + (1 - SOFT_FROM) * p
        scaleX = s
        scaleY = s
        alpha = (SOFT_ALPHA + (1 - SOFT_ALPHA) * p).coerceIn(0f, 1f)
    }
}

private const val SOFT_FROM = 0.6f
private const val SOFT_ALPHA = 0.3f

/**
 * `@keyframes og-hop` over `dur-slow` on the spring, once, when the element enters the composition:
 * up 9 px and squashed to 1.12 × 0.92 at 35 %, down past rest by 1 px and stretched to 0.95 × 1.06
 * at 65 %, then at rest — each keyframe interval eased on its own, as CSS does. The BottomNav's
 * active icon hops this way. Under reduced motion the element is simply there.
 */
@Composable
internal fun Modifier.oldgeHopIn(): Modifier {
    val motion = OldgeTheme.motion
    val hop = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) { hop.animateTo(1f, tween(motion.slow, easing = LinearEasing)) }
    val ease = motion.spring
    return graphicsLayer {
        val p = hop.value
        val i = HOP_AT.indexOfLast { it <= p }.coerceAtMost(HOP_AT.size - 2)
        val k = ease.transform(((p - HOP_AT[i]) / (HOP_AT[i + 1] - HOP_AT[i])).coerceIn(0f, 1f))

        fun lerp(v: FloatArray) = v[i] + (v[i + 1] - v[i]) * k
        translationY = lerp(HOP_Y) * density
        scaleX = lerp(HOP_SX)
        scaleY = lerp(HOP_SY)
    }
}

private val HOP_AT = floatArrayOf(0f, 0.35f, 0.65f, 1f)
private val HOP_Y = floatArrayOf(0f, -9f, 1f, 0f)
private val HOP_SX = floatArrayOf(1f, 1.12f, 0.95f, 1f)
private val HOP_SY = floatArrayOf(1f, 0.92f, 1.06f, 1f)

/**
 * `@keyframes og-balloon-in` over `dur-slow` on the spring, once, when the element enters the
 * composition: from 0.4 turned −6° and clear, over to 1.05 at +1.5° at 60 %, to rest — each
 * keyframe interval eased on its own — about [origin]. A chat bubble inflates from its tail corner
 * this way. Under reduced motion the element is simply there.
 */
@Composable
internal fun Modifier.oldgeInflateIn(origin: TransformOrigin): Modifier {
    val motion = OldgeTheme.motion
    val inflate = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) { inflate.animateTo(1f, tween(motion.slow, easing = LinearEasing)) }
    val ease = motion.spring
    return graphicsLayer {
        val p = inflate.value
        val (s, turn, a) =
            if (p < INFLATE_PEAK) {
                val k = ease.transform(p / INFLATE_PEAK)
                Triple(
                    INFLATE_FROM + (INFLATE_OVER - INFLATE_FROM) * k,
                    INFLATE_TURN + (INFLATE_OVER_TURN - INFLATE_TURN) * k,
                    k,
                )
            } else {
                val k = ease.transform((p - INFLATE_PEAK) / (1 - INFLATE_PEAK))
                Triple(INFLATE_OVER + (1 - INFLATE_OVER) * k, INFLATE_OVER_TURN * (1 - k), 1f)
            }
        scaleX = s
        scaleY = s
        rotationZ = turn
        alpha = a.coerceIn(0f, 1f)
        transformOrigin = origin
    }
}

private const val INFLATE_PEAK = 0.6f
private const val INFLATE_FROM = 0.4f
private const val INFLATE_OVER = 1.05f
private const val INFLATE_TURN = -6f
private const val INFLATE_OVER_TURN = 1.5f
