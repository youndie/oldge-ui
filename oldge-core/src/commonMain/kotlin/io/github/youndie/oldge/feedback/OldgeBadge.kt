package io.github.youndie.oldge.feedback

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText

/**
 * A badge's tone: [Neutral] silver, [Accent] for what is new or a count, and [Success], [Warning] and
 * [Danger] — sunken plates with coloured text and an icon.
 */
public enum class OldgeBadgeTone { Neutral, Accent, Success, Warning, Danger }

/**
 * A badge — the design system's `Badge`: a short status or a count in a pill. [text] is one or two
 * words, or a number when [count]; a count is set in the lcd face and bumps when it changes.
 *
 * Its README's rule: the three states are told by an icon beside the coloured word, not by the colour
 * alone — so a count, which has no word, takes no icon.
 */
@Composable
public fun OldgeBadge(
    text: String,
    modifier: Modifier = Modifier,
    tone: OldgeBadgeTone = OldgeBadgeTone.Neutral,
    count: Boolean = false,
) {
    val c = OldgeTheme.colors
    val type = OldgeTheme.type
    val (background, border, ink) =
        when (tone) {
            OldgeBadgeTone.Neutral -> {
                Triple(
                    CssBackground.Linear(listOf(0f to c.chromeHi, 1f to c.chromeLo)),
                    c.edge,
                    c.onChrome,
                )
            }

            OldgeBadgeTone.Accent -> {
                Triple(
                    CssBackground.Linear(listOf(0f to c.accentHi, 1f to c.accentLo)),
                    c.accentLo,
                    c.onAccent,
                )
            }

            OldgeBadgeTone.Success -> {
                Triple(CssBackground.Solid(c.well), c.edge, c.success)
            }

            OldgeBadgeTone.Warning -> {
                Triple(CssBackground.Solid(c.well), c.edge, c.warning)
            }

            OldgeBadgeTone.Danger -> {
                Triple(CssBackground.Solid(c.well), c.edge, c.danger)
            }
        }
    val sunken = tone == OldgeBadgeTone.Success || tone == OldgeBadgeTone.Warning || tone == OldgeBadgeTone.Danger
    val icon =
        when {
            count -> null
            tone == OldgeBadgeTone.Success -> OldgeIcons.Ok
            tone == OldgeBadgeTone.Warning -> OldgeIcons.Warn
            tone == OldgeBadgeTone.Danger -> OldgeIcons.Error
            else -> null
        }
    Row(
        modifier
            .defaultMinSize(minWidth = if (count) HEIGHT else 0.dp, minHeight = HEIGHT)
            .cssBox(OldgeRadii.pill, background, BORDER, border, if (sunken) OldgeTheme.shadows.sunken else emptyList())
            .padding(horizontal = BORDER + if (count) COUNT_PADDING else OldgeTheme.spacing.space2),
        horizontalArrangement = Arrangement.spacedBy(GAP, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) OldgeIcon(icon, contentDescription = null, size = ICON, tint = ink)
        if (count) {
            // `.og-badge--count { font-family: lcd; font-weight: 400; font-size: 0.8125rem }`, the
            // 1rem line kept; `.og-badge__n` bumps when the number changes.
            Bump(text) {
                LcdText(
                    text,
                    type.caption.copy(fontSize = type.label.fontSize, fontWeight = FontWeight.Normal, color = ink),
                )
            }
        } else {
            OldgeText(text, style = type.caption.copy(fontWeight = FontWeight.Bold, color = ink), softWrap = false)
        }
    }
}

/** `og-bump` over `dur-base` on the spring when [key] changes: to 1.35 at 40 %, back to 1. */
@Composable
private fun Bump(
    key: Any,
    content: @Composable () -> Unit,
) {
    val motion = OldgeTheme.motion
    // The number it shows now; a bump when it changes, not when the badge first appears.
    val shown = remember { Holder(key) }
    val p = remember { Animatable(1f) }
    LaunchedEffect(key, motion.reduced) {
        if (key != shown.value) {
            shown.value = key
            if (!motion.reduced) {
                p.snapTo(0f)
                p.animateTo(1f, tween(motion.base, easing = LinearEasing))
            }
        }
    }
    val spring = motion.spring
    Box(
        Modifier.graphicsLayer {
            val t = p.value
            val s =
                if (t < BUMP_PEAK) {
                    1 + (BUMP_OVER - 1) * spring.transform(t / BUMP_PEAK)
                } else {
                    BUMP_OVER + (1 - BUMP_OVER) * spring.transform((t - BUMP_PEAK) / (1 - BUMP_PEAK))
                }
            scaleX = s
            scaleY = s
        },
    ) { content() }
}

private class Holder(
    var value: Any,
)

private const val BUMP_PEAK = 0.4f
private const val BUMP_OVER = 1.35f
private val BORDER = 1.dp
private val HEIGHT = 22.dp // css literal: bundle.css `.og-badge { min-height: 22px }`
private val GAP = 4.dp // css literal: bundle.css `.og-badge { gap: 4px }`
private val COUNT_PADDING = 6.dp // css literal: bundle.css `.og-badge--count { padding: 0 6px }`
private val ICON = 14.dp // css literal: bundle.js `Badge`, `Icon size: 14`
