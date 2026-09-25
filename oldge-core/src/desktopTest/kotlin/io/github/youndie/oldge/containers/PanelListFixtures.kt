package io.github.youndie.oldge.containers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.pressed
import io.github.youndie.oldge.feedback.OldgeBadge
import io.github.youndie.oldge.feedback.OldgeBadgeTone
import io.github.youndie.oldge.feedback.OldgeMeter
import io.github.youndie.oldge.forms.OldgeSwitch
import io.github.youndie.oldge.harness.DemoText
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.type.OldgeText
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/{Panel,List,ListItem,ListSection}/preview.html, string for string.

@Composable
private fun PanelDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgePanel(title = "Хранилище") {
            DemoText("Использовано 23,4 ГБ из 64 ГБ.")
            OldgeMeter(0.37f, "Диск", "37%")
        }
        OldgePanel(sunken = true) {
            // `.og-muted`.
            OldgeText(
                "Утопленная панель — для вывода и журналов.",
                style = OldgeTheme.type.body.copy(color = OldgeTheme.colors.inkMuted),
            )
        }
    }

@Composable
private fun ListDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        var chosen by remember { mutableStateOf("b") }
        var sync by remember { mutableStateOf(true) }
        OldgeList(label = "Аккаунт") {
            row {
                OldgeListItem(
                    "Профиль",
                    subtitle = "Павел · pavel@example.com",
                    icon = OldgeIcons.User,
                    chevron = true,
                    onClick = {},
                )
            }
            row {
                OldgeListItem(
                    "Синхронизация",
                    subtitle = "Последняя — 5 минут назад",
                    icon = OldgeIcons.Cloud,
                    trailing = { OldgeSwitch("Автосинхронизация", sync, { sync = it }, showLabel = false) },
                )
            }
            row {
                OldgeListItem("Пароль и вход", icon = OldgeIcons.Lock, value = "Изменить", chevron = true, onClick = {})
            }
            row {
                OldgeListItem(
                    "Резервная копия",
                    subtitle = "Выбрано",
                    icon = OldgeIcons.Folder,
                    selected = chosen == "b",
                    onClick = { chosen = "b" },
                )
            }
        }
    }

@Composable
private fun ListItemDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeList {
            row { OldgeListItem("Уведомления", icon = OldgeIcons.Bell, value = "Вкл.", chevron = true, onClick = {}) }
            row {
                OldgeListItem(
                    "Загрузки",
                    icon = OldgeIcons.Download,
                    trailing = { OldgeBadge("12", tone = OldgeBadgeTone.Accent, count = true) },
                )
            }
        }
    }

/** `.og-demo` with `height: 260; overflow: auto`: the demo is the scroll, its padding inside it. */
@Composable
private fun ListSectionDemo(
    skin: OldgeSkin,
    scrolled: Int = 0,
) = OldgeDemo(skin, padded = false) {
    val s = OldgeTheme.spacing.space4
    LazyColumn(
        Modifier.fillMaxWidth().height(SECTION_DEMO_HEIGHT),
        state = rememberLazyListState(0, scrolled),
        contentPadding = PaddingValues(s),
    ) {
        // `.og-demo`'s own 12 px flex gap stands between the sections, on top of their margin.
        oldgeListSections(extraSpacing = OldgeTheme.spacing.space3) {
            section("Сегодня", meta = "3 новых") {
                OldgeList {
                    row { Mail("Анна Ким", "Фото с поездки", "09:14") }
                    row { Mail("Банк", "Выписка за сентябрь", "08:02") }
                }
            }
            section("Вчера") {
                OldgeList {
                    row { Mail("Иван Ли", "Созвон переносим", "чт") }
                    row { Mail("Сервис", "Код входа", "чт") }
                }
            }
        }
    }
}

@Composable
private fun Mail(
    title: String,
    subtitle: String,
    value: String,
) = OldgeListItem(title, subtitle = subtitle, icon = OldgeIcons.Mail, value = value, onClick = {})

/** What the previews do not show: a pressed row, a dense one, the selected one pressed, a striped list. */
@Composable
private fun ListItemStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeList(striped = true) {
            row {
                OldgeListItem("Нажата", icon = OldgeIcons.Bell, value = "Вкл.", chevron = true, onClick = {
                }, interactionSource = pressed())
            }
            row { OldgeListItem("Плотная строка", icon = OldgeIcons.Doc, dense = true, chevron = true, onClick = {}) }
            row { OldgeListItem("Зебра", subtitle = "Чётная строка — surface-2", icon = OldgeIcons.Folder) }
            row {
                OldgeListItem(
                    "Выбрана",
                    subtitle = "on-select",
                    icon = OldgeIcons.Star,
                    value = "Да",
                    chevron = true,
                    selected = true,
                    onClick = {},
                )
            }
        }
    }

/**
 * The ListItem README's stress rules: a long title wraps to two lines rather than being cut, and in a
 * list of 300 dp (20 em of its 15 px font) the value moves under the title.
 */
