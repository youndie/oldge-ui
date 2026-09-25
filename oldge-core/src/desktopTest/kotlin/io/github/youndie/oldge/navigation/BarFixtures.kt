package io.github.youndie.oldge.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import io.github.youndie.oldge.actions.OldgeOrbTone
import io.github.youndie.oldge.feedback.OldgeAvatar
import io.github.youndie.oldge.feedback.OldgeAvatarSize
import io.github.youndie.oldge.feedback.OldgeAvatarStatus
import io.github.youndie.oldge.feedback.OldgeBadge
import io.github.youndie.oldge.feedback.OldgeBadgeTone
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/WindowBar/preview.html and BottomNav/preview.html, string for string.

internal val sections =
    listOf(
        OldgeNavItem("home", OldgeIcons.Home, "Главная"),
        OldgeNavItem("search", OldgeIcons.Search, "Поиск"),
        OldgeNavItem("bell", OldgeIcons.Bell, "События"),
        OldgeNavItem("me", OldgeIcons.User, "Профиль"),
    )

@Composable
private fun WindowBarDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeWindowBar(
            "Центр задач",
            icon = OldgeIcons.Grid,
            actions =
                listOf(
                    OldgeWindowAction(OldgeIcons.Help, "Справка", {}),
                    OldgeWindowAction(OldgeIcons.Close, "Закрыть", {}),
                ),
        )
        OldgeWindowBar(
            "Настройки",
            onBack = {},
            subtitle = "Аккаунт и синхронизация",
            actions = listOf(OldgeWindowAction(OldgeIcons.Search, "Поиск", {})),
        )
        OldgeWindowBar(
            "Файлы",
            icon = OldgeIcons.Folder,
            center = { OldgeBadge("3", tone = OldgeBadgeTone.Accent, count = true) },
            actions = listOf(OldgeWindowAction(OldgeIcons.Plus, "Создать", {}, OldgeOrbTone.Accent)),
        )
    }

@Composable
private fun BottomNavDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        var value by remember { mutableStateOf("home") }
        OldgeBottomNav(sections, value, { value = it })
    }

/**
 * What the WindowBar preview does not show: an overlay bar over full-screen media, a square bar with
 * an Avatar as its lead, and a title too long for the bar.
 */
@Composable
private fun WindowBarStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        // A stand-in for a photograph under the overlay: its colours are the picture's, not the design's.
        Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Color(0xFFB0C8E0), Color(0xFF405060))))) {
            OldgeWindowBar(
                "Фото 12 из 40",
                onBack = {},
                overlay = true,
                actions = listOf(OldgeWindowAction(OldgeIcons.Upload, "Отправить", {})),
            )
        }
        OldgeWindowBar(
            "Анна Ким",
            subtitle = "в сети",
            square = true,
            lead = { OldgeAvatar(name = "Анна Ким", size = OldgeAvatarSize.Small, status = OldgeAvatarStatus.Online) },
            actions = listOf(OldgeWindowAction(OldgeIcons.Search, "Поиск", {})),
        )
        OldgeWindowBar(
            "Резервное копирование и синхронизация",
            icon = OldgeIcons.Cloud,
            actions =
                listOf(
                    OldgeWindowAction(OldgeIcons.Help, "Справка", {}),
                    OldgeWindowAction(OldgeIcons.Close, "Закрыть", {}),
                ),
        )
    }

/** What the BottomNav preview does not show: a square dock of five, and a label too long for its slot. */
@Composable
private fun BottomNavStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeBottomNav(
            sections + OldgeNavItem("files", OldgeIcons.Folder, "Документация"),
            "files",
            {},
            square = true,
        )
    }

@ViddikScreenshot(group = "WindowBarStates", name = "Overlay square lead long title Toxic", width = 390, height = 230)
@Composable
fun WindowBarStatesToxic() = WindowBarStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "WindowBarStates", name = "Overlay square lead long title Media", width = 390, height = 230)
@Composable
fun WindowBarStatesMedia() = WindowBarStates(OldgeSkin.Media)

@ViddikScreenshot(group = "WindowBarStates", name = "Overlay square lead long title Crystal", width = 390, height = 230)
@Composable
fun WindowBarStatesCrystal() = WindowBarStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "BottomNavStates", name = "Square five long label Toxic", width = 390, height = 100)
@Composable
fun BottomNavStatesToxic() = BottomNavStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "BottomNavStates", name = "Square five long label Media", width = 390, height = 100)
@Composable
fun BottomNavStatesMedia() = BottomNavStates(OldgeSkin.Media)

@ViddikScreenshot(group = "BottomNavStates", name = "Square five long label Crystal", width = 390, height = 100)
@Composable
fun BottomNavStatesCrystal() = BottomNavStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "WindowBar", name = "Toxic", width = 390, height = 230)
@Composable
fun WindowBarToxic() = WindowBarDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "WindowBar", name = "Media", width = 390, height = 230)
@Composable
fun WindowBarMedia() = WindowBarDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "WindowBar", name = "Crystal", width = 390, height = 230)
@Composable
fun WindowBarCrystal() = WindowBarDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "BottomNav", name = "Toxic", width = 390, height = 100)
@Composable
fun BottomNavToxic() = BottomNavDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "BottomNav", name = "Media", width = 390, height = 100)
@Composable
fun BottomNavMedia() = BottomNavDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "BottomNav", name = "Crystal", width = 390, height = 100)
@Composable
fun BottomNavCrystal() = BottomNavDemo(OldgeSkin.Crystal)
