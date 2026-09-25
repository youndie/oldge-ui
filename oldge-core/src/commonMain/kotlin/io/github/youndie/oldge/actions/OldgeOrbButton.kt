package io.github.youndie.oldge.actions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.material.OrbMaterial
import io.github.youndie.oldge.material.glossGradient
import io.github.youndie.oldge.material.lerpShadows
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.press.oldgePressScale
import io.github.youndie.oldge.press.pressFraction
import io.github.youndie.oldge.theme.LocalOldgeContentColor
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeShadow

/** The orb's sizes: [Small] 30 dp in a 44 dp touch zone, [Medium] 48 dp, [Large] 60 dp. */
public enum class OldgeOrbSize(
    internal val orb: Dp,
    internal val rim: Dp,
    internal val icon: Dp,
) {
    /** Window buttons in a WindowBar or a Dialog: help, minimise, close. */
    Small(30.dp, 2.dp, 16.dp), // css literal: bundle.css `.og-orb--sm { --orb: 30px; --rim: 2px }`

    /** Secondary round actions. */
    Medium(48.dp, 3.dp, 24.dp), // css literal: bundle.css `.og-orb { --orb: 48px; --rim: 3px }`

    /** The one floating main action. */
    Large(60.dp, 4.dp, 30.dp), // css literal: bundle.css `.og-orb--lg { --orb: 60px; --rim: 4px }`
}

/** The core's colour: [Bezel] the skin's (the default), [Accent] the main action, [Chrome] neutral. */
public enum class OldgeOrbTone { Bezel, Accent, Chrome }

/**
 * A round glossy button in a chrome rim — the design system's `OrbButton`, the window buttons and
 * round corner actions of the launcher shells.
 *
 * [label] is the accessible name and is not optional: the orb shows only an icon. No more than three
 * orbs in a row (the design system's README). Pressed, the rim squashes to 0.86 and springs back, and
 * the gloss flash spreads inside the core; the core's highlight dims and a shadow sinks into it.
 */
@Composable
public fun OldgeOrbButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: OldgeOrbSize = OldgeOrbSize.Medium,
    tone: OldgeOrbTone = OldgeOrbTone.Bezel,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val c = OldgeTheme.colors
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val t = pressFraction(source, enabled)
    val (core, content) =
        when (tone) {
            OldgeOrbTone.Bezel -> glossGradient(c.bezelHi, null, c.bezelLo) to c.onBezel
            OldgeOrbTone.Accent -> glossGradient(c.accentHi, c.accent, c.accentLo) to c.onAccent
            OldgeOrbTone.Chrome -> glossGradient(c.chromeHi, null, c.chromeLo) to c.onChrome
        }
    val hit = maxOf(size.orb, OldgeTheme.spacing.hitMin)
    Box(
        modifier
            .size(hit)
            .alpha(if (enabled) 1f else DISABLED_ALPHA)
            .semantics { contentDescription = label }
            // The ring round the whole orb, the flash inside its core only (`pressOn(…, '.og-orb__core')`).
            .clickable(
                source,
                OldgeIndication(CircleShape, flash = false),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalOldgeContentColor provides content) {
            OrbMaterial(
                core = core,
                size = size.orb,
                rim = size.rim,
                modifier = Modifier.oldgePressScale(source, if (enabled) PRESSED_SCALE else 1f),
                coreShadows = lerpShadows(listOf(CORE_RING), listOf(PRESSED_CORE), t),
                highlight = 1f - t * HIGHLIGHT_DIM,
                coreModifier = Modifier.indication(source, OldgeIndication(CircleShape, ring = false)),
            ) {
                OldgeIcon(icon, contentDescription = null, size = size.icon)
            }
        }
    }
}

private const val PRESSED_SCALE = 0.86f
private const val DISABLED_ALPHA = 0.45f

/** `.og-orb:active .og-orb__core::before { opacity: .4 }`. */
private const val HIGHLIGHT_DIM = 0.6f

/** At rest: `inset 0 0 0 1px rgba(0,0,0,0.35)`. */
private val CORE_RING =
    OldgeShadow(
        inset = true,
        offsetX = 0.dp,
        offsetY = 0.dp,
        blur = 0.dp,
        spread = 1.dp,
        color = Color(0f, 0f, 0f, 0.35f),
    )

/** Pressed: `inset 0 2px 4px rgba(0,0,0,0.5)`. */
private val PRESSED_CORE =
    OldgeShadow(
        inset = true,
        offsetX = 0.dp,
        offsetY = 2.dp, // css literal: bundle.css .og-orb:active .og-orb__core box-shadow
        blur = 4.dp, // css literal: bundle.css .og-orb:active .og-orb__core box-shadow
        spread = 0.dp,
        color = Color(0f, 0f, 0f, 0.5f),
    )
