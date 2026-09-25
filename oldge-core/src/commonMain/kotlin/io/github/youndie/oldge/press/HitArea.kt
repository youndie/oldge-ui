package io.github.youndie.oldge.press

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.offset

/**
 * A touch target larger than what is drawn, without taking more room — CSS's `::after { inset: -6px
 * -2px }` over a control, which catches the pointer outside the box but is out of flow.
 *
 * Everything after this modifier in the chain is measured [horizontal] and [vertical] larger on each
 * side and placed that far up and to the left, while the layout reports the size it would have had.
 * Put the `clickable` right after it and pad the drawn part back in by the same amounts: the
 * clickable's bounds, which are what Compose hit-tests, are the enlarged ones, and the neighbours
 * are laid out against the drawn box. Compose does not clip hit testing to a parent's bounds, so the
 * overhang receives touches (`ButtonBehaviourTest`-style check in `ChipBehaviourTest`).
 */
internal fun Modifier.oldgeHitArea(
    horizontal: Dp,
    vertical: Dp,
): Modifier =
    layout { measurable, constraints ->
        val h = horizontal.roundToPx()
        val v = vertical.roundToPx()
        val placeable = measurable.measure(constraints.offset(2 * h, 2 * v))
        layout(placeable.width - 2 * h, placeable.height - 2 * v) { placeable.place(-h, -v) }
    }
