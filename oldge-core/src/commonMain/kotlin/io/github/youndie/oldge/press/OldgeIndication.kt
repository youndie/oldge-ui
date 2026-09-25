package io.github.youndie.oldge.press

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.LocalOldgeMotion
import io.github.youndie.oldge.theme.LocalOldgeSkin
import io.github.youndie.oldge.theme.LocalOldgeSwitches
import io.github.youndie.oldge.tokens.OldgeEasings
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * The design system's press feedback and focus ring, as the theme's `LocalIndication` (B-10), so a
 * clickable cannot forget them.
 *
 * **The flash** (bundle.js `pressFx`, bundle.css `.og-glint`): a spot `min(w, h)` wide, clamped to
 * 28…56 dp, centred on the press, a radial gradient of white at 75 % to `gloss` at 22 % to nothing
 * at 64 % of its farthest corner, blended `screen`, drawn over the element's background and under
 * its content, clipped to [shape]. It scales 0.3 → 1 and fades 0.9 → 0.8 → 0 over `dur-press` on
 * `ease-out` — the curve applied per keyframe interval, as CSS does. Off under reduced motion or
 * `pressFlash = false`; several presses overlap, as several spans do.
 *
 * **The focus ring** (README, focus): 2 dp of `focus`, 2 dp outside [shape], drawn only while the
 * input mode is the keyboard — on desktop a click also takes focus, and a ring drawn on focus alone
 * would show after every mouse press (kvadrant-ui D19). The mode is read while drawing, which Compose
 * observes, so a change of mode redraws the ring by itself. Compose emits no `FocusInteraction` for
 * focus taken in touch mode (measured in B-10), so a control focused by a tap and then reached by the
 * keyboard shows its ring from the next keyboard focus on.
 *
 * A component passes its own [shape]; the theme's default is a rectangle.
 */
public class OldgeIndication(
    private val shape: Shape = RectangleShape,
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode =
        OldgeIndicationNode(interactionSource, shape)

    override fun equals(other: Any?): Boolean = other is OldgeIndication && other.shape == shape

    override fun hashCode(): Int = shape.hashCode()
}

private class Flash(
    val centre: Offset,
    val progress: Animatable<Float, *>,
)

private class OldgeIndicationNode(
    private val interactionSource: InteractionSource,
    private val shape: Shape,
) : androidx.compose.ui.Modifier.Node(),
    DelegatableNode,
    DrawModifierNode,
    CompositionLocalConsumerModifierNode {
    private val flashes = mutableListOf<Flash>()
    private var focused = false

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> press(interaction.pressPosition)
                    is FocusInteraction.Focus -> focused = true
                    is FocusInteraction.Unfocus -> focused = false
                }
                invalidateDraw()
            }
        }
    }

    private fun press(at: Offset) {
        val motion = currentValueOf(LocalOldgeMotion)
        if (motion.reduced || !currentValueOf(LocalOldgeSwitches).pressFlash) return
        val flash = Flash(at, Animatable(0f))
        flashes += flash
        coroutineScope.launch {
            flash.progress.animateTo(1f, tween(motion.press, easing = LinearEasing)) { invalidateDraw() }
            flashes -= flash
            invalidateDraw()
        }
    }

    override fun ContentDrawScope.draw() {
        val skin = currentValueOf(LocalOldgeSkin)
        val outline = shape.createOutline(size, layoutDirection, this)
        if (flashes.isNotEmpty()) {
            val clip = Path().apply { addOutline(outline) }
            clipPath(
                clip,
            ) { for (flash in flashes) drawOldgeFlash(flash.centre, flash.progress.value, skin.colors.gloss) }
        }
        drawContent()
        val keyboard = currentValueOf(LocalInputModeManager).inputMode == InputMode.Keyboard
        if (focused && keyboard) drawOldgeFocusRing(shape, skin.colors.focus)
    }
}

/**
 * One frame of the flash: [t] is the animation's linear progress, 0…1. Shared by the indication and
 * the `Press` goldens, which freeze it at a chosen frame.
 */
internal fun DrawScope.drawOldgeFlash(
    centre: Offset,
    t: Float,
    gloss: Color,
) {
    val spot = min(FLASH_MAX.toPx(), max(FLASH_MIN.toPx(), min(size.width, size.height)))
    val scale = FLASH_START_SCALE + (1 - FLASH_START_SCALE) * OldgeEasings.out.transform(t)
    val opacity =
        if (t < 0.5f) {
            FLASH_OPACITY_0 + (FLASH_OPACITY_50 - FLASH_OPACITY_0) * OldgeEasings.out.transform(t / 0.5f)
        } else {
            FLASH_OPACITY_50 * (1 - OldgeEasings.out.transform((t - 0.5f) / 0.5f))
        }
    // `radial-gradient(circle, …)` sizes to the farthest corner of the spot's square: √2 × half.
    val radius = spot / 2 * sqrt(2f)
    withTransform({ scale(scale, scale, pivot = centre) }) {
        drawCircle(
            Brush.radialGradient(
                0f to Color.White.copy(alpha = FLASH_CORE_ALPHA),
                FLASH_GLOSS_STOP to gloss,
                FLASH_FADE_STOP to gloss.copy(alpha = 0f),
                center = centre,
                radius = radius,
            ),
            radius = spot / 2,
            center = centre,
            alpha = opacity,
            blendMode = BlendMode.Screen,
        )
    }
}

/** The focus ring round [shape]: 2 dp of [focus], 2 dp outside it. */
internal fun DrawScope.drawOldgeFocusRing(
    shape: Shape,
    focus: Color,
) {
    val width = FOCUS_WIDTH.toPx()
    val gap = FOCUS_OFFSET.toPx()
    val grow = gap + width / 2
    val outline =
        shape.createOutline(
            androidx.compose.ui.geometry
                .Size(size.width + 2 * grow, size.height + 2 * grow),
            layoutDirection,
            this,
        )
    withTransform({ translate(-grow, -grow) }) {
        when (outline) {
            is Outline.Rectangle -> {
                drawRect(focus, outline.rect.topLeft, outline.rect.size, style = Stroke(width))
            }

            is Outline.Rounded -> {
                drawPath(
                    Path().apply { addRoundRect(outline.roundRect) },
                    focus,
                    style = Stroke(width),
                )
            }

            is Outline.Generic -> {
                drawPath(outline.path, focus, style = Stroke(width))
            }
        }
    }
}

private val FLASH_MIN = 28.dp
private val FLASH_MAX = 56.dp
private const val FLASH_START_SCALE = 0.3f
private const val FLASH_OPACITY_0 = 0.9f
private const val FLASH_OPACITY_50 = 0.8f
private const val FLASH_CORE_ALPHA = 0.75f
private const val FLASH_GLOSS_STOP = 0.22f
private const val FLASH_FADE_STOP = 0.64f
private val FOCUS_WIDTH = 2.dp // css literal: bundle.css `outline: 2px solid var(--focus)`
private val FOCUS_OFFSET = 2.dp // css literal: bundle.css `outline-offset: 2px`
