package io.github.youndie.oldge.containers

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.forms.OldgeSwitch
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The rules in the Panel, List, ListItem and ListSection READMEs that are behaviour rather than look. */
@OptIn(ExperimentalTestApi::class)
class PanelListBehaviourTest {
    @Test
    fun a_pressable_row_is_one_52_px_button_and_a_dense_one_44() =
        runComposeUiTest {
            var taps by mutableIntStateOf(0)
            setContent {
                OldgeTheme {
                    OldgeList {
                        row {
                            OldgeListItem(
                                "Уведомления",
                                Modifier.testTag("row"),
                                icon = OldgeIcons.Bell,
                                onClick = { taps++ },
                            )
                        }
                        row { OldgeListItem("Плотная", Modifier.testTag("dense"), dense = true, onClick = {}) }
                    }
                }
            }
            onNodeWithTag("row")
                .assertHeightIsEqualTo(52.dp)
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
                .performClick()
            assertEquals(1, taps)
            onNodeWithTag("dense").assertHeightIsEqualTo(44.dp)
        }

    @Test
    fun a_row_without_an_action_is_not_a_button_and_a_chevron_makes_it_one() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    OldgeList {
                        row { OldgeListItem("Загрузки", Modifier.testTag("plain")) }
                        row { OldgeListItem("Пароль", Modifier.testTag("chevron"), chevron = true) }
                    }
                }
            }
            onNodeWithTag("plain").assert(SemanticsMatcher.keyNotDefined(SemanticsActions.OnClick))
            onNodeWithTag("chevron").assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
        }

    @Test
    fun a_selected_row_says_it_is_selected() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    OldgeList {
                        row { OldgeListItem("Резервная копия", Modifier.testTag("on"), selected = true, onClick = {}) }
                        row { OldgeListItem("Профиль", Modifier.testTag("off"), onClick = {}) }
                    }
                }
            }
            onNodeWithTag("on").assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
            onNodeWithTag("off").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Selected))
        }

    @Test
    fun a_long_title_wraps_to_two_lines_and_stops_there() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    OldgeList(Modifier.width(360.dp)) {
                        row {
                            OldgeListItem(
                                "Автоматическое резервное копирование фотографий, видео и документов с телефона",
                                icon = OldgeIcons.Cloud,
                                value = "Только по Wi-Fi",
                            )
                        }
                    }
                }
            }
            // Two 20 px lines of the body style.
            onNode(hasText("Автоматическое", substring = true), useUnmergedTree = true).assertHeightIsEqualTo(40.dp)
        }

    @Test
    fun a_value_takes_at_most_42_percent_of_the_row() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    OldgeList(Modifier.width(362.dp)) {
                        row { OldgeListItem("Сеть", value = "Только по Wi-Fi, кроме роуминга и ночных часов") }
                    }
                }
            }
            // The row's content box: 360 dp of list less 12 dp of padding each side.
            val value = onNode(hasText("Только", substring = true), useUnmergedTree = true).getUnclippedBoundsInRoot()
            assertTrue(value.right - value.left <= 336.dp * 0.42f, "the value is ${value.right - value.left} wide")
        }

    @Test
    fun in_a_list_of_300_dp_the_value_moves_under_the_title() {
        assertTrue(valueIsUnderTitle(listContent = 300.dp), "at 300 dp the value stayed beside the title")
        assertTrue(!valueIsUnderTitle(listContent = 301.dp), "at 301 dp the value left the title's line")
    }

    @Test
    fun the_narrow_width_grows_with_the_system_font() {
        // 20 em of the list's 15 sp: 360 dp at a font scale of 1.2.
        assertTrue(
            valueIsUnderTitle(listContent = 360.dp, fontScale = 1.2f),
            "at 360 dp and 1.2 the value stayed beside",
        )
        assertTrue(!valueIsUnderTitle(listContent = 361.dp, fontScale = 1.2f), "at 361 dp and 1.2 the value moved")
    }

    private fun valueIsUnderTitle(
        listContent: Dp,
        fontScale: Float = 1f,
    ): Boolean {
        var under = false
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    val d = LocalDensity.current
                    CompositionLocalProvider(LocalDensity provides Density(d.density, fontScale)) {
                        OldgeList(Modifier.width(listContent + 2.dp)) {
                            row {
                                OldgeListItem(
                                    "Уведомления",
                                    icon = OldgeIcons.Bell,
                                    value = "Вкл.",
                                    chevron = true,
                                    onClick = {},
                                )
                            }
                        }
                    }
                }
            }
            val title = onNodeWithText("Уведомления", useUnmergedTree = true).getUnclippedBoundsInRoot()
            val value = onNodeWithText("Вкл.", useUnmergedTree = true).getUnclippedBoundsInRoot()
            under = value.top >= title.bottom
        }
        return under
    }

    @Test
    fun a_switch_in_a_row_is_named_by_its_label_without_showing_it() =
        runComposeUiTest {
            var on by mutableStateOf(true)
            setContent {
                OldgeTheme {
                    OldgeList {
                        row {
                            OldgeListItem(
                                "Синхронизация",
                                trailing = {
                                    OldgeSwitch(
                                        "Автосинхронизация",
                                        on,
                                        { on = it },
                                        Modifier.testTag("sw"),
                                        showLabel = false,
                                    )
                                },
                            )
                        }
                    }
                }
            }
            onNode(hasText("Автосинхронизация"), useUnmergedTree = true).assertDoesNotExist()
            // The empty text column keeps its 12 px gap before the 56 px track.
            onNodeWithTag("sw")
                .assert(hasContentDescription("Автосинхронизация"))
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch))
                .assertWidthIsEqualTo(68.dp)
                .performClick()
            assertEquals(false, on)
        }

    @Test
    fun a_list_is_a_collection_of_its_rows_under_its_label() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    OldgeList(Modifier.testTag("list"), label = "Аккаунт") {
                        row { OldgeListItem("Профиль") }
                        row { OldgeListItem("Пароль") }
                        row { OldgeListItem("Копия") }
                    }
                }
            }
            onNodeWithTag("list")
                .assert(hasContentDescription("Аккаунт"))
                .assert(SemanticsMatcher("three rows") { it.config[SemanticsProperties.CollectionInfo].rowCount == 3 })
        }

    @Test
    fun a_panel_title_and_a_section_header_are_headings() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    OldgePanel(title = "Хранилище") {}
                    OldgeListSectionHeader("Сегодня", meta = "3 новых")
                }
            }
            onNode(hasText("Хранилище") and SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading)).assertExists()
            onNode(hasText("Сегодня") and SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading)).assertExists()
        }

    @Test
    fun a_section_header_holds_at_the_top_while_its_section_scrolls_under_it() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    LazyColumn(Modifier.testTag("scroll").height(200.dp), state = rememberLazyListState(0, 60)) {
                        oldgeListSections {
                            section("Сегодня") {
                                OldgeList { repeat(4) { i -> row { OldgeListItem("Письмо $i") } } }
                            }
                            section("Вчера") { OldgeList { row { OldgeListItem("Старое") } } }
                        }
                    }
                }
            }
            val top = onNodeWithTag("scroll").getUnclippedBoundsInRoot().top
            // Unstuck, 60 dp of scroll would have put the header's title above the list's top.
            // A header scrolled out stays composed but unplaced, with unspecified bounds that pass any
            // comparison: it must be on screen first.
            val title =
                onNode(
                    hasText("Сегодня"),
                    useUnmergedTree = true,
                ).assertIsDisplayed().getUnclippedBoundsInRoot()
            assertTrue(title.top >= top && title.bottom <= top + HEAD_HEIGHT, "the header left the top: $title")
            // Scrolled 60 dp, the first row (8 dp below a 30 dp header) has gone under the header.
            val first = onNodeWithText("Письмо 0", useUnmergedTree = true).getUnclippedBoundsInRoot()
            assertTrue(first.top < top + HEAD_HEIGHT, "the rows did not scroll under the header")
        }
}

private val HEAD_HEIGHT = 30.dp
