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
import io.github.youndie.oldge.navigation.OldgeMenuAlign
import io.github.youndie.oldge.navigation.OldgeMenuEntry
import io.github.youndie.oldge.navigation.OldgeMenuPopup
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
 * list. Compose has no such control, and here a tap opens the options under the frame as an
 * [io.github.youndie.oldge.navigation.OldgeMenu], without the Menu's icon strip when no option has
 * an icon (B-63). For two to four options in view, Segmented or RadioGroup is the better control.
 * The arrow dips while the frame is pressed or focused.
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
    val current = options.firstOrNull { it.value == selected } ?: options.first()
    val dip by animateFloatAsState(
        if (pressed || focused || open) 1f else 0f,
        tween(motion.base, easing = motion.bounce),
        label = "oldge select arrow",
    )
    val shape = RoundedCornerShape(OldgeRadii.sm)
    Column(modifier, verticalArrangement = Arrangement.spacedBy(FIELD_GAP)) {
        FieldLabel(label)
        // The list a tap opens is Menu's (B-28), as a phone's is the system's. The strip under the
        // icons goes when no option has one: empty, it read as a stray stripe (B-63).
        OldgeMenuPopup(
            open,
            { open = it },
            options.mapIndexed { i, o -> OldgeMenuEntry.Item(i.toString(), o.label, o.icon) },
            { id -> onSelect(options[id.toInt()].value) },
            Modifier.fillMaxWidth(),
            OldgeMenuAlign.Start,
            label,
            strip = options.any { it.icon != null },
        ) { toggle ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .defaultMinSize(minHeight = OldgeTheme.spacing.hitMin)
                    .cssBox(
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
                    }.clickable(source, indication = null, role = Role.DropdownList, onClick = toggle)
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
private const val HIGHLIGHT_HEIGHT = 0.36f
private const val ARROW_PRESSED_SCALE = 0.92f
