package io.github.youndie.oldge.containers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.theme.LocalOldgeTextStyle
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText

/**
 * An inset panel on the screen's body — the design system's `Panel`: it groups related content under
 * an optional small [title] in `ink-muted`.
 *
 * [sunken] is the read-only well, for output and logs; [flush] drops the body's padding and gap, for
 * an [OldgeList] inside. Its README's rules: panels do not nest, and a group that folds is an
 * Accordion, not a panel.
 */
@Composable
public fun OldgePanel(
    modifier: Modifier = Modifier,
    title: String? = null,
    sunken: Boolean = false,
    flush: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val c = OldgeTheme.colors
    val s = OldgeTheme.spacing
    Column(
        modifier
            .fillMaxWidth()
            .cssBox(
                OldgeRadii.md,
                CssBackground.Solid(if (sunken) c.well else c.surface),
                BORDER,
                c.edge,
                if (sunken) OldgeTheme.shadows.sunken else OldgeTheme.shadows.raised,
            ).padding(BORDER)
            // `overflow: hidden`: a flush list's rows end at the panel's rounded inner edge.
            .clip(RoundedCornerShape(OldgeRadii.md - BORDER)),
    ) {
        if (title != null) {
            // `.og-panel__title`: an `h2` at 0.8125rem / 1rem, bold, `ink-muted`, padded 12 16 0.
            OldgeText(
                title,
                Modifier
                    .padding(start = s.space4, end = s.space4, top = s.space3)
                    .semantics { heading() },
                style = OldgeTheme.type.label.copy(color = c.inkMuted),
            )
        }
        CompositionLocalProvider(LocalOldgeTextStyle provides OldgeTheme.type.body.copy(color = c.ink)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .then(if (flush) Modifier else Modifier.padding(horizontal = s.space4, vertical = s.space3)),
                verticalArrangement = Arrangement.spacedBy(if (flush) 0.dp else s.space2),
                content = content,
            )
        }
    }
}

private val BORDER = 1.dp
