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
