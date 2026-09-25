package io.github.youndie.oldge.actions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import io.github.youndie.oldge.theme.OldgeTheme

/**
 * A row of buttons — the design system's `og-actions`, its answer to a row that squeezes its labels:
 * the buttons share a line at its end, each grown from its own width, and when they no longer fit the
 * next one wraps to a line of its own and fills it. At 320 dp in German two buttons stand in a
 * column; at 390 dp in Russian they share the row.
 *
 * `bundle.css`: `.og-actions { display: flex; flex-wrap: wrap; justify-content: flex-end; gap:
 * space-2 }` and `.og-actions > .og-btn { flex: 1 1 auto }`. Each [content] child is laid out that way
 * as it is, with no modifier of its own: `flex: 1 1 auto` grows a button from its own width, where
 * Compose's `weight` shares the space from nothing and makes the buttons equal.
 */
@Composable
public fun OldgeActions(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val gap = OldgeTheme.spacing.space2
    Layout(content, modifier) { measurables, constraints ->
        val gapPx = gap.roundToPx()
        val own = measurables.map { it.maxIntrinsicWidth(Constraints.Infinity) }
        // Unbounded (in a horizontal scroll) there is nothing to wrap to: one line, as wide as it needs.
        val most =
            if (constraints.hasBoundedWidth) {
                constraints.maxWidth
            } else {
                own.sum() +
                    gapPx * (own.size - 1).coerceAtLeast(0)
            }
        // `flex-basis: auto`: each button's own width, and no wider than the line (`flex-shrink: 1`).
        val basis = own.map { it.coerceAtMost(most) }
        // `flex-wrap: wrap`: a line takes buttons while they fit with the gaps between them.
        val lines = mutableListOf<IntRange>()
        var start = 0
        var used = 0
        basis.forEachIndexed { i, w ->
            val next = if (i == start) w else used + gapPx + w
            if (i > start && next > most) {
                lines += start until i
                start = i
                used = w
            } else {
                used = next
            }
        }
        if (basis.isNotEmpty()) lines += start until basis.size
        // `flex-grow: 1` each: a line's free space is shared equally; the pixel left over by the share
        // goes to the first buttons, one each.
        val widths = IntArray(basis.size)
        for (line in lines) {
            val n = line.last - line.first + 1
            val free = (most - line.sumOf { basis[it] } - gapPx * (n - 1)).coerceAtLeast(0)
            line.forEachIndexed { k, i -> widths[i] = basis[i] + free / n + if (k < free % n) 1 else 0 }
        }
        val placeables = measurables.mapIndexed { i, m -> m.measure(Constraints.fixedWidth(widths[i])) }
        val heights = lines.map { line -> line.maxOf { placeables[it].height } }
        val height = heights.sum() + gapPx * (lines.size - 1).coerceAtLeast(0)
        layout(most, height.coerceIn(constraints.minHeight, constraints.maxHeight)) {
            var y = 0
            lines.forEachIndexed { l, line ->
                // `justify-content: flex-end`, which a line grown to its width leaves nothing to do.
                var x = most - (line.sumOf { widths[it] } + gapPx * (line.last - line.first))
                for (i in line) {
                    val p = placeables[i]
                    // Centred in its line where CSS stretches it: a line's buttons share a height, and
                    // stretching would measure each one twice.
                    p.place(x, y + (heights[l] - p.height) / 2)
                    x += p.width + gapPx
                }
                y += heights[l] + gapPx
            }
        }
    }
}
