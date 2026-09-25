package io.github.youndie.oldge.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.cssRoundRect
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText
import kotlinx.coroutines.delay

/** A line of an [OldgeMenu]: an [Item], or a [Divider] between groups. */
public sealed interface OldgeMenuEntry {
    /** A choice: its [id], [label], an optional [icon] and [hint] (a shortcut: «⌘O»), and [danger]. */
    public class Item(
        public val id: String,
        public val label: String,
        public val icon: ImageVector? = null,
        public val hint: String? = null,
        public val danger: Boolean = false,
    ) : OldgeMenuEntry

    /** The etched line between groups. */
    public data object Divider : OldgeMenuEntry
}

/** Which edge of the trigger a menu hangs from: [Start] (the default) or [End]. */
public enum class OldgeMenuAlign { Start, End }

/**
 * A drop-down menu — the design system's `Menu`, an XP menu: a coloured strip under the icons on the
 * left, and the item under the pointer or the focus in the `select` blue.
 *
 * The [trigger] is any button; it is given the toggle to call. The menu opens [expanded] under it,
 * from its [align] corner, and closes — through [onExpandedChange] — when an item is chosen (after
 * [onSelect]), on a tap outside, or on Escape. [label] names the menu for a screen reader.
 *
 * Its README's rules: a destructive item goes last, after a divider, marked [OldgeMenuEntry.Item.danger];
 * the menu opens from its corner on the spring and its items fade in one after another.
 */
@Composable
public fun OldgeMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    items: List<OldgeMenuEntry>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    align: OldgeMenuAlign = OldgeMenuAlign.Start,
    label: String? = null,
    trigger: @Composable (toggle: () -> Unit) -> Unit,
): Unit = OldgeMenuPopup(expanded, onExpandedChange, items, onSelect, modifier, align, label, strip = true, trigger)

/**
 * [OldgeMenu], with the [strip] under the icons optional. Select opens its options here without the
 * strip when none of them has an icon (B-63). The design system's Select is a native `<select>`,
 * so its list is the library's choice, while its Menu always has the strip.
 */
@Composable
internal fun OldgeMenuPopup(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    items: List<OldgeMenuEntry>,
    onSelect: (String) -> Unit,
    modifier: Modifier,
    align: OldgeMenuAlign,
    label: String?,
    strip: Boolean,
    trigger: @Composable (toggle: () -> Unit) -> Unit,
) {
    Box(modifier) {
        trigger { onExpandedChange(!expanded) }
        if (expanded) {
            val gap = with(LocalDensity.current) { GAP.roundToPx() }
            Popup(
                popupPositionProvider = remember(align, gap) { Below(align, gap) },
                onDismissRequest = { onExpandedChange(false) },
                properties = PopupProperties(focusable = true),
            ) {
                Panel(items, align, label, strip, dismiss = { onExpandedChange(false) }) { id ->
                    onSelect(id)
                    onExpandedChange(false)
                }
            }
        }
    }
}

/** `.og-menu { top: calc(100% + 4px); left: 0 }`, or `right: 0` for the end. */
private class Below(
    private val align: OldgeMenuAlign,
    private val gap: Int,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val x = if (align == OldgeMenuAlign.Start) anchorBounds.left else anchorBounds.right - popupContentSize.width
        return IntOffset(x, anchorBounds.bottom + gap)
    }
}

/**
 * `.og-menu`: at least 220 px wide, `pill-lo` for its first 40 px and `surface` after
 * (`linear-gradient(90deg, pill-lo 0 40px, surface 40px)`), a 1 px `edge` border, `radius-md`,
 * `shadow-window`, 3 px of padding above and below.
 */
@Composable
private fun Panel(
    items: List<OldgeMenuEntry>,
    align: OldgeMenuAlign,
    label: String?,
    strip: Boolean,
    dismiss: () -> Unit,
    pick: (String) -> Unit,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val open = remember { Animatable(if (motion.reduced) 1f else 0f) }
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        // The open menu takes the focus, so the keys — Escape, the arrows — reach it.
        focus.requestFocus()
        open.animateTo(1f, tween(motion.base, easing = motion.spring))
    }
    Column(
        Modifier
            .focusRequester(focus)
            .focusable()
            .onPreviewKeyEvent { e ->
                // `useOutside`: Escape closes the menu, as a tap outside does.
                if (e.key == Key.Escape && e.type == KeyEventType.KeyDown) {
                    dismiss()
                    true
                } else {
                    false
                }
            }.graphicsLayer {
                // `og-window-in` from the corner: 0.82 and 14 px low, clear, to rest.
                val p = open.value
                val s = WINDOW_FROM + (1 - WINDOW_FROM) * p
                scaleX = s
                scaleY = s
                translationY = WINDOW_DROP.toPx() * (1 - p)
                alpha = p.coerceIn(0f, 1f)
                transformOrigin = TransformOrigin(if (align == OldgeMenuAlign.Start) 0f else 1f, 0f)
            }.width(IntrinsicSize.Max)
            .widthIn(min = MIN_WIDTH)
            .cssBox(OldgeRadii.md, CssBackground.Solid(c.surface), BORDER, c.edge, OldgeTheme.shadows.window)
            .drawBehind {
                if (!strip) return@drawBehind
                // The strip, the background's first 40 px: laid out on the padding box and cut to its
                // rounded corners, as the background is.
                val b = BORDER.toPx()
                val padding =
                    cssRoundRect(
                        Offset(b, b),
                        Size(size.width - 2 * b, size.height - 2 * b),
                        OldgeRadii.md.toPx() - b,
                    )
                clipPath(Path().apply { addRoundRect(padding) }) {
                    drawRect(c.pillLo, Offset(b, b), Size(STRIP.toPx(), size.height - 2 * b))
                }
            }.padding(horizontal = BORDER, vertical = BORDER + PAD_Y)
            .semantics { if (label != null) contentDescription = label },
    ) {
        var index = 0
        for (entry in items) {
            when (entry) {
                is OldgeMenuEntry.Item -> MenuRow(entry, index++, strip, pick)
                OldgeMenuEntry.Divider -> Divider()
            }
        }
    }
}

