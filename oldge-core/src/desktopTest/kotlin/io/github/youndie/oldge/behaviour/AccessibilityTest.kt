package io.github.youndie.oldge.behaviour

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsConfiguration
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.actions.OldgeButtonSize
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.viddik.annotations.ViddikComponent
import io.github.youndie.viddik.generated.GeneratedViddikRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The design system's accessibility rules (README «Отступы и касание», «Фокус и доступность»), held
 * over the whole registry rather than component by component (B-43): every fixture group of viddik's
 * registry, in one skin, since a skin changes colours and not semantics.
 *
 * - Every control is at least `hit-min`, 44 dp, to touch. Compose itself extends a small pointer
 *   target to its `minimumTouchTargetSize` for touch input, so that rule rests on the platform, and
 *   [touch_reaches_hit_min_through_the_platforms_own_extension] holds the platform to it (B-60).
 * - Every control has a role and a label.
 * - Colour is never the only carrier of meaning: a node that carries a state (an error, on or off, a
 *   selection, progress) also carries a word.
 *
 * A shortfall is reported, not hidden: each known one is in the `KNOWN_*` set with the item that
 * owns it, and the sets are compared whole, so a new one fails and so does a fixed one.
 */
@OptIn(ExperimentalTestApi::class)
class AccessibilityTest {
    private val fixtures: List<ViddikComponent> =
        GeneratedViddikRegistry.components
            .groupBy { it.group }
            .map { (_, skins) -> skins.firstOrNull { it.name.endsWith("Toxic") } ?: skins.first() }

    private class Found(
        val group: String,
        val node: SemanticsNode,
    )

    private class Shortfalls(
        val unnamed: List<String>,
        val wordless: List<String>,
        val nodes: Int,
        val controls: Int,
    )

    /** Each fixture composed and read while it is alive: a disposed node's merged semantics are empty. */
    private fun read(fixture: ViddikComponent): Shortfalls {
        var result = Shortfalls(emptyList(), emptyList(), 0, 0)
        runComposeUiTest {
            setContent { Box(Modifier.size(fixture.width.dp, fixture.height.dp)) { fixture.content() } }
            waitForIdle()
            // Every root: a Dialog, a sheet or a menu is a window of its own.
            val all =
                onAllNodes(isRoot()).fetchSemanticsNodes().flatMap { root ->
                    generateSequence(listOf(root)) { level -> level.flatMap { it.children }.takeIf { it.isNotEmpty() } }
                        .flatten()
                        .map { Found(fixture.group, it) }
                        .toList()
                }
            // A control on the screen: a node scrolled out of a lazy list, or clipped away, is composed
            // but not placed, with empty bounds.
            val clickable =
                all.filter {
                    it.node.config.contains(SemanticsActions.OnClick) && it.node.boundsInRoot.width > 0f &&
                        it.node.boundsInRoot.height > 0f
                }
            val unnamed =
                clickable
                    .filter { f ->
                        // A text field has no role in Compose: its editable text, kept when it is disabled, says it is one.
                        val role =
                            f.node.config.contains(SemanticsProperties.Role) ||
                                f.node.config.contains(SemanticsProperties.EditableText)
                        !role || !f.node.config.hasWord()
                    }.map { "${it.group}: ${it.label()} role=${it.node.config.getOrNull(SemanticsProperties.Role)}" }
            val wordless =
                all
                    .filter { f ->
                        val c = f.node.config
                        c.contains(SemanticsProperties.Error) ||
                            c.contains(SemanticsProperties.ToggleableState) ||
                            c.contains(SemanticsProperties.Selected) ||
                            c.contains(SemanticsProperties.ProgressBarRangeInfo)
                    }.filter { !it.node.config.hasWord() }
                    .map { "${it.group}: ${it.label()}" }
            result = Shortfalls(unnamed, wordless, all.size, clickable.size)
        }
        return result
    }

    private fun Found.label(): String {
        val c = node.config
        return c.getOrNull(SemanticsProperties.ContentDescription)?.joinToString()
            ?: c.getOrNull(SemanticsProperties.Text)?.joinToString()
            ?: "(unlabelled)"
    }

    private fun SemanticsConfiguration.hasWord(): Boolean =
        contains(SemanticsProperties.ContentDescription) ||
            contains(SemanticsProperties.Text) ||
            contains(SemanticsProperties.StateDescription) ||
            contains(SemanticsProperties.EditableText)

    @Test
    fun every_control_has_a_role_and_a_label_and_says_its_state_in_words() {
        val read = fixtures.map { read(it) }
        val nodes = read.sumOf { it.nodes }
        val controls = read.sumOf { it.controls }
        assertTrue(
            nodes > MANY_NODES,
            "only $nodes nodes over ${fixtures.size} fixtures: the check would pass over nothing",
        )
        assertTrue(controls > MANY_CONTROLS, "only $controls controls found")
        val unnamed = read.flatMap { it.unnamed }.toSet()
        val wordless = read.flatMap { it.wordless }.toSet()
        assertEquals(
            KNOWN_UNNAMED to KNOWN_WORDLESS,
            unnamed to wordless,
            "unnamed:\n${unnamed.joinToString("\n")}\nwordless:\n${wordless.joinToString("\n")}",
        )
    }

    /**
     * «Любой контрол — не меньше hit-min (44px)» for touch, as Compose provides it: a pointer target
     * smaller than the platform's `minimumTouchTargetSize` is extended to it for a touch that hits
     * nothing else, and not for a mouse, which is precise. The design system's small button (36 dp) is
     * the case: a touch 6 dp above its drawn edge presses it, and a mouse click there does not, as a
     * mouse in Chrome reaches only the drawn 36 px. B-43 first counted the controls' own sizes as touch
     * targets; that measured the mouse's, and B-60 found it.
     */
    @Test
    fun touch_reaches_hit_min_through_the_platforms_own_extension() =
        runComposeUiTest {
            var taps by mutableIntStateOf(0)
            var least = DpSize.Zero
            setContent {
                least = LocalViewConfiguration.current.minimumTouchTargetSize
                OldgeTheme {
                    Column(Modifier.padding(PAD)) {
                        Box(Modifier.testTag("b")) { OldgeButton("Позже", { taps++ }, size = OldgeButtonSize.Small) }
                    }
                }
            }
            assertTrue(least.width >= HIT && least.height >= HIT, "the platform's minimum touch target is $least")
            val drawn = onNodeWithTag("b").getUnclippedBoundsInRoot()
            assertEquals(SMALL, drawn.bottom - drawn.top, "the small button is not drawn 36 dp high")
            val above = Offset(((drawn.left + drawn.right) / 2).value, (drawn.top - OUTSIDE).value)
            onRoot().performTouchInput { click(above * density) }
            assertEquals(1, taps, "a touch 6 dp above a small button did not reach it")
            onRoot().performMouseInput { click(above * density) }
            assertEquals(1, taps, "a mouse click 6 dp above a small button reached it")
        }
}

private val HIT = 44.dp // the design system's `hit-min`, `OldgeTheme.spacing.hitMin`
private val SMALL = 36.dp // css literal: bundle.css `.og-btn--sm { min-height: 36px }`
private val OUTSIDE = 6.dp
private val PAD = 40.dp
private const val MANY_NODES = 500
private const val MANY_CONTROLS = 100

/** Controls without a role or a label, each owned by an item. */
private val KNOWN_UNNAMED = emptySet<String>()

/** States said only in colour, each owned by an item. */
private val KNOWN_WORDLESS = emptySet<String>()
