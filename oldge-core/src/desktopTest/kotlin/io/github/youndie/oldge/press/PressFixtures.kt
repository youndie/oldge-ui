package io.github.youndie.oldge.press

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.glossGradient
import io.github.youndie.oldge.material.skinBody
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.viddik.annotations.ViddikScreenshot

// B-10 goldens: a chrome button-like surface with the flash frozen at a quarter of its run, and the
// same surface with the focus ring. The frames come from the functions the indication itself draws
// with, so the golden is of the shipped drawing, not of a copy.

@Composable
private fun PressSurface(
    skin: OldgeSkin,
    flashAt: Float?,
    focused: Boolean,
) = OldgeTheme(skin = skin, reducedMotion = true) {
    val c = OldgeTheme.colors
    val shape = RoundedCornerShape(OldgeRadii.sm)
    Box(Modifier.size(200.dp, 76.dp).skinBody()) {
        Box(
            Modifier
                .offset(20.dp, 16.dp)
                .size(160.dp, 44.dp)
                .cssBox(
                    OldgeRadii.sm,
                    glossGradient(c.chromeHi, null, c.chromeLo),
                    1.dp,
                    c.edge,
                    OldgeTheme.shadows.raised,
                    gloss = c.gloss,
                ).drawWithContent {
                    flashAt?.let { drawOldgeFlash(Offset(60.dp.toPx(), 22.dp.toPx()), it, c.gloss) }
                    drawContent()
                    if (focused) drawOldgeFocusRing(shape, c.focus)
                },
        )
    }
}

@ViddikScreenshot(group = "Press", name = "Flash Toxic", width = 200, height = 76)
@Composable
fun PressFlashToxic() = PressSurface(OldgeSkin.Toxic, flashAt = 0.25f, focused = false)

@ViddikScreenshot(group = "Press", name = "Flash Media", width = 200, height = 76)
@Composable
fun PressFlashMedia() = PressSurface(OldgeSkin.Media, flashAt = 0.25f, focused = false)

@ViddikScreenshot(group = "Press", name = "Flash Crystal", width = 200, height = 76)
@Composable
fun PressFlashCrystal() = PressSurface(OldgeSkin.Crystal, flashAt = 0.25f, focused = false)

@ViddikScreenshot(group = "Press", name = "Focus Toxic", width = 200, height = 76)
@Composable
fun PressFocusToxic() = PressSurface(OldgeSkin.Toxic, flashAt = null, focused = true)

@ViddikScreenshot(group = "Press", name = "Focus Media", width = 200, height = 76)
@Composable
fun PressFocusMedia() = PressSurface(OldgeSkin.Media, flashAt = null, focused = true)

@ViddikScreenshot(group = "Press", name = "Focus Crystal", width = 200, height = 76)
@Composable
fun PressFocusCrystal() = PressSurface(OldgeSkin.Crystal, flashAt = null, focused = true)
