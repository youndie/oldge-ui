package io.github.youndie.oldge.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.feedback.OldgeBadge
import io.github.youndie.oldge.feedback.OldgeBadgeTone
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.CssCorners
import io.github.youndie.oldge.material.bezel
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.grain
import io.github.youndie.oldge.material.lerpShadows
import io.github.youndie.oldge.material.premultipliedLerp
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.press.oldgeScrimTaps
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.FontCoverage
import io.github.youndie.oldge.type.OldgeScriptText
import io.github.youndie.oldge.type.OldgeText
import kotlin.math.min
import kotlin.math.roundToInt

/** A line of an [OldgeNavDrawer]: a destination, or a section title over the ones that follow. */
public sealed interface OldgeDrawerEntry {
    /** A destination, with an optional count [badge]. */
    @Immutable
    public data class Item(
        val id: String,
        val icon: ImageVector,
        val label: String,
        val badge: String? = null,
    ) : OldgeDrawerEntry

    /** A section title («Сервис»), in the pixel face. */
    @Immutable
    public data class Section(
        val title: String,
    ) : OldgeDrawerEntry
}

/**
 * The side menu — the design system's `NavDrawer`, the equivalent of Material's navigation drawer:
 * a header in the skin's frame with the [avatar], [title] and [subtitle], the [entries] under it,
 * and the [value] destination as a pill. A tap calls [onChange] with its id.
 *
 * [inline] is the drawer alone, sized by its parent. Otherwise it fills its parent — put it over the
 * screen in a `Box` — with a scrim that calls [onClose] when tapped, and it is not drawn while
 * [open] is false.
 *
 * Its README's rules: the drawer slides in from the left on the spring and its items follow in a
 * stagger; for three to five main sections the BottomNav is the better choice, and a drawer is for
 * when there are more.
 */
@Composable
public fun OldgeNavDrawer(
    entries: List<OldgeDrawerEntry>,
    value: String,
    onChange: (String) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    avatar: (@Composable () -> Unit)? = null,
    label: String = "Меню",
    inline: Boolean = false,
    open: Boolean = true,
    onClose: () -> Unit = {},
) {
    if (!open) return
    if (inline) {
        Drawer(entries, value, onChange, title, subtitle, avatar, label, modifier.fillMaxHeight(), slideIn = false)
        return
    }
    val motion = OldgeTheme.motion
    val fade = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) { fade.animateTo(1f, tween(motion.base, easing = motion.out)) }
    Box(modifier.fillMaxSize()) {
        // `.og-drawer-scrim`: `rgba(0,0,0,0.5)` over the screen, fading in; a tap closes.
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = fade.value }
                .drawBehind { drawRect(SCRIM) }
                .oldgeScrimTaps(onClose),
        )
        Drawer(entries, value, onChange, title, subtitle, avatar, label, Modifier.fillMaxHeight(), slideIn = true)
    }
}

/**
 * `.og-drawer`: `min(304px, 86vw)` wide, `body-hi` to `ground` under a `glow` ellipse at its bottom
 * right and the grain, `radius-xl` on its right corners, `shadow-window`; it slides in from the left
 * (`og-drawer-in`, `dur-slow` on the spring).
 */
