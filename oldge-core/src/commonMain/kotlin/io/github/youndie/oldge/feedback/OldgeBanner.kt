package io.github.youndie.oldge.feedback

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.actions.OldgeButtonSize
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.theme.LocalOldgeTextStyle
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText

/** An action of an [OldgeBanner]: a small chrome button. */
@Immutable
public data class OldgeBannerAction(
    val label: String,
    val onClick: () -> Unit,
)

/**
 * A banner — the design system's `Banner`, Material's banner as the XP information strip: a yellow
 * band with the [icon], the [title] over [content], and [actions] under them on the right.
 * [boxed] makes it a plate of its own rather than a band across the width. Not [open], nothing is
 * drawn.
 *
 * Its README's rules: it is for a state that wants attention without blocking — little space, no
 * network; it slides down from the top; its actions are chrome buttons only, since the skin's
 * colours do not read on the yellow, so the banner draws them itself from [actions].
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun OldgeBanner(
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: ImageVector = OldgeIcons.Info,
    boxed: Boolean = false,
    actions: List<OldgeBannerAction> = emptyList(),
    open: Boolean = true,
    content: (@Composable () -> Unit)? = null,
) {
    if (!open) return
    val c = OldgeTheme.colors
    val s = OldgeTheme.spacing
    val motion = OldgeTheme.motion
    val slide = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) { slide.animateTo(1f, tween(motion.slow, easing = motion.spring)) }
    val ink = c.onBalloon
    val frame =
        if (boxed) {
            Modifier
                .cssBox(
                    OldgeRadii.md,
                    CssBackground.Solid(c.balloon),
                    BORDER,
                    ink,
                    OldgeTheme.shadows.balloon,
                ).padding(BORDER)
        } else {
            // A band: `border-width: 0 0 1px`, the rule along its bottom only.
            Modifier
                .drawBehind {
                    drawRect(c.balloon)
                    drawRect(ink, Offset(0f, size.height - BORDER.toPx()), Size(size.width, BORDER.toPx()))
                }.padding(bottom = BORDER)
        }
    Column(
        modifier
            .fillMaxWidth()
            // `og-banner-in`: from its own height above and transparent, over `dur-slow` on the spring.
            .graphicsLayer {
                translationY = -size.height * (1 - slide.value)
                alpha = slide.value.coerceIn(0f, 1f)
            }.semantics { liveRegion = LiveRegionMode.Polite }
            .then(frame)
            .padding(horizontal = s.space4, vertical = s.space3),
        verticalArrangement = Arrangement.spacedBy(s.space2),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(s.space3)) {
            OldgeIcon(icon, contentDescription = null, size = ICON, tint = c.balloonMark)
            Column(Modifier.weight(1f)) {
                // `.og-banner`: 0.8125rem / 1.125rem; the title bold at 0.875rem on the same line height.
                val text = OldgeTheme.type.label.copy(fontWeight = FontWeight.Normal, lineHeight = LINE, color = ink)
                if (title !=
                    null
                ) {
                    OldgeText(title, style = text.copy(fontSize = TITLE_SIZE, fontWeight = FontWeight.Bold))
                }
                if (content != null) CompositionLocalProvider(LocalOldgeTextStyle provides text) { content() }
            }
        }
        if (actions.isNotEmpty()) {
            FlowRow(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(s.space2, Alignment.End),
                verticalArrangement = Arrangement.spacedBy(s.space2),
            ) { for (a in actions) OldgeButton(a.label, a.onClick, size = OldgeButtonSize.Small) }
        }
    }
}

private val BORDER = 1.dp
private val ICON = 20.dp // css literal: bundle.js `Banner`, `Icon size: 20`
private val LINE = 18.sp // css literal: bundle.css `.og-banner { line-height: 1.125rem }`
private val TITLE_SIZE = 14.sp // css literal: bundle.css `.og-banner__title { font-size: 0.875rem }`
