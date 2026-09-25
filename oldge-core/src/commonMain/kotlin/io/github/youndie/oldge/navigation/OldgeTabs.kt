package io.github.youndie.oldge.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.feedback.OldgeBadge
import io.github.youndie.oldge.feedback.OldgeBadgeTone
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.theme.LocalOldgeTextStyle
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText

/** A tab of an [OldgeTabs]: its [label], an optional 16 px [icon] and a count [badge]. */
@Immutable
public data class OldgeTab(
    val id: String,
    val label: String,
    val icon: ImageVector? = null,
    val badge: String? = null,
)

/**
 * Tabs in the style of an XP property sheet — the design system's `Tabs`, the equivalent of Material
 * tabs: the [value] tab is raised and runs into the panel under it, with a strip of the accent on
 * top; [content] is the current tab's page, drawn in that panel. [label] names the tab list.
 *
 * Its README's rules: two to five short tabs — more is CategoryTabs' job. The accent strip shoots
 * out from the middle with a bounce, and the panel fades in.
 *
 * Only the current tab is in the Tab order, as bundle.js's roving `tabIndex` makes it; ← and → move
 * to the tab beside it, which bundle.js leaves out and the ARIA tabs pattern expects.
 */
@Composable
public fun OldgeTabs(
    items: List<OldgeTab>,
    value: String,
    onChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
) {
    require(items.size in MIN_ITEMS..MAX_ITEMS) { "tabs take $MIN_ITEMS to $MAX_ITEMS tabs, got ${items.size}" }
    val c = OldgeTheme.colors
    val s = OldgeTheme.spacing
    val focus = remember(items.size) { List(items.size) { FocusRequester() } }
    val at = items.indexOfFirst { it.id == value }
    // A tab reached by an arrow key takes focus once it is current, which is what lets it be focused.
    val moved = remember { mutableStateOf(false) }
    LaunchedEffect(value) {
        if (moved.value && at >= 0) focus[at].requestFocus()
        moved.value = false
    }
    Column(modifier) {
        val edge = c.edge
        Row(
            Modifier
                .fillMaxWidth()
                .semantics { contentDescription = label }
                .onPreviewKeyEvent { e ->
                    val step =
                        when (e.key) {
                            Key.DirectionRight -> 1
                            Key.DirectionLeft -> -1
                            else -> 0
                        }
                    if (step == 0 || e.type != KeyEventType.KeyDown || at < 0) return@onPreviewKeyEvent false
                    val next = (at + step).mod(items.size)
                    moved.value = true
                    onChange(items[next].id)
                    true
                }
                // `.og-tabs__list { border-bottom: 1px solid edge }`, under the tabs: the current one
                // covers it.
                .drawBehind { drawRect(edge, Offset(0f, size.height - BORDER.toPx()), Size(size.width, BORDER.toPx())) }
                .horizontalScroll(rememberScrollState())
                .padding(start = s.space2, end = s.space2, bottom = BORDER),
            horizontalArrangement = Arrangement.spacedBy(TAB_GAP),
            verticalAlignment = Alignment.Bottom,
        ) {
            items.forEachIndexed { i, it ->
                key(it.id) { Tab(it, it.id == value, focus[i]) { onChange(it.id) } }
            }
        }
        if (content != null) {
            key(value) { Panel(content) }
        }
    }
}

/**
 * `.og-tab`: 0.875rem / 1.125rem in `ink-muted` on `surface-2`, 40 px high and 4 px below the top, a
 * 1 px `edge` frame open at the bottom with 6 px top corners. Current, it is 44 px high on
 * `surface`, bold in `ink`, and reaches 1 px down over the list's border (`margin-bottom: -1px`); the
 * accent strip over its top edge grows from the middle on the bounce.
 */
