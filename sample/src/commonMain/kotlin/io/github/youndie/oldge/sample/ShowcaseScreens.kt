package io.github.youndie.oldge.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.actions.OldgeButtonVariant
import io.github.youndie.oldge.actions.OldgeChipGroup
import io.github.youndie.oldge.actions.OldgeFab
import io.github.youndie.oldge.actions.OldgeFilterChip
import io.github.youndie.oldge.actions.OldgeSegment
import io.github.youndie.oldge.actions.OldgeSegmented
import io.github.youndie.oldge.containers.OldgeAccordion
import io.github.youndie.oldge.containers.OldgeActionTile
import io.github.youndie.oldge.containers.OldgeActionTileTone
import io.github.youndie.oldge.containers.OldgeCard
import io.github.youndie.oldge.containers.OldgeList
import io.github.youndie.oldge.containers.OldgeListItem
import io.github.youndie.oldge.containers.OldgeScreenBody
import io.github.youndie.oldge.feedback.OldgeAvatar
import io.github.youndie.oldge.feedback.OldgeAvatarSize
import io.github.youndie.oldge.feedback.OldgeAvatarStatus
import io.github.youndie.oldge.feedback.OldgeBadge
import io.github.youndie.oldge.feedback.OldgeBadgeTone
import io.github.youndie.oldge.feedback.OldgeMeter
import io.github.youndie.oldge.feedback.OldgeSnackbar
import io.github.youndie.oldge.forms.OldgeSearchBar
import io.github.youndie.oldge.forms.OldgeSelect
import io.github.youndie.oldge.forms.OldgeSelectOption
import io.github.youndie.oldge.forms.OldgeSlider
import io.github.youndie.oldge.forms.OldgeSwitch
import io.github.youndie.oldge.forms.OldgeTextField
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.navigation.OldgeBottomNav
import io.github.youndie.oldge.navigation.OldgeCategory
import io.github.youndie.oldge.navigation.OldgeCategoryTabs
import io.github.youndie.oldge.navigation.OldgeNavItem
import io.github.youndie.oldge.navigation.OldgeWindowAction
import io.github.youndie.oldge.navigation.OldgeWindowBar
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.math.roundToInt

/**
 * The design system's Launcher page, the start screen of a mid-2000s launch shell: WindowBar,
 * CategoryTabs, a grid of ActionTiles, an Accordion with a list, a Meter and the BottomNav, on the
 * skin's body. Its strings are the page's.
 */
@Composable
public fun LauncherScreen(modifier: Modifier = Modifier) {
    val s = OldgeTheme.spacing
    var category by remember { mutableStateOf("doc") }
    var nav by remember { mutableStateOf("home") }
    Page(
        modifier,
        bar = {
            OldgeWindowBar(
                "Центр задач",
                icon = OldgeIcons.Grid,
                center = { OldgeBadge("Облако", tone = OldgeBadgeTone.Neutral) },
                actions =
                    listOf(
                        OldgeWindowAction(OldgeIcons.Help, "Справка", {
                        }),
                        OldgeWindowAction(OldgeIcons.Minus, "Свернуть", {}),
                    ),
            )
            OldgeCategoryTabs(
                listOf(
                    OldgeCategory("fav", OldgeIcons.Star, "Избранное"),
                    OldgeCategory("doc", OldgeIcons.Doc, "Документы"),
                    OldgeCategory("music", OldgeIcons.Note, "Музыка"),
                    OldgeCategory("photo", OldgeIcons.Image, "Фото"),
                    OldgeCategory("video", OldgeIcons.Film, "Видео"),
                    OldgeCategory("more", OldgeIcons.Plus, "Ещё"),
                ),
                category,
                { category = it },
            )
        },
        nav = { Nav(nav) { nav = it } },
        top = 0.dp,
    ) {
        // The page's `.grid`: two columns, 2 px between rows and `space-2` between columns, each tile
        // stretched to its row, as a grid item is.
        Column(verticalArrangement = Arrangement.spacedBy(GRID_ROW_GAP)) {
            Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(s.space2)) {
                OldgeActionTile(
                    "Новый документ",
                    OldgeIcons.Doc,
                    {},
                    Modifier.weight(1f).fillMaxHeight(),
                    description = "Пустой или из шаблона",
                )
                OldgeActionTile(
                    "Загрузить файлы",
                    OldgeIcons.Upload,
                    {},
                    Modifier.weight(1f).fillMaxHeight(),
                    tone = OldgeActionTileTone.Accent,
                )
            }
            Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(s.space2)) {
                OldgeActionTile(
                    "Загрузки",
                    OldgeIcons.Download,
                    {},
                    Modifier.weight(1f).fillMaxHeight(),
                    description = "12 новых",
                )
                OldgeActionTile(
                    "Обновить",
                    OldgeIcons.Refresh,
                    {},
                    Modifier.weight(1f).fillMaxHeight(),
                    tone = OldgeActionTileTone.Bezel,
                )
            }
        }
        OldgeAccordion("Недавние", icon = OldgeIcons.Star) {
            OldgeList(bare = true) {
                row {
                    OldgeListItem(
                        "Отчёт за квартал",
                        icon = OldgeIcons.Doc,
                        value = "09:14",
                        dense = true,
                        onClick = {},
                    )
                }
                row {
                    OldgeListItem(
                        "Скан паспорта.png",
                        icon = OldgeIcons.Image,
                        value = "вчера",
                        dense = true,
                        onClick = {},
                    )
                }
                row {
                    OldgeListItem(
                        "Голосовая заметка",
                        icon = OldgeIcons.Note,
                        value = "пн",
                        dense = true,
                        onClick = {},
                    )
                }
            }
        }
        OldgeAccordion("Инструменты", icon = OldgeIcons.Gear, defaultOpen = false) {}
        OldgeMeter(0.37f, "Хранилище", "23,4 / 64 ГБ")
    }
}

