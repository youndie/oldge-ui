package io.github.youndie.oldge.forms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.GlossStops
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.lerpGloss
import io.github.youndie.oldge.material.premultipliedLerp
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.tokens.OldgeShadow
import io.github.youndie.oldge.type.FontCoverage
import io.github.youndie.oldge.type.OldgeScriptText
import io.github.youndie.oldge.type.OldgeText

/**
 * An on/off switch — the design system's `Switch`: a sunken capsule with a chrome knob and an ON/OFF
 * lamp, beside its [label] and an optional [hint]. The whole row is the 44 dp target.
 *
 * Its README's rules: on, the capsule fills with the accent and the lamp says so in a word; a switch
 * acts at once — where a change waits for «Сохранить», use a checkbox. In a list row, pass the row's
 * title as [label] with [showLabel] false and leave it to the row to show it: the label is then only
 * the switch's accessible name, as the design's `og-sr` span is.
 *
 * The knob stretches under the finger and flies over with a bounce; the lamp blinks as it comes on.
 * Disabled, the capsule is drawn at half opacity and taps do nothing.
 */
@Composable
public fun OldgeSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    hint: String? = null,
    enabled: Boolean = true,
    showLabel: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val c = OldgeTheme.colors
    val type = OldgeTheme.type
    val source = interactionSource ?: remember { MutableInteractionSource() }
    Row(
        modifier
            .toggleable(
                checked,
                source,
                indication = null,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            ).defaultMinSize(minHeight = OldgeTheme.spacing.hitMin)
            .then(if (showLabel) Modifier else Modifier.semantics { contentDescription = label }),
        horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showLabel) {
            Column(Modifier.weight(1f)) {
                OldgeText(label, style = type.body.copy(color = c.ink))
                if (hint != null) OldgeText(hint, style = type.caption.copy(color = c.inkMuted))
            }
        } else {
            // The empty `.og-switch__text` is still a flex item: the 12 px gap stays before the track.
            Spacer(Modifier)
        }
        Track(checked, enabled, source)
    }
}

@Composable
private fun Track(
    checked: Boolean,
    enabled: Boolean,
    source: MutableInteractionSource,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val pressed by source.collectIsPressedAsState()
    val active = pressed && enabled
    val t by animateFloatAsState(
        if (checked) 1f else 0f,
        tween(motion.base, easing = motion.out),
        label = "oldge switch fill",
    )
    // `.og-switch__knob { transition: left dur-base ease-bounce, width dur-fast ease-out }`; held, the
    // knob is 30 px wide, and when on it keeps its right edge by starting at 22 instead of 28.
    val left by animateDpAsState(
        when {
            !checked -> KNOB_OFF
            active -> KNOB_ON_HELD
            else -> KNOB_ON
        },
        tween(motion.base, easing = motion.bounce),
        label = "oldge switch knob",
    )
    val width by animateDpAsState(
        if (active) KNOB_HELD else KNOB,
        tween(motion.fast, easing = motion.out),
        label = "oldge switch knob width",
    )
    Box(
        Modifier
            .graphicsLayer { alpha = if (enabled) 1f else DISABLED_ALPHA }
            .size(TRACK_WIDTH, TRACK_HEIGHT)
            .cssBox(
                OldgeRadii.pill,
                lerpGloss(GlossStops(c.well, null, c.well), GlossStops(c.accentHi, null, c.accentLo), t).background(),
                BORDER,
                premultipliedLerp(c.edge, c.accentLo, t),
                OldgeTheme.shadows.sunken,
            ).indication(source, OldgeIndication(RoundedCornerShape(OldgeRadii.pill), flash = false)),
    ) {
        Lamp(checked, Modifier.align(if (checked) Alignment.TopStart else Alignment.TopEnd))
        Box(
            Modifier
                .offset(x = BORDER + left, y = BORDER + KNOB_TOP)
                .size(width, KNOB)
                .cssBox(
                    OldgeRadii.pill,
                    CssBackground.Radial(listOf(0f to c.chromeHi, KNOB_STOP to c.chromeLo), KNOB_X, KNOB_Y),
                    shadows = OldgeTheme.shadows.orb + KNOB_RING,
                ),
        )
    }
}

