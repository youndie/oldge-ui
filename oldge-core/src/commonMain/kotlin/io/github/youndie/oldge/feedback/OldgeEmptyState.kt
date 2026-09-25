package io.github.youndie.oldge.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.OrbMaterial
import io.github.youndie.oldge.press.oldgeHopIn
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeShadow
import io.github.youndie.oldge.type.OldgeText

/** An empty state's kind: [Empty] (nothing yet), [Error] (it failed) or [Offline]. */
public enum class OldgeEmptyTone { Empty, Error, Offline }

/**
 * An empty, error or offline state — the design system's `EmptyState`: a big glossy orb with the
 * [icon], the [title] (a fact: «Всё прочитано»), one sentence of [text], and an [action].
 *
 * Its README's rules:
 * - an error needs an action, «Повторить» with the `refresh` icon (`require`);
 * - the orb hops once when it appears, and nothing loops;
 * - it is shown in the content's place, not in a popup.
 *
 * An error or offline state is announced as it appears, as `role="alert"` is.
 */
@Composable
public fun OldgeEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: ImageVector? = null,
    tone: OldgeEmptyTone = OldgeEmptyTone.Empty,
    action: (@Composable () -> Unit)? = null,
) {
    require(tone != OldgeEmptyTone.Error || action != null) { "an error state needs an action, «Повторить»" }
    val c = OldgeTheme.colors
    val s = OldgeTheme.spacing
    Column(
        modifier
            .fillMaxWidth()
            .semantics { if (tone != OldgeEmptyTone.Empty) liveRegion = LiveRegionMode.Assertive }
            .padding(horizontal = s.space4, vertical = s.space6),
        verticalArrangement = Arrangement.spacedBy(s.space2),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Orb(icon ?: defaultIcon(tone), tone, Modifier.padding(bottom = s.space2))
        OldgeText(
            title,
            Modifier.semantics { heading() },
            style = OldgeTheme.type.heading.copy(color = c.ink, textAlign = TextAlign.Center),
        )
        if (text != null) {
            // `max-width: 30ch`: thirty widths of the body face's «0».
            val measure = with(LocalDensity.current) { (OldgeTheme.type.body.fontSize * CH_OF_EM * TEXT_CH).toDp() }
            OldgeText(
                text,
                Modifier.widthIn(max = measure),
                style = OldgeTheme.type.body.copy(color = c.inkMuted, textAlign = TextAlign.Center),
            )
        }
        if (action != null) Box(Modifier.padding(top = s.space3)) { action() }
    }
}

private fun defaultIcon(tone: OldgeEmptyTone): ImageVector =
    when (tone) {
        OldgeEmptyTone.Empty -> OldgeIcons.Folder
        OldgeEmptyTone.Error -> OldgeIcons.Error
        OldgeEmptyTone.Offline -> OldgeIcons.Cloud
    }

/**
 * `.og-empty__orb`: 88 px, the orb's chrome rim 4 px wide round a core of `bezel-hi` to `bezel-lo`
 * (a radial at 35 % 30 %, to 80 %) in `on-bezel`; error is `danger` on a sunken `well`, offline
 * chrome. It hops once (`og-hop`) as it appears.
 */
@Composable
private fun Orb(
    icon: ImageVector,
    tone: OldgeEmptyTone,
    modifier: Modifier,
) {
    val c = OldgeTheme.colors
    val (core, ink, extra) =
        when (tone) {
            OldgeEmptyTone.Empty -> {
                Triple(
                    CssBackground.Radial(listOf(0f to c.bezelHi, CORE_STOP to c.bezelLo), CORE_X, CORE_Y),
                    c.onBezel,
                    emptyList(),
                )
            }

            OldgeEmptyTone.Error -> {
                Triple(CssBackground.Solid(c.well), c.danger, OldgeTheme.shadows.sunken)
            }

            OldgeEmptyTone.Offline -> {
                Triple(
                    CssBackground.Radial(listOf(0f to c.chromeHi, CORE_STOP to c.chromeLo), CORE_X, CORE_Y),
                    c.onChrome,
                    emptyList(),
                )
            }
        }
    Box(modifier.oldgeHopIn()) {
        OrbMaterial(core, ORB, RIM, coreShadows = extra + CORE_RING, highlightTop = HIGHLIGHT_TOP) {
            OldgeIcon(icon, contentDescription = null, size = ICON, tint = ink)
        }
    }
}

private const val CORE_STOP = 0.8f
private const val CORE_X = 0.35f
private const val CORE_Y = 0.3f
private const val HIGHLIGHT_TOP = 0.04f
private const val TEXT_CH = 30f

// `1ch` is the width of «0»: 0.572 em in the bundled ui face, DejaVu Sans Condensed
// (`dejavu_sans_condensed.ttf`, `hmtx` of «0» over `unitsPerEm`, read in B-32), which the references
// render with too.
private const val CH_OF_EM = 0.572265625f

/** `inset 0 0 0 1px rgba(0,0,0,0.35)` on the core. */
private val CORE_RING = OldgeShadow(true, 0.dp, 0.dp, 0.dp, 1.dp, Color(0f, 0f, 0f, 0.35f))
private val ORB = 88.dp // css literal: bundle.css `.og-empty__orb { width: 88px; height: 88px }`
private val RIM = 4.dp // css literal: bundle.css `.og-empty__orb { padding: 4px }`
private val ICON = 36.dp // css literal: bundle.js `EmptyState`, `Icon size: 36`