@Composable
private fun ListItemNarrow(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeList {
            row {
                OldgeListItem(
                    "Автоматическое резервное копирование фотографий и видео",
                    icon = OldgeIcons.Cloud,
                    value = "Только по Wi-Fi",
                    chevron = true,
                    onClick = {},
                )
            }
        }
        OldgeList(Modifier.width(NARROW_LIST)) {
            row {
                OldgeListItem(
                    "Уведомления",
                    icon = OldgeIcons.Bell,
                    value = "Вкл., кроме ночи",
                    chevron = true,
                    onClick = {},
                )
            }
            row { OldgeListItem("Загрузки", subtitle = "Без значения", icon = OldgeIcons.Download) }
        }
    }

/** A flush panel holding a bare list, which is what `flush` is for. */
@Composable
private fun PanelStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgePanel(title = "Устройства", flush = true) {
            OldgeList(bare = true) {
                row {
                    OldgeListItem(
                        "Ноутбук",
                        subtitle = "В сети",
                        icon = OldgeIcons.Doc,
                        chevron = true,
                        onClick = {},
                    )
                }
                row {
                    OldgeListItem(
                        "Телефон",
                        subtitle = "Вчера",
                        icon = OldgeIcons.User,
                        chevron = true,
                        onClick = {},
                    )
                }
            }
        }
    }

private val SECTION_DEMO_HEIGHT = 260.dp
private val NARROW_LIST = 302.dp // 300 dp of content inside its two 1 dp borders

@ViddikScreenshot(group = "Panel", name = "Toxic", width = 390, height = 246)
@Composable
fun PanelToxic() = PanelDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Panel", name = "Media", width = 390, height = 246)
@Composable
fun PanelMedia() = PanelDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Panel", name = "Crystal", width = 390, height = 246)
@Composable
fun PanelCrystal() = PanelDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "List", name = "Toxic", width = 390, height = 300)
@Composable
fun ListToxic() = ListDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "List", name = "Media", width = 390, height = 300)
@Composable
fun ListMedia() = ListDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "List", name = "Crystal", width = 390, height = 300)
@Composable
fun ListCrystal() = ListDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "ListItem", name = "Toxic", width = 390, height = 138)
@Composable
fun ListItemToxic() = ListItemDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "ListItem", name = "Media", width = 390, height = 138)
@Composable
fun ListItemMedia() = ListItemDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "ListItem", name = "Crystal", width = 390, height = 138)
@Composable
fun ListItemCrystal() = ListItemDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "ListSection", name = "Toxic", width = 390, height = 300)
@Composable
fun ListSectionToxic() = ListSectionDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "ListSection", name = "Media", width = 390, height = 300)
@Composable
fun ListSectionMedia() = ListSectionDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "ListSection", name = "Crystal", width = 390, height = 300)
@Composable
fun ListSectionCrystal() = ListSectionDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "PanelStates", name = "Flush with a bare list Toxic", width = 390, height = 180)
@Composable
fun PanelStatesToxic() = PanelStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "PanelStates", name = "Flush with a bare list Media", width = 390, height = 180)
@Composable
fun PanelStatesMedia() = PanelStates(OldgeSkin.Media)

@ViddikScreenshot(group = "PanelStates", name = "Flush with a bare list Crystal", width = 390, height = 180)
@Composable
fun PanelStatesCrystal() = PanelStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "ListItemStates", name = "Pressed dense striped selected Toxic", width = 390, height = 250)
@Composable
fun ListItemStatesToxic() = ListItemStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "ListItemStates", name = "Pressed dense striped selected Media", width = 390, height = 250)
@Composable
fun ListItemStatesMedia() = ListItemStates(OldgeSkin.Media)

@ViddikScreenshot(group = "ListItemStates", name = "Pressed dense striped selected Crystal", width = 390, height = 250)
@Composable
fun ListItemStatesCrystal() = ListItemStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "ListItemStates", name = "Long title and narrow list Toxic", width = 390, height = 250)
@Composable
fun ListItemNarrowToxic() = ListItemNarrow(OldgeSkin.Toxic)

@ViddikScreenshot(group = "ListItemStates", name = "Long title and narrow list Media", width = 390, height = 250)
@Composable
fun ListItemNarrowMedia() = ListItemNarrow(OldgeSkin.Media)

@ViddikScreenshot(group = "ListItemStates", name = "Long title and narrow list Crystal", width = 390, height = 250)
@Composable
fun ListItemNarrowCrystal() = ListItemNarrow(OldgeSkin.Crystal)

// Scrolled 40 px: the first header holds at the top and the next section comes up under it. Compose
// sticks it at the list's edge, over the 16 px content padding; Chrome sticks it inside the padding,
// 16 px down (measured on the preview, B-20). A deviation, in research §1.7, not a parity number.
@ViddikScreenshot(group = "ListSectionStates", name = "Scrolled 40 Toxic", width = 390, height = 300)
@Composable
fun ListSectionStatesToxic() = ListSectionDemo(OldgeSkin.Toxic, scrolled = 40)

@ViddikScreenshot(group = "ListSectionStates", name = "Scrolled 40 Media", width = 390, height = 300)
@Composable
fun ListSectionStatesMedia() = ListSectionDemo(OldgeSkin.Media, scrolled = 40)

@ViddikScreenshot(group = "ListSectionStates", name = "Scrolled 40 Crystal", width = 390, height = 300)
@Composable
fun ListSectionStatesCrystal() = ListSectionDemo(OldgeSkin.Crystal, scrolled = 40)
