package io.github.youndie.oldge.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/Readout/preview.html, Meter/preview.html and
// ProgressBar/preview.html, string for string.

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReadoutDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        // `display: flex; gap: 8px; flex-wrap: wrap; align-items: center`.
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
            verticalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            OldgeReadout("12:47", label = "Таймер")
            OldgeReadout("21.5", label = "Темп.", unit = "°C")
            OldgeReadout("8406", label = "Шаги", size = OldgeReadoutSize.Small)
        }
    }

@Composable
private fun MeterDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeMeter(0.37f, "Хранилище", "23,4 / 64 ГБ")
        OldgeMeter(0.72f, "Сигнал", "−61 dBm", segments = 12)
        OldgeMeter(0.94f, "Нагрузка", "94%")
    }

@Composable
private fun ProgressDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeProgressBar(0.45f, label = "Загрузка · 18 из 40 файлов", detail = "2,4 МБ/с · осталось 1:12")
        OldgeProgressBar(1f, label = "Проверка", detail = "Готово — ошибок нет", status = OldgeProgressStatus.Done)
        OldgeProgressBar(
            0.31f,
            label = "Отправка",
            detail = "Ошибка: нет соединения",
            status = OldgeProgressStatus.Error,
        )
        OldgeProgressBar(null, label = "Подключение…")
    }

@ViddikScreenshot(group = "Readout", name = "Toxic", width = 390, height = 180)
@Composable
fun ReadoutToxic() = ReadoutDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Readout", name = "Media", width = 390, height = 180)
@Composable
fun ReadoutMedia() = ReadoutDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Readout", name = "Crystal", width = 390, height = 180)
@Composable
fun ReadoutCrystal() = ReadoutDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "Meter", name = "Toxic", width = 390, height = 218)
@Composable
fun MeterToxic() = MeterDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Meter", name = "Media", width = 390, height = 218)
@Composable
fun MeterMedia() = MeterDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Meter", name = "Crystal", width = 390, height = 218)
@Composable
fun MeterCrystal() = MeterDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "ProgressBar", name = "Toxic", width = 390, height = 290)
@Composable
fun ProgressBarToxic() = ProgressDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "ProgressBar", name = "Media", width = 390, height = 290)
@Composable
fun ProgressBarMedia() = ProgressDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "ProgressBar", name = "Crystal", width = 390, height = 290)
@Composable
fun ProgressBarCrystal() = ProgressDemo(OldgeSkin.Crystal)