@Composable
private fun Tab(
    tab: OldgeTab,
    current: Boolean,
    focus: FocusRequester,
    onClick: () -> Unit,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val height by animateDpAsState(
        if (current) TAB_CURRENT else TAB,
        tween(motion.base, easing = motion.spring),
        label = "oldge tab height",
    )
    val strip by animateFloatAsState(
        if (current) 1f else 0f,
        tween(motion.base, easing = motion.bounce),
        label = "oldge tab strip",
    )
    val ground = if (current) c.surface else c.surface2
    val edge = c.edge
    val accent = c.accentHi
    val shape = RoundedCornerShape(topStart = TAB_RADIUS, topEnd = TAB_RADIUS)
    Row(
        Modifier
            .layout { measurable, constraints ->
                // `margin-top: 4px` on a tab at rest, which makes the list 44 px inside its border even
                // though the current tab is 44 high; current, `margin-bottom: -1px`, so it takes a
                // pixel less than it draws and starts 1 px down.
                val p = measurable.measure(constraints)
                val above = if (current) 0 else TAB_TOP.roundToPx()
                val below = if (current) BORDER.roundToPx() else 0
                layout(p.width, p.height + above - below) { p.place(0, above) }
            }.focusRequester(focus)
            .focusProperties { canFocus = current }
            .selectable(
                current,
                remember {
                    MutableInteractionSource()
                },
                OldgeIndication(shape),
                role = Role.Tab,
                onClick = onClick,
            ).heightIn(min = height)
            .drawWithContent {
                val b = BORDER.toPx()
                val r = TAB_RADIUS.toPx()
                val round = CornerRadius(r, r)
                val outer =
                    RoundRect(0f, 0f, size.width, size.height, round, round, CornerRadius.Zero, CornerRadius.Zero)
                val inner = CornerRadius(r - b, r - b)
                val fill =
                    RoundRect(b, b, size.width - b, size.height, inner, inner, CornerRadius.Zero, CornerRadius.Zero)
                drawPath(Path().apply { addRoundRect(outer) }, edge)
                drawPath(Path().apply { addRoundRect(fill) }, ground)
                drawContent()
                // `::before`: over the border box's top edge, 3 px high, `scaleX` from the middle.
                if (strip > 0f) {
                    scale(strip, 1f, Offset(size.width / 2, 0f)) {
                        val band =
                            RoundRect(
                                0f,
                                0f,
                                size.width,
                                STRIP.toPx(),
                                round,
                                round,
                                CornerRadius.Zero,
                                CornerRadius.Zero,
                            )
                        drawPath(Path().apply { addRoundRect(band) }, accent)
                    }
                }
            }
            // `box-sizing: border-box`: the 12 px padding is inside the 1 px frame on three sides.
            .padding(
                start = BORDER + OldgeTheme.spacing.space3,
                end = BORDER + OldgeTheme.spacing.space3,
                top = BORDER,
                bottom = if (current) BORDER else 0.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(TAB_INNER_GAP),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val ink = if (current) c.ink else c.inkMuted
        if (tab.icon != null) OldgeIcon(tab.icon, contentDescription = null, size = TAB_ICON, tint = ink)
        OldgeText(
            tab.label,
            style =
                OldgeTheme.type.body.copy(
                    fontSize = TAB_SIZE,
                    lineHeight = TAB_LINE,
                    color = ink,
                    fontWeight = if (current) FontWeight.Bold else FontWeight.Normal,
                ),
            softWrap = false,
            maxLines = 1,
        )
        if (tab.badge != null) OldgeBadge(tab.badge, tone = OldgeBadgeTone.Accent, count = true)
    }
}

/** `.og-tabs__panel`: `surface` in a 1 px `edge` frame open at the top, `radius-md` below, padded 12 16; `og-fade` in. */
@Composable
private fun Panel(content: @Composable () -> Unit) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val fade = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) { fade.animateTo(1f, tween(motion.base, easing = motion.out)) }
    val edge = c.edge
    val ground = c.surface
    Box(
        Modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = fade.value }
            .drawBehind {
                val b = BORDER.toPx()
                val r = OldgeRadii.md.toPx()
                val round = CornerRadius(r, r)
                val inner = CornerRadius(r - b, r - b)
                val outer =
                    RoundRect(0f, 0f, size.width, size.height, CornerRadius.Zero, CornerRadius.Zero, round, round)
                val fill =
                    RoundRect(
                        b,
                        0f,
                        size.width - b,
                        size.height - b,
                        CornerRadius.Zero,
                        CornerRadius.Zero,
                        inner,
                        inner,
                    )
                drawPath(Path().apply { addRoundRect(outer) }, edge)
                drawPath(Path().apply { addRoundRect(fill) }, ground)
            }.padding(start = BORDER, end = BORDER, bottom = BORDER)
            .padding(horizontal = OldgeTheme.spacing.space4, vertical = OldgeTheme.spacing.space3),
    ) {
        CompositionLocalProvider(LocalOldgeTextStyle provides OldgeTheme.type.body.copy(color = c.ink)) { content() }
    }
}

private const val MIN_ITEMS = 2
private const val MAX_ITEMS = 5
private val BORDER = 1.dp
private val TAB_TOP = 4.dp // css literal: bundle.css `.og-tab { margin-top: 4px }`
private val TAB_GAP = 2.dp // css literal: bundle.css `.og-tabs__list { gap: 2px }`
private val TAB_INNER_GAP = 6.dp // css literal: bundle.css `.og-tab { gap: 6px }`
private val TAB = 40.dp // css literal: bundle.css `.og-tab { min-height: 40px }`
private val TAB_CURRENT = 44.dp // css literal: bundle.css `.og-tab[aria-selected="true"] { min-height: 44px }`
private val TAB_RADIUS = 6.dp // css literal: bundle.css `.og-tab { border-radius: 6px 6px 0 0 }`
private val STRIP = 3.dp // css literal: bundle.css `.og-tab::before { height: 3px }`
private val TAB_ICON = 16.dp // css literal: bundle.js `Tabs`, `Icon size: 16`
private val TAB_SIZE = 14.sp // css literal: bundle.css `.og-tab { font: 400 0.875rem/1.125rem }`
private val TAB_LINE = 18.sp // css literal: bundle.css `.og-tab { font: 400 0.875rem/1.125rem }`