/**
 * The design system's FeedScreen page: a SearchBar with the user's Avatar, a ChipGroup filter, a
 * media Card and a Card under a bar, the Fab that rises above the Snackbar, and the BottomNav. Closing
 * the Snackbar lets the Fab down again. Its strings are the page's.
 */
@Composable
public fun FeedScreen(modifier: Modifier = Modifier) {
    var filter by remember { mutableStateOf("new") }
    var nav by remember { mutableStateOf("home") }
    var snack by remember { mutableStateOf(true) }
    var fab by remember { mutableStateOf(false) }
    Box(modifier) {
        Page(
            Modifier.fillMaxSize(),
            bar = {
                OldgeWindowBar(
                    "Лента",
                    icon = OldgeIcons.Grid,
                    actions = listOf(OldgeWindowAction(OldgeIcons.Bell, "События", {})),
                )
            },
            nav = {
                OldgeBottomNav(
                    listOf(
                        OldgeNavItem("home", OldgeIcons.Home, "Лента"),
                        OldgeNavItem("search", OldgeIcons.Search, "Поиск"),
                        OldgeNavItem("bell", OldgeIcons.Bell, "События"),
                        OldgeNavItem("me", OldgeIcons.User, "Профиль"),
                    ),
                    nav,
                    { nav = it },
                )
            },
        ) {
            OldgeSearchBar(
                placeholder = "Поиск по ленте",
                trailing = {
                    OldgeAvatar(
                        name = "Павел Вотяков",
                        size = OldgeAvatarSize.Small,
                        status = OldgeAvatarStatus.Online,
                    )
                },
            )
            OldgeChipGroup("Фильтр", scroll = true) {
                OldgeFilterChip("Новое", filter == "new", { filter = "new" })
                OldgeFilterChip("Популярное", filter == "pop", { filter = "pop" })
                OldgeFilterChip("Моё", filter == "my", { filter = "my" })
            }
            OldgeCard(title = "Отпуск 2006", subtitle = "Анна Ким · 148 фото", media = OldgeIcons.Image, onClick = {})
            OldgeCard(
                title = "Резервная копия через час",
                text = "Подключите зарядку, чтобы не прерывать.",
                bar = "Напоминание",
                barIcon = OldgeIcons.Bell,
            )
        }
        // The page's own placement: the Fab 16 dp from the right, 148 dp up while the Snackbar is
        // there and 88 without it; the Snackbar 12 dp in from each side, 84 dp up.
        OldgeFab(
            OldgeIcons.Plus,
            "Создать",
            { fab = !fab },
            Modifier
                .align(
                    Alignment.BottomEnd,
                ).padding(end = FAB_END, bottom = if (snack) FAB_OVER_SNACK else FAB_BOTTOM),
            expanded = fab,
        )
        if (snack) {
            OldgeSnackbar(
                "Пост сохранён в черновики",
                Modifier
                    .align(
                        Alignment.BottomCenter,
                    ).padding(start = SNACK_SIDE, end = SNACK_SIDE, bottom = SNACK_BOTTOM),
                actionLabel = "Отменить",
                onClose = { snack = false },
                inline = true,
            )
        }
    }
}

/**
 * The design system's SettingsScreen page: Segmented to choose the [skin], a List with a Switch, a
 * Select, a Slider, a TextField and a pair of buttons. Choosing a skin calls [onSkinChange]; the app
 * above changes the theme. Its strings are the page's.
 */
