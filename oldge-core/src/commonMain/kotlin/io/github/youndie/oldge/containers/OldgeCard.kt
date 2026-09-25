package io.github.youndie.oldge.containers

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.bezel
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.cssLinearGradient
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.press.oldgePressScale
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText

/**
 * A card — the design system's `Card`: an inset surface with an optional [bar] header in the skin's
 * frame, a media zone, a [title], [subtitle] and [text], and [actions].
 *
 * The media zone is an [image] when there is one (cropped to fill, with [imageDescription] its
 * accessible name), and otherwise the frame's gradient with a large [media] icon on it. [sunken] makes
 * it the read-only, sunken surface for output and logs.
 *
 * Its README's rules: with [onClick] the whole card is one button, with a gloss flash, a squash to
 * 0.975 and the media icon jumping; such a card takes **no [actions]** — two pressable layers confuse
 * — and that one is enforced. (The design system's own preview breaks it, nesting two buttons in a
 * pressable card; the fixture ports it without the press.) [actions] are small buttons.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun OldgeCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    text: String? = null,
    bar: String? = null,
    barIcon: ImageVector? = null,
    media: ImageVector? = null,
    image: Painter? = null,
    imageDescription: String? = null,
    sunken: Boolean = false,
    onClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    actions: (@Composable FlowRowScope.() -> Unit)? = null,
) {
    require(onClick == null || actions == null) {
        "A card pressed as a whole takes no actions: two pressable layers confuse (the Card README)"
    }
    val c = OldgeTheme.colors
    val spacing = OldgeTheme.spacing
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(OldgeRadii.lg)
    val surface =
        modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.oldgePressScale(source, PRESSED_SCALE) else Modifier)
            .cssBox(
                OldgeRadii.lg,
                CssBackground.Solid(if (sunken) c.well else c.surface),
                BORDER,
                c.edge,
                if (sunken) OldgeTheme.shadows.sunken else OldgeTheme.shadows.raised,
            ).then(
                if (onClick != null) {
                    Modifier.clickable(source, OldgeIndication(shape), role = Role.Button, onClick = onClick)
                } else {
                    Modifier
                },
            ).padding(BORDER)
            // `.og-card { overflow: hidden }`: the bar and the media are cut to the card's corners.
            .clip(RoundedCornerShape(OldgeRadii.lg - BORDER))
    Column(surface) {
        if (bar != null) Bar(bar, barIcon)
        if (image != null || media != null) Media(image, imageDescription, media, source, onClick != null)
        if (title != null || subtitle != null || text != null) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = spacing.space4, vertical = spacing.space3),
                verticalArrangement = Arrangement.spacedBy(BODY_GAP),
            ) {
                val type = OldgeTheme.type
                if (title != null) OldgeText(title, style = type.title.copy(color = c.ink))
                if (subtitle != null) OldgeText(subtitle, style = type.caption.copy(color = c.inkMuted))
                // `.og-card__text { margin: space-1 0 0 }`.
                if (text !=
                    null
                ) {
                    OldgeText(text, Modifier.padding(top = spacing.space1), style = type.body.copy(color = c.ink))
                }
            }
        }
        if (actions != null) {
            FlowRow(
                Modifier.fillMaxWidth().padding(start = spacing.space3, end = spacing.space3, bottom = spacing.space3),
                horizontalArrangement = Arrangement.spacedBy(spacing.space2, Alignment.End),
                verticalArrangement = Arrangement.spacedBy(spacing.space2),
                content = actions,
            )
        }
    }
}

/** `.og-bezel.og-card__bar`: the frame, at least 40 px high, the title face at 0.9375rem bold. */
@Composable
private fun Bar(
    text: String,
    icon: ImageVector?,
) {
    val c = OldgeTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = BAR_HEIGHT)
            .bezel(0.dp)
            .padding(horizontal = OldgeTheme.spacing.space3),
        horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) OldgeIcon(icon, contentDescription = null, size = BAR_ICON, tint = c.onBezel)
        OldgeText(
            text,
            style = OldgeTheme.type.title.copy(fontSize = BAR_SIZE, lineHeight = BAR_LINE, color = c.onBezel),
        )
    }
}

