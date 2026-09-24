package io.github.youndie.oldge.containers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.type.FontCoverage
import io.github.youndie.oldge.type.OldgeScriptText

/**
 * A divider — the design system's `Divider`, Material's Divider drawn as an etched double line: a
 * `line` hairline over a `gloss` one, 2 dp in all (reference/design-system/components/Divider).
 *
 * With a [label] («или» between two ways in), the label sits between two such lines in the pixel
 * face, in capitals, in `ink-muted` — the design system's README: pixel labels are capitals and carry
 * only duplicated information, so the label is also the separator's accessible description.
 */
@Composable
public fun OldgeDivider(
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    val margin = OldgeTheme.spacing.space2
    if (label == null) {
        EtchedLine(modifier.padding(vertical = margin).fillMaxWidth())
        return
    }
    val type = OldgeTheme.type
    Row(
        modifier
            .padding(vertical = margin)
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { role = Role.Image },
        horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EtchedLine(Modifier.weight(1f))
        OldgeScriptText(
            label.uppercase(),
            type.pixelTag.copy(color = OldgeTheme.colors.inkMuted),
            type.families.pixelCompanion,
            FontCoverage.silkscreen,
        )
        EtchedLine(Modifier.weight(1f))
    }
}

/** `linear-gradient(var(--line) 0 1px, var(--gloss) 1px 2px)` in a 2 dp strip. */
@Composable
private fun EtchedLine(modifier: Modifier) {
    val c = OldgeTheme.colors
    Box(
        modifier.height(HAIRLINE * 2).drawBehind {
            val px = HAIRLINE.toPx()
            drawRect(c.line, size = Size(size.width, px))
            drawRect(c.gloss, topLeft = Offset(0f, px), size = Size(size.width, px))
        },
    )
}

/** One of the divider's two lines: `line` on top, `gloss` under it. */
private val HAIRLINE = 1.dp
