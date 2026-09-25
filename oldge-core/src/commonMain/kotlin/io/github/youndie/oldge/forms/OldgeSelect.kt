package io.github.youndie.oldge.forms

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.CssRadii
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.cssRoundRect
import io.github.youndie.oldge.press.drawOldgeFocusRing
import io.github.youndie.oldge.theme.LocalOldgeContentColor
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.tokens.OldgeShadow
import io.github.youndie.oldge.type.OldgeText

/** One choice of an [OldgeSelect]: its [value], its [label], and an optional [icon]. */
public class OldgeSelectOption<T>(
    public val value: T,
    public val label: String,
    public val icon: ImageVector? = null,
)

/**
 * A drop-down choice — the design system's `Select`: a chrome frame showing the chosen option, with a
 * square accent arrow button on the right, under its [label].
 *
 * Its README's rules: under the frame is the platform's own `select`, so a phone opens the system's
 * list — Compose has no such control, and here a tap opens a list of the options under the frame
 * (a minimal one until Menu, B-28, exists); for two to four options in view, Segmented or
 * RadioGroup is the better control. The arrow dips while the frame is pressed or focused.
 */
@Composable
public fun <T> OldgeSelect(
    label: String,
    options: List<OldgeSelectOption<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
) {
    require(options.isNotEmpty()) { "Select needs at least one option" }
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val focused by source.collectIsFocusedAsState()
    var open by remember { mutableStateOf(false) }
    var frameHeight by remember { mutableStateOf(0) }
    var frameWidth by remember { mutableStateOf(0) }
    val current = options.firstOrNull { it.value == selected } ?: options.first()
    val dip by animateFloatAsState(
        if (pressed || focused || open) 1f else 0f,
        tween(motion.base, easing = motion.bounce),
        label = "oldge select arrow",
    )
    val shape = RoundedCornerShape(OldgeRadii.sm)
    Column(modifier, verticalArrangement = Arrangement.spacedBy(FIELD_GAP)) {
        FieldLabel(label)
        Box {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .defaultMinSize(minHeight = OldgeTheme.spacing.hitMin)
                    .onGloballyPositioned {
                        frameHeight = it.size.height
                        frameWidth = it.size.width
                    }.cssBox(
                        OldgeRadii.sm,
                        CssBackground.Linear(listOf(0f to c.chromeHi, 1f to c.chromeLo)),
                        BORDER,
                        c.edge,
                        OldgeTheme.shadows.raised,
                    ).drawBehind { drawHighlight(c.gloss) }
                    .drawWithContent {
                        drawContent()
                        if (focused) drawOldgeFocusRing(shape, c.focus)
                    }.semantics {
                        contentDescription = label
                        stateDescription = current.label
                    }.clickable(source, indication = null, role = Role.DropdownList) { open = true }
                    .padding(BORDER),
            ) {
                // `.og-select__value`: bold, one line, cut with an ellipsis.
                Row(
                    Modifier.weight(1f).fillMaxHeight().padding(horizontal = OldgeTheme.spacing.space3),
                    horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (current.icon !=
                        null
                    ) {
                        OldgeIcon(current.icon, contentDescription = null, size = ICON, tint = c.onChrome)
                    }
                    OldgeText(
                        current.label,
                        style = OldgeTheme.type.bodyStrong.copy(color = c.onChrome),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Arrow(dip)
            }
            if (open) {
                val density = LocalDensity.current
                Popup(
                    offset = IntOffset(0, frameHeight + with(density) { LIST_GAP.roundToPx() }),
                    onDismissRequest = { open = false },
                    properties = PopupProperties(focusable = true),
                ) {
                    OptionList(options, current.value, with(density) { frameWidth.toDp() }) {
                        open = false
                        onSelect(it)
                    }
                }
            }
        }
    }
}

/** `.og-select__arrow`: a 34 px accent square 4 px inside the frame, the chevron in `on-accent`. */
@Composable
private fun Arrow(dip: Float) {
    val c = OldgeTheme.colors
    CompositionLocalProvider(LocalOldgeContentColor provides c.onAccent) {
        Box(
            Modifier
                .padding(ARROW_MARGIN)
                .fillMaxHeight()
                .graphicsLayer {
                    // `transform: translateY(2px) scale(.92)` while pressed or focused.
                    val s = 1f - (1f - ARROW_PRESSED_SCALE) * dip
                    scaleX = s
                    scaleY = s
                    translationY = ARROW_DIP.toPx() * dip
                }
                // `span`: content-box, so the 34 px width is inside its 1 px border.
                .width(ARROW + BORDER * 2)
                .cssBox(
                    ARROW_RADIUS,
                    CssBackground.Linear(listOf(0f to c.accentHi, 1f to c.accentLo)),
                    BORDER,
                    c.accentLo,
                    listOf(OldgeShadow(true, 0.dp, 1.dp, 0.dp, 0.dp, c.gloss)),
                ),
            contentAlignment = Alignment.Center,
        ) { OldgeIcon(OldgeIcons.Down, contentDescription = null, size = CHEVRON, tint = c.onAccent) }
    }
}

/**
 * The list a tap opens: the options on a raised surface as wide as the frame, the chosen one in
 * `accent-ink`. A stand-in for the system list the design gets from `select`, kept to what a choice
 * needs until Menu (B-28) gives the design system's own popup.
 */
@Composable
private fun <T> OptionList(
    options: List<OldgeSelectOption<T>>,
    selected: T,
    width: Dp,
    onPick: (T) -> Unit,
) {
    val c = OldgeTheme.colors
    Column(
        Modifier
            .width(width)
            .cssBox(
                OldgeRadii.sm,
                CssBackground.Solid(c.surface),
                BORDER,
                c.edge,
                OldgeTheme.shadows.raised + OldgeTheme.shadows.window,
            ).padding(BORDER)
            .selectableGroup(),
    ) {
        for (option in options) {
            val on = option.value == selected
            val tone = if (on) c.accentInk else c.ink
            Row(
                Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = OldgeTheme.spacing.hitMin)
                    .selectable(on, role = Role.RadioButton) { onPick(option.value) }
                    .padding(horizontal = OldgeTheme.spacing.space3),
                horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (option.icon != null) OldgeIcon(option.icon, contentDescription = null, size = ICON, tint = tone)
                OldgeText(
                    option.label,
                    style = (if (on) OldgeTheme.type.bodyStrong else OldgeTheme.type.body).copy(color = tone),
                )
            }
        }
    }
}

