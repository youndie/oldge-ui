package io.github.youndie.oldge.actions

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.CssRadii
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.cssRoundRect
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.press.oldgePressScale
import io.github.youndie.oldge.theme.LocalOldgeContentColor
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText

/** A Fab's face: [Accent] (the default, the main action) or [Chrome] (neutral). */
public enum class OldgeFabTone { Accent, Chrome }

/**
 * The floating action button — the design system's `Fab`: a rounded glossy square in a chrome frame,
 * for the screen's one main creative action. One per screen; raise it above a snackbar that appears
 * under it.
 *
 * It is only an icon, so [contentDescription] is required ("otherwise `ariaLabel`", the Fab README).
 * With a label it is [OldgeExtendedFab].
 *
 * [expanded] makes it a disclosure: the icon turns 135° — a plus becomes a cross — and the button
 * offers expand or collapse to accessibility services. Null, the default, is a plain action.
 *
 * It screws in on a spring when it first appears, squashes to 0.9 while pressed and spreads a gloss
 * flash from the finger; under reduced motion it simply appears.
 */
@Composable
public fun OldgeFab(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: OldgeFabTone = OldgeFabTone.Accent,
    expanded: Boolean? = null,
    interactionSource: MutableInteractionSource? = null,
): Unit = FabBox(icon, text = null, contentDescription, onClick, modifier, tone, expanded, interactionSource)

/** A Fab with a label beside its icon — the design system's `Fab` given `label`. See [OldgeFab]. */
@Composable
public fun OldgeExtendedFab(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: OldgeFabTone = OldgeFabTone.Accent,
    expanded: Boolean? = null,
    interactionSource: MutableInteractionSource? = null,
): Unit = FabBox(icon, text, contentDescription = null, onClick, modifier, tone, expanded, interactionSource)

/**
 * `docked`: the Fab in the bottom-right corner of a screen, above its BottomNav (`.og-fab-dock`:
 * right `space-4`, bottom 88 px). Place it last in the screen's `Box`, so it draws over the content.
 */
@Composable
public fun OldgeFabDock(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier.fillMaxSize().padding(end = OldgeTheme.spacing.space4, bottom = DOCK_BOTTOM),
        contentAlignment = Alignment.BottomEnd,
    ) { content() }
}

@Composable
private fun FabBox(
    icon: ImageVector,
    text: String?,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier,
    tone: OldgeFabTone,
    expanded: Boolean?,
    interactionSource: MutableInteractionSource?,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(OldgeRadii.lg)
    val fill =
        when (tone) {
            OldgeFabTone.Accent -> {
                CssBackground.Linear(listOf(0f to c.accentHi, GLOSS_MID to c.accent, 1f to c.accentLo))
            }

            OldgeFabTone.Chrome -> {
                CssBackground.Linear(listOf(0f to c.chromeHi, 1f to c.chromeLo))
            }
        }
    val content = if (tone == OldgeFabTone.Accent) c.onAccent else c.onChrome
    val rim = CssBackground.Linear(listOf(0f to c.chromeHi, RIM_MID to c.chromeLo, 1f to c.chromeHi), RIM_ANGLE)
    // `@keyframes og-fab-in`: from `scale(0) rotate(-120deg)` over `dur-slow` on the spring.
    val entrance = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) { entrance.animateTo(1f, tween(motion.slow, easing = motion.spring)) }
    val turn by animateFloatAsState(
        if (expanded == true) EXPANDED_TURN else 0f,
        tween(motion.slow, easing = motion.bounce),
        label = "oldge fab icon turn",
    )
    CompositionLocalProvider(LocalOldgeContentColor provides content) {
        Row(
            modifier
                .graphicsLayer {
                    val e = entrance.value
                    scaleX = e
                    scaleY = e
                    rotationZ = ENTRANCE_TURN * (1 - e)
                }.oldgePressScale(source, PRESSED_SCALE)
                .height(SIZE)
                .then(if (text == null) Modifier.width(SIZE) else Modifier.defaultMinSize(minWidth = SIZE))
                .cssBox(
                    OldgeRadii.lg,
                    fill,
                    FRAME,
                    shadows = OldgeTheme.shadows.orb + OldgeTheme.shadows.window,
                    borderBackground = rim,
                ).semantics {
                    if (contentDescription != null) this.contentDescription = contentDescription
                    when (expanded) {
                        true -> {
                            collapse {
                                onClick()
                                true
                            }
                        }

                        false -> {
                            expand {
                                onClick()
                                true
                            }
                        }

                        null -> {}
                    }
                }.clickable(
                    source,
                    OldgeIndication(shape, ringOffset = RING_OFFSET),
                    role = Role.Button,
                    onClick = onClick,
                ).drawWithContent {
                    drawContent()
                    // `.og-fab::before` has `z-index: 2`: the highlight is over the icon and label.
                    drawFabHighlight(c.gloss, FRAME.toPx())
                }.padding(start = FRAME, end = FRAME)
                .then(if (text == null) Modifier else Modifier.padding(start = PAD_START, end = PAD_END)),
            horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OldgeIcon(icon, contentDescription = null, modifier = Modifier.rotate(turn), size = ICON)
            if (text != null) OldgeText(text, style = OldgeTheme.type.button.copy(color = content), softWrap = false)
        }
    }
}

