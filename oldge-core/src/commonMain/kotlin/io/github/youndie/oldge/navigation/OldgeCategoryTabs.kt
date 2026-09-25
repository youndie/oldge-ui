package io.github.youndie.oldge.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.press.oldgeHopIn
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText

/** A category of an [OldgeCategoryTabs]. */
@Immutable
public data class OldgeCategory(
    val id: String,
    val icon: ImageVector,
    val label: String,
)

/**
 * A row of big category glyphs under a WindowBar — the design system's `CategoryTabs`: only the
 * [value] category is labelled, bold and underlined by the «hook». Tapping one calls [onChange] with
 * its id.
 *
 * Its README's rules:
 * - four to eight categories;
 * - the other glyphs are `ink-muted`, the current one `ink`;
 * - the row scrolls sideways, with room for the label kept under it;
 * - a label is one or two words («Документы», «Фото»);
 * - this switches the content of the current screen; the app's sections are the BottomNav's.
 *
 * The chosen glyph hops, and its label and hook are drawn in from left to right.
 */
@Composable
public fun OldgeCategoryTabs(
    items: List<OldgeCategory>,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Категории",
) {
    require(items.size in MIN_ITEMS..MAX_ITEMS) {
        "category tabs take $MIN_ITEMS to $MAX_ITEMS categories, got ${items.size}"
    }
    val s = OldgeTheme.spacing
    // `padding-bottom: calc(1.5rem + 16px)`: the label's line, which follows the font, and 16 px.
    val labelRoom =
        with(LocalDensity.current) {
            OldgeTheme.type.heading.lineHeight
                .toDp()
        } + LABEL_ROOM
    Row(
        modifier
            .semantics { contentDescription = label }
            .horizontalScroll(rememberScrollState())
            .padding(start = s.space4, end = s.space4, top = s.space2, bottom = labelRoom),
        horizontalArrangement = Arrangement.spacedBy(s.space1),
        verticalAlignment = Alignment.Top,
    ) {
        for (it in items) key(it.id) { Category(it, it.id == value) { onChange(it.id) } }
    }
}

/**
 * `.og-cat`: a 56 × 52 target with the 36 px glyph in its middle, casting `drop-shadow(0 2px 2px
 * rgba(0,0,0,0.35))`; the current one's label hangs under it, 6 px in and 4 px down, outside the
 * button's box, as CSS's `position: absolute` puts it.
 */
@Composable
private fun Category(
    item: OldgeCategory,
    current: Boolean,
    onClick: () -> Unit,
) {
    val c = OldgeTheme.colors
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val ink = if (current) c.ink else c.inkMuted
    Layout(
        content = {
            Box(contentAlignment = Alignment.Center) {
                // `.og-cat:active .og-cat__glyph { transform: scale(.88) }`, at once.
                val glyph =
                    Modifier.graphicsLayer {
                        val k = if (pressed) PRESSED else 1f
                        scaleX = k
                        scaleY = k
                    }
                if (current) {
                    key("current") { Glyph(item.icon, ink, glyph.oldgeHopIn()) }
                } else {
                    Glyph(item.icon, ink, glyph)
                }
            }
            if (current) key("label") { Hook(item.label) }
        },
        // The tab is the whole layout, so the current one's hanging label is its name; the others are
        // named by `aria-label`, as bundle.js does.
        Modifier
            .selectable(
                current,
                source,
                OldgeIndication(RoundedCornerShape(OldgeRadii.md)),
                role = Role.Tab,
                onClick = onClick,
            ).then(if (current) Modifier else Modifier.semantics { contentDescription = item.label }),
    ) { measurables, constraints ->
        val w = CAT_WIDTH.roundToPx()
        val h = CAT_HEIGHT.roundToPx()
        val cat = measurables[0].measure(constraints.copy(minWidth = w, maxWidth = w, minHeight = h, maxHeight = h))
        val hook =
            measurables
                .getOrNull(
                    1,
                )?.measure(constraints.copy(minWidth = 0, minHeight = 0, maxWidth = Int.MAX_VALUE))
        layout(w, h) {
            cat.place(0, 0)
            hook?.place(LABEL_X.roundToPx(), h + LABEL_Y.roundToPx())
        }
    }
}

