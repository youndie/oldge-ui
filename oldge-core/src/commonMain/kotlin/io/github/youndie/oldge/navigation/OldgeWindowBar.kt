package io.github.youndie.oldge.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.OldgeOrbButton
import io.github.youndie.oldge.actions.OldgeOrbSize
import io.github.youndie.oldge.actions.OldgeOrbTone
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CSS_BLUR_TO_TEXT_SHADOW
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.CssCorners
import io.github.youndie.oldge.material.bezel
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.theme.LocalOldgeTextStyle
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText

/** A window button in an [OldgeWindowBar]: a small orb named by [label]. */
@Immutable
public data class OldgeWindowAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
    val tone: OldgeOrbTone = OldgeOrbTone.Bezel,
)

/**
 * The top of a screen — the design system's `WindowBar`: the skin's bezel with the [title], like a
 * window's title bar, and small orb [actions] on the right.
 *
 * A root screen shows its [icon]; a nested one takes [onBack] instead, a back orb named [backLabel].
 * [lead] replaces the icon (an Avatar in a chat); [subtitle] is a line under the title; [center] is
 * a badge or a compact switch between the title and the actions.
 *
 * Its README's rules:
 * - the top corners are `radius-xl`, so the screen reads as a window; [square] drops them where the
 *   bar meets the system status bar;
 * - the title is the `title` style with a text shadow;
 * - no search or tabs inside the bar — sections are CategoryTabs' — and at most three actions;
 * - [overlay] is a transparent bar under a dark veil for full-screen media: no frame, white text
 *   with a shadow.
 */
@Composable
public fun OldgeWindowBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    onBack: (() -> Unit)? = null,
    backLabel: String = "Назад",
    lead: (@Composable () -> Unit)? = null,
    center: (@Composable () -> Unit)? = null,
    actions: List<OldgeWindowAction> = emptyList(),
    square: Boolean = false,
    overlay: Boolean = false,
) {
    require(actions.size <= MAX_ACTIONS) { "a window bar takes at most $MAX_ACTIONS actions, got ${actions.size}" }
    val c = OldgeTheme.colors
    val s = OldgeTheme.spacing
    val ink = if (overlay) Color.White else c.onBezel
    val frame =
        if (overlay) {
            // `.og-winbar--overlay`: `linear-gradient(rgba(0,0,0,0.62), rgba(0,0,0,0))`, square, 64 px.
            Modifier.cssBox(0.dp, CssBackground.Linear(listOf(0f to VEIL, 1f to Color.Transparent)))
        } else {
            Modifier.bezel(if (square) 0.dp else OldgeRadii.xl, CssCorners.Top)
        }
    Row(
        modifier
            .fillMaxWidth()
            .then(frame)
            // `.og-winbar` is `content-box`: the overlay's 8 px under its 64 px minimum adds to it, 72 in
            // all with the content centred in the top 64, as Chrome lays it out (B-57).
            .padding(bottom = if (overlay) s.space2 else 0.dp)
            .defaultMinSize(minHeight = if (overlay) OVERLAY_HEIGHT else HEIGHT)
            .padding(start = s.space3, end = s.space1),
        horizontalArrangement = Arrangement.spacedBy(s.space1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            // `.og-winbar__lead { margin-left: calc(-1 * space-2) }`.
            Box(Modifier.pullStart(s.space2)) {
                OldgeOrbButton(OldgeIcons.Back, backLabel, onBack, size = OldgeOrbSize.Small)
            }
        }
        Row(
            Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(s.space2),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when {
                lead != null -> lead()
                onBack == null && icon != null -> OldgeIcon(icon, contentDescription = null, size = ICON, tint = ink)
            }
            Column(Modifier.weight(1f, fill = false)) {
                OldgeText(
                    title,
                    Modifier.semantics { heading() },
                    style =
                        OldgeTheme.type.title.copy(
                            color = ink,
                            shadow = if (overlay) OVERLAY_TITLE_SHADOW else TITLE_SHADOW,
                        ),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                )
                if (subtitle != null) OldgeText(subtitle, style = OldgeTheme.type.caption.copy(color = ink))
            }
        }
        if (center != null) {
            CompositionLocalProvider(LocalOldgeTextStyle provides OldgeTheme.type.body.copy(color = ink)) { center() }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            for (a in actions) {
                // `.og-winbar__actions .og-orb { margin-left: -6px }`: the orbs' 44 px targets overlap.
                Box(Modifier.pullStart(ACTION_OVERLAP)) {
                    OldgeOrbButton(a.icon, a.label, a.onClick, size = OldgeOrbSize.Small, tone = a.tone)
                }
            }
        }
    }
}

/** A negative start margin: the child is placed [by] earlier and takes that much less room. */
private fun Modifier.pullStart(by: Dp): Modifier =
    layout { measurable, constraints ->
        val p = measurable.measure(constraints)
        val pull = by.roundToPx()
        layout((p.width - pull).coerceAtLeast(0), p.height) { p.placeRelative(-pull, 0) }
    }

private const val MAX_ACTIONS = 3

/** `text-shadow: 0 1px 0 rgba(0,0,0,0.45)`. */
private val TITLE_SHADOW = Shadow(Color(0f, 0f, 0f, 0.45f), Offset(0f, 1f), 0f)

/** `.og-winbar--overlay .og-winbar__title { text-shadow: 0 1px 2px rgba(0,0,0,0.8) }`. */
private val OVERLAY_TITLE_SHADOW = Shadow(Color(0f, 0f, 0f, 0.8f), Offset(0f, 1f), 2 * CSS_BLUR_TO_TEXT_SHADOW)
private val VEIL = Color(0f, 0f, 0f, 0.62f)
private val HEIGHT = 56.dp // css literal: bundle.css `.og-winbar { min-height: 56px }`
private val OVERLAY_HEIGHT = 64.dp // css literal: bundle.css `.og-winbar--overlay { min-height: 64px }`
private val ICON = 24.dp // css literal: bundle.js `WindowBar`, `Icon size: 24`
private val ACTION_OVERLAP = 6.dp // css literal: bundle.css `.og-winbar__actions .og-orb { margin-left: -6px }`