/**
 * `.og-switch__led`: «ON» or «OFF», 9 px in from the padding box's top and its right (left when on),
 * `400 8px/10px` in the pixel face with `letter-spacing: .04em`, `ink-muted` or `on-accent`. One of
 * the two texts whose size the design fixes in px (research §1.7), so it does not follow the system
 * font scale. Coming on, it blinks twice (`og-blink … steps(1) 2`).
 */
@Composable
private fun Lamp(
    on: Boolean,
    modifier: Modifier,
) {
    val c = OldgeTheme.colors
    val type = OldgeTheme.type
    val motion = OldgeTheme.motion
    val blink = remember { Animatable(1f) }
    LaunchedEffect(on, motion.reduced) {
        if (on && !motion.reduced) {
            blink.snapTo(0f)
            blink.animateTo(1f, tween(motion.base * 2, easing = LinearEasing))
        }
    }
    val density = LocalDensity.current
    val style =
        with(density) {
            type.pixelTag.copy(
                color = if (on) c.onAccent else c.inkMuted,
                fontSize = LAMP_SIZE.toSp(),
                lineHeight = LAMP_LINE.toSp(),
                fontWeight = FontWeight.Normal,
                letterSpacing = LAMP_TRACKING.em,
            )
        }
    Box(
        modifier
            .offset(x = if (on) BORDER + LAMP_INSET else -(BORDER + LAMP_INSET), y = BORDER + LAMP_INSET)
            .graphicsLayer {
                // steps(1): .2 for the first half of each of the two runs, then 1.
                val run = (blink.value * 2) % 1f
                alpha = if (blink.value < 1f && run < 0.5f) BLINK_LOW else 1f
            },
    ) {
        // The word is for the eye, so that colour is never alone; a screen reader hears the switch's
        // own on or off state, and the lamp's word would repeat it and stand in for a missing name (B-43).
        OldgeScriptText(
            if (on) "ON" else "OFF",
            style,
            type.families.pixelCompanion,
            FontCoverage.silkscreen,
            Modifier.clearAndSetSemantics {},
        )
    }
}

/** `inset 0 0 0 1px rgba(0,0,0,0.3)` on the knob. */
private val KNOB_RING =
    OldgeShadow(
        inset = true,
        offsetX = 0.dp,
        offsetY = 0.dp,
        blur = 0.dp,
        spread = 1.dp,
        color = Color(0f, 0f, 0f, 0.3f),
    )

private val BORDER = 1.dp
private val TRACK_WIDTH = 56.dp // css literal: bundle.css `.og-switch__track { width: 56px }`
private val TRACK_HEIGHT = 30.dp // css literal: bundle.css `.og-switch__track { height: 30px }`
private val KNOB = 24.dp // css literal: bundle.css `.og-switch__knob { width: 24px; height: 24px }`
private val KNOB_HELD = 30.dp // css literal: bundle.css `.og-switch:active .og-switch__knob { width: 30px }`
private val KNOB_TOP = 2.dp // css literal: bundle.css `.og-switch__knob { top: 2px; left: 2px }`
private val KNOB_OFF = 2.dp // css literal: bundle.css `.og-switch__knob { top: 2px; left: 2px }`
private val KNOB_ON = 28.dp // css literal: bundle.css `:checked … .og-switch__knob { left: 28px }`
private val KNOB_ON_HELD = 22.dp // css literal: bundle.css `:active :checked … .og-switch__knob { left: 22px }`
private val LAMP_INSET = 9.dp // css literal: bundle.css `.og-switch__led { top: 9px; right: 9px }`
private val LAMP_SIZE = 8.dp // css literal: bundle.css `.og-switch__led { font: 400 8px/10px }`
private val LAMP_LINE = 10.dp // css literal: bundle.css `.og-switch__led { font: 400 8px/10px }`
private const val LAMP_TRACKING = 0.04f
private const val KNOB_STOP = 0.75f
private const val KNOB_X = 0.35f
private const val KNOB_Y = 0.3f
private const val DISABLED_ALPHA = 0.5f
private const val BLINK_LOW = 0.2f
