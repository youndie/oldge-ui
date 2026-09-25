package io.github.youndie.oldge.sample

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeSkin
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * B-39's check that no text on the Edge pages is cut: every text's layout, asked for through its
 * semantics, is read for a cut. That is a line cut off at the bottom, a line ended in an ellipsis, or a
 * line wider than its text node. Not `hasVisualOverflow`: its width half is true for every text
 * here, since the paragraph is laid out at the width available while the node takes the text's own
 * (B-39's findings). The only texts allowed to overflow are the BottomNav labels
 * the design system itself cuts with an ellipsis, as the references show them («Benachric…»,
 * «Einstellu…»). The set is compared whole, so a new clip fails, and so does a label that stops
 * needing its exemption.
 */
@OptIn(ExperimentalTestApi::class)
class EdgeClippingTest {
    @Test
    fun nothing_on_the_narrow_page_is_cut_but_the_navigations_own_ellipses() =
        assertEquals(setOf("Benachrichtigungen", "Einstellungen"), overflowing(320, 680, 1f) { EdgeNarrowScreen(it) })

    @Test
    fun nothing_on_the_200_percent_page_is_cut() =
        assertEquals(
            emptySet(),
            overflowing(390, 860, 2f) {
                EdgeScaleScreen(it)
            },
        )

    private fun overflowing(
        width: Int,
        height: Int,
        fontScale: Float,
        screen: @Composable (Modifier) -> Unit,
    ): Set<String> {
        var cut = emptySet<String>()
        runComposeUiTest {
            setContent { Phone(OldgeSkin.Toxic, fontScale) { screen(Modifier.size(width.dp, height.dp)) } }
            val results = mutableListOf<TextLayoutResult>()
            onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.GetTextLayoutResult), useUnmergedTree = true)
                .fetchSemanticsNodes()
                .forEach { it.config[SemanticsActions.GetTextLayoutResult].action?.invoke(results) }
            check(results.size > 10) { "only ${results.size} texts found: the check would pass over nothing" }
            cut = results.filter { it.isCut() }.map { it.layoutInput.text.text }.toSet()
        }
        return cut
    }
}

private fun TextLayoutResult.isCut(): Boolean =
    didOverflowHeight ||
        (0 until lineCount).any { isLineEllipsized(it) || getLineRight(it) > size.width + HALF_PIXEL }

private const val HALF_PIXEL = 0.5f
