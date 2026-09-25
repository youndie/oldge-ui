package io.github.youndie.oldge.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.actions.OldgeChipGroup
import io.github.youndie.oldge.actions.OldgeFilterChip
import io.github.youndie.oldge.containers.OldgeList
import io.github.youndie.oldge.containers.OldgeListItem
import io.github.youndie.oldge.containers.OldgeScreenBody
import io.github.youndie.oldge.containers.OldgeSwipeAction
import io.github.youndie.oldge.containers.OldgeSwipeRow
import io.github.youndie.oldge.containers.OldgeSwipeTone
import io.github.youndie.oldge.containers.oldgeListSections
import io.github.youndie.oldge.feedback.OldgeAvatar
import io.github.youndie.oldge.feedback.OldgeAvatarSize
import io.github.youndie.oldge.feedback.OldgeEmptyState
import io.github.youndie.oldge.feedback.OldgeEmptyTone
import io.github.youndie.oldge.feedback.OldgeLazyPullRefresh
import io.github.youndie.oldge.feedback.OldgeSpinner
import io.github.youndie.oldge.feedback.OldgeSpinnerSize
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.navigation.OldgeBottomNav
import io.github.youndie.oldge.navigation.OldgeNavItem
import io.github.youndie.oldge.navigation.OldgeWindowAction
import io.github.youndie.oldge.navigation.OldgeWindowBar
import io.github.youndie.oldge.theme.OldgeTheme
import kotlinx.coroutines.delay

/** What [InboxScreen]'s filter shows: every letter, the unread (none), or the archive (which fails). */
public enum class InboxFilter { All, Unread, Archive }

private class Letter(
    val from: String,
    val text: String,
    val time: String,
)

private val today =
    listOf(
        Letter("Анна Ким", "Фото с поездки: 148 штук, выбрала лучшие", "09:14"),
        Letter("Банк", "Выписка за сентябрь готова", "08:02"),
        Letter("Иван Ли", "Созвон переносим на четверг, ок?", "07:40"),
    )
private val yesterday =
    listOf(
        Letter("Сервис", "Код входа: 482 910", "вчера"),
        Letter("Мария Орлова", "Черновик договора во вложении", "вчера"),
        Letter("Лента", "3 новых комментария", "вчера"),
    )
private val earlier =
    listOf(
        Letter("Павел", "Напоминание: резервная копия", "пн"),
        Letter("Магазин", "Заказ передан в доставку", "пн"),
        Letter("Анна Ким", "Ну что, едем?", "вс"),
    )

/**
 * The design system's InboxScreen stress page, from this library's components alone: sticky sections
 * of swipe rows inside a pull to refresh, a spinner that loads more at the end, and the filters
 * «Непрочитанные» and «Архив», which show the empty state and the error. The list between the bar and
 * the navigation is what scrolls, so a large font never pushes the navigation off the screen.
 *
 * It opens refreshing for [refreshMillis], as the page does. Its strings are the page's.
 */
@Composable
public fun InboxScreen(
    modifier: Modifier = Modifier,
    refreshMillis: Long = REFRESH_MS,
) {
    val s = OldgeTheme.spacing
    var filter by remember { mutableStateOf(InboxFilter.All) }
    var nav by remember { mutableStateOf("mail") }
    var busy by remember { mutableStateOf(true) }
    LaunchedEffect(busy) {
        if (busy) {
            delay(refreshMillis)
            busy = false
        }
    }
    OldgeScreenBody(modifier) {
        Column(Modifier.fillMaxSize()) {
            OldgeWindowBar(
                "Входящие",
                subtitle = "128 писем",
                icon = OldgeIcons.Mail,
                actions =
                    listOf(
                        OldgeWindowAction(OldgeIcons.Search, "Поиск", {}),
                        OldgeWindowAction(OldgeIcons.More, "Ещё", {}),
                    ),
            )
            Box(Modifier.padding(start = s.space4, end = s.space4, top = s.space3, bottom = s.space2)) {
                OldgeChipGroup("Фильтр", scroll = true) {
                    OldgeFilterChip("Все", filter == InboxFilter.All, { filter = InboxFilter.All })
                    OldgeFilterChip("Непрочитанные", filter == InboxFilter.Unread, { filter = InboxFilter.Unread })
                    OldgeFilterChip("Архив", filter == InboxFilter.Archive, { filter = InboxFilter.Archive })
                }
            }
            // `flex: 1; min-height: 0`: the body takes what is left and scrolls inside it.
            Box(Modifier.weight(1f).fillMaxWidth().padding(horizontal = s.space4)) {
                when (filter) {
                    InboxFilter.Unread -> {
                        OldgeEmptyState("Всё прочитано", text = "Новые письма появятся здесь.", icon = OldgeIcons.Mail)
                    }

                    InboxFilter.Archive -> {
                        OldgeEmptyState(
                            "Архив не загрузился",
                            text = "Сервер не ответил за 30 секунд. Проверьте соединение.",
                            tone = OldgeEmptyTone.Error,
                            action = { OldgeButton("Повторить", {}, icon = OldgeIcons.Refresh) },
                        )
                    }

                    InboxFilter.All -> {
                        OldgeLazyPullRefresh(busy, { busy = true }, Modifier.fillMaxSize()) {
                            oldgeListSections {
                                section("Сегодня", meta = "3 новых") { Letters(today, open = 1) }
                                section("Вчера") { Letters(yesterday) }
                                section("Ранее") { Letters(earlier) }
                            }
                            item {
                                Box(Modifier.fillMaxWidth().padding(s.space4), contentAlignment = Alignment.Center) {
                                    OldgeSpinner(size = OldgeSpinnerSize.Small, label = "Загружаю ещё…")
                                }
                            }
                        }
                    }
                }
            }
            OldgeBottomNav(
                listOf(
                    OldgeNavItem("mail", OldgeIcons.Mail, "Почта"),
                    OldgeNavItem("search", OldgeIcons.Search, "Поиск"),
                    OldgeNavItem("bell", OldgeIcons.Bell, "События"),
                    OldgeNavItem("me", OldgeIcons.User, "Профиль"),
                ),
                nav,
                { nav = it },
            )
        }
    }
}

/** A section's letters, each a swipe row with «Архив» and «Удалить»; the [open]th starts open. */
@Composable
private fun Letters(
    letters: List<Letter>,
    open: Int = -1,
) = OldgeList {
    letters.forEachIndexed { i, l ->
        row {
            OldgeSwipeRow(
                listOf(
                    OldgeSwipeAction(OldgeIcons.Folder, "Архив", {}),
                    OldgeSwipeAction(OldgeIcons.Trash, "Удалить", {}, OldgeSwipeTone.Danger),
                ),
                defaultOpen = i == open,
            ) {
                OldgeListItem(
                    l.from,
                    subtitle = l.text,
                    lead = { OldgeAvatar(name = l.from, size = OldgeAvatarSize.Small) },
                    value = l.time,
                    onClick = {},
                )
            }
        }
    }
}

private const val REFRESH_MS = 1600L // css literal: InboxScreen preview, `setTimeout(…, 1600)`
