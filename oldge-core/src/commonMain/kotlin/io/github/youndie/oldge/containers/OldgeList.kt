package io.github.youndie.oldge.containers

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.premultipliedLerp
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.theme.LocalOldgeContentColor
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText

/** The rows of an [OldgeList]: each [row] becomes one `li`. */
public class OldgeListScope internal constructor() {
    internal val rows = mutableListOf<@Composable () -> Unit>()

    /** One row, usually an [OldgeListItem]. */
    public fun row(content: @Composable () -> Unit) {
        rows += content
    }
}

/**
 * A rounded inset list — the design system's `List`: every [OldgeListScope.row] is a row, and rows
 * after the first are divided by a `line`.
 *
 * [striped] shades every second row with `surface-2`; [bare] drops the background and the frame, for
 * a list inside an Accordion or a Panel; [label] names the list for a screen reader.
 *
 * Narrow, the list moves each row's value under its title: when its content is 20 em of the list's
 * own font or less — 300 dp at the default size, and less as the system font grows (bundle.css's
 * `@container og-list (max-width: 20em)`; measured in Chrome, B-20).
 */
@Composable
public fun OldgeList(
    modifier: Modifier = Modifier,
    label: String? = null,
    striped: Boolean = false,
    bare: Boolean = false,
    content: OldgeListScope.() -> Unit,
) {
    val c = OldgeTheme.colors
    val rows = OldgeListScope().apply(content).rows
    val box =
        if (bare) {
            modifier.fillMaxWidth()
        } else {
            modifier
                .fillMaxWidth()
                .cssBox(OldgeRadii.md, CssBackground.Solid(c.surface), BORDER, c.edge)
                .padding(BORDER)
                .clip(RoundedCornerShape(OldgeRadii.md - BORDER))
        }
    val narrowAt = with(LocalDensity.current) { (OldgeTheme.type.body.fontSize * NARROW_EM).toDp() }
    BoxWithConstraints(
        box.semantics {
            if (label != null) contentDescription = label
            collectionInfo = CollectionInfo(rows.size, 1)
        },
    ) {
        val narrow = maxWidth <= narrowAt
        Column(Modifier.fillMaxWidth()) {
            rows.forEachIndexed { i, row ->
                CompositionLocalProvider(LocalOldgeListRow provides OldgeListRow(i, striped, narrow)) { row() }
            }
        }
    }
}

/** Where a row sits in its list: whether it takes the divider, a stripe, and the narrow layout. */
@Immutable
internal data class OldgeListRow(
    val index: Int,
    val striped: Boolean,
    val narrow: Boolean,
)

internal val LocalOldgeListRow = compositionLocalOf<OldgeListRow?> { null }

/**
 * A list row — the design system's `ListItem`: an [icon] or a [lead] element (an Avatar, a
 * thumbnail), the [title] with an optional [subtitle], a [value] or a [trailing] control on the right
 * (a Switch, a Badge), and a [chevron]. [icon] is the common case, an icon at the design system's
 * 22 dp in the row's colour; [lead] takes any element into the same place, as the design system's
 * `icon` takes one that is not an icon name. A row has one lead, so not both.
 *
 * Its README's rules:
 * - with [onClick] (or a [chevron]) the whole row is one button, at least 52 dp high, 44 dp when
 *   [dense];
 * - a row whose trailing control is a Switch takes no [onClick], since the switch is the action;
 * - [selected] is a solid `select` with all its text in `on-select`;
 * - the title wraps to two lines rather than being cut; in a narrow list the value moves under it.
 *
 * Pressed, the row turns `surface-2`, a gloss flash spreads from the finger, and the chevron is
 * nudged 4 dp right on the spring.
 */
