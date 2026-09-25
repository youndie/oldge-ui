package io.github.youndie.oldge.containers

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.dialog
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.CssCorners
import io.github.youndie.oldge.material.bezel
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.windowBody
import io.github.youndie.oldge.press.oldgeScrimTaps
import io.github.youndie.oldge.theme.LocalOldgeTextStyle
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii

/**
 * The bottom sheet — the design system's `BottomSheet`, the equivalent of Material's: the skin's
 * frame with its tab, the [title] and a «Закрыть» orb ([onClose]), and the skin's ground inside with
 * [content].
 *
 * [inline] shows it in place with no scrim, for documentation and embedding. Otherwise it fills its
 * parent — put it over the screen in a `Box` — with a scrim a tap on which calls [onClose], the
 * sheet at the bottom, and says it is a dialog. Not [open], nothing is drawn.
 *
 * Its README's rules: it rises from below and overshoots; a tap on the scrim closes it; inside is a
 * list of actions (`OldgeList(bare = true)`) or a short form.
 */
@Composable
public fun OldgeBottomSheet(
    title: String,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null,
    inline: Boolean = false,
    open: Boolean = true,
    content: @Composable () -> Unit,
) {
    if (!open) return
    if (inline) {
        Sheet(title, onClose, content, modifier, overlay = false, bodyMax = Dp.Unspecified)
        return
    }
    val motion = OldgeTheme.motion
    val fade = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) { fade.animateTo(1f, tween(motion.base, easing = motion.out)) }
    BoxWithConstraints(modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        // `.og-sheet-scrim`: `rgba(0,0,0,0.5)`, fading in; a tap closes.
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = fade.value }
                .drawBehind { drawRect(SCRIM) }
                .oldgeScrimTaps { onClose?.invoke() },
        )
        // `.og-sheet__body { max-height: 70vh }`: 70 % of the screen the sheet is over.
        Sheet(title, onClose, content, Modifier, overlay = true, bodyMax = maxHeight * BODY_SHARE)
    }
}

/**
 * `.og-sheet`: the bezel with `radius-xl` on its top corners, `shadow-bezel` and `shadow-window`, at
 * most 480 px wide and centred, padded 0 6 6; over a screen it rises from its own height below
 * (`og-sheet-in`, `dur-slow` on the spring, which overshoots).
 */
@Composable
private fun Sheet(
    title: String,
    onClose: (() -> Unit)?,
    content: @Composable () -> Unit,
    modifier: Modifier,
    overlay: Boolean,
    bodyMax: Dp,
) {
    val c = OldgeTheme.colors
    val s = OldgeTheme.spacing
    val motion = OldgeTheme.motion
    val rise = remember { Animatable(if (overlay && !motion.reduced) 0f else 1f) }
    LaunchedEffect(Unit) { rise.animateTo(1f, tween(motion.slow, easing = motion.spring)) }
    Column(
        modifier
            .widthIn(max = WIDTH)
            .fillMaxWidth()
            .graphicsLayer { translationY = size.height * (1 - rise.value) }
            .semantics {
                if (overlay) {
                    dialog()
                    paneTitle = title
                }
            }.cssBox(
                OldgeRadii.xl,
                CssBackground.Solid(Color.Transparent),
                shadows = OldgeTheme.shadows.window,
                corners = CssCorners.Top,
            ).bezel(OldgeRadii.xl, CssCorners.Top)
            .padding(start = FRAME, end = FRAME, bottom = FRAME),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // `.og-sheet__grab`: a 44 × 6 chrome tab, 8 px from the top and 2 px above the bar.
        Box(
            Modifier
                .padding(top = GRAB_TOP, bottom = GRAB_BOTTOM)
                .size(GRAB_WIDTH, GRAB_HEIGHT)
                .cssBox(
                    OldgeRadii.pill,
                    CssBackground.Linear(listOf(0f to c.chromeHi, 1f to c.chromeLo)),
                    shadows = OldgeTheme.shadows.orb,
                ),
        )
        WindowBar(title, null, onClose, BAR_HEIGHT)
        Column(
            Modifier
                .fillMaxWidth()
                .then(if (bodyMax.isSpecified) Modifier.heightIn(max = bodyMax) else Modifier)
                .clip(RoundedCornerShape(OldgeRadii.lg))
                .windowBody(GLOW_HEIGHT)
                .cssBox(OldgeRadii.lg, CssBackground.Solid(Color.Transparent), shadows = OldgeTheme.shadows.sunken)
                .verticalScroll(rememberScrollState())
                .padding(start = s.space4, end = s.space4, top = s.space3, bottom = s.space4),
            verticalArrangement = Arrangement.spacedBy(s.space3),
        ) {
            CompositionLocalProvider(
                LocalOldgeTextStyle provides OldgeTheme.type.body.copy(color = c.ink),
            ) { content() }
        }
    }
}

private const val GLOW_HEIGHT = 0.8f
private const val BODY_SHARE = 0.7f
private val SCRIM = Color(0f, 0f, 0f, 0.5f)
private val WIDTH = 480.dp // css literal: bundle.css `.og-sheet { max-width: 480px }`
private val FRAME = 6.dp // css literal: bundle.css `.og-sheet { padding: 0 6px 6px }`
private val GRAB_WIDTH = 44.dp // css literal: bundle.css `.og-sheet__grab { width: 44px }`
private val GRAB_HEIGHT = 6.dp // css literal: bundle.css `.og-sheet__grab { height: 6px }`
private val GRAB_TOP = 8.dp // css literal: bundle.css `.og-sheet__grab { margin: 8px auto 2px }`
private val GRAB_BOTTOM = 2.dp // css literal: bundle.css `.og-sheet__grab { margin: 8px auto 2px }`
private val BAR_HEIGHT = 44.dp // css literal: bundle.css `.og-sheet__bar { min-height: 44px }`
