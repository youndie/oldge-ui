package io.github.youndie.oldge.forms

import androidx.compose.runtime.Composable
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/Checkbox/preview.html and RadioGroup/preview.html, string for
// string.

@Composable
private fun CheckboxDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeCheckbox("Запомнить это устройство", true, {})
        OldgeCheckbox("Присылать сводку по почте", false, {})
        OldgeCheckbox("Шифрование (управляется организацией)", true, {}, enabled = false)
    }

@Composable
private fun CheckStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeCheckbox("Выключен и пуст", false, {}, enabled = false)
        OldgeRadioGroup(
            "Недоступные",
            listOf(
                OldgeRadioOption("a", "Выбран, но недоступен", enabled = false),
                OldgeRadioOption("b", "Недоступен", enabled = false),
            ),
            "a",
            {},
        )
    }

private val quality =
    listOf(
        OldgeRadioOption("orig", "Оригинал"),
        OldgeRadioOption("hi", "Высокое"),
        OldgeRadioOption("save", "Экономия трафика"),
    )

@Composable
private fun RadioDemo(skin: OldgeSkin) = OldgeDemo(skin) { OldgeRadioGroup("Качество загрузки", quality, "hi", {}) }

@ViddikScreenshot(group = "Checkbox", name = "Toxic", width = 390, height = 188)
@Composable
fun CheckboxToxic() = CheckboxDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Checkbox", name = "Media", width = 390, height = 188)
@Composable
fun CheckboxMedia() = CheckboxDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Checkbox", name = "Crystal", width = 390, height = 188)
@Composable
fun CheckboxCrystal() = CheckboxDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "CheckboxStates", name = "Disabled Toxic", width = 390, height = 200)
@Composable
fun CheckStatesToxic() = CheckStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "CheckboxStates", name = "Disabled Media", width = 390, height = 200)
@Composable
fun CheckStatesMedia() = CheckStates(OldgeSkin.Media)

@ViddikScreenshot(group = "CheckboxStates", name = "Disabled Crystal", width = 390, height = 200)
@Composable
fun CheckStatesCrystal() = CheckStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "RadioGroup", name = "Toxic", width = 390, height = 182)
@Composable
fun RadioGroupToxic() = RadioDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "RadioGroup", name = "Media", width = 390, height = 182)
@Composable
fun RadioGroupMedia() = RadioDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "RadioGroup", name = "Crystal", width = 390, height = 182)
@Composable
fun RadioGroupCrystal() = RadioDemo(OldgeSkin.Crystal)

/**
 * B-86: a second, muted line under a choice's label. Not a design system state, so there is no
 * reference to hold it to; the strings are a removal dialog's and an add dialog's.
 */
@Composable
private fun CheckDescription(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeCheckbox("Удалить скачанные данные", true, {}, description = "~/Downloads/ubuntu-24.04")
        OldgeCheckbox("Шифрование", false, {}, enabled = false, description = "Управляется организацией")
        OldgeRadioGroup(
            "Порядок загрузки",
            listOf(
                OldgeRadioOption("any", "Как удобнее раздаче"),
                OldgeRadioOption("seq", "По порядку", description = "Видео можно смотреть сразу, раздаче хуже"),
                OldgeRadioOption("paused", "На паузе", enabled = false, description = "Появится позже"),
            ),
            "seq",
            {},
        )
    }

@ViddikScreenshot(group = "CheckDescription", name = "Toxic", width = 390, height = 330)
@Composable
fun CheckDescriptionToxic() = CheckDescription(OldgeSkin.Toxic)

@ViddikScreenshot(group = "CheckDescription", name = "Media", width = 390, height = 330)
@Composable
fun CheckDescriptionMedia() = CheckDescription(OldgeSkin.Media)

@ViddikScreenshot(group = "CheckDescription", name = "Crystal", width = 390, height = 330)
@Composable
fun CheckDescriptionCrystal() = CheckDescription(OldgeSkin.Crystal)
