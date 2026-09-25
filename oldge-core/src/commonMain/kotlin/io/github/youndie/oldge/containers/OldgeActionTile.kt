package io.github.youndie.oldge.containers

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.material.CSS_BLUR_TO_TEXT_SHADOW
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.drawGlossCap
import io.github.youndie.oldge.material.lerpShadows
import io.github.youndie.oldge.material.premultipliedLerp
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.press.oldgePressScale
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.tokens.OldgeShadow
import io.github.youndie.oldge.type.OldgeText

/** An ActionTile's badge: [Chrome] (the default), [Accent] (the grid's one main action), [Bezel]. */
public enum class OldgeActionTileTone { Chrome, Accent, Bezel }

/** An ActionTile's layout: [Row] (badge beside the text) or [Stack] (badge over it, for a grid). */
public enum class OldgeActionTileLayout { Row, Stack }

/**
 * A launcher-style action — the design system's `ActionTile`: a large round glossy badge and a bold
 * [title], with no card round it; [description] is one line under the title.
 *
 * Its README's rules: tiles go in a two-column grid or a stack; in a two-column grid the title is one
 * or two short words, up to 12 characters a line, or the tile wants the [OldgeActionTileLayout.Row]
 * layout in one column. The title is a verb («Загрузить файлы»).
 *
 * Pressed, a sunken panel shows under the tile, a gloss flash spreads from the finger, and the badge
 * squashes to 0.86 and turns −10°.
 */
@Composable
public fun OldgeActionTile(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    tone: OldgeActionTileTone = OldgeActionTileTone.Chrome,
    layout: OldgeActionTileLayout = OldgeActionTileLayout.Row,
    interactionSource: MutableInteractionSource? = null,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    // `.og-tile:active`: the panel shows over `dur-fast`, and goes over `dur-base`, on `ease-out`.
    val t by animateFloatAsState(
        if (pressed) 1f else 0f,
        tween(if (pressed) motion.fast else motion.base, easing = motion.out),
        label = "oldge tile panel",
    )
    val shape = RoundedCornerShape(OldgeRadii.lg)
    val clear = Color.Transparent
    val box =
        modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = MIN_HEIGHT)
            .cssBox(
                OldgeRadii.lg,
                CssBackground.Solid(premultipliedLerp(clear, c.surface, t)),
                BORDER,
                premultipliedLerp(clear, c.line, t),
                lerpShadows(OldgeTheme.shadows.sunken.map { it.none() }, OldgeTheme.shadows.sunken, t),
            ).clickable(source, OldgeIndication(shape), role = Role.Button, onClick = onClick)
            .padding(BORDER + OldgeTheme.spacing.space2)
    val badge = @Composable { Badge(icon, tone, source) }
    val text = @Composable { TileText(title, description, layout) }
    when (layout) {
        OldgeActionTileLayout.Row -> {
            Row(
                box,
                horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space3),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                badge()
                Box(Modifier.weight(1f)) { text() }
            }
        }

        OldgeActionTileLayout.Stack -> {
            Column(
                box,
                verticalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space3),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                badge()
                text()
            }
        }
    }
}

