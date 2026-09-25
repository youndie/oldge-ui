package io.github.youndie.oldge.actions

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.GlossStops
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.lerpGloss
import io.github.youndie.oldge.material.premultipliedLerp
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.press.oldgeHitArea
import io.github.youndie.oldge.press.oldgePopIn
import io.github.youndie.oldge.press.oldgePressScale
import io.github.youndie.oldge.theme.LocalOldgeContentColor
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText

/**
 * An action chip — the design system's `Chip` of kind `assist`: one or two words and an optional
 * [icon], for an action next to content.
 *
 * All three chips are 34 dp high as drawn and 46 dp high to the finger (the Chip README: "visible
 * height 34px, the touch zone widened to 46px"), 4 dp wider too, without the larger zone taking room
 * in the row. Pressed, a chip squashes to 0.92 and springs back, and a gloss flash crosses it.
 */
@Composable
public fun OldgeAssistChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    interactionSource: MutableInteractionSource? = null,
): Unit =
    PressableChip(text, icon, selected = null, modifier, interactionSource) { m, source ->
        m.clickable(source, indication = null, role = Role.Button, onClick = onClick)
    }

/**
 * A filter chip — kind `filter`: a toggle. Selected, it fills with the accent and a check pops in
 * with a turn in place of its [icon]. See [OldgeAssistChip] for the size and the press.
 */
@Composable
public fun OldgeFilterChip(
    text: String,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    interactionSource: MutableInteractionSource? = null,
): Unit =
    PressableChip(text, icon, selected, modifier, interactionSource) { m, source ->
        m.toggleable(selected, source, indication = null, role = Role.Checkbox, onValueChange = onSelectedChange)
    }

/**
 * An input chip — kind `input`: a value the user entered, with a round remove button. The chip
 * itself is not pressable; the remove button is, and being only an icon it needs [removeLabel],
 * its accessible name (the design system's is «Убрать <text>»). Its touch zone is 44 dp round a
 * 22 dp disc.
 */
@Composable
public fun OldgeInputChip(
    text: String,
    onRemove: () -> Unit,
    removeLabel: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val c = OldgeTheme.colors
    val source = remember { MutableInteractionSource() }
    CompositionLocalProvider(LocalOldgeContentColor provides c.onPill) {
        Row(
            modifier
                // `span.og-chip` is `box-sizing: content-box` — only buttons are border-box — so its
                // 34 px minimum is the content's and the border comes on top: 36 px, as measured.
                .defaultMinSize(minHeight = HEIGHT + BORDER * 2)
                .chipBox(t = 0f)
                // `.og-chip__x { margin-right: -6px }`: the disc eats into the end padding.
                .padding(start = BORDER + OldgeTheme.spacing.space3, end = BORDER + OldgeTheme.spacing.space3 - X_PULL),
            horizontalArrangement = Arrangement.spacedBy(GAP),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) OldgeIcon(icon, contentDescription = null, size = ICON)
            ChipLabel(text, c.onPill)
            Box(
                Modifier
                    .oldgeHitArea(X_REACH, X_REACH)
                    .semantics { contentDescription = removeLabel }
                    .clickable(source, indication = null, role = Role.Button, onClick = onRemove)
                    .padding(X_REACH)
                    .size(X_SIZE)
                    .background(X_FILL, CircleShape)
                    .indication(source, OldgeIndication(CircleShape, flash = false, ringOffset = X_RING_OFFSET)),
                contentAlignment = Alignment.Center,
            ) { OldgeIcon(OldgeIcons.Close, contentDescription = null, size = X_ICON) }
        }
    }
}