@Composable
private fun Drawer(
    entries: List<OldgeDrawerEntry>,
    value: String,
    onChange: (String) -> Unit,
    title: String,
    subtitle: String?,
    avatar: (@Composable () -> Unit)?,
    label: String,
    modifier: Modifier,
    slideIn: Boolean,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val slide = remember { Animatable(if (slideIn && !motion.reduced) 0f else 1f) }
    LaunchedEffect(Unit) { slide.animateTo(1f, tween(motion.slow, easing = motion.spring)) }
    val radius = OldgeRadii.xl
    Column(
        modifier
            // `min(304px, 86vw)`: 86 % of the parent (the viewport a composable can see) or 304, the
            // smaller. One measure: a `widthIn(max)` after a `fillMaxWidth` cannot undercut its minimum.
            .layout { measurable, constraints ->
                val w = min((constraints.maxWidth * WIDTH_OF_SCREEN).roundToInt(), WIDTH.roundToPx())
                val p = measurable.measure(constraints.copy(minWidth = w, maxWidth = w))
                layout(p.width, p.height) { p.place(0, 0) }
            }.graphicsLayer { translationX = -size.width * (1 - slide.value) }
            .semantics { contentDescription = label }
            .cssBox(
                radius,
                CssBackground.Linear(listOf(0f to c.bodyHi, 1f to c.ground)),
                shadows = OldgeTheme.shadows.window,
                corners = CssCorners.End,
            ).clip(RoundedCornerShape(topEnd = radius, bottomEnd = radius))
            .drawBehind {
                // `radial-gradient(120% 60% at 100% 100%, glow, transparent 60%)`: an ellipse 120 % of
                // the drawer wide and 60 % high round its bottom-right corner, as a scaled circle.
                val rx = size.width * GLOW_RX
                val ry = size.height * GLOW_RY
                val corner = Offset(size.width, size.height)
                withTransform({ scale(1f, ry / rx, pivot = corner) }) {
                    drawRect(
                        Brush.radialGradient(
                            0f to c.glow,
                            GLOW_FADE to c.glow.copy(alpha = 0f),
                            center = corner,
                            radius = rx,
                        ),
                        topLeft = Offset(0f, size.height - size.height * rx / ry),
                        size = Size(size.width, size.height * rx / ry),
                    )
                }
            }.grain(),
    ) {
        Head(title, subtitle, avatar)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(OldgeTheme.spacing.space2),
            verticalArrangement = Arrangement.spacedBy(ITEM_GAP),
        ) {
            var n = 0
            for ((i, e) in entries.withIndex()) {
                when (e) {
                    is OldgeDrawerEntry.Section -> {
                        key("s$i") { Section(e.title) }
                    }

                    is OldgeDrawerEntry.Item -> {
                        val order = n++
                        key(e.id) { Item(e, e.id == value, order) { onChange(e.id) } }
                    }
                }
            }
        }
    }
}

