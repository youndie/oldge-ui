package io.github.youndie.oldge.actions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** The rules in the Fab and Segmented READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class FabSegmentedBehaviourTest {
    private fun role(role: Role) = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

    private val canExpand = SemanticsMatcher.keyIsDefined(SemanticsActions.Expand)
    private val canCollapse = SemanticsMatcher.keyIsDefined(SemanticsActions.Collapse)

    @Test
    fun an_icon_fab_is_a_labelled_56_px_button() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeFab(OldgeIcons.Plus, "Создать", {}) } }
            onNodeWithContentDescription("Создать")
                .assertWidthIsEqualTo(56.dp)
                .assertHeightIsEqualTo(56.dp)
                .assert(role(Role.Button))
                .assert(!canExpand and !canCollapse)
        }

    @Test
    fun an_expandable_fab_offers_the_opposite_of_its_state() =
        runComposeUiTest {
            var open by mutableStateOf(false)
            setContent { OldgeTheme { OldgeFab(OldgeIcons.Plus, "Создать", { open = !open }, expanded = open) } }
            onNodeWithContentDescription("Создать").assert(canExpand and !canCollapse).performClick()
            assertEquals(true, open)
            onNodeWithContentDescription("Создать").assert(canCollapse and !canExpand)
        }

    @Test
    fun an_extended_fab_is_named_by_its_label() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeExtendedFab("Написать", OldgeIcons.Edit, {}) } }
            onNodeWithText("Написать").assertHeightIsEqualTo(56.dp).assert(role(Role.Button))
        }

    @Test
    fun a_segment_is_a_radio_button_and_selecting_one_reports_its_value() =
        runComposeUiTest {
            var period by mutableStateOf("week")
            setContent {
                OldgeTheme {
                    OldgeSegmented(
                        listOf(OldgeSegment("day", "День"), OldgeSegment("week", "Неделя")),
                        period,
                        { period = it },
                        label = "Период",
                    )
                }
            }
            onNodeWithText("Неделя").assertIsSelected().assert(role(Role.RadioButton))
            onNodeWithText("День").assertIsNotSelected().performClick()
            assertEquals("day", period)
            onNodeWithText("День").assertIsSelected()
            onNodeWithContentDescription("Период").assertHeightIsEqualTo(46.dp)
        }

    @Test
    fun segmented_takes_two_to_four_options() {
        for (count in listOf(1, 5)) {
            assertFailsWith<IllegalArgumentException>("$count options") {
                runComposeUiTest {
                    setContent {
                        OldgeTheme {
                            OldgeSegmented((1..count).map { OldgeSegment(it, "$it") }, 1, {}, label = "N")
                        }
                    }
                }
            }
        }
    }
}
