package io.github.youndie.oldge.feedback

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.actions.OldgeButtonSize
import io.github.youndie.oldge.harness.DemoText
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/{Banner,Snackbar,EmptyState}/preview.html, string for string.

@Composable
private fun BannerDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeBanner(
            title = "Мало места",
            icon = OldgeIcons.Warn,
            boxed = true,
            actions = listOf(OldgeBannerAction("Позже", {}), OldgeBannerAction("Освободить", {})),
        ) { DemoText("Осталось 1,2 ГБ. Новые фото не будут загружаться.") }
    }

@Composable
private fun SnackbarDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeSnackbar("Файл перемещён в корзину", actionLabel = "Отменить", onClose = {}, inline = true)
        // A child of `.og-demo`, a flex column, stretches to its width.
        OldgeButton("Показать ещё раз", {}, Modifier.fillMaxWidth(), size = OldgeButtonSize.Small)
    }

@Composable
private fun EmptyStateDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeEmptyState("Всё прочитано", text = "Новые письма появятся здесь.", icon = OldgeIcons.Mail)
        OldgeEmptyState(
            "Не удалось загрузить",
            text = "Сервер не ответил за 30 секунд.",
            tone = OldgeEmptyTone.Error,
            action = { OldgeButton("Повторить", {}, icon = OldgeIcons.Refresh) },
        )
    }

/** What the Banner preview does not show: the full-width band, with and without a title or actions. */
@Composable
private fun BannerStates(skin: OldgeSkin) =
    OldgeDemo(skin, padded = false) {
        OldgeBanner(title = "Нет сети", actions = listOf(OldgeBannerAction("Повторить", {}))) {
            DemoText("Изменения сохранятся, когда связь вернётся.")
        }
        OldgeBanner { DemoText("Синхронизация приостановлена до вечера.") }
    }

/** What the Snackbar preview does not show: over a screen by its bottom edge, a long message, no action. */
@Composable
private fun SnackbarStates(skin: OldgeSkin) =
    OldgeDemo(skin, padded = false) {
        Box(Modifier.fillMaxWidth().height(STATES_HEIGHT)) {
            Column(Modifier.padding(OldgeTheme.spacing.space4)) { DemoText("Экран под снэкбаром.") }
            OldgeSnackbar("Письмо отправлено, копия сохранена в папке «Отправленные»", onClose = {})
        }
    }

/** What the EmptyState preview does not show: the offline tone, an empty state with no text, the default error icon. */
@Composable
private fun EmptyStateStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeEmptyState("Нет связи", text = "Проверьте сеть — данные обновятся сами.", tone = OldgeEmptyTone.Offline)
        OldgeEmptyState("Пусто")
    }

private val STATES_HEIGHT = 200.dp

@ViddikScreenshot(group = "BannerStates", name = "Band with and without title Toxic", width = 390, height = 220)
@Composable
fun BannerStatesToxic() = BannerStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "BannerStates", name = "Band with and without title Media", width = 390, height = 220)
@Composable
fun BannerStatesMedia() = BannerStates(OldgeSkin.Media)

@ViddikScreenshot(group = "BannerStates", name = "Band with and without title Crystal", width = 390, height = 220)
@Composable
fun BannerStatesCrystal() = BannerStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "SnackbarStates", name = "Over a screen Toxic", width = 390, height = 200)
@Composable
fun SnackbarStatesToxic() = SnackbarStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "SnackbarStates", name = "Over a screen Media", width = 390, height = 200)
@Composable
fun SnackbarStatesMedia() = SnackbarStates(OldgeSkin.Media)

@ViddikScreenshot(group = "SnackbarStates", name = "Over a screen Crystal", width = 390, height = 200)
@Composable
fun SnackbarStatesCrystal() = SnackbarStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "EmptyStateStates", name = "Offline and bare Toxic", width = 390, height = 520)
@Composable
fun EmptyStateStatesToxic() = EmptyStateStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "EmptyStateStates", name = "Offline and bare Media", width = 390, height = 520)
@Composable
fun EmptyStateStatesMedia() = EmptyStateStates(OldgeSkin.Media)

@ViddikScreenshot(group = "EmptyStateStates", name = "Offline and bare Crystal", width = 390, height = 520)
@Composable
fun EmptyStateStatesCrystal() = EmptyStateStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "Banner", name = "Toxic", width = 390, height = 170)
@Composable
fun BannerToxic() = BannerDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Banner", name = "Media", width = 390, height = 170)
@Composable
fun BannerMedia() = BannerDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Banner", name = "Crystal", width = 390, height = 170)
@Composable
fun BannerCrystal() = BannerDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "Snackbar", name = "Toxic", width = 390, height = 140)
@Composable
fun SnackbarToxic() = SnackbarDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Snackbar", name = "Media", width = 390, height = 140)
@Composable
fun SnackbarMedia() = SnackbarDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Snackbar", name = "Crystal", width = 390, height = 140)
@Composable
fun SnackbarCrystal() = SnackbarDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "EmptyState", name = "Toxic", width = 390, height = 560)
@Composable
fun EmptyStateToxic() = EmptyStateDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "EmptyState", name = "Media", width = 390, height = 560)
@Composable
fun EmptyStateMedia() = EmptyStateDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "EmptyState", name = "Crystal", width = 390, height = 560)
@Composable
fun EmptyStateCrystal() = EmptyStateDemo(OldgeSkin.Crystal)
