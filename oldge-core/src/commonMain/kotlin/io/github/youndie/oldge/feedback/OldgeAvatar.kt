package io.github.youndie.oldge.feedback

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.tokens.OldgeShadow
import io.github.youndie.oldge.type.OldgeText

/** An avatar's size: [Small] 32 dp, [Medium] 44 dp, [Large] 64 dp. */
public enum class OldgeAvatarSize(
    internal val outer: Dp,
    internal val rim: Dp,
) {
    Small(32.dp, 2.dp), // css literal: bundle.css `.og-avatar--sm { --s: 32px }`, `padding: 2px`
    Medium(44.dp, 2.dp), // css literal: bundle.css `.og-avatar { --s: 44px; padding: 2px }`
    Large(64.dp, 3.dp), // css literal: bundle.css `.og-avatar--lg { --s: 64px; padding: 3px }`
}

/** An avatar's status lamp: [Online] (green, pulsing), [Busy] (red), [Away] (amber). */
public enum class OldgeAvatarStatus(
    internal val word: String,
) {
    Online("в сети"),
    Busy("занят"),
    Away("отошёл"),
}

/**
 * An avatar — the design system's `Avatar`: a round face in a chrome ring, with the initials of
 * [name] on the skin's frame, or an [image], or an [icon], and a status lamp.
 *
 * Its README's rule: the [status] is also said in words in the accessible name («Павел Вотяков, в
 * сети»). Online, the lamp pulses. The initials are sized from the avatar (36 % of it), so they do
 * not follow the system font scale.
 */
@Composable
public fun OldgeAvatar(
    modifier: Modifier = Modifier,
    name: String? = null,
    image: Painter? = null,
    icon: ImageVector? = null,
    size: OldgeAvatarSize = OldgeAvatarSize.Medium,
    status: OldgeAvatarStatus? = null,
) {
    val c = OldgeTheme.colors
    val initials = oldgeInitials(name)
    val pulse = if (status == OldgeAvatarStatus.Online) loopPhase(PULSE_MS) else null
    Box(
        modifier
            .size(size.outer)
            .clearAndSetSemantics {
                role = Role.Image
                contentDescription = listOfNotNull(name, status?.word).joinToString(", ")
            },
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .cssBox(
                    OldgeRadii.pill,
                    CssBackground.Linear(listOf(0f to c.chromeHi, RIM_MID to c.chromeLo, 1f to c.chromeHi), RIM_ANGLE),
                    shadows = OldgeTheme.shadows.orb,
                ).padding(size.rim)
                .clip(CircleShape)
                .cssBox(OldgeRadii.pill, CssBackground.Linear(listOf(0f to c.bezelHi, 1f to c.bezelLo))),
            contentAlignment = Alignment.Center,
        ) {
            when {
                image != null -> {
                    Image(image, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }

                icon != null -> {
                    OldgeIcon(icon, contentDescription = null, size = ICON, tint = c.onBezel)
                }

                else -> {
                    // `font: 700 calc(var(--s) * .36)/1 var(--font-title); text-shadow: 0 1px 0 rgba(0,0,0,.4)`,
                    // in px: dp converted, so the system font scale does not grow it.
                    val px = with(LocalDensity.current) { (size.outer * INITIALS).toSp() }
                    OldgeText(
                        initials,
                        style =
                            OldgeTheme.type.title.copy(
                                fontSize = px,
                                lineHeight = px,
                                color = c.onBezel,
                                shadow = Shadow(INITIALS_SHADOW, Offset(0f, 1f), 0f),
                            ),
                    )
                }
            }
        }
        if (status != null) Lamp(status, size, pulse)
    }
}

/** `Avatar`'s initials: the first letters of the first two words of [name], upper-cased. */
internal fun oldgeInitials(name: String?): String =
    name
        .orEmpty()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().toString() }
        .uppercase()

/**
 * `.og-avatar__led`: 28 % of the avatar and at least 10 px, 1 px past its bottom-right corner, in the
 * status colour inside a 2 px `ground` ring; online, a glow of 6 px spreading 2 px swells and fades
 * over a second and a fifth (`og-led`).
 */
@Composable
private fun BoxScope.Lamp(
    status: OldgeAvatarStatus,
    size: OldgeAvatarSize,
    pulse: Float?,
) {
    val c = OldgeTheme.colors
    val colour =
        when (status) {
            OldgeAvatarStatus.Online -> c.success
            OldgeAvatarStatus.Busy -> c.danger
            OldgeAvatarStatus.Away -> c.warning
        }
    val d = max(size.outer * LAMP, LAMP_MIN)
    // `ease-in-out` to the middle and back, each half eased on its own: 0 at the ends, 1 at half.
    val swell = pulse?.let { p -> EASE_IN_OUT.transform(if (p < HALF) p * 2 else (1 - p) * 2) } ?: 0f
    val glow =
        if (swell > 0f) {
            listOf(OldgeShadow(false, 0.dp, 0.dp, LED_GLOW * swell, LED_SPREAD * swell, colour))
        } else {
            emptyList()
        }
    Box(
        Modifier
            .align(Alignment.BottomEnd)
            .offset(x = LAMP_OUT, y = LAMP_OUT)
            .size(d)
            .cssBox(OldgeRadii.pill, CssBackground.Solid(colour), LAMP_RING, c.ground, glow),
    )
}

private val INITIALS_SHADOW = Color(0f, 0f, 0f, 0.4f)
private const val INITIALS = 0.36f
private const val LAMP = 0.28f
private const val RIM_MID = 0.6f
private const val RIM_ANGLE = 160f
private const val PULSE_MS = 1200
private const val HALF = 0.5f
private val EASE_IN_OUT = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
private val ICON = 20.dp // css literal: bundle.js `Avatar`, `Icon size: 20`
private val LAMP_MIN = 10.dp // css literal: bundle.css `.og-avatar__led { min-width: 10px; min-height: 10px }`
private val LAMP_RING = 2.dp // css literal: bundle.css `.og-avatar__led { border: 2px solid var(--ground) }`
private val LAMP_OUT = 1.dp // css literal: bundle.css `.og-avatar__led { right: -1px; bottom: -1px }`
private val LED_GLOW = 6.dp // css literal: bundle.css `@keyframes og-led { 50% { box-shadow: 0 0 6px 2px } }`
private val LED_SPREAD = 2.dp // css literal: bundle.css `@keyframes og-led { 50% { box-shadow: 0 0 6px 2px } }`
