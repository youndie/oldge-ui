package io.github.youndie.oldge.forms

import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.press.oldgePopIn
import io.github.youndie.oldge.press.oldgePressScale
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText

/**
 * A checkbox — the design system's `Checkbox`: a sunken square with a green check, and the whole row
 * a 44 dp target (its README). The [label] is a statement that becomes true when it is checked
 * («Запомнить это устройство»).
 *
 * Checked, the check pops in with a turn; pressed, the square squashes to 0.88. Disabled, the square
 * goes to `surface-2` with a `line` border and everything goes to `ink-muted`, and taps do nothing.
 */
@Composable
public fun OldgeCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val source = remember { MutableInteractionSource() }
    CheckRow(
        label,
        enabled,
        modifier.toggleable(
            checked,
            source,
            indication = null,
            enabled = enabled,
            role = Role.Checkbox,
            onValueChange = onCheckedChange,
        ),
        source,
        RoundedCornerShape(OldgeRadii.sm),
    ) {
        if (checked) {
            OldgeIcon(OldgeIcons.Check, contentDescription = null, Modifier.oldgePopIn(), size = CHECK, tint = it)
        }
    }
}

/** One choice of an [OldgeRadioGroup]: its [value], its [label], and whether it can be chosen. */
public class OldgeRadioOption<T>(
    public val value: T,
    public val label: String,
    public val enabled: Boolean = true,
)

/**
 * A group of "one of" switches — the design system's `RadioGroup`: sunken circles with an accent dot,
 * stacked, under the group's [label]. Two to six options, by its README; that one is enforced.
 * Each row is a 44 dp target, as a checkbox's is.
 */
@Composable
public fun <T> OldgeRadioGroup(
    label: String,
    options: List<OldgeRadioOption<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    require(options.size in MIN_OPTIONS..MAX_OPTIONS) {
        "RadioGroup takes 2 to 6 options, got ${options.size}"
    }
    val c = OldgeTheme.colors
    Column(modifier.semantics { isTraversalGroup = true }) {
        // `.og-radios__legend`: 0.8125rem / 1rem bold, 2 px above the first row.
        OldgeText(label, Modifier.padding(bottom = LEGEND_GAP), style = OldgeTheme.type.label.copy(color = c.ink))
        Column(Modifier.selectableGroup()) {
            for (option in options) {
                val source = remember { MutableInteractionSource() }
                val on = option.value == selected
                CheckRow(
                    option.label,
                    option.enabled,
                    Modifier.selectable(
                        on,
                        source,
                        indication = null,
                        enabled = option.enabled,
                        role = Role.RadioButton,
                    ) { onSelect(option.value) },
                    source,
                    CircleShape,
                ) {
                    if (on) RadioDot()
                }
            }
        }
    }
}

/** `label.og-check`: the box and the label in a row at least `hit-min` high, `space-2` apart. */
@Composable
private fun CheckRow(
    label: String,
    enabled: Boolean,
    interaction: Modifier,
    source: MutableInteractionSource,
    shape: Shape,
    mark: @Composable (tint: Color) -> Unit,
) {
    val c = OldgeTheme.colors
    val ink = if (enabled) c.ink else c.inkMuted
    Row(
        interaction.defaultMinSize(minHeight = OldgeTheme.spacing.hitMin),
        horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .oldgePressScale(source, if (enabled) PRESSED_SCALE else 1f)
                .size(BOX)
                .cssBox(
                    if (shape == CircleShape) OldgeRadii.pill else OldgeRadii.sm,
                    CssBackground.Solid(if (enabled) c.well else c.surface2),
                    BORDER,
                    if (enabled) c.edge else c.line,
                    // `:disabled` changes the fill, the border and the colour; the bevel stays.
                    OldgeTheme.shadows.sunken,
                ).indication(source, OldgeIndication(shape, flash = false)),
            contentAlignment = Alignment.Center,
        ) { mark(if (enabled) c.success else c.inkMuted) }
        OldgeText(label, style = OldgeTheme.type.body.copy(color = ink))
    }
}

/**
 * `.og-check__dot`: 10 px, `radial-gradient(circle at 35% 30%, accent-hi, accent-lo)` — a circle
 * reaching the farthest corner, CSS's default size.
 */
@Composable
private fun RadioDot() {
    val c = OldgeTheme.colors
    Box(
        Modifier
            .oldgePopIn()
            .size(DOT)
            .cssBox(OldgeRadii.pill, CssBackground.Radial(listOf(0f to c.accentHi, 1f to c.accentLo), DOT_X, DOT_Y)),
    )
}

private const val MIN_OPTIONS = 2
private const val MAX_OPTIONS = 6
private const val PRESSED_SCALE = 0.88f
private const val DOT_X = 0.35f
private const val DOT_Y = 0.3f
private val BORDER = 1.dp
private val BOX: Dp = 20.dp // css literal: bundle.css `.og-check__box { width: 20px; height: 20px }`
private val CHECK = 16.dp // css literal: bundle.js `Checkbox`, `Icon size: 16`
private val DOT = 10.dp // css literal: bundle.css `.og-check__dot { width: 10px; height: 10px }`
private val LEGEND_GAP = 2.dp // css literal: bundle.css `.og-radios__legend { margin-bottom: 2px }`