/**
 * `.og-menu__item`: a 44 px row 3 px in from each side, radius 3, the icon centred in the 40 px strip,
 * the label in body text and the hint in 0.75rem `ink-muted` at the end; under the pointer, the focus
 * or the finger the row is `select` and everything on it `on-select`. It fades in 30 ms after the one
 * above it (`og-item-in`).
 */
@Composable
private fun MenuRow(
    item: OldgeMenuEntry.Item,
    index: Int,
    strip: Boolean,
    pick: (String) -> Unit,
) {
    val c = OldgeTheme.colors
    val type = OldgeTheme.type
    val motion = OldgeTheme.motion
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val focused by source.collectIsFocusedAsState()
    val pressed by source.collectIsPressedAsState()
    val lit = hovered || focused || pressed
    val show = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) {
        if (!motion.reduced) {
            delay(STAGGER_MS * index)
            show.animateTo(1f, tween(motion.base, easing = motion.out))
        }
    }
    val ink =
        if (lit) {
            c.onSelect
        } else if (item.danger) {
            c.danger
        } else {
            c.ink
        }
    val hint = if (lit) c.onSelect else c.inkMuted
    Row(
        Modifier
            .graphicsLayer {
                val p = show.value
                translationY = -ITEM_DROP.toPx() * (1 - p)
                alpha = p
            }.fillMaxWidth()
            .padding(horizontal = ROW_INSET)
            .defaultMinSize(minHeight = OldgeTheme.spacing.hitMin)
            .cssBox(ROW_RADIUS, CssBackground.Solid(if (lit) c.select else Color.Transparent))
            .clickable(source, indication = null, role = Role.Button) { pick(item.id) }
            .padding(end = OldgeTheme.spacing.space3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // `grid-template-columns: 40px 1fr auto`: the item's own first 40 px, which start 3 px in from
        // the menu's edge — so the icon column is not the strip, but 3 px to the right of it.
        if (strip) {
            Box(Modifier.width(STRIP), contentAlignment = Alignment.Center) {
                if (item.icon != null) {
                    OldgeIcon(
                        item.icon,
                        contentDescription = null,
                        size = ICON,
                        tint = if (lit) c.onSelect else c.onPill,
                    )
                }
            }
        } else {
            // No strip and no icons: the label starts `space-3` in, as the closed Select's value does.
            Spacer(Modifier.width(OldgeTheme.spacing.space3))
        }
        OldgeText(item.label, Modifier.weight(1f), style = type.body.copy(color = ink))
        if (item.hint !=
            null
        ) {
            OldgeText(item.hint, style = type.body.copy(fontSize = type.caption.fontSize, color = hint))
        }
    }
}

/** `.og-menu__sep`: 2 px, `line` over `gloss`, 3 px above and below, 44 px in from the left and 3 from the right. */
@Composable
private fun Divider() {
    val c = OldgeTheme.colors
    Box(
        Modifier
            .fillMaxWidth()
            .padding(start = SEP_START, end = ROW_INSET, top = ROW_INSET, bottom = ROW_INSET)
            .height(HAIRLINE * 2)
            .drawBehind {
                val px = HAIRLINE.toPx()
                drawRect(c.line, size = Size(size.width, px))
                drawRect(c.gloss, topLeft = Offset(0f, px), size = Size(size.width, px))
            },
    )
}

private val BORDER = 1.dp
private val HAIRLINE = 1.dp
private val GAP = 4.dp // css literal: bundle.css `.og-menu { top: calc(100% + 4px) }`
private val MIN_WIDTH = 220.dp // css literal: bundle.css `.og-menu { min-width: 220px }`
private val STRIP = 40.dp // css literal: bundle.css `.og-menu { background: linear-gradient(90deg, pill-lo 0 40px, …)`
private val PAD_Y = 3.dp // css literal: bundle.css `.og-menu { padding: 3px 0 }`
private val ROW_INSET = 3.dp // css literal: bundle.css `.og-menu__item { width: calc(100% - 6px); margin: 0 3px }`
private val ROW_RADIUS = 3.dp // css literal: bundle.css `.og-menu__item { border-radius: 3px }`
private val ICON = 18.dp // css literal: bundle.js `Menu`, `Icon size: 18`
private val SEP_START = 44.dp // css literal: bundle.css `.og-menu__sep { margin: 3px 3px 3px 44px }`
private val ITEM_DROP = 6.dp // css literal: bundle.css `@keyframes og-item-in { from { transform: translateY(-6px) } }`
private val WINDOW_DROP = 14.dp // css literal: bundle.css `@keyframes og-window-in`, translateY(14px)
private const val WINDOW_FROM = 0.82f
private const val STAGGER_MS = 30L
