package io.github.youndie.oldge.containers

import androidx.compose.runtime.Composable
import io.github.youndie.oldge.harness.DemoText
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/Divider/preview.html, string for string.

@Composable
private fun DividerDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        DemoText("Выше черты")
        OldgeDivider()
        OldgeDivider(label = "или")
        DemoText("Ниже черты")
    }

@ViddikScreenshot(group = "Divider", name = "Toxic", width = 390, height = 154)
@Composable
fun DividerToxic() = DividerDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Divider", name = "Media", width = 390, height = 154)
@Composable
fun DividerMedia() = DividerDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Divider", name = "Crystal", width = 390, height = 154)
@Composable
fun DividerCrystal() = DividerDemo(OldgeSkin.Crystal)
