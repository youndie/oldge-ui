package io.github.youndie.oldge.actions

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawTransform
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.GlossStops
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.lerpGloss
import io.github.youndie.oldge.material.lerpShadows
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.press.oldgePressScale
import io.github.youndie.oldge.press.pressFraction
import io.github.youndie.oldge.theme.LocalOldgeContentColor
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeColors
import io.github.youndie.oldge.tokens.OldgeEasings
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.tokens.OldgeShadow
import io.github.youndie.oldge.type.OldgeText
import kotlinx.coroutines.flow.filterIsInstance

/**
 * The design system's three kinds of button (reference/design-system/components/Button).
 *
 * - [Primary] — the screen's one main action, in the skin's accent, always inside its `accent-rim`,
 *   its label the dark `on-accent`, never white. One per screen.
 * - [Secondary] — silver chrome, beside a primary for the way out.
 * - [Plain] — a text link in `accent-ink`.
 */
public enum class OldgeButtonVariant { Primary, Secondary, Plain }

/**
 * Button heights: [Small] 36 dp — dense rows only; [Medium] 44 dp; [Large] 52 dp — the main action at
 * the bottom of a screen.
 */
public enum class OldgeButtonSize { Small, Medium, Large }

/**
 * A glossy button with a label — the design system's `Button`. The label is a verb («Сохранить»,
 * «Отправить»); an [icon] goes before it.
 *
 * Pressed, the gradient turns over, the bevel sinks, it squashes to 0.94 and moves down a pixel, and
 * springs back; a gloss flash spreads from the finger, and across a primary a glint runs and fades at
 * its right edge. There is no hover state, by the design system's rule.
 */
@Composable
public fun OldgeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: OldgeButtonVariant = OldgeButtonVariant.Secondary,
    size: OldgeButtonSize = OldgeButtonSize.Medium,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    block: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
): Unit =
    ButtonBox(
        onClick,
        modifier,
        variant,
        size,
        icon,
        text,
        contentDescription = null,
        enabled,
        block,
        interactionSource,
    )

/**
 * A button that is only an icon. The design system requires an accessible name for it — "icon only:
 * `aria-label` is mandatory" — so [contentDescription] is not optional. For a round action there is
 * [OldgeOrbButton].
 */
@Composable
public fun OldgeIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: OldgeButtonVariant = OldgeButtonVariant.Secondary,
    size: OldgeButtonSize = OldgeButtonSize.Medium,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
): Unit =
    ButtonBox(
        onClick,
        modifier,
        variant,
        size,
        icon,
        text = null,
        contentDescription,
        enabled,
        block = false,
        interactionSource,
    )

