package io.github.youndie.oldge.behaviour

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsConfiguration
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
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
 * - Every control is at least `hit-min`, 44 dp, to touch.
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
        val touch: List<String>,
        val unnamed: List<String>,
        val wordless: List<String>,
        val nodes: Int,
        val controls: Int,
    )

    /** Each fixture composed and read while it is alive: a disposed node's merged semantics are empty. */
    private fun read(fixture: ViddikComponent): Shortfalls {
        var result = Shortfalls(emptyList(), emptyList(), emptyList(), 0, 0)
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
            val touch =
                clickable.mapNotNull { f ->
                    // The control's own laid-out size, which `oldgeHitArea` enlarges. Not `touchBoundsInRoot`:
                    // that is at least the platform's minimum touch target by definition and could never fall
                    // short. Not the clipped bounds either: a chip at a scroll's edge or a pressed button's
                    // squash is not a smaller target.
                    val w =
                        with(density) {
                            f.node.size.width
                                .toDp()
                        }
                    val h =
                        with(density) {
                            f.node.size.height
                                .toDp()
                        }
                    if (w + 0.5.dp < HIT ||
                        h + 0.5.dp < HIT
                    ) {
                        "${f.group}: ${w.value.toInt()}×${h.value.toInt()} dp"
                    } else {
                        null
                    }
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
            result = Shortfalls(touch, unnamed, wordless, all.size, clickable.size)
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
    fun every_control_is_44_dp_to_touch_has_a_role_and_a_label_and_says_its_state_in_words() {
        val read = fixtures.map { read(it) }
        val nodes = read.sumOf { it.nodes }
        val controls = read.sumOf { it.controls }
        assertTrue(
            nodes > MANY_NODES,
            "only $nodes nodes over ${fixtures.size} fixtures: the check would pass over nothing",
        )
        assertTrue(controls > MANY_CONTROLS, "only $controls controls found")
        val touch = read.flatMap { it.touch }.toSet()
        val unnamed = read.flatMap { it.unnamed }.toSet()
        val wordless = read.flatMap { it.wordless }.toSet()
        assertEquals(
            Triple(KNOWN_TOUCH, KNOWN_UNNAMED, KNOWN_WORDLESS),
            Triple(touch, unnamed, wordless),
            "touch:\n${touch.joinToString(
                "\n",
            )}\nunnamed:\n${unnamed.joinToString("\n")}\nwordless:\n${wordless.joinToString("\n")}",
        )
    }
}

private val HIT = 44.dp // the design system's `hit-min`, `OldgeTheme.spacing.hitMin`
private const val MANY_NODES = 500
private const val MANY_CONTROLS = 100

/**
 * Controls smaller than 44 dp to touch, by fixture group and size: the design system's own CSS sizes
 * them so (`og-btn--sm` 36 px high, `og-seg__opt`, `og-tab` and the calendar's days 40, `og-dots__dot`
 * 24 wide, the Composer's field 38, an `og-field__input` 42), and widens only the chips' target
 * (`::after` insets). The library follows the CSS; bringing them to `hit-min` without changing what is
 * drawn is B-60.
 */
private val KNOWN_TOUCH =
    setOf(
        "ActionsProbe: 101×36 dp",
        "ActionsProbe: 174×36 dp",
        "ActionsProbe: 67×36 dp",
        "Banner: 107×36 dp",
        "Banner: 71×36 dp",
        "BannerStates: 97×36 dp",
        "Button: 108×36 dp",
        "Button: 46×36 dp",
        "Card: 107×36 dp",
        "Card: 84×36 dp",
        "Composer: 224×38 dp",
        "ComposerStates: 224×38 dp",
        "DatePicker: 44×40 dp",
        "DatePickerStates: 44×40 dp",
        "FieldProbe: 290×42 dp",
        "PageDots: 24×44 dp",
        "PageDots: 36×44 dp",
        "PageDotsStates: 24×44 dp",
        "PageDotsStates: 36×44 dp",
        "Segmented: 116×40 dp",
        "SegmentedStates: 116×40 dp",
        "SegmentedStates: 175×40 dp",
        "Snackbar: 358×36 dp",
        "Snackbar: 91×36 dp",
        "Stepper: 68×36 dp",
        "Stepper: 69×36 dp",
        "Tabs: 102×40 dp",
        "Tabs: 82×40 dp",
        "TabsStates: 110×40 dp",
        "TabsStates: 89×40 dp",
        "TabsStates: 93×40 dp",
        "TextField: 304×42 dp",
        "TextField: 332×42 dp",
        "TextFieldStates: 290×42 dp",
        "TextFieldStates: 332×42 dp",
    )

/** Controls without a role or a label, each owned by an item. */
private val KNOWN_UNNAMED = emptySet<String>()

/** States said only in colour, each owned by an item. */
private val KNOWN_WORDLESS = emptySet<String>()
