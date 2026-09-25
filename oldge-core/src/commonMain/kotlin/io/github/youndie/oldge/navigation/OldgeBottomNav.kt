package io.github.youndie.oldge.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.CssCorners
import io.github.youndie.oldge.material.CssRadii
import io.github.youndie.oldge.material.bezel
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.cssRoundRect
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.press.oldgeHopIn
import io.github.youndie.oldge.press.oldgePopSoftIn
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText

/** A section of an [OldgeBottomNav]. */
@Immutable
public data class OldgeNavItem(
    val id: String,
    val icon: ImageVector,
    val label: String,
)

/**
 * The bottom dock — the design system's `BottomNav`: the skin's bezel with three to five sections;
 * the [value] section's icon lies in a chrome capsule. Tapping a section calls [onChange] with its id.
 *
 * Its README's rules: the bottom corners are `radius-xl`, the pair of the WindowBar's top ([square]
 * drops them); a label is one word of up to nine letters. The capsule pops out when a section
 * becomes current, and its icon hops. The dock pads itself above the system's bottom inset, as
 * `env(safe-area-inset-bottom)` does.
 */
@Composable
public fun OldgeBottomNav(
    items: List<OldgeNavItem>,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Разделы",
    square: Boolean = false,
) {
    require(
        items.size in MIN_ITEMS..MAX_ITEMS,
    ) { "a bottom nav has $MIN_ITEMS to $MAX_ITEMS sections, got ${items.size}" }
    val s = OldgeTheme.spacing
    Row(
        modifier
            .fillMaxWidth()
            .bezel(if (square) 0.dp else OldgeRadii.xl, CssCorners.Bottom)
            .semantics { contentDescription = label }
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
            .padding(horizontal = s.space2, vertical = s.space1),
        horizontalArrangement = Arrangement.spacedBy(s.space1),
    ) {
        for (it in items) {
            key(it.id) { Item(it, it.id == value, { onChange(it.id) }, Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun Item(
    item: OldgeNavItem,
    current: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val c = OldgeTheme.colors
    val shape = RoundedCornerShape(OldgeRadii.md)
    Column(
        modifier
            .defaultMinSize(minHeight = ITEM_HEIGHT)
            .clickable(
                remember { MutableInteractionSource() },
                OldgeIndication(shape),
                role = Role.Button,
                onClick = onClick,
            ).semantics { selected = current }
            .padding(horizontal = ITEM_SIDE, vertical = OldgeTheme.spacing.space1),
        verticalArrangement = Arrangement.spacedBy(ITEM_GAP, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (current) {
            // A new capsule on becoming current, so `og-pop-soft` and `og-hop` play once each time.
            key("current") { Capsule(item.icon) }
        } else {
            Box(Modifier.size(CAPSULE_WIDTH, CAPSULE_HEIGHT), contentAlignment = Alignment.Center) {
                OldgeIcon(item.icon, contentDescription = null, size = ICON, tint = c.onBezel)
            }
        }
        OldgeText(
            item.label,
            style =
                OldgeTheme.type.caption.copy(
                    color = c.onBezel,
                    fontWeight = if (current) FontWeight.Bold else FontWeight.Normal,
                ),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * `.og-nav__item[aria-current="page"] .og-nav__icon`: a 44 × 28 pill of `chrome-hi` to `chrome-lo`
 * in `on-chrome`, `shadow-orb`, and a `gloss` cap 1 px down and 4 px in, 40 % high, round on top and
 * square below.
 */
@Composable
private fun Capsule(icon: ImageVector) {
    val c = OldgeTheme.colors
    Box(
        Modifier
            .oldgePopSoftIn()
            .size(CAPSULE_WIDTH, CAPSULE_HEIGHT)
            .cssBox(
                OldgeRadii.pill,
                CssBackground.Linear(listOf(0f to c.chromeHi, 1f to c.chromeLo)),
                shadows = OldgeTheme.shadows.orb,
            ).drawBehind {
                val side = CAP_SIDE.toPx()
                val top = CAP_TOP.toPx()
                val cap = CornerRadius(PILL_PX, PILL_PX)
                val band =
                    cssRoundRect(
                        Offset(side, top),
                        Size(size.width - 2 * side, size.height * CAP_HEIGHT),
                        0f,
                        CssRadii(cap, cap, CornerRadius.Zero, CornerRadius.Zero),
                    )
                drawPath(Path().apply { addRoundRect(band) }, c.gloss)
            },
        contentAlignment = Alignment.Center,
    ) {
        OldgeIcon(icon, contentDescription = null, modifier = Modifier.oldgeHopIn(), size = ICON, tint = c.onChrome)
    }
}

private const val MIN_ITEMS = 3
private const val MAX_ITEMS = 5
private const val CAP_HEIGHT = 0.4f
private const val PILL_PX = 999f
private val CAP_SIDE = 4.dp // css literal: bundle.css `.og-nav__icon::before { inset: 1px 4px auto }`
private val CAP_TOP = 1.dp
private val ITEM_HEIGHT = 56.dp // css literal: bundle.css `.og-nav__item { min-height: 56px }`
private val ITEM_SIDE = 2.dp // css literal: bundle.css `.og-nav__item { padding: var(--space-1) 2px }`
private val ITEM_GAP = 2.dp // css literal: bundle.css `.og-nav__item { gap: 2px }`
private val CAPSULE_WIDTH = 44.dp // css literal: bundle.css `.og-nav__icon { width: 44px }`
private val CAPSULE_HEIGHT = 28.dp // css literal: bundle.css `.og-nav__icon { height: 28px }`
private val ICON = 22.dp // css literal: bundle.js `BottomNav`, `Icon size: 22`
