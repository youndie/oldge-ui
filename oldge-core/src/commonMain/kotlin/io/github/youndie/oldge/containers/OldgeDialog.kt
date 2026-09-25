package io.github.youndie.oldge.containers

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.dialog
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.OldgeOrbButton
import io.github.youndie.oldge.actions.OldgeOrbSize
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.bezel
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.windowBody
import io.github.youndie.oldge.press.oldgeScrimTaps
import io.github.youndie.oldge.theme.LocalOldgeTextStyle
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText

/**
 * A dialog window — the design system's `Dialog`: the skin's frame with the [title] and an orb
 * «Закрыть» ([onClose]), the body on the skin's ground with [content] and the [actions] at the
 * bottom right.
 *
 * [modal] puts it over the screen, centred on a scrim, and says it is a dialog; it then fills its
 * parent, so it goes over the screen in a `Box`. Without it, it is the window alone, in place. Not
 * [open], nothing is drawn.
 *
 * Its README's rules:
 * - the title is a question or a fact, and the body one or two sentences;
 * - the actions run cancel first, then the primary one;
 * - a destructive action is the primary one with the `trash` icon and the verb «Удалить», not «OK»;
 * - moving the focus on opening is the app's.
 *
 * The window opens on the spring from under its centre.
 */
@Composable
public fun OldgeDialog(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClose: (() -> Unit)? = null,
    modal: Boolean = false,
    open: Boolean = true,
    actions: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    if (!open) return
    if (!modal) {
        Window(title, icon, onClose, actions, content, modifier, modal = false)
        return
    }
    val motion = OldgeTheme.motion
    val fade = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) { fade.animateTo(1f, tween(motion.base, easing = motion.out)) }
    // `.og-scrim`: `rgba(0,0,0,0.55)` over the screen, the window in its middle, 16 px clear of the
    // edges. The scrim is the window's sibling: a dialog cannot sit inside a clickable node.
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = fade.value }
                .drawBehind { drawRect(SCRIM) }
                // It takes the taps under it; a modal closes only by its own buttons.
                .oldgeScrimTaps {},
        )
        Window(title, icon, onClose, actions, content, Modifier.padding(OldgeTheme.spacing.space4), modal = true)
    }
}

/**
 * `.og-dialog`: the bezel, `radius-xl`, `shadow-bezel` and `shadow-window`, at most 358 px wide and
 * padded 0 6 6; `og-window-in` opens it from 0.82 and 14 px down, over `dur-slow` on the spring.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Window(
    title: String,
    icon: ImageVector?,
    onClose: (() -> Unit)?,
    actions: (@Composable () -> Unit)?,
    content: @Composable () -> Unit,
    modifier: Modifier,
    modal: Boolean,
) {
    val c = OldgeTheme.colors
    val s = OldgeTheme.spacing
    val motion = OldgeTheme.motion
    val open = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) { open.animateTo(1f, tween(motion.slow, easing = motion.spring)) }
    Column(
        modifier
            .widthIn(max = WIDTH)
            .fillMaxWidth()
            .graphicsLayer {
                val k = open.value
                val scale = FROM + (1 - FROM) * k
                scaleX = scale
                scaleY = scale
                translationY = RISE.toPx() * (1 - k)
                alpha = k.coerceIn(0f, 1f)
            }.semantics {
                if (modal) {
                    dialog()
                    paneTitle = title
                }
            }.cssBox(OldgeRadii.xl, CssBackground.Solid(Color.Transparent), shadows = OldgeTheme.shadows.window)
            .bezel(OldgeRadii.xl)
            .padding(start = FRAME, end = FRAME, bottom = FRAME),
    ) {
        WindowBar(title, icon, onClose, BAR_HEIGHT)
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(OldgeRadii.lg))
                .windowBody(GLOW_HEIGHT)
                .cssBox(OldgeRadii.lg, CssBackground.Solid(Color.Transparent), shadows = OldgeTheme.shadows.sunken)
                .padding(s.space4),
            verticalArrangement = Arrangement.spacedBy(s.space4),
        ) {
            CompositionLocalProvider(
                LocalOldgeTextStyle provides OldgeTheme.type.body.copy(color = c.ink),
            ) { content() }
            if (actions != null) {
                // `.og-dialog__actions`: at the end, 8 px apart, wrapping rather than squeezing.
                FlowRow(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(s.space2, Alignment.End),
                    verticalArrangement = Arrangement.spacedBy(s.space2),
                ) { actions() }
            }
        }
    }
}

/**
 * The title bar the Dialog and the BottomSheet share: the [icon], the [title] in the title face with
 * `text-shadow: 0 1px 0 rgba(0,0,0,.45)` and the small «Закрыть» orb, 12 px in, [height] high.
 */
@Composable
internal fun WindowBar(
    title: String,
    icon: ImageVector?,
    onClose: (() -> Unit)?,
    height: androidx.compose.ui.unit.Dp,
) {
    val c = OldgeTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = height)
            .padding(start = OldgeTheme.spacing.space3),
        horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) OldgeIcon(icon, contentDescription = null, size = ICON, tint = c.onBezel)
        OldgeText(
            title,
            Modifier.weight(1f).semantics { heading() },
            style = OldgeTheme.type.title.copy(color = c.onBezel, shadow = TITLE_SHADOW),
        )
        if (onClose != null) OldgeOrbButton(OldgeIcons.Close, "Закрыть", onClose, size = OldgeOrbSize.Small)
    }
}

private const val FROM = 0.82f
private const val GLOW_HEIGHT = 0.8f
private val SCRIM = Color(0f, 0f, 0f, 0.55f)
private val TITLE_SHADOW = Shadow(Color(0f, 0f, 0f, 0.45f), Offset(0f, 1f), 0f)
private val WIDTH = 358.dp // css literal: bundle.css `.og-dialog { max-width: 358px }`
private val FRAME = 6.dp // css literal: bundle.css `.og-dialog { padding: 0 6px 6px }`
private val BAR_HEIGHT = 48.dp // css literal: bundle.css `.og-dialog__bar { min-height: 48px }`
private val ICON = 22.dp // css literal: bundle.js `Dialog`, `Icon size: 22`
private val RISE = 14.dp // css literal: bundle.css `@keyframes og-window-in { 0% { translateY(14px) } }`