/** `.og-tile__badge`: 52 px, the tone's radial gloss, `shadow-orb` and a dark inner ring, the cap highlight. */
@Composable
private fun Badge(
    icon: ImageVector,
    tone: OldgeActionTileTone,
    source: MutableInteractionSource,
) {
    val c = OldgeTheme.colors
    val (hi, lo, stop, ink) =
        when (tone) {
            OldgeActionTileTone.Chrome -> Quad(c.chromeHi, c.chromeLo, CHROME_STOP, c.onChrome)
            OldgeActionTileTone.Accent -> Quad(c.accentHi, c.accentLo, TONE_STOP, c.onAccent)
            OldgeActionTileTone.Bezel -> Quad(c.bezelHi, c.bezelLo, TONE_STOP, c.onBezel)
        }
    val pressed by source.collectIsPressedAsState()
    val motion = OldgeTheme.motion
    // `.og-tile:active .og-tile__badge { transform: scale(.86) rotate(-10deg) }`: in over
    // `dur-instant`, back on the spring.
    val turn by animateFloatAsState(
        if (pressed) 1f else 0f,
        tween(if (pressed) motion.instant else motion.base, easing = motion.spring),
        label = "oldge tile badge",
    )
    Box(
        Modifier
            .graphicsLayer { rotationZ = BADGE_TURN * turn }
            .oldgePressScale(source, BADGE_PRESSED)
            .size(BADGE)
            .cssBox(
                OldgeRadii.pill,
                CssBackground.Radial(listOf(0f to hi, stop to lo), BADGE_X, BADGE_Y),
                shadows = OldgeTheme.shadows.orb + BADGE_RING,
            ).drawBehind { drawGlossCap(c.gloss, CAP_SIDE, CAP_TOP, CAP_HEIGHT) },
        contentAlignment = Alignment.Center,
    ) { OldgeIcon(icon, contentDescription = null, size = ICON, tint = ink) }
}

@Composable
private fun TileText(
    title: String,
    description: String?,
    layout: OldgeActionTileLayout,
) {
    val c = OldgeTheme.colors
    val type = OldgeTheme.type
    val align = if (layout == OldgeActionTileLayout.Stack) TextAlign.Center else TextAlign.Start
    // `.og-tile__text { hyphens: auto }`, inherited by the title and the description: Android hyphenates; desktop and iOS Skia cannot (B-59).
    Column(verticalArrangement = Arrangement.spacedBy(TEXT_GAP)) {
        // `.og-tile__title { font-weight: 700; text-shadow: 0 1px 1px rgba(0,0,0,0.25) }`.
        OldgeText(
            title,
            style =
                type.bodyStrong.copy(
                    color = c.ink,
                    textAlign = align,
                    shadow = Shadow(TITLE_SHADOW, Offset(0f, 1f), CSS_BLUR_TO_TEXT_SHADOW),
                    hyphens = Hyphens.Auto,
                ),
        )
        if (description !=
            null
        ) {
            OldgeText(
                description,
                style = type.caption.copy(color = c.inkMuted, textAlign = align, hyphens = Hyphens.Auto),
            )
        }
    }
}

private data class Quad(
    val hi: Color,
    val lo: Color,
    val stop: Float,
    val ink: Color,
)

private fun OldgeShadow.none(): OldgeShadow =
    copy(offsetX = 0.dp, offsetY = 0.dp, blur = 0.dp, spread = 0.dp, color = Color.Transparent)

/** `inset 0 0 0 1px rgba(0,0,0,0.3)` on the badge. */
private val BADGE_RING = listOf(OldgeShadow(true, 0.dp, 0.dp, 0.dp, 1.dp, Color(0f, 0f, 0f, 0.3f)))
private val TITLE_SHADOW = Color(0f, 0f, 0f, 0.25f)

private val BORDER = 1.dp
private val MIN_HEIGHT = 64.dp // css literal: bundle.css `.og-tile { min-height: 64px }`
private val BADGE = 52.dp // css literal: bundle.css `.og-tile__badge { width: 52px; height: 52px }`
private val ICON = 28.dp // css literal: bundle.js `ActionTile`, `Icon size: 28`
private val TEXT_GAP = 2.dp // css literal: bundle.css `.og-tile__text { gap: 2px }`
private const val CHROME_STOP = 0.7f
private const val TONE_STOP = 0.75f
private const val BADGE_X = 0.35f
private const val BADGE_Y = 0.3f
private const val CAP_SIDE = 0.12f
private const val CAP_TOP = 0.03f
private const val CAP_HEIGHT = 0.44f
private const val BADGE_PRESSED = 0.86f
private const val BADGE_TURN = -10f
