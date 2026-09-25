package io.github.youndie.oldge.containers

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.press.oldgeScrimTaps
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.type.OldgeText
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** A swipe action's look: [Chrome] (the default), [Accent], or [Danger], the sunken red one, last. */
public enum class OldgeSwipeTone { Chrome, Accent, Danger }

/** An action a [OldgeSwipeRow] reveals: a 72 dp button with its [icon] over its [label]. */
@Immutable
public data class OldgeSwipeAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
    val tone: OldgeSwipeTone = OldgeSwipeTone.Chrome,
)

/**
 * A row whose [actions] a left swipe reveals — the design system's `SwipeRow`, usually around an
 * [OldgeListItem] in an [OldgeList]. [defaultOpen] starts it open.
 *
 * Its README's rules:
 * - one to three actions, and a [OldgeSwipeTone.Danger] one goes last, on the right (both `require`);
 * - the row follows the finger, resists past its ends, and when let go settles open or shut on the
 *   spring;
 * - the gesture starts only on a horizontal move, so vertical scrolling is untouched;
 * - the actions stay in the focus order, and focusing one opens the row; they are also the row's
 *   accessibility actions, which is the «menu of the row» the README asks for.
 *
 * Tapping the row while it is open shuts it rather than pressing it; tapping an action shuts it and
 * runs the action.
 */
@Composable
public fun OldgeSwipeRow(
    actions: List<OldgeSwipeAction>,
    modifier: Modifier = Modifier,
    defaultOpen: Boolean = false,
    content: @Composable () -> Unit,
) {
    require(actions.size in 1..MAX_ACTIONS) { "a swipe row takes 1 to $MAX_ACTIONS actions, got ${actions.size}" }
    require(actions.dropLast(1).none { it.tone == OldgeSwipeTone.Danger }) { "a danger action goes last" }
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val width = ACTION_WIDTH * actions.size
    val w = with(LocalDensity.current) { width.toPx() }
    val x = remember { Animatable(if (defaultOpen) -w else 0f) }
    val scope = rememberCoroutineScope()
    // Where the finger has taken the row, before the rubber band past either end.
    val raw = remember { floatArrayOf(0f) }

    fun settle(open: Boolean) {
        scope.launch { x.animateTo(if (open) -w else 0f, tween(motion.slow, easing = motion.spring)) }
    }
    val drag =
        rememberDraggableState { delta ->
            raw[0] += delta
            val r = raw[0]
            // bundle.js: `if (nx > 0) nx = nx / 4; if (nx < -W) nx = -W + (nx + W) / 4`.
            val banded =
                when {
                    r > 0f -> r / RUBBER
                    r < -w -> -w + (r + w) / RUBBER
                    else -> r
                }
            scope.launch { x.snapTo(banded) }
        }
    // A row inside a SwipeRow is not a `li`'s own child, so the list's divider and stripe
    // (`.og-list > li + li > .og-item`) do not reach it; the narrow layout does.
    val row = LocalOldgeListRow.current
    Box(
        modifier
            .fillMaxWidth()
            .clipToBounds()
            .semantics {
                customActions =
                    actions.map { a ->
                        CustomAccessibilityAction(a.label) {
                            a.onClick()
                            true
                        }
                    }
            },
    ) {
        Box(Modifier.matchParentSize(), contentAlignment = Alignment.CenterEnd) {
            Row(Modifier.width(width).fillMaxHeight()) {
                for (a in actions) {
                    Action(a, Modifier.weight(1f).onFocusChanged { if (it.isFocused) settle(open = true) }) {
                        settle(open = false)
                        a.onClick()
                    }
                }
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .offset { IntOffset(x.value.roundToInt(), 0) }
                .background(c.surface)
                .draggable(
                    drag,
                    Orientation.Horizontal,
                    onDragStarted = { raw[0] = x.value },
                    onDragStopped = { settle(open = x.value < -w / 2) },
                ),
        ) {
            CompositionLocalProvider(LocalOldgeListRow provides row?.copy(index = 0, striped = false)) { content() }
            // Open, a tap on the row shuts it and does not reach the row's own action.
            if (x.value != 0f) {
                Box(
                    Modifier
                        .matchParentSize()
                        .oldgeScrimTaps { settle(open = false) },
                )
            }
        }
    }
}

/**
 * `.og-swipe__act`: a column of the 20 px icon over the label at 0.75rem / 1rem bold, 2 px apart,
 * with a 1 px `edge` rule on its left; [OldgeSwipeTone.Chrome] and [OldgeSwipeTone.Accent] are their
 * gradients, [OldgeSwipeTone.Danger] `danger` on a sunken `well`.
 */
@Composable
private fun Action(
    action: OldgeSwipeAction,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val c = OldgeTheme.colors
    val (ground, ink) =
        when (action.tone) {
            OldgeSwipeTone.Chrome -> CssBackground.Linear(listOf(0f to c.chromeHi, 1f to c.chromeLo)) to c.onChrome
            OldgeSwipeTone.Accent -> CssBackground.Linear(listOf(0f to c.accentHi, 1f to c.accentLo)) to c.onAccent
            OldgeSwipeTone.Danger -> CssBackground.Solid(c.well) to c.danger
        }
    val edge = c.edge
    Column(
        modifier
            .fillMaxHeight()
            .cssBox(
                0.dp,
                ground,
                shadows = if (action.tone == OldgeSwipeTone.Danger) OldgeTheme.shadows.sunken else emptyList(),
            ).drawBehind { drawRect(edge, Offset.Zero, Size(BORDER.toPx(), size.height)) }
            .clickable(
                remember { MutableInteractionSource() },
                OldgeIndication(),
                role = Role.Button,
                onClick = onClick,
            ),
        verticalArrangement = Arrangement.spacedBy(ACTION_GAP, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OldgeIcon(action.icon, contentDescription = null, size = ACTION_ICON, tint = ink)
        OldgeText(action.label, style = OldgeTheme.type.caption.copy(color = ink, fontWeight = FontWeight.Bold))
    }
}

private const val MAX_ACTIONS = 3
private const val RUBBER = 4f
private val BORDER = 1.dp
private val ACTION_WIDTH = 72.dp // css literal: bundle.js `SwipeRow`, `W = acts.length * 72`
private val ACTION_ICON = 20.dp // css literal: bundle.js `SwipeRow`, `Icon size: 20`
private val ACTION_GAP = 2.dp // css literal: bundle.css `.og-swipe__act { gap: 2px }`