@Composable
public fun SettingsScreen(
    skin: OldgeSkin,
    onSkinChange: (OldgeSkin) -> Unit,
    modifier: Modifier = Modifier,
) {
    var nav by remember { mutableStateOf("me") }
    var notify by remember { mutableStateOf(true) }
    var network by remember { mutableStateOf("wifi") }
    var volume by remember { mutableFloatStateOf(VOLUME) }
    var name by remember { mutableStateOf("Телефон Павла") }
    Page(
        modifier,
        bar = {
            OldgeWindowBar(
                "Настройки",
                subtitle = "Павел · Pro",
                onBack = {},
                actions = listOf(OldgeWindowAction(OldgeIcons.Close, "Закрыть", {})),
            )
        },
        nav = { Nav(nav) { nav = it } },
        top = OldgeTheme.spacing.space4,
    ) {
        OldgeSegmented(
            listOf(
                OldgeSegment(OldgeSkin.Toxic, "Toxic"),
                OldgeSegment(OldgeSkin.Media, "Media"),
                OldgeSegment(OldgeSkin.Crystal, "Crystal"),
            ),
            skin,
            onSkinChange,
            "Скин",
            Modifier.fillMaxWidth(),
        )
        OldgeList(label = "Основное") {
            row {
                OldgeListItem(
                    "Профиль",
                    subtitle = "pavel@example.com",
                    icon = OldgeIcons.User,
                    chevron = true,
                    onClick = {},
                )
            }
            row {
                OldgeListItem(
                    "Уведомления",
                    icon = OldgeIcons.Bell,
                    trailing = { OldgeSwitch("Уведомления", notify, { notify = it }, showLabel = false) },
                )
            }
            row {
                OldgeListItem(
                    "Пароль и вход",
                    icon = OldgeIcons.Lock,
                    value = "3 дня назад",
                    chevron = true,
                    onClick = {},
                )
            }
        }
        OldgeSelect(
            "Синхронизировать через",
            listOf(
                OldgeSelectOption("wifi", "Только Wi-Fi", OldgeIcons.Cloud),
                OldgeSelectOption("any", "Любую сеть", OldgeIcons.Refresh),
            ),
            network,
            { network = it },
            Modifier.fillMaxWidth(),
        )
        OldgeSlider(volume, {
            volume = it
        }, Modifier.fillMaxWidth(), label = "Громкость сигналов", valueText = "${(volume * PERCENT).roundToInt()}%")
        OldgeTextField("Имя устройства", name, { name = it }, Modifier.fillMaxWidth())
        // The page's `display: flex; gap: 8; justify-content: flex-end` row, not `og-actions`.
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2, Alignment.End),
        ) {
            OldgeButton("Сбросить", {})
            OldgeButton("Сохранить", {}, variant = OldgeButtonVariant.Primary)
        }
    }
}

/** The four sections the Launcher and Settings pages share. */
@Composable
private fun Nav(
    value: String,
    onChange: (String) -> Unit,
) = OldgeBottomNav(
    listOf(
        OldgeNavItem("home", OldgeIcons.Home, "Главная"),
        OldgeNavItem("search", OldgeIcons.Search, "Поиск"),
        OldgeNavItem("bell", OldgeIcons.Bell, "События"),
        OldgeNavItem("me", OldgeIcons.User, "Профиль"),
    ),
    value,
    onChange,
)

/** A page: the [bar], the `.scroll` column (padded [top] 16 16 16, `space-3` apart) that takes what is left, and the [nav]. */
@Composable
private fun Page(
    modifier: Modifier,
    bar: @Composable () -> Unit,
    nav: @Composable () -> Unit,
    top: Dp = OldgeTheme.spacing.space3,
    content: @Composable ColumnScope.() -> Unit,
) {
    val s = OldgeTheme.spacing
    OldgeScreenBody(modifier) {
        Column(Modifier.fillMaxSize()) {
            bar()
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = s.space4, end = s.space4, top = top, bottom = s.space4),
                verticalArrangement = Arrangement.spacedBy(s.space3),
                content = content,
            )
            nav()
        }
    }
}

private const val VOLUME = 0.6f
private const val PERCENT = 100
private val GRID_ROW_GAP = 2.dp // css literal: Launcher preview, `.grid { gap: 2px var(--space-2) }`
private val FAB_END = 16.dp // css literal: FeedScreen preview, the Fab's `right: 16`
private val FAB_OVER_SNACK = 148.dp // css literal: FeedScreen preview, the Fab's `bottom: snack ? 148 : 88`
private val FAB_BOTTOM = 88.dp // css literal: FeedScreen preview, the Fab's `bottom: snack ? 148 : 88`
private val SNACK_SIDE = 12.dp // css literal: FeedScreen preview, the Snackbar's `left: 12, right: 12`
private val SNACK_BOTTOM = 84.dp // css literal: FeedScreen preview, the Snackbar's `bottom: 84`
