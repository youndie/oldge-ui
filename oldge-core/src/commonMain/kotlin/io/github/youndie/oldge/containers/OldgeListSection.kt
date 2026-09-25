package io.github.youndie.oldge.containers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.tokens.OldgeShadow
import io.github.youndie.oldge.type.FontCoverage
import io.github.youndie.oldge.type.OldgeScriptText
import io.github.youndie.oldge.type.OldgeText

/** The sections of a scrolling list, built by [oldgeListSections]. */
public class OldgeListSectionsScope internal constructor() {
    internal class Section(
        val title: String,
        val meta: String?,
        val key: Any,
        val content: @Composable () -> Unit,
    )

    internal val sections = mutableListOf<Section>()

    /**
     * One section: its pill header with the [title] and an optional [meta] tag on the right («3
     * новых»), then [content], usually an [OldgeList]. [key] must be unique among the sections.
     */
    public fun section(
        title: String,
        meta: String? = null,
        key: Any = title,
        content: @Composable () -> Unit,
    ) {
        sections += Section(title, meta, key, content)
    }
}

/**
 * List sections whose pill headers stick to the top of the scroll — the design system's
 * `ListSection`, in a lazy list: each header is a `stickyHeader`, so it holds at the top while its
 * section scrolls under it and is pushed off by the next.
 *
 * Its README's rules: the sticking works inside a scrolling container (here, the `LazyColumn` these
 * sections are emitted into), and the titles are stretches of time or groups — «Сегодня», «Вчера»,
 * «Ранее». Sections after the first stand 12 dp below the one before; the header is 8 dp above its
 * content. [extraSpacing] is space the surrounding layout adds between sections on top of that, as a
 * flex column's `gap` does in CSS (the design's own preview adds 12).
 */
@OptIn(ExperimentalFoundationApi::class)
public fun LazyListScope.oldgeListSections(
    extraSpacing: Dp = 0.dp,
    build: OldgeListSectionsScope.() -> Unit,
) {
    OldgeListSectionsScope().apply(build).sections.forEachIndexed { i, s ->
        // `.og-lsec + .og-lsec { margin-top: space-3 }` is the section's, not the header's: a stuck
        // header sits flush at the top.
        if (i > 0) item(key = SectionGap(s.key)) { Spacer(Modifier.height(OldgeTheme.spacing.space3 + extraSpacing)) }
        stickyHeader(key = SectionHead(s.key)) { OldgeListSectionHeader(s.title, s.meta) }
        item(key = SectionBody(s.key)) {
            Column {
                Spacer(Modifier.height(OldgeTheme.spacing.space2))
                s.content()
            }
        }
    }
}

private data class SectionGap(
    val key: Any,
)

private data class SectionHead(
    val key: Any,
)

private data class SectionBody(
    val key: Any,
)

/**
 * `.og-lsec__head`: a pill of `pill-hi` to `pill-lo` in `on-pill`, a 1 px `edge` frame,
 * `shadow-raised` and a 4 px drop under it, padded 6 12; the title bold at 0.8125rem, the meta in
 * `pixel-tag` upper case, the two on one baseline at either end.
 */
@Composable
public fun OldgeListSectionHeader(
    title: String,
    meta: String? = null,
    modifier: Modifier = Modifier,
) {
    val c = OldgeTheme.colors
    val type = OldgeTheme.type
    Row(
        modifier
            .fillMaxWidth()
            .cssBox(
                OldgeRadii.pill,
                CssBackground.Linear(listOf(0f to c.pillHi, 1f to c.pillLo)),
                BORDER,
                c.edge,
                OldgeTheme.shadows.raised + HEAD_DROP,
            ).padding(BORDER)
            .padding(horizontal = OldgeTheme.spacing.space3, vertical = HEAD_PADDING)
            .semantics(mergeDescendants = true) { heading() },
        horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
    ) {
        OldgeText(title, Modifier.weight(1f).alignByBaseline(), style = type.label.copy(color = c.onPill))
        if (meta != null) {
            OldgeScriptText(
                meta.uppercase(),
                type.pixelTag.copy(color = c.onPill),
                type.families.pixelCompanion,
                FontCoverage.silkscreen,
                Modifier.alignByBaseline(),
            )
        }
    }
}

private val DROP_Y = 4.dp // css literal: bundle.css `.og-lsec__head { box-shadow: …, 0 4px 8px }`
private val DROP_BLUR = 8.dp // css literal: bundle.css `.og-lsec__head { box-shadow: …, 0 4px 8px }`

/** `0 4px 8px rgba(0,0,0,0.25)`, after `shadow-raised`. */
private val HEAD_DROP = listOf(OldgeShadow(false, 0.dp, DROP_Y, DROP_BLUR, 0.dp, Color(0f, 0f, 0f, 0.25f)))
private val BORDER = 1.dp
private val HEAD_PADDING = 6.dp // css literal: bundle.css `.og-lsec__head { padding: 6px var(--space-3) }`
