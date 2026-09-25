package io.github.youndie.oldge.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.youndie.oldge.actions.OldgeActions
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.actions.OldgeButtonVariant
import io.github.youndie.oldge.actions.OldgeChipGroup
import io.github.youndie.oldge.actions.OldgeFilterChip
import io.github.youndie.oldge.containers.OldgeList
import io.github.youndie.oldge.containers.OldgeListItem
import io.github.youndie.oldge.containers.OldgeScreenBody
import io.github.youndie.oldge.feedback.OldgeBanner
import io.github.youndie.oldge.feedback.OldgeBannerAction
import io.github.youndie.oldge.forms.OldgeRadioGroup
import io.github.youndie.oldge.forms.OldgeRadioOption
import io.github.youndie.oldge.forms.OldgeSwitch
import io.github.youndie.oldge.forms.OldgeTextField
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.navigation.OldgeBottomNav
import io.github.youndie.oldge.navigation.OldgeNavItem
import io.github.youndie.oldge.navigation.OldgeTab
import io.github.youndie.oldge.navigation.OldgeTabs
import io.github.youndie.oldge.navigation.OldgeWindowAction
import io.github.youndie.oldge.navigation.OldgeWindowBar
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.type.OldgeText

/**
 * The design system's EdgeNarrow stress page: 320 dp and long German strings. A list's value moves
 * under its title, the buttons stand in a column, and a long choice is a RadioGroup rather than a
 * Segmented. Its strings are the page's.
 */
@Composable
public fun EdgeNarrowScreen(modifier: Modifier = Modifier) {
    var mode by remember { mutableStateOf("auto") }
    var nav by remember { mutableStateOf("set") }
    var ads by remember { mutableStateOf(true) }
    var security by remember { mutableStateOf(true) }
    var updates by remember { mutableStateOf(false) }
    EdgePage(
        modifier,
        bar = {
            OldgeWindowBar(
                "Datenschutzeinstellungen",
                subtitle = "Konto und Synchronisierung",
                onBack = {},
                actions = listOf(OldgeWindowAction(OldgeIcons.Help, "Hilfe", {})),
            )
        },
        nav = {
            OldgeBottomNav(
                listOf(
                    OldgeNavItem("home", OldgeIcons.Home, "Startseite"),
                    OldgeNavItem("bell", OldgeIcons.Bell, "Benachrichtigungen"),
                    OldgeNavItem("set", OldgeIcons.Gear, "Einstellungen"),
                    OldgeNavItem("me", OldgeIcons.User, "Profil"),
                ),
                nav,
                { nav = it },
            )
        },
    ) {
        OldgeRadioGroup(
            "Modus",
            listOf(
                OldgeRadioOption("auto", "Automatisch"),
                OldgeRadioOption("push", "Nur Benachrichtigungen"),
                OldgeRadioOption("off", "Aus"),
            ),
            mode,
            { mode = it },
        )
        OldgeList {
            row {
                OldgeListItem(
                    "Personalisierte Werbung anzeigen",
                    icon = OldgeIcons.User,
                    trailing = { OldgeSwitch("Werbung", ads, { ads = it }, showLabel = false) },
                )
            }
            row {
                OldgeListItem(
                    "Standortverlauf",
                    icon = OldgeIcons.Cloud,
                    value = "Nur während der Nutzung",
                    chevron = true,
                    onClick = {},
                )
            }
        }
        OldgeChipGroup("Themen") {
            OldgeFilterChip("Sicherheitsbenachrichtigungen", security, { security = it })
            OldgeFilterChip("Systemaktualisierungen", updates, { updates = it })
        }
        OldgeBanner(
            title = "Speicherplatz fast voll",
            icon = OldgeIcons.Warn,
            boxed = true,
            actions = listOf(OldgeBannerAction("Später erinnern", {}), OldgeBannerAction("Speicher freigeben", {})),
        ) { OldgeText("Neue Fotos werden nicht mehr gesichert.") }
        OldgeActions {
            OldgeButton("Änderungen verwerfen", {})
            OldgeButton("Speichern und fortfahren", {}, variant = OldgeButtonVariant.Primary)
        }
    }
}

/**
 * The design system's EdgeScale stress page: the system font at 200 %. Every text size is in `rem`,
 * so it grows with the setting; the controls grow in height rather than cut their text. Its strings
 * are the page's; the 200 % is the caller's `fontScale`.
 */
@Composable
public fun EdgeScaleScreen(modifier: Modifier = Modifier) {
    var nav by remember { mutableStateOf("me") }
    var tab by remember { mutableStateOf("gen") }
    var notify by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("Телефон Павла") }
    EdgePage(
        modifier,
        bar = {
            OldgeWindowBar(
                "Настройки",
                subtitle = "Павел · Pro",
                onBack = {},
                actions = listOf(OldgeWindowAction(OldgeIcons.Close, "Закрыть", {})),
            )
        },
        nav = {
            OldgeBottomNav(
                listOf(
                    OldgeNavItem("home", OldgeIcons.Home, "Главная"),
                    OldgeNavItem("search", OldgeIcons.Search, "Поиск"),
                    OldgeNavItem("me", OldgeIcons.User, "Профиль"),
                ),
                nav,
                { nav = it },
            )
        },
    ) {
        OldgeTabs(
            listOf(OldgeTab("gen", "Общие"), OldgeTab("acc", "Доступ")),
            tab,
            { tab = it },
            "Раздел",
        ) { OldgeText("Имя и значок") }
        OldgeList {
            row {
                OldgeListItem(
                    "Уведомления",
                    icon = OldgeIcons.Bell,
                    trailing = { OldgeSwitch("Уведомления", notify, { notify = it }, showLabel = false) },
                )
            }
            row { OldgeListItem("Пароль", icon = OldgeIcons.Lock, value = "3 дня назад", chevron = true, onClick = {}) }
        }
        OldgeTextField("Имя устройства", name, { name = it })
        OldgeActions {
            OldgeButton("Сбросить", {})
            OldgeButton("Сохранить", {}, variant = OldgeButtonVariant.Primary)
        }
    }
}

/**
 * Both Edge pages: the bar, the scrolling `.scroll` column (padded 12 16 16, `space-3` apart) that
 * takes what is left, and the navigation.
 */
@Composable
private fun EdgePage(
    modifier: Modifier,
    bar: @Composable () -> Unit,
    nav: @Composable () -> Unit,
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
                    .padding(start = s.space4, end = s.space4, top = s.space3, bottom = s.space4),
                verticalArrangement = Arrangement.spacedBy(s.space3),
                content = content,
            )
            nav()
        }
    }
}
