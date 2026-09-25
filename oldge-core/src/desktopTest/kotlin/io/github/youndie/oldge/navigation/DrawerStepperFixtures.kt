package io.github.youndie.oldge.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.actions.OldgeButtonSize
import io.github.youndie.oldge.actions.OldgeButtonVariant
import io.github.youndie.oldge.feedback.OldgeAvatar
import io.github.youndie.oldge.feedback.OldgeAvatarStatus
import io.github.youndie.oldge.harness.DemoText
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/{NavDrawer,Stepper,PageDots}/preview.html, string for string.

internal val drawerEntries =
    listOf(
        OldgeDrawerEntry.Item("home", OldgeIcons.Home, "Главная"),
        OldgeDrawerEntry.Item("files", OldgeIcons.Folder, "Файлы", badge = "4"),
        OldgeDrawerEntry.Item("photo", OldgeIcons.Image, "Фото"),
        OldgeDrawerEntry.Section("Сервис"),
        OldgeDrawerEntry.Item("sync", OldgeIcons.Refresh, "Синхронизация"),
        OldgeDrawerEntry.Item("set", OldgeIcons.Gear, "Настройки"),
        OldgeDrawerEntry.Item("help", OldgeIcons.Help, "Справка"),
    )

internal val wizard = listOf("Файлы", "Настройки", "Проверка", "Готово")

/** A plain `div` of `height: 500px; padding: 16px; box-sizing: border-box`, not an `.og-demo`. */
@Composable
private fun NavDrawerDemo(skin: OldgeSkin) =
    OldgeDemo(skin, padded = false) {
        var value by remember { mutableStateOf("files") }
        Box(Modifier.fillMaxWidth().height(DRAWER_DEMO).padding(OldgeTheme.spacing.space4)) {
            OldgeNavDrawer(
                drawerEntries,
                value,
                { value = it },
                title = "Павел",
                subtitle = "pavel@example.com",
                avatar = { OldgeAvatar(name = "Павел Вотяков", status = OldgeAvatarStatus.Online) },
                inline = true,
            )
        }
    }

@Composable
private fun StepperDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        var step by remember { mutableIntStateOf(1) }
        OldgeStepper(wizard, step)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2, androidx.compose.ui.Alignment.End),
        ) {
            OldgeButton("Назад", { step-- }, size = OldgeButtonSize.Small, enabled = step != 0)
            OldgeButton(
                "Далее",
                { step++ },
                size = OldgeButtonSize.Small,
                variant = OldgeButtonVariant.Primary,
                enabled =
                    step != 3,
            )
        }
    }

@Composable
private fun PageDotsDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        var page by remember { mutableIntStateOf(2) }
        OldgePageDots(5, page, { page = it })
    }

/** What the NavDrawer preview does not show: the drawer over a screen, on its scrim, a later item current. */
@Composable
private fun NavDrawerStates(skin: OldgeSkin) =
    OldgeDemo(skin, padded = false) {
        Box(Modifier.fillMaxWidth().height(DRAWER_DEMO)) {
            Column(
                Modifier.padding(OldgeTheme.spacing.space4),
            ) { DemoText("Экран под меню: список писем, кнопки и прочее.") }
            OldgeNavDrawer(drawerEntries, "set", {}, title = "Павел", subtitle = "pavel@example.com")
        }
    }

/** What the Stepper preview does not show: the first step current, three steps, and all but the last done of five. */
@Composable
private fun StepperStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeStepper(wizard, 0)
        OldgeStepper(listOf("Вход", "Код", "Готово"), 2)
        OldgeStepper(listOf("Один", "Два", "Три", "Четыре", "Пять"), 4)
    }

/** What the PageDots preview does not show: seven dots at either end, and the look over a photograph. */
@Composable
private fun PageDotsStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgePageDots(7, 0, {})
        OldgePageDots(7, 6, {})
        // A stand-in for a photograph: its colours are the picture's, not the design's.
        Box(
            Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Color(0xFF6A8CAF), Color(0xFFD9A066)))),
        ) {
            OldgePageDots(5, 1, {}, onDark = true)
        }
    }

@ViddikScreenshot(group = "NavDrawerStates", name = "Over a screen Toxic", width = 390, height = 520)
@Composable
fun NavDrawerStatesToxic() = NavDrawerStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "NavDrawerStates", name = "Over a screen Media", width = 390, height = 520)
@Composable
fun NavDrawerStatesMedia() = NavDrawerStates(OldgeSkin.Media)

@ViddikScreenshot(group = "NavDrawerStates", name = "Over a screen Crystal", width = 390, height = 520)
@Composable
fun NavDrawerStatesCrystal() = NavDrawerStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "StepperStates", name = "First last three and five Toxic", width = 390, height = 230)
@Composable
fun StepperStatesToxic() = StepperStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "StepperStates", name = "First last three and five Media", width = 390, height = 230)
@Composable
fun StepperStatesMedia() = StepperStates(OldgeSkin.Media)

@ViddikScreenshot(group = "StepperStates", name = "First last three and five Crystal", width = 390, height = 230)
@Composable
fun StepperStatesCrystal() = StepperStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "PageDotsStates", name = "Seven and on dark Toxic", width = 390, height = 200)
@Composable
fun PageDotsStatesToxic() = PageDotsStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "PageDotsStates", name = "Seven and on dark Media", width = 390, height = 200)
@Composable
fun PageDotsStatesMedia() = PageDotsStates(OldgeSkin.Media)

@ViddikScreenshot(group = "PageDotsStates", name = "Seven and on dark Crystal", width = 390, height = 200)
@Composable
fun PageDotsStatesCrystal() = PageDotsStates(OldgeSkin.Crystal)

private val DRAWER_DEMO = 500.dp

@ViddikScreenshot(group = "NavDrawer", name = "Toxic", width = 390, height = 520)
@Composable
fun NavDrawerToxic() = NavDrawerDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "NavDrawer", name = "Media", width = 390, height = 520)
@Composable
fun NavDrawerMedia() = NavDrawerDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "NavDrawer", name = "Crystal", width = 390, height = 520)
@Composable
fun NavDrawerCrystal() = NavDrawerDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "Stepper", name = "Toxic", width = 390, height = 150)
@Composable
fun StepperToxic() = StepperDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Stepper", name = "Media", width = 390, height = 150)
@Composable
fun StepperMedia() = StepperDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Stepper", name = "Crystal", width = 390, height = 150)
@Composable
fun StepperCrystal() = StepperDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "PageDots", name = "Toxic", width = 390, height = 90)
@Composable
fun PageDotsToxic() = PageDotsDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "PageDots", name = "Media", width = 390, height = 90)
@Composable
fun PageDotsMedia() = PageDotsDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "PageDots", name = "Crystal", width = 390, height = 90)
@Composable
fun PageDotsCrystal() = PageDotsDemo(OldgeSkin.Crystal)
