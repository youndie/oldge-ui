package io.github.youndie.oldge.forms

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals

/** The rules in the Select and CodeInput READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class SelectCodeBehaviourTest {
    private val networks =
        listOf(
            OldgeSelectOption("wifi", "Только Wi-Fi"),
            OldgeSelectOption("any", "Любую сеть"),
            OldgeSelectOption("off", "Вручную"),
        )

    /**
     * The open list of a Select whose options have no icons has no Menu strip: its labels start
     * `space-3` into the row, on `surface`. With an icon on any option, the strip and its 40 px
     * column are back, which is also the control that the sampled pixel can see the strip (B-63).
     */
    @Test
    fun an_icon_less_selects_list_has_no_empty_strip_and_one_with_icons_keeps_it() {
        fun open(options: List<OldgeSelectOption<String>>): Triple<Float, Color, Pair<Color, Color>> {
            lateinit var result: Triple<Float, Color, Pair<Color, Color>>
            runComposeUiTest {
                lateinit var colors: Pair<Color, Color>
                setContent {
                    OldgeTheme {
                        colors = OldgeTheme.colors.surface to OldgeTheme.colors.pillLo
                        OldgeSelect("Экран", options, "a", {})
                    }
                }
                onNodeWithContentDescription("Экран").performClick()
                val label = onNodeWithText("Вход", useUnmergedTree = true).getBoundsInRoot()
                val popup = onAllNodes(isRoot()).let { it[it.fetchSemanticsNodes().size - 1] }
                val image = popup.captureToImage().toPixelMap()
                val y = with(density) { ((label.top + label.bottom) / 2).roundToPx() }
                val x = with(density) { STRIP_PROBE.roundToPx() }
                result = Triple(label.left.value, image[x, y], colors)
            }
            return result
        }
        val (plainAt, plainPixel, colors) =
            open(
                listOf(OldgeSelectOption("a", "Лента"), OldgeSelectOption("b", "Вход")),
            )
        val (surface, pillLo) = colors
        assertEquals(PLAIN_LABEL, plainAt, "the icon-less list's label starts at $plainAt dp")
        assertEquals(surface, plainPixel, "the icon-less list has the strip's colour where the strip would be")
        val (iconAt, iconPixel, _) =
            open(
                listOf(
                    OldgeSelectOption("a", "Лента", OldgeIcons.Grid),
                    OldgeSelectOption("b", "Вход", OldgeIcons.Lock),
                ),
            )
        assertEquals(ICON_LABEL, iconAt, "the list with icons has its label at $iconAt dp")
        assertEquals(pillLo, iconPixel, "the list with icons lost its strip")
    }

    @Test
    fun a_select_is_a_named_drop_down_list_that_reads_its_choice_and_opens_to_change_it() =
        runComposeUiTest {
            var picked by mutableStateOf("wifi")
            setContent { OldgeTheme { OldgeSelect("Синхронизировать через", networks, picked, { picked = it }) } }
            onNodeWithContentDescription("Синхронизировать через")
                .assertHeightIsAtLeast(44.dp)
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.DropdownList))
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Только Wi-Fi"))
                .performClick()
            onNodeWithText("Вручную").performClick()
            assertEquals("off", picked)
            onNodeWithContentDescription("Синхронизировать через")
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Вручную"))
        }

    @Test
    fun the_code_field_is_one_real_field_for_a_one_time_code() =
        runComposeUiTest {
            setContent { OldgeTheme { OldgeCodeInput("Код из СМС", "", {}, error = "Код устарел — запросите новый") } }
            onNodeWithContentDescription("Код из СМС")
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentType, ContentType.SmsOtpCode))
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Код устарел — запросите новый"))
        }

    @Test
    fun only_digits_up_to_the_length_are_taken_and_the_last_one_completes_the_code() =
        runComposeUiTest {
            var code by mutableStateOf("")
            var completed by mutableIntStateOf(0)
            var last = ""
            setContent {
                OldgeTheme {
                    OldgeCodeInput("Код", code, { code = it }, length = 4, onComplete = {
                        completed++
                        last = it
                    })
                }
            }
            onNodeWithContentDescription("Код").performTextInput("1a2-3")
            assertEquals("123", code)
            assertEquals(0, completed)
            onNodeWithContentDescription("Код").performTextInput("45")
            assertEquals("1234", code)
            assertEquals(1, completed)
            assertEquals("1234", last)
        }
}

// The row starts at the 1 px border and 3 px inset, and the label follows the 12 px pad or the 40 px column.
private const val PLAIN_LABEL = 16f
private const val ICON_LABEL = 44f
private val STRIP_PROBE = 10.dp
