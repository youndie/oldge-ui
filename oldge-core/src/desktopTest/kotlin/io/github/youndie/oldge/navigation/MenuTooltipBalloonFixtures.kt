package io.github.youndie.oldge.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.actions.OldgeOrbButton
import io.github.youndie.oldge.actions.OldgeOrbTone
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/Menu/preview.html, Tooltip/preview.html and
// Balloon/preview.html, string for string.

internal val fileMenu =
    listOf(
        OldgeMenuEntry.Item("open", "Открыть", OldgeIcons.Doc, hint = "⌘O"),
        OldgeMenuEntry.Item("share", "Отправить…", OldgeIcons.Upload),
        OldgeMenuEntry.Item("ren", "Переименовать", OldgeIcons.Edit),
        OldgeMenuEntry.Divider,
        OldgeMenuEntry.Item("del", "Удалить", OldgeIcons.Trash, danger = true),
    )

@Composable
private fun MenuDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        var open by remember { mutableStateOf(true) }
        OldgeMenu(open, { open = it }, fileMenu, {}, label = "Действия с файлом") { toggle ->
            OldgeButton("Файл", toggle, icon = OldgeIcons.Menu)
        }
    }

@Composable
private fun TooltipDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        // `style: { paddingTop: 48, paddingLeft: 90 }` on the demo, over its 16 px.
        val s = OldgeTheme.spacing.space4
        Row(
            Modifier.padding(start = TOOLTIP_LEFT - s, top = TOOLTIP_TOP - s),
            horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OldgeTooltip("Синхронизировать сейчас", initiallyOpen = true) {
                OldgeOrbButton(OldgeIcons.Refresh, "Синхронизировать", {})
            }
            OldgeTooltip("Справка") { OldgeOrbButton(OldgeIcons.Help, "Справка", {}, tone = OldgeOrbTone.Chrome) }
        }
    }

@Composable
private fun BalloonDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeBalloon(
            "Копия создана",
            text = "Сохранено 1 204 файла. Следующая копия — завтра в 03:00.",
            icon = OldgeIcons.Ok,
            onClose = {},
        )
        OldgeBalloon(
            "Доступен новый скин",
            text = "Нажмите, чтобы применить Media.",
            icon = OldgeIcons.Info,
            tail = OldgeBalloonTail.Top,
        )
    }

@Composable
private fun States(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeBalloon("Не удалось отправить", text = "Нет сети.", icon = OldgeIcons.Error, tail = OldgeBalloonTail.None)
        OldgeBalloon("Мало места", icon = OldgeIcons.Warn, tail = OldgeBalloonTail.Bottom)
        var open by remember { mutableStateOf(true) }
        // At the right edge, so an end-aligned menu has room to hang left of its trigger's end.
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            OldgeMenu(open, { open = it }, fileMenu.take(2), {}, align = OldgeMenuAlign.End) { toggle ->
                OldgeButton("Справа", toggle)
            }
        }
    }

private val TOOLTIP_LEFT = 90.dp
private val TOOLTIP_TOP = 48.dp

@ViddikScreenshot(group = "Menu", name = "Toxic", width = 390, height = 330)
@Composable
fun MenuToxic() = MenuDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Menu", name = "Media", width = 390, height = 330)
@Composable
fun MenuMedia() = MenuDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Menu", name = "Crystal", width = 390, height = 330)
@Composable
fun MenuCrystal() = MenuDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "Tooltip", name = "Toxic", width = 390, height = 112)
@Composable
fun TooltipToxic() = TooltipDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Tooltip", name = "Media", width = 390, height = 112)
@Composable
fun TooltipMedia() = TooltipDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Tooltip", name = "Crystal", width = 390, height = 112)
@Composable
fun TooltipCrystal() = TooltipDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "Balloon", name = "Toxic", width = 390, height = 222)
@Composable
fun BalloonToxic() = BalloonDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Balloon", name = "Media", width = 390, height = 222)
@Composable
fun BalloonMedia() = BalloonDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Balloon", name = "Crystal", width = 390, height = 222)
@Composable
fun BalloonCrystal() = BalloonDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "BalloonStates", name = "Tails and an end menu Toxic", width = 390, height = 330)
@Composable
fun BalloonStatesToxic() = States(OldgeSkin.Toxic)

@ViddikScreenshot(group = "BalloonStates", name = "Tails and an end menu Media", width = 390, height = 330)
@Composable
fun BalloonStatesMedia() = States(OldgeSkin.Media)

@ViddikScreenshot(group = "BalloonStates", name = "Tails and an end menu Crystal", width = 390, height = 330)
@Composable
fun BalloonStatesCrystal() = States(OldgeSkin.Crystal)