/** `.og-drawer__head`: the skin's frame, padded 20 16 16, the avatar and the name 12 px apart. */
@Composable
private fun Head(
    title: String,
    subtitle: String?,
    avatar: (@Composable () -> Unit)?,
) {
    val c = OldgeTheme.colors
    val s = OldgeTheme.spacing
    Row(
        Modifier
            .fillMaxWidth()
            .bezel(0.dp)
            .padding(start = s.space4, end = s.space4, top = s.space5, bottom = s.space4),
        horizontalArrangement = Arrangement.spacedBy(s.space3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        avatar?.invoke()
        Column {
            // `.og-drawer__name`: the title face at 1.0625rem / 1.375rem with `0 1px 0 rgba(0,0,0,.45)`.
            OldgeText(
                title,
                Modifier.semantics { heading() },
                style = OldgeTheme.type.title.copy(color = c.onBezel, shadow = NAME_SHADOW),
            )
            if (subtitle != null) OldgeText(subtitle, style = OldgeTheme.type.caption.copy(color = c.onBezel))
        }
    }
}

/** `.og-drawer__section`: `pixel-tag` upper case in `ink-muted`, padded 12 12 4. */
@Composable
private fun Section(title: String) {
    val s = OldgeTheme.spacing
    val type = OldgeTheme.type
    OldgeScriptText(
        title.uppercase(),
        type.pixelTag.copy(color = OldgeTheme.colors.inkMuted),
        type.families.pixelCompanion,
        FontCoverage.silkscreen,
        Modifier.padding(start = s.space3, end = s.space3, top = s.space3, bottom = s.space1),
    )
}

/**
 * `.og-drawer__item`: a 48 px pill in the body style, the 22 px icon, the label and a count badge
 * 12 px apart inside a 1 px border that is clear until it is current. Current: `pill-hi` to
 * `pill-lo` in `on-pill`, an `edge` border, bold, `shadow-raised`, changing over `dur-base`. Each
 * slides in from 24 px left (`og-slide-r`, `dur-slow` on the spring) 80 ms plus 35 ms per item
 * after the drawer.
 */
@Composable
private fun Item(
    item: OldgeDrawerEntry.Item,
    current: Boolean,
    order: Int,
    onClick: () -> Unit,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val slide = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) {
        slide.animateTo(
            1f,
            tween(
                motion.slow,
                delayMillis = if (motion.reduced) 0 else STAGGER_FROM + STAGGER * order,
                easing = motion.spring,
            ),
        )
    }
    val on by animateFloatAsState(
        if (current) 1f else 0f,
        tween(motion.base, easing = motion.out),
        label = "oldge drawer item",
    )
    val clear = Color.Transparent
    val ink = premultipliedLerp(c.ink, c.onPill, on)
    Row(
        Modifier
            .graphicsLayer {
                translationX = -SLIDE.toPx() * (1 - slide.value)
                alpha = slide.value.coerceIn(0f, 1f)
            }.fillMaxWidth()
            .defaultMinSize(minHeight = ITEM_HEIGHT)
            .cssBox(
                OldgeRadii.pill,
                CssBackground.Linear(
                    listOf(
                        0f to premultipliedLerp(clear, c.pillHi, on),
                        1f to premultipliedLerp(clear, c.pillLo, on),
                    ),
                ),
                BORDER,
                premultipliedLerp(clear, c.edge, on),
                lerpShadows(OldgeTheme.shadows.raised.map { it.copy(color = clear) }, OldgeTheme.shadows.raised, on),
            ).clickable(
                remember {
                    MutableInteractionSource()
                },
                OldgeIndication(RoundedCornerShape(OldgeRadii.pill)),
                role = Role.Button,
                onClick = onClick,
            ).semantics { selected = current }
            .padding(horizontal = BORDER + OldgeTheme.spacing.space3),
        horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OldgeIcon(item.icon, contentDescription = null, size = ICON, tint = ink)
        OldgeText(
            item.label,
            Modifier.weight(1f),
            style =
                OldgeTheme.type.body.copy(
                    color = ink,
                    fontWeight = if (current) FontWeight.Bold else FontWeight.Normal,
                ),
        )
        if (item.badge != null) OldgeBadge(item.badge, tone = OldgeBadgeTone.Accent, count = true)
    }
}

private const val WIDTH_OF_SCREEN = 0.86f
private const val GLOW_RX = 1.2f
private const val GLOW_RY = 0.6f
private const val GLOW_FADE = 0.6f
private const val STAGGER_FROM = 80 // css literal: bundle.css `.og-drawer__item { animation-delay: … + 80ms }`
private const val STAGGER = 35 // css literal: bundle.css `.og-drawer__item { animation-delay: var(--i) * 35ms … }`
private val SCRIM = Color(0f, 0f, 0f, 0.5f)
private val NAME_SHADOW = Shadow(Color(0f, 0f, 0f, 0.45f), Offset(0f, 1f), 0f)
private val BORDER = 1.dp
private val WIDTH = 304.dp // css literal: bundle.css `.og-drawer { width: min(304px, 86vw) }`
private val ITEM_HEIGHT = 48.dp // css literal: bundle.css `.og-drawer__item { min-height: 48px }`
private val ITEM_GAP = 2.dp // css literal: bundle.css `.og-drawer__list { gap: 2px }`
private val ICON = 22.dp // css literal: bundle.js `NavDrawer`, `Icon size: 22`
private val SLIDE = 24.dp // css literal: bundle.css `@keyframes og-slide-r { from { translateX(-24px) } }`
