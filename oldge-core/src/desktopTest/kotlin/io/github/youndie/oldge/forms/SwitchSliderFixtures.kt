package io.github.youndie.oldge.forms

import androidx.compose.runtime.Composable
import io.github.youndie.oldge.actions.pressed
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/Switch/preview.html and Slider/preview.html, string for string.

@Composable
private fun SwitchDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeSwitch("Автосинхронизация", true, {}, hint = "Только при зарядке")
        OldgeSwitch("Тёмный скин ночью", false, {})
        OldgeSwitch("Недоступно", false, {}, enabled = false)
    }

@Composable
private fun SwitchStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeSwitch("Включено, недоступно", true, {}, enabled = false)
        OldgeSwitch("Нажат, выключен", false, {}, interactionSource = pressed())
        OldgeSwitch("Нажат, включён", true, {}, interactionSource = pressed())
        OldgeSlider(0f, {}, label = "Пусто", valueText = "0%")
        OldgeSlider(1f, {}, label = "Полно", valueText = "100%")
        OldgeSlider(0.25f, {})
    }

@Composable
private fun SliderDemo(skin: OldgeSkin) =
    OldgeDemo(skin) { OldgeSlider(0.6f, {}, label = "Громкость", valueText = "60%") }

@ViddikScreenshot(group = "Switch", name = "Toxic", width = 390, height = 188)
@Composable
fun SwitchToxic() = SwitchDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Switch", name = "Media", width = 390, height = 188)
@Composable
fun SwitchMedia() = SwitchDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Switch", name = "Crystal", width = 390, height = 188)
@Composable
fun SwitchCrystal() = SwitchDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "SwitchStates", name = "Held disabled and slider ends Toxic", width = 390, height = 400)
@Composable
fun SwitchStatesToxic() = SwitchStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "SwitchStates", name = "Held disabled and slider ends Media", width = 390, height = 400)
@Composable
fun SwitchStatesMedia() = SwitchStates(OldgeSkin.Media)

@ViddikScreenshot(group = "SwitchStates", name = "Held disabled and slider ends Crystal", width = 390, height = 400)
@Composable
fun SwitchStatesCrystal() = SwitchStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "Slider", name = "Toxic", width = 390, height = 110)
@Composable
fun SliderToxic() = SliderDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Slider", name = "Media", width = 390, height = 110)
@Composable
fun SliderMedia() = SliderDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Slider", name = "Crystal", width = 390, height = 110)
@Composable
fun SliderCrystal() = SliderDemo(OldgeSkin.Crystal)
