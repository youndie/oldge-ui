package io.github.youndie.oldge.feedback

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.tokens.OldgeShadow
import kotlinx.coroutines.launch

/**
 * Pull to refresh — the design system's `PullRefresh`: a glossy disc comes down from the top as the
 * [content] is pulled past its start, turns after the finger, and spins while [refreshing]. Let go
 * past the threshold, it calls [onRefresh]. [height] is the scroll's own, where its container does
 * not set one; [label] is what a screen reader hears while it refreshes.
 *
 * Its README's rules:
 * - the threshold is 64 px, and past it the disc lights with the accent;
 * - the gesture works only at the very top of the scroll;
 * - offer the refresh as a button or a menu item too, since the gesture cannot be made from a
 *   keyboard.
 *
 * The content is scrolled by the component, so it goes in as it is, not in a scroll of its own.
 */
@Composable
public fun OldgePullRefresh(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = Dp.Unspecified,
    label: String = "Обновляю",
    content: @Composable () -> Unit,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val density = LocalDensity.current
    val threshold = with(density) { THRESHOLD.toPx() }
    val most = with(density) { MOST.toPx() }
    val pull = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val busy by rememberUpdatedState(refreshing)
    val refresh by rememberUpdatedState(onRefresh)
    // bundle.js: the finger's way down times 0.55, up to 120 px; the scroll's own way is left alone.
    val finger = remember { floatArrayOf(0f) }
    val connection =
        remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    // Pushing back up takes the pull in before the list scrolls.
                    if (source != NestedScrollSource.UserInput || available.y >= 0f ||
                        finger[0] <= 0f
                    ) {
                        return Offset.Zero
                    }
                    val used = maxOf(available.y, -finger[0])
                    finger[0] += used
                    scope.launch { pull.snapTo((finger[0] * RESIST).coerceIn(0f, most)) }
                    return Offset(0f, used)
                }

                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    // What the list could not scroll down at its top is the pull.
                    if (source != NestedScrollSource.UserInput || available.y <= 0f || busy) return Offset.Zero
                    finger[0] += available.y
                    scope.launch { pull.snapTo((finger[0] * RESIST).coerceIn(0f, most)) }
                    return Offset(0f, available.y)
                }

                override suspend fun onPreFling(available: Velocity): Velocity {
                    if (finger[0] <= 0f) return Velocity.Zero
                    if (pull.value >= threshold) refresh()
                    finger[0] = 0f
                    pull.snapTo(0f)
                    return available
                }
            }
        }
    val shownPx = if (refreshing) with(density) { BUSY_HEIGHT.toPx() } else pull.value
    // `.og-pull__ind { transition: height dur-slow spring, opacity dur-base ease-out }`.
    val shown by animateFloatAsState(shownPx, tween(motion.slow, easing = motion.spring), label = "oldge pull")
    val visible = shownPx > 0f
    val seen by animateFloatAsState(
        if (visible) 1f else 0f,
        tween(motion.base, easing = motion.out),
        label = "oldge pull fade",
    )
    val ready = pull.value >= threshold
    val spin = if (refreshing) loopPhase(SPIN_MS) ?: 0f else 0f
    Column(modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(with(density) { shown.coerceAtLeast(0f).toDp() })
                .graphicsLayer { alpha = seen }
                .clipToBounds()
                .then(
                    if (refreshing) {
                        Modifier.semantics {
                            contentDescription = label
                            liveRegion = LiveRegionMode.Polite
                        }
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            val turnDegrees = with(density) { pull.value.toDp().value } * TURN_PER_PX
            val scale = minOf(1f, SCALE_FROM + with(density) { shown.toDp().value } / SCALE_OVER)
            Box(
                Modifier
                    .graphicsLayer {
                        rotationZ = turnDegrees
                        scaleX = scale
                        scaleY = scale
                    }.size(DISC)
                    .cssBox(
                        OldgeRadii.pill,
                        CssBackground.Solid(Color.Transparent),
                        shadows = OldgeTheme.shadows.orb + DISC_RING + if (ready) readyRing(c.accent) else emptyList(),
                    ).drawBehind { drawConicRing(c, spin) },
            )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .then(if (height.isSpecified) Modifier.height(height) else Modifier)
                .nestedScroll(connection)
                .verticalScroll(rememberScrollState()),
        ) { content() }
    }
}

/** `.og-pull__ind--ready`: a 2 px `accent` ring and a 10 px `accent` glow round the disc. */
private fun readyRing(accent: Color) =
    listOf(
        OldgeShadow(false, 0.dp, 0.dp, 0.dp, READY_RING, accent),
        OldgeShadow(false, 0.dp, 0.dp, READY_GLOW, 0.dp, accent),
    )

/** `inset 0 0 0 1px rgba(0,0,0,0.35)`, under the sweep, which covers it. */
private val DISC_RING = listOf(OldgeShadow(true, 0.dp, 0.dp, 0.dp, 1.dp, Color(0f, 0f, 0f, 0.35f)))
private const val RESIST = 0.55f
private const val TURN_PER_PX = 4f
private const val SCALE_FROM = 0.4f
private const val SCALE_OVER = 90f
private const val SPIN_MS = 1200
private val THRESHOLD = 64.dp // css literal: bundle.js `PullRefresh`, `TH = 64`
private val MOST = 120.dp // css literal: bundle.js `PullRefresh`, `Math.min(120, dy * 0.55)`
private val BUSY_HEIGHT = 56.dp // css literal: bundle.js `PullRefresh`, `shown = p.refreshing ? 56 : pull`
private val DISC = 32.dp // css literal: bundle.css `.og-pull__disc { width: 32px; height: 32px }`
private val READY_RING = 2.dp // css literal: bundle.css `.og-pull__ind--ready .og-pull__disc { 0 0 0 2px accent }`
private val READY_GLOW = 10.dp // css literal: bundle.css `.og-pull__ind--ready .og-pull__disc { 0 0 10px accent }`