@Composable
private fun ButtonBox(
    onClick: () -> Unit,
    modifier: Modifier,
    variant: OldgeButtonVariant,
    size: OldgeButtonSize,
    icon: ImageVector?,
    text: String?,
    contentDescription: String?,
    enabled: Boolean,
    block: Boolean,
    interactionSource: MutableInteractionSource?,
) {
    val c = OldgeTheme.colors
    val s = OldgeTheme.shadows
    val spacing = OldgeTheme.spacing
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(OldgeRadii.sm)
    val t = pressFraction(source, enabled)
    val look = variant.look(c, s, enabled, t)
    val iconOnly = text == null
    val padding =
        when {
            iconOnly -> 0.dp
            variant == OldgeButtonVariant.Plain -> spacing.space2
            size == OldgeButtonSize.Small -> spacing.space3
            size == OldgeButtonSize.Large -> spacing.space5
            else -> spacing.space4
        }
    val shine = rememberShine(source, enabled && variant == OldgeButtonVariant.Primary)
    CompositionLocalProvider(LocalOldgeContentColor provides look.content) {
        Row(
            modifier
                .oldgePressScale(
                    source,
                    if (enabled) PRESSED_SCALE else 1f,
                    pressedOffsetY = if (enabled) 1.dp else 0.dp,
                ).then(if (block) Modifier.fillMaxWidth() else Modifier)
                .defaultMinSize(minWidth = spacing.hitMin, minHeight = size.minHeight())
                .then(if (iconOnly) Modifier.width(spacing.hitMin) else Modifier)
                .cssBox(OldgeRadii.sm, look.background, BORDER, look.border, look.shadows, look.ring, look.gloss)
                .semantics { if (contentDescription != null) this.contentDescription = contentDescription }
                .clickable(source, OldgeIndication(shape), enabled = enabled, role = Role.Button, onClick = onClick)
                .drawWithContent {
                    drawContent()
                    // `.og-press { overflow: hidden }`: the glint stays inside the button; the focus
                    // ring, drawn by the indication, is outside it and must not be clipped.
                    shine()?.let { p ->
                        val outline = shape.createOutline(this.size, layoutDirection, this)
                        clipPath(Path().apply { addOutline(outline) }) { drawShine(p, c.gloss) }
                    }
                    // The content box starts inside the 1 px border and then the padding, as CSS's
                    // `box-sizing: border-box` has it; the padding alone left every button 2 px narrow.
                }.padding(horizontal = padding + BORDER),
            horizontalArrangement = Arrangement.spacedBy(spacing.space2, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon !=
                null
            ) {
                OldgeIcon(
                    icon,
                    contentDescription = null,
                    size =
                        if (size ==
                            OldgeButtonSize.Large
                        ) {
                            LARGE_ICON
                        } else {
                            ICON
                        },
                )
            }
            if (text != null) {
                val underline = variant == OldgeButtonVariant.Plain && t > 0.5f
                OldgeText(
                    text,
                    style =
                        size.textStyle().copy(
                            color = look.content,
                            textDecoration = if (underline) TextDecoration.Underline else null,
                        ),
                    softWrap = false,
                )
            }
        }
    }
}

/** What a variant looks like at press fraction [t]: CSS's resting and `:active` rules blended. */
private class Look(
    val background: CssBackground,
    val border: Color,
    val shadows: List<OldgeShadow>,
    val ring: List<OldgeShadow>,
    val gloss: Color?,
    val content: Color,
)

private fun OldgeButtonVariant.look(
    c: OldgeColors,
    s: io.github.youndie.oldge.tokens.OldgeShadows,
    enabled: Boolean,
    t: Float,
): Look {
    if (!enabled) {
        // `.og-btn:disabled`: surface-2, ink-muted, a `line` border, no shadow, no highlight.
        return if (this == OldgeButtonVariant.Plain) {
            Look(CssBackground.Solid(Color.Transparent), Color.Transparent, emptyList(), emptyList(), null, c.inkMuted)
        } else {
            Look(CssBackground.Solid(c.surface2), c.line, emptyList(), emptyList(), null, c.inkMuted)
        }
    }
    // `.og-btn:active::before { opacity: 0 }`: the highlight goes as the button sinks.
    val gloss = c.gloss.copy(alpha = c.gloss.alpha * (1 - t))
    return when (this) {
        OldgeButtonVariant.Primary -> {
            Look(
                lerpGloss(
                    GlossStops(c.accentHi, c.accent, c.accentLo),
                    GlossStops(c.accentLo, c.accent, c.accentHi),
                    t,
                ).background(),
                c.accentLo,
                lerpShadows(s.raised, s.sunken, t),
                listOf(OldgeShadow(false, 0.dp, 0.dp, 0.dp, 1.dp, c.accentRim)),
                gloss,
                c.onAccent,
            )
        }

        OldgeButtonVariant.Secondary -> {
            Look(
                lerpGloss(
                    GlossStops(c.chromeHi, null, c.chromeLo),
                    GlossStops(c.chromeLo, null, c.chromeHi),
                    t,
                ).background(),
                c.edge,
                lerpShadows(s.raised, s.sunken, t),
                emptyList(),
                gloss,
                c.onChrome,
            )
        }

        OldgeButtonVariant.Plain -> {
            Look(CssBackground.Solid(Color.Transparent), Color.Transparent, emptyList(), emptyList(), null, c.accentInk)
        }
    }
}