/**
 * `.og-fab::before`: from the padding box, 6 px in on the left and right, 2 px down, 36 % of its
 * height, radius `10px 10px 40% 40%` — round top corners over elliptical bottom ones.
 */
private fun DrawScope.drawFabHighlight(
    gloss: Color,
    frame: Float,
) {
    val left = frame + HIGHLIGHT_SIDE.toPx()
    val top = frame + HIGHLIGHT_TOP.toPx()
    val w = size.width - 2 * left
    val h = (size.height - 2 * frame) * HIGHLIGHT_HEIGHT
    val round = CornerRadius(HIGHLIGHT_TOP_RADIUS.toPx())
    val bottom = CornerRadius(w * HIGHLIGHT_BOTTOM_RADIUS, h * HIGHLIGHT_BOTTOM_RADIUS)
    val band = cssRoundRect(Offset(left, top), Size(w, h), 0f, CssRadii(round, round, bottom, bottom))
    drawPath(Path().apply { addRoundRect(band) }, gloss)
}

private val SIZE = 56.dp // css literal: bundle.css `.og-fab { height: 56px; min-width: 56px }`
private val FRAME = 3.dp // css literal: bundle.css `.og-fab { border: 3px solid transparent }`
private val PAD_START = 16.dp // css literal: bundle.css `.og-fab { padding: 0 20px 0 16px }`
private val PAD_END = 20.dp // css literal: bundle.css `.og-fab { padding: 0 20px 0 16px }`
private val ICON = 26.dp // css literal: bundle.js `Fab`, `Icon size: 26`
private val RING_OFFSET = 3.dp // css literal: bundle.css `.og-fab:focus-visible { outline-offset: 3px }`
private val DOCK_BOTTOM = 88.dp // css literal: bundle.css `.og-fab-dock { bottom: 88px }`
private val HIGHLIGHT_SIDE = 6.dp // css literal: bundle.css `.og-fab::before { left: 6px; right: 6px }`
private val HIGHLIGHT_TOP = 2.dp // css literal: bundle.css `.og-fab::before { top: 2px }`
private val HIGHLIGHT_TOP_RADIUS = 10.dp // css literal: bundle.css `.og-fab::before { border-radius: 10px … }`
private const val HIGHLIGHT_HEIGHT = 0.36f
private const val HIGHLIGHT_BOTTOM_RADIUS = 0.4f
private const val GLOSS_MID = 0.55f
private const val RIM_MID = 0.6f
private const val RIM_ANGLE = 160f
private const val PRESSED_SCALE = 0.9f
private const val EXPANDED_TURN = 135f
private const val ENTRANCE_TURN = -120f