@Composable
private fun Glyph(
    icon: ImageVector,
    ink: Color,
    modifier: Modifier,
) = Box(modifier) {
    // The drop shadow: the glyph again, dark, 2 px down, blurred as CSS blurs a 2 px drop-shadow (a
    // Gaussian of 1 px, which Compose's radius reaches at 0.87; see OldgeCard's media glyph).
    OldgeIcon(
        icon,
        contentDescription = null,
        Modifier
            .offset(
                y = SHADOW_Y,
            ).graphicsLayer { renderEffect = BlurEffect(SHADOW_BLUR.toPx(), SHADOW_BLUR.toPx()) },
        size = GLYPH,
        tint = SHADOW,
    )
    OldgeIcon(icon, contentDescription = null, size = GLYPH, tint = ink)
}

/**
 * `.og-cat__label`: the heading face at 1.25rem / 1.5rem, bold, in `ink`, padded 0 12 3 6 inside a
 * 2 px `ink` border on the left and the bottom — the hook; `og-draw` reveals it from the left over
 * `dur-slow` on `ease-out`.
 */
@Composable
private fun Hook(label: String) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val draw = remember { Animatable(if (motion.reduced) 1f else 0f) }
    LaunchedEffect(Unit) { draw.animateTo(1f, tween(motion.slow, easing = motion.out)) }
    val ink = c.ink
    Box(
        Modifier
            .drawWithContent {
                clipRect(right = size.width * draw.value) {
                    val b = HOOK.toPx()
                    drawRect(ink, Offset.Zero, Size(b, size.height))
                    drawRect(ink, Offset(0f, size.height - b), Size(size.width, b))
                    this@drawWithContent.drawContent()
                }
            }.padding(start = HOOK + LABEL_START, end = LABEL_END, bottom = HOOK + LABEL_BOTTOM),
    ) {
        OldgeText(label, style = OldgeTheme.type.heading.copy(color = ink), softWrap = false, maxLines = 1)
    }
}

private const val MIN_ITEMS = 4
private const val MAX_ITEMS = 8
private const val PRESSED = 0.88f
private val SHADOW = Color(0f, 0f, 0f, 0.35f)
private val CAT_WIDTH = 56.dp // css literal: bundle.css `.og-cat { width: 56px }`
private val CAT_HEIGHT = 52.dp // css literal: bundle.css `.og-cat { height: 52px }`
private val GLYPH = 36.dp // css literal: bundle.js `CategoryTabs`, `Icon size: 36`
private val SHADOW_Y = 2.dp // css literal: bundle.css `.og-cat__glyph { filter: drop-shadow(0 2px 2px …) }`
private val SHADOW_BLUR = 0.87.dp // css literal: bundle.css `drop-shadow(… 2px …)`, σ 1 as Compose's blur radius
private val LABEL_X = 6.dp // css literal: bundle.css `.og-cat__label { left: 6px }`
private val LABEL_Y = 4.dp // css literal: bundle.css `.og-cat__label { top: calc(100% + 4px) }`
private val LABEL_START = 6.dp // css literal: bundle.css `.og-cat__label { padding: 0 12px 3px 6px }`
private val LABEL_END = 12.dp // css literal: bundle.css `.og-cat__label { padding: 0 12px 3px 6px }`
private val LABEL_BOTTOM = 3.dp // css literal: bundle.css `.og-cat__label { padding: 0 12px 3px 6px }`
private val LABEL_ROOM = 16.dp // css literal: bundle.css `.og-cats { padding-bottom: calc(1.5rem + 16px) }`
private val HOOK = 2.dp // css literal: bundle.css `.og-cat__label { border-left: 2px; border-bottom: 2px }`