@Composable
private fun OldgeButtonSize.textStyle(): TextStyle {
    val type = OldgeTheme.type
    // bundle.css keeps the button's 1.25rem line in every size and changes the size only: 0.8125rem
    // small (the `label` size), 0.9375rem medium (`button`), 1.0625rem large (the `title` size).
    return when (this) {
        OldgeButtonSize.Small -> type.button.copy(fontSize = type.label.fontSize)
        OldgeButtonSize.Medium -> type.button
        OldgeButtonSize.Large -> type.button.copy(fontSize = type.title.fontSize)
    }
}

@Composable
private fun OldgeButtonSize.minHeight(): Dp =
    when (this) {
        OldgeButtonSize.Small -> SMALL_HEIGHT
        OldgeButtonSize.Medium -> OldgeTheme.spacing.hitMin
        OldgeButtonSize.Large -> LARGE_HEIGHT
    }

/**
 * The primary's glint (`.og-btn--primary::after`, `@keyframes og-shine`): a band 30 % wide of
 * `transparent → gloss → transparent`, skewed −20°, swept from −120 % to 360 % of its own width over
 * `dur-press`, fading out in the last fifth; above the content, and never shown under reduced motion
 * or while at rest. Returns the running progress, or null.
 */
@Composable
private fun rememberShine(
    source: MutableInteractionSource,
    active: Boolean,
): () -> Float? {
    val motion = OldgeTheme.motion
    val progress = remember { Animatable(1f) }
    LaunchedEffect(source, active, motion.reduced) {
        if (!active || motion.reduced) return@LaunchedEffect
        source.interactions.filterIsInstance<PressInteraction.Press>().collect {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(motion.press, easing = LinearEasing))
        }
    }
    return { progress.value.takeIf { it < 1f } }
}

private fun DrawScope.drawShine(
    t: Float,
    gloss: Color,
) {
    val band = size.width * SHINE_WIDTH
    val x = band * (SHINE_FROM + (SHINE_TO - SHINE_FROM) * OldgeEasings.out.transform(t))
    // Opacity is 1 until 80 %, then out to 0; CSS eases each keyframe interval on its own.
    val opacity = if (t < SHINE_HOLD) 1f else 1f - OldgeEasings.out.transform((t - SHINE_HOLD) / (1 - SHINE_HOLD))
    withTransform({
        translate(left = x)
        skew(SHINE_SKEW)
    }) {
        drawRect(
            Brush.horizontalGradient(
                0f to Color.Transparent,
                0.5f to gloss,
                1f to Color.Transparent,
                startX = 0f,
                endX = band,
            ),
            topLeft = Offset.Zero,
            size = Size(band, size.height),
            alpha = opacity,
        )
    }
}

private fun DrawTransform.skew(degrees: Float) {
    val k = kotlin.math.tan(degrees * kotlin.math.PI.toFloat() / 180f)
    transform(
        Matrix().apply {
            values[Matrix.SkewX] = k
        },
    )
}

private val BORDER = 1.dp
private const val PRESSED_SCALE = 0.94f
private const val SHINE_WIDTH = 0.3f
private const val SHINE_FROM = -1.2f
private const val SHINE_TO = 3.6f
private const val SHINE_HOLD = 0.8f
private const val SHINE_SKEW = -20f
private val SMALL_HEIGHT = 36.dp
private val LARGE_HEIGHT = 52.dp
private val ICON = 20.dp // css literal: bundle.js `Icon size: size === 'lg' ? 24 : 20`
private val LARGE_ICON = 24.dp // css literal: bundle.js `Icon size: size === 'lg' ? 24 : 20`