/**
 * `.og-card__media`: 132 px of `bezel-hi` → `bezel-lo` under a `glow` ellipse (`radial-gradient(90%
 * 90% at 85% 100%, glow, transparent 60%)`), with the image over it or the icon on it; the icon casts
 * `drop-shadow(0 3px 3px rgba(0,0,0,.4))` and, on a pressed card, jumps to 1.15 turned −6°.
 */
@Composable
private fun Media(
    image: Painter?,
    imageDescription: String?,
    icon: ImageVector?,
    source: MutableInteractionSource,
    pressable: Boolean,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val pressed by source.collectIsPressedAsState()
    val jump by animateFloatAsState(
        if (pressed && pressable) 1f else 0f,
        tween(motion.slow, easing = motion.spring),
        label = "oldge card media",
    )
    Box(
        Modifier
            .fillMaxWidth()
            .height(MEDIA_HEIGHT)
            // A background is painted inside its box: the glow is centred on the bottom edge and would
            // otherwise spill into the body below.
            .clipToBounds()
            .drawBehind {
                drawRect(cssLinearGradient(listOf(0f to c.bezelHi, 1f to c.bezelLo), 180f, size))
                // An ellipse 90 % of the box wide and high, drawn as a circle scaled on y.
                val rx = size.width * GLOW_SIZE
                val ry = size.height * GLOW_SIZE
                val centre = Offset(size.width * GLOW_X, size.height)
                scale(1f, ry / rx, centre) {
                    drawCircle(
                        Brush.radialGradient(
                            0f to c.glow,
                            GLOW_STOP to c.glow.copy(alpha = 0f),
                            center = centre,
                            radius = rx,
                        ),
                        radius = rx,
                        center = centre,
                    )
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        if (image != null) {
            Image(image, imageDescription, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else if (icon != null) {
            Box(
                Modifier.graphicsLayer {
                    val s = 1f + (JUMP_SCALE - 1f) * jump
                    scaleX = s
                    scaleY = s
                    rotationZ = JUMP_TURN * jump
                },
            ) {
                // The drop shadow: the glyph again, dark, 3 px down and blurred as CSS blurs a 3 px
                // drop-shadow (a Gaussian of 1.5 px, which Compose's radius reaches at √3).
                OldgeIcon(
                    icon,
                    contentDescription = null,
                    Modifier.offset(y = SHADOW_Y).graphicsLayer {
                        renderEffect = BlurEffect(SHADOW_BLUR.toPx(), SHADOW_BLUR.toPx())
                    },
                    size = MEDIA_ICON,
                    tint = SHADOW,
                )
                OldgeIcon(icon, contentDescription = null, size = MEDIA_ICON, tint = c.onBezel)
            }
        }
    }
}

private val SHADOW = Color(0f, 0f, 0f, 0.4f)
private val BORDER = 1.dp
private val BAR_HEIGHT = 40.dp // css literal: bundle.css `.og-card__bar { min-height: 40px }`
private val BAR_ICON = 18.dp // css literal: bundle.js `Card`, bar `Icon size: 18`
private val BAR_SIZE = 15.sp // css literal: bundle.css `.og-card__bar { font: 700 0.9375rem/1.25rem }`
private val BAR_LINE = 20.sp // css literal: bundle.css `.og-card__bar { font: 700 0.9375rem/1.25rem }`
private val MEDIA_HEIGHT = 132.dp // css literal: bundle.css `.og-card__media { height: 132px }`
private val MEDIA_ICON = 56.dp // css literal: bundle.js `Card`, media `Icon size: 56`
private val SHADOW_Y = 3.dp // css literal: bundle.css `.og-card__media .og-icon { filter: drop-shadow(0 3px 3px …) }`
private val SHADOW_BLUR = 1.73.dp // css literal: bundle.css `drop-shadow(… 3px …)`, σ 1.5 as Compose's blur radius
private val BODY_GAP = 2.dp // css literal: bundle.css `.og-card__body { gap: 2px }`
private const val PRESSED_SCALE = 0.975f
private const val JUMP_SCALE = 1.15f
private const val JUMP_TURN = -6f
private const val GLOW_SIZE = 0.9f
private const val GLOW_X = 0.85f
private const val GLOW_STOP = 0.6f
