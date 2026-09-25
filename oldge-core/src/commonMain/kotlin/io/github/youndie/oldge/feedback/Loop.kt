package io.github.youndie.oldge.feedback

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import io.github.youndie.oldge.theme.OldgeTheme

/**
 * A fixed moment for the design system's endless loops (the spinner's turn, the skeleton's scanner
 * and dial), 0…1 of each loop's period, in place of the clock. A golden cannot wait for a loop to
 * settle, so the fixtures that show one moving provide this (B-30); the library never does.
 */
internal val LocalOldgeLoopPhase: ProvidableCompositionLocal<Float?> = compositionLocalOf { null }

/**
 * Where a loop of [periodMs] is, 0…1: the frozen phase when one is provided, nothing under reduced
 * motion (the loop stands still, as the README asks), and the running loop otherwise. [reverse]
 * runs it back and forth, as CSS's `alternate` does.
 */
@Composable
internal fun loopPhase(
    periodMs: Int,
    reverse: Boolean = false,
): Float? {
    LocalOldgeLoopPhase.current?.let { return it }
    if (OldgeTheme.motion.reduced) return null
    val t = rememberInfiniteTransition(label = "oldge loop")
    return t
        .animateFloat(
            0f,
            1f,
            infiniteRepeatable(
                tween(periodMs, easing = LinearEasing),
                if (reverse) RepeatMode.Reverse else RepeatMode.Restart,
            ),
            label = "oldge loop phase",
        ).value
}