/**
 * `.og-select::before`: 1 px in from the padding box on three sides, 36 % of the box high, radius
 * `3px 3px 0 0`, the `gloss` highlight under the content.
 */
private fun DrawScope.drawHighlight(gloss: Color) {
    val inset = (BORDER + HIGHLIGHT_INSET).toPx()
    val round = CornerRadius(ARROW_RADIUS.toPx())
    val band =
        cssRoundRect(
            Offset(inset, inset),
            Size(size.width - 2 * inset, (size.height - 2 * BORDER.toPx()) * HIGHLIGHT_HEIGHT),
            0f,
            CssRadii(round, round, CornerRadius.Zero, CornerRadius.Zero),
        )
    drawPath(Path().apply { addRoundRect(band) }, gloss)
}

private val BORDER = 1.dp
private val ICON = 18.dp // css literal: bundle.js `Select`, `Icon size: 18`
private val CHEVRON = 16.dp // css literal: bundle.js `Select`, arrow `Icon size: 16`
private val ARROW = 34.dp // css literal: bundle.css `.og-select__arrow { width: 34px }`
private val ARROW_MARGIN = 4.dp // css literal: bundle.css `.og-select__arrow { margin: 4px }`
private val ARROW_RADIUS = 3.dp // css literal: bundle.css `.og-select__arrow { border-radius: 3px }`
private val ARROW_DIP = 2.dp // css literal: bundle.css `.og-select:active .og-select__arrow` translateY(2px)
private val HIGHLIGHT_INSET = 1.dp // css literal: bundle.css `.og-select::before { left: 1px; right: 1px; top: 1px }`
private val LIST_GAP = 4.dp // css literal: bundle.css `.og-menu { top: calc(100% + 4px) }`, where Menu opens
private const val HIGHLIGHT_HEIGHT = 0.36f
private const val ARROW_PRESSED_SCALE = 0.92f
