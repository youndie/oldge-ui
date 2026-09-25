package io.github.youndie.oldge.containers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.actions.OldgeButtonVariant
import io.github.youndie.oldge.harness.DemoText
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/Dialog/preview.html and BottomSheet/preview.html, string for string.

@Composable
private fun DialogDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeDialog(
            "Удалить папку?",
            icon = OldgeIcons.Warn,
            onClose = {},
            actions = {
                OldgeButton("Отмена", {})
                OldgeButton("Удалить", {}, variant = OldgeButtonVariant.Primary, icon = OldgeIcons.Trash)
            },
        ) { DemoText("Папка «Отчёты 2006» и 48 файлов в ней будут удалены. Действие нельзя отменить.") }
    }

/** The inline sheet and the button that would open one over the screen, as the preview has them. */
@Composable
private fun BottomSheetDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeBottomSheet("Отправить файл", onClose = {}, inline = true) {
            OldgeList(bare = true) {
                row { OldgeListItem("Контакту", icon = OldgeIcons.User, dense = true, onClick = {}) }
                row { OldgeListItem("Ссылкой", icon = OldgeIcons.Cloud, dense = true, onClick = {}) }
            }
        }
        // A child of `.og-demo`, a flex column, stretches to its width.
        OldgeButton("Открыть шторку поверх", {}, Modifier.fillMaxWidth())
    }

/** The overlay forms the previews do not render: a modal dialog over a screen, and a sheet over one. */
@Composable
private fun DialogStates(skin: OldgeSkin) =
    OldgeDemo(skin, padded = false) {
        Box(Modifier.fillMaxWidth().height(OVERLAY)) {
            Column(
                Modifier.padding(OldgeTheme.spacing.space4),
            ) { DemoText("Экран под диалогом: список писем, кнопки и прочее.") }
            OldgeDialog(
                "Выйти без сохранения?",
                modal = true,
                actions = {
                    OldgeButton("Остаться", {})
                    OldgeButton("Выйти", {}, variant = OldgeButtonVariant.Primary)
                },
            ) { DemoText("Изменения в документе будут потеряны.") }
        }
    }

@Composable
private fun BottomSheetStates(skin: OldgeSkin) =
    OldgeDemo(skin, padded = false) {
        Box(Modifier.fillMaxWidth().height(OVERLAY)) {
            Column(
                Modifier.padding(OldgeTheme.spacing.space4),
            ) { DemoText("Экран под шторкой: список писем, кнопки и прочее.") }
            OldgeBottomSheet(
                "Шторка",
                onClose = {},
            ) { DemoText("Въезжает снизу с пружиной; тап по затемнению закрывает.") }
        }
    }

private val OVERLAY = 400.dp

@ViddikScreenshot(group = "DialogStates", name = "Modal over a screen Toxic", width = 390, height = 400)
@Composable
fun DialogStatesToxic() = DialogStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "DialogStates", name = "Modal over a screen Media", width = 390, height = 400)
@Composable
fun DialogStatesMedia() = DialogStates(OldgeSkin.Media)

@ViddikScreenshot(group = "DialogStates", name = "Modal over a screen Crystal", width = 390, height = 400)
@Composable
fun DialogStatesCrystal() = DialogStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "BottomSheetStates", name = "Over a screen Toxic", width = 390, height = 400)
@Composable
fun BottomSheetStatesToxic() = BottomSheetStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "BottomSheetStates", name = "Over a screen Media", width = 390, height = 400)
@Composable
fun BottomSheetStatesMedia() = BottomSheetStates(OldgeSkin.Media)

@ViddikScreenshot(group = "BottomSheetStates", name = "Over a screen Crystal", width = 390, height = 400)
@Composable
fun BottomSheetStatesCrystal() = BottomSheetStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "Dialog", name = "Toxic", width = 390, height = 270)
@Composable
fun DialogToxic() = DialogDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Dialog", name = "Media", width = 390, height = 270)
@Composable
fun DialogMedia() = DialogDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Dialog", name = "Crystal", width = 390, height = 270)
@Composable
fun DialogCrystal() = DialogDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "BottomSheet", name = "Toxic", width = 390, height = 330)
@Composable
fun BottomSheetToxic() = BottomSheetDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "BottomSheet", name = "Media", width = 390, height = 330)
@Composable
fun BottomSheetMedia() = BottomSheetDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "BottomSheet", name = "Crystal", width = 390, height = 330)
@Composable
fun BottomSheetCrystal() = BottomSheetDemo(OldgeSkin.Crystal)
