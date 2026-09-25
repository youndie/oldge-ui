package io.github.youndie.oldge.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Where a tooltip shows: [Above] its element (the default) or [Below] it. */
public enum class OldgeTooltipPlacement { Above, Below }

/**
 * A tooltip — the design system's `Tooltip`: the small yellow XP plate over the [content] it
 * explains, usually an OrbButton.
 *
 * Its README's rules: it shows on hover, on keyboard focus, and on a long press (450 ms); after a
 * touch it goes out 1.5 s later. It only clarifies icons, and nothing in it is pressable — the plate takes no
 * input. [initiallyOpen] shows it from the start, as the design system's `defaultOpen` does.
 */
@Composable
public fun OldgeTooltip(
    label: String,
    modifier: Modifier = Modifier,
    placement: OldgeTooltipPlacement = OldgeTooltipPlacement.Above,
    initiallyOpen: Boolean = false,
    content: @Composable () -> Unit,
) {
    var hovered by remember { mutableStateOf(false) }
    var focused by remember { mutableStateOf(false) }
    var focusByPress by remember { mutableStateOf(false) }
    var touched by remember { mutableStateOf(initiallyOpen) }
    val scope = rememberCoroutineScope()
    var timer by remember { mutableStateOf<Job?>(null) }
    // Focus counts only when no press brought it — `:focus-visible`, not `:focus`. A tap focuses the
    // element too, and the README gives a touch its own rule (450 ms, then 1.5 s); read literally,
    // the JSX's `onFocus` would show the plate at once on every tap.
    val open = hovered || (focused && !focusByPress) || touched
    Box(
        modifier
            .onFocusChanged {
                focused = it.hasFocus
                if (!it.hasFocus) focusByPress = false
            }.pointerInput(Unit) {
                // Watched on the way in and never consumed: the element under the plate still gets
                // its press.
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.firstOrNull() ?: continue
                        if (event.type == PointerEventType.Press) focusByPress = true
                        // Hover is a mouse's: a finger resting on the element is the long press below.
                        if (change.type == PointerType.Mouse) {
                            when (event.type) {
                                PointerEventType.Enter -> hovered = true
                                PointerEventType.Exit -> hovered = false
                            }
                            continue
                        }
                        if (change.type != PointerType.Touch || event.type == PointerEventType.Move) continue
                        timer?.cancel()
                        timer =
                            if (change.pressed) {
                                scope.launch {
                                    delay(LONG_PRESS_MS)
                                    touched = true
                                }
                            } else {
                                scope.launch {
                                    delay(TOUCH_LINGER_MS)
                                    touched = false
                                }
                            }
                    }
                }
            },
    ) {
        content()
        if (open) {
            val gap = with(LocalDensity.current) { GAP.roundToPx() }
            Popup(
                popupPositionProvider = remember(placement, gap) { Placement(placement, gap) },
            ) { Plate(label, placement) }
        }
    }
}

/** `.og-tip`: centred on the element, 6 px above (or below) it. */
private class Placement(
    private val placement: OldgeTooltipPlacement,
    private val gap: Int,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        // `left: 50%; transform: translateX(-50%)`: CSS rounds the half-width away the same way.
        val x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
        val y =
            when (placement) {
                OldgeTooltipPlacement.Above -> anchorBounds.top - gap - popupContentSize.height
                OldgeTooltipPlacement.Below -> anchorBounds.bottom + gap
            }
        return IntOffset(x, y)
    }
}

/** `.og-tip`: `balloon`, a 1 px `on-balloon` border, `radius-xs`, `shadow-balloon`, 0.75rem / 1rem, 3 × 8 padding. */
@Composable
private fun Plate(
    label: String,
    placement: OldgeTooltipPlacement,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val grow = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) { grow.animateTo(1f, tween(motion.base, easing = LinearEasing)) }
    val spring = motion.spring
    Box(
        Modifier
            .cssBox(OldgeRadii.xs, CssBackground.Solid(c.balloon), BORDER, c.onBalloon, OldgeTheme.shadows.balloon)
            .padding(horizontal = BORDER + PAD_X, vertical = BORDER + PAD_Y),
    ) {
        OldgeText(
            label,
            Modifier.graphicsLayer {
                // `.og-tip__in { animation: og-balloon-in; transform-origin: 50% 100% }`: the text inflates, the plate does not.
                val p = grow.value
                val s =
                    if (p < PEAK) {
                        FROM + (OVER - FROM) * spring.transform(p / PEAK)
                    } else {
                        OVER + (1 - OVER) * spring.transform((p - PEAK) / (1 - PEAK))
                    }
                scaleX = s
                scaleY = s
                alpha = if (p < PEAK) spring.transform(p / PEAK).coerceIn(0f, 1f) else 1f
                transformOrigin = TransformOrigin(0.5f, if (placement == OldgeTooltipPlacement.Above) 1f else 0f)
            },
            style = OldgeTheme.type.caption.copy(color = c.onBalloon),
            softWrap = false,
            maxLines = 1,
        )
    }
}

private const val LONG_PRESS_MS = 450L
private const val TOUCH_LINGER_MS = 1500L
private const val PEAK = 0.6f
private const val FROM = 0.4f
private const val OVER = 1.05f
private val BORDER = 1.dp
private val GAP = 6.dp // css literal: bundle.css `.og-tip { bottom: calc(100% + 6px) }`
private val PAD_X = 8.dp // css literal: bundle.css `.og-tip { padding: 3px 8px }`
private val PAD_Y = 3.dp // css literal: bundle.css `.og-tip { padding: 3px 8px }`
