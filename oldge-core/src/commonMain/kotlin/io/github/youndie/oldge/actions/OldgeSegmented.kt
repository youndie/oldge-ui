package io.github.youndie.oldge.actions

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.GlossStops
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.lerpGloss
import io.github.youndie.oldge.material.lerpShadows
import io.github.youndie.oldge.material.premultipliedLerp
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.press.oldgePressScale
import io.github.youndie.oldge.theme.LocalOldgeContentColor
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.tokens.OldgeShadow
import io.github.youndie.oldge.type.OldgeText

/** One option of an [OldgeSegmented]: its [value], a one-word [label], and an optional [icon]. */
public class OldgeSegment<T>(
    public val value: T,
    public val label: String,
    public val icon: ImageVector? = null,
)

/**
 * A capsule switch of two to four options — the design system's `Segmented`; the selected one lights
 * up in the accent. [label] names the group for a screen reader.
 *
 * The README's rules: labels are one short word, **at most 12 characters** — a longer one (German, a
 * large system font) wraps and looks cramped, and wants a RadioGroup or a Select instead; more than
 * four options want a Select or a RadioGroup, and that one is enforced: [options] must hold 2 to 4.
 *
 * The selection's gloss, border, shadow and label colour blend over `dur-base`; the newly selected
 * option pops in from 0.6 on the spring, and a pressed one squashes to 0.95. There is no press flash.
 */
@Composable
public fun <T> OldgeSegmented(
    options: List<OldgeSegment<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    require(options.size in MIN_OPTIONS..MAX_OPTIONS) {
        "Segmented takes 2 to 4 options, got ${options.size}; more want a Select or a RadioGroup"
    }
    val c = OldgeTheme.colors
    Row(
        modifier
            .cssBox(
                OldgeRadii.pill,
                CssBackground.Linear(listOf(0f to c.pillHi, 1f to c.pillLo)),
                BORDER,
                c.edge,
                OldgeTheme.shadows.raised,
            ).semantics { contentDescription = label }
            .selectableGroup()
            .padding(BORDER + INNER),
        horizontalArrangement = Arrangement.spacedBy(INNER),
    ) {
        for (option in options) {
            Segment(option, option.value == selected) { onSelect(option.value) }
        }
    }
}

@Composable
private fun <T> RowScope.Segment(
    option: OldgeSegment<T>,
    on: Boolean,
    onClick: () -> Unit,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val source = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(OldgeRadii.pill)
    val t by animateFloatAsState(
        if (on) 1f else 0f,
        tween(motion.base, easing = motion.out),
        label = "oldge segment selection",
    )
    // `@keyframes og-pop-soft` on the newly checked option: scale .6 → 1, opacity .3 → 1.
    val pop = remember { Animatable(1f) }
    LaunchedEffect(on, motion.reduced) {
        if (on && !motion.reduced) {
            pop.snapTo(0f)
            pop.animateTo(1f, tween(motion.base, easing = motion.spring))
        }
    }
    val clear = Color.Transparent
    val content = premultipliedLerp(c.onPill, c.onAccent, t)
    Row(
        Modifier
            .weight(1f)
            .graphicsLayer {
                val p = pop.value
                val scale = POP_FROM_SCALE + (1 - POP_FROM_SCALE) * p
                scaleX = scale
                scaleY = scale
                alpha = (POP_FROM_ALPHA + (1 - POP_FROM_ALPHA) * p).coerceIn(0f, 1f)
            }.oldgePressScale(source, PRESSED_SCALE)
            .defaultMinSize(minHeight = MIN_HEIGHT)
            .cssBox(
                OldgeRadii.pill,
                lerpGloss(
                    GlossStops(clear, clear, clear),
                    GlossStops(c.accentHi, c.accent, c.accentLo),
                    t,
                ).background(),
                BORDER,
                premultipliedLerp(clear, c.accentLo, t),
                // `box-shadow` from none: CSS pads `none` with transparent zero shadows of the same kind.
                lerpShadows(OldgeTheme.shadows.raised.map { it.none() }, OldgeTheme.shadows.raised, t),
            ).selectable(
                on,
                source,
                OldgeIndication(shape, flash = false),
                role = Role.RadioButton,
                onClick = onClick,
            ).padding(horizontal = BORDER + OldgeTheme.spacing.space3, vertical = BORDER + BLOCK_PADDING),
        horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space1, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalOldgeContentColor provides content) {
            if (option.icon != null) OldgeIcon(option.icon, contentDescription = null, size = ICON)
            OldgeText(
                option.label,
                style =
                    OldgeTheme.type.button.copy(
                        color = content,
                        fontSize = FONT_SIZE,
                        // `line-height: 1.1` of the font size, in sp so the CSS baseline can be computed from it.
                        lineHeight = (FONT_SIZE.value * LINE_HEIGHT).sp,
                        textAlign = TextAlign.Center,
                    ),
            )
        }
    }
}

private fun OldgeShadow.none(): OldgeShadow =
    copy(offsetX = 0.dp, offsetY = 0.dp, blur = 0.dp, spread = 0.dp, color = Color.Transparent)

private const val MIN_OPTIONS = 2
private const val MAX_OPTIONS = 4
private val BORDER = 1.dp
private val INNER = 2.dp // css literal: bundle.css `.og-seg { padding: 2px; gap: 2px }`
private val MIN_HEIGHT = 40.dp // css literal: bundle.css `.og-seg__opt { min-height: 40px }`
private val BLOCK_PADDING = 4.dp // css literal: bundle.css `.og-seg__opt { padding-top: 4px; padding-bottom: 4px }`
private val ICON = 18.dp // css literal: bundle.js `Segmented`, `Icon size: 18`
private val FONT_SIZE = 14.sp // css literal: bundle.css `.og-seg__opt { font-size: 0.875rem }`
private const val LINE_HEIGHT = 1.1f
private const val PRESSED_SCALE = 0.95f
private const val POP_FROM_SCALE = 0.6f
private const val POP_FROM_ALPHA = 0.3f