/**
 * A group of chips — the design system's `ChipGroup`: they wrap onto new lines 8 dp apart, or with
 * [scroll] stay on one line that scrolls sideways. [label] names the group for a screen reader.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun OldgeChipGroup(
    label: String,
    modifier: Modifier = Modifier,
    scroll: Boolean = false,
    content: @Composable () -> Unit,
) {
    val gap = OldgeTheme.spacing.space2
    val group =
        modifier.semantics {
            contentDescription = label
            isTraversalGroup = true
        }
    if (scroll) {
        // `.og-chips--scroll { flex-wrap: nowrap; overflow-x: auto; padding: 4px 0 }`.
        Row(
            group.horizontalScroll(rememberScrollState()).padding(vertical = SCROLL_PADDING),
            horizontalArrangement = Arrangement.spacedBy(gap),
        ) { content() }
    } else {
        FlowRow(
            group,
            horizontalArrangement = Arrangement.spacedBy(gap),
            verticalArrangement = Arrangement.spacedBy(gap),
        ) {
            content()
        }
    }
}

@Composable
private fun PressableChip(
    text: String,
    icon: ImageVector?,
    selected: Boolean?,
    modifier: Modifier,
    interactionSource: MutableInteractionSource?,
    interaction: (Modifier, MutableInteractionSource) -> Modifier,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val t by animateFloatAsState(
        if (selected == true) 1f else 0f,
        tween(motion.base, easing = motion.out),
        label = "oldge chip selection",
    )
    val content = premultipliedLerp(c.onPill, c.onAccent, t)
    CompositionLocalProvider(LocalOldgeContentColor provides content) {
        Row(
            interaction(modifier.oldgeHitArea(REACH_X, REACH_Y), source)
                .padding(horizontal = REACH_X, vertical = REACH_Y)
                .oldgePressScale(source, PRESSED_SCALE)
                .defaultMinSize(minHeight = HEIGHT)
                .chipBox(t)
                .indication(source, OldgeIndication(RoundedCornerShape(OldgeRadii.pill), flashOverContent = true))
                .padding(horizontal = BORDER + OldgeTheme.spacing.space3),
            horizontalArrangement = Arrangement.spacedBy(GAP),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (selected == true) {
                key(
                    "check",
                ) { OldgeIcon(OldgeIcons.Check, contentDescription = null, Modifier.oldgePopIn(), size = ICON) }
            } else if (icon != null) {
                key("icon") { OldgeIcon(icon, contentDescription = null, size = ICON) }
            }
            ChipLabel(text, content)
        }
    }
}

/** The chip's capsule at selection [t]: pill gloss to accent gloss, `edge` to `accent-lo`, `shadow-raised`. */
@Composable
private fun Modifier.chipBox(t: Float): Modifier {
    val c = OldgeTheme.colors
    return cssBox(
        OldgeRadii.pill,
        lerpGloss(GlossStops(c.pillHi, null, c.pillLo), GlossStops(c.accentHi, null, c.accentLo), t).background(),
        BORDER,
        premultipliedLerp(c.edge, c.accentLo, t),
        OldgeTheme.shadows.raised,
    )
}

@Composable
private fun ChipLabel(
    text: String,
    color: Color,
) = OldgeText(text, style = OldgeTheme.type.label.copy(color = color), softWrap = false)

private val BORDER = 1.dp
private val HEIGHT: Dp = 34.dp // css literal: bundle.css `.og-chip { min-height: 34px }`
private val GAP = 6.dp // css literal: bundle.css `.og-chip { gap: 6px }`
private val ICON = 16.dp // css literal: bundle.js `Chip`, `Icon size: 16`
private val REACH_X = 2.dp // css literal: bundle.css `.og-chip::after { inset: -6px -2px }`
private val REACH_Y = 6.dp // css literal: bundle.css `.og-chip::after { inset: -6px -2px }`
private val X_SIZE = 22.dp // css literal: bundle.css `.og-chip__x { width: 22px; height: 22px }`
private val X_PULL = 6.dp // css literal: bundle.css `.og-chip__x { margin-right: -6px }`
private val X_REACH = 11.dp // css literal: bundle.css `.og-chip__x::after { inset: -11px }`
private val X_ICON = 12.dp // css literal: bundle.js `Chip`, close `Icon size: 12`
private val X_RING_OFFSET = 1.dp // css literal: bundle.css `.og-chip__x:focus-visible { outline-offset: 1px }`
private val X_FILL = Color(0f, 0f, 0f, 0.12f) // bundle.css `.og-chip__x { background: rgba(0,0,0,0.12) }`
private val SCROLL_PADDING = 4.dp // css literal: bundle.css `.og-chips--scroll { padding: 4px 0 }`
private const val PRESSED_SCALE = 0.92f