@Composable
public fun OldgeListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    lead: (@Composable () -> Unit)? = null,
    value: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    chevron: Boolean = false,
    selected: Boolean = false,
    dense: Boolean = false,
    onClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    require(icon == null || lead == null) { "a row has one lead: an icon or a lead element, not both" }
    val c = OldgeTheme.colors
    val s = OldgeTheme.spacing
    val motion = OldgeTheme.motion
    val row = LocalOldgeListRow.current
    val interactive = onClick != null || chevron
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    // `button.og-item:active { background: surface-2 }`: in over `dur-fast`, out over `dur-base`, on `ease-out`.
    val held by animateFloatAsState(
        if (pressed && interactive) 1f else 0f,
        tween(if (pressed) motion.fast else motion.base, easing = motion.out),
        label = "oldge row press",
    )
    val nudge by animateFloatAsState(
        if (pressed && interactive) 1f else 0f,
        tween(motion.base, easing = motion.spring),
        label = "oldge row chevron",
    )
    val stripe = row != null && row.striped && row.index % 2 == 1
    val rest = if (stripe) c.surface2 else Color.Transparent
    val ground = if (selected) c.select else premultipliedLerp(rest, c.surface2, held)
    val ink = if (selected) c.onSelect else c.ink
    val muted = if (selected) c.onSelect else c.inkMuted
    val divided = row != null && row.index > 0
    val line = c.line
    val box =
        modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = if (dense) DENSE_HEIGHT else HEIGHT)
            .drawBehind {
                drawRect(ground)
                if (divided) drawRect(line, Offset.Zero, Size(size.width, BORDER.toPx()))
            }.then(
                if (interactive) {
                    Modifier.clickable(source, OldgeIndication(), role = Role.Button, onClick = onClick ?: {})
                } else {
                    Modifier
                },
            ).then(if (selected) Modifier.semantics { this.selected = true } else Modifier)
            .padding(top = if (divided) BORDER else 0.dp)
            .padding(horizontal = s.space3, vertical = if (dense) s.space1 else s.space2)
    val type = OldgeTheme.type
    // `.og-item__lead { flex: none; color: ink; place-items: center }`: an element takes the row's ink.
    val leading =
        @Composable {
            if (icon != null) {
                OldgeIcon(icon, contentDescription = null, size = LEAD, tint = ink)
            } else if (lead != null) {
                Box(contentAlignment = Alignment.Center) {
                    CompositionLocalProvider(LocalOldgeContentColor provides ink) { lead() }
                }
            }
        }
    val text =
        @Composable { m: Modifier ->
            Column(m) {
                // `.og-item__title`: the body style, clamped at two lines.
                OldgeText(title, style = type.body.copy(color = ink), maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (subtitle != null) {
                    OldgeText(
                        subtitle,
                        style = type.caption.copy(color = muted),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    val valueStyle = type.body.copy(fontSize = type.body.fontSize * VALUE_SCALE, color = muted)
    val tail =
        @Composable {
            trailing?.invoke()
            if (chevron) {
                OldgeIcon(
                    OldgeIcons.Chevron,
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer { translationX = CHEVRON_NUDGE.toPx() * nudge },
                    size = CHEVRON,
                    tint = muted,
                )
            }
        }
    if (row?.narrow == true && value != null) {
        // `@container og-list (max-width: 20em)`: the value wraps to its own line, left-aligned under
        // the title, indented by the 22 px icon column and its gap whether or not there is an icon.
        Column(box, verticalArrangement = Arrangement.Center) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(s.space3),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                leading()
                text(Modifier.weight(1f))
                tail()
            }
            OldgeText(value, Modifier.padding(start = LEAD + s.space3), style = valueStyle)
        }
    } else {
        BoxWithConstraints(box, contentAlignment = Alignment.CenterStart) {
            val valueMax = maxWidth * VALUE_MAX
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(s.space3),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                leading()
                text(Modifier.weight(1f))
                if (value != null) {
                    OldgeText(
                        value,
                        Modifier.widthIn(max = valueMax),
                        style = valueStyle.copy(textAlign = TextAlign.End),
                    )
                }
                tail()
            }
        }
    }
}

private const val NARROW_EM = 20f
private const val VALUE_MAX = 0.42f

// `.og-item__value { font-size: 0.875rem }` on the body's 0.9375rem, its 1.25rem line inherited.
private const val VALUE_SCALE = 0.875f / 0.9375f
private val BORDER = 1.dp
private val HEIGHT = 52.dp // css literal: bundle.css `.og-item { min-height: 52px }`
private val DENSE_HEIGHT = 44.dp // css literal: bundle.css `.og-item--dense { min-height: 44px }`
private val LEAD = 22.dp // css literal: bundle.js `ListItem`, `Icon size: 22`
private val CHEVRON = 18.dp // css literal: bundle.js `ListItem`, chevron `Icon size: 18`
private val CHEVRON_NUDGE = 4.dp // css literal: bundle.css `button.og-item:active .og-item__chev { translateX(4px) }`
