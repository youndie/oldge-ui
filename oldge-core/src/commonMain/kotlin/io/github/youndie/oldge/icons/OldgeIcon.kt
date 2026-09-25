package io.github.youndie.oldge.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.LocalOldgeContentColor

/**
 * An icon of the design system's set — the design system's `Icon`: a single-colour glyph on a
 * 24 × 24 grid in the current content colour (`currentColor`).
 *
 * @param contentDescription what the icon means, for an icon that is the only label of a control
 *   ("an icon-only control needs a label", the Button README); `null` for one next to a text that
 *   already says it, which then stays out of the accessibility tree.
 */
@Composable
public fun OldgeIcon(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = DEFAULT_SIZE,
    tint: Color = LocalOldgeContentColor.current,
) {
    Image(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        colorFilter = if (tint.isSpecified) ColorFilter.tint(tint) else null,
    )
}

/** The grid's own size, which is bundle.js's default (`p.size || 24`). */
private val DEFAULT_SIZE = GRID.dp

/** One path on the 24 × 24 grid, filled even-odd, as bundle.js draws it. */
internal fun oldgeIcon(
    name: String,
    pathData: String,
): ImageVector =
    ImageVector
        .Builder(
            name = "OldgeIcons.$name",
            defaultWidth = GRID.dp,
            defaultHeight = GRID.dp,
            viewportWidth = GRID,
            viewportHeight = GRID,
        ).addPath(
            pathData = addPathNodes(pathData),
            pathFillType = PathFillType.EvenOdd,
            fill = SolidColor(Color.Black),
        ).build()

private const val GRID = 24f
