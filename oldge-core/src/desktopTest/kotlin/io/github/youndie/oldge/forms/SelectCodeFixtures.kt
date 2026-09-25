package io.github.youndie.oldge.forms

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/Select/preview.html and CodeInput/preview.html, string for
// string.

private val networks =
    listOf(
        OldgeSelectOption("wifi", "Только Wi-Fi", OldgeIcons.Cloud),
        OldgeSelectOption("any", "Любую сеть", OldgeIcons.Refresh),
        OldgeSelectOption("off", "Вручную", OldgeIcons.Lock),
    )

@Composable
private fun SelectDemo(skin: OldgeSkin) =
    OldgeDemo(skin) { OldgeSelect("Синхронизировать через", networks, "wifi", {}) }

@Composable
private fun CodeDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeCodeInput("Код из СМС", "482", {}, help = "Отправили на +382 •• ••• 41")
        OldgeCodeInput("Код из письма", "120934", {}, error = "Код устарел — запросите новый")
    }

/** A source held focused, waiting a frame first for the reason `pressed()` does (B-12). */
@Composable
private fun held(): MutableInteractionSource {
    val source = remember { MutableInteractionSource() }
    LaunchedEffect(source) {
        withFrameNanos { }
        source.emit(FocusInteraction.Focus())
    }
    return source
}

@Composable
private fun States(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeSelect("Сфокусирован", networks, "any", {}, interactionSource = held())
        OldgeCodeInput("Ввод", "48", {}, interactionSource = held())
        OldgeCodeInput("Четыре цифры", "7", {}, length = 4)
    }

@ViddikScreenshot(group = "Select", name = "Toxic", width = 390, height = 110)
@Composable
fun SelectToxic() = SelectDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Select", name = "Media", width = 390, height = 110)
@Composable
fun SelectMedia() = SelectDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Select", name = "Crystal", width = 390, height = 110)
@Composable
fun SelectCrystal() = SelectDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "CodeInput", name = "Toxic", width = 390, height = 248)
@Composable
fun CodeInputToxic() = CodeDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "CodeInput", name = "Media", width = 390, height = 248)
@Composable
fun CodeInputMedia() = CodeDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "CodeInput", name = "Crystal", width = 390, height = 248)
@Composable
fun CodeInputCrystal() = CodeDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "CodeInputStates", name = "Focused and short Toxic", width = 390, height = 320)
@Composable
fun SelectCodeStatesToxic() = States(OldgeSkin.Toxic)

@ViddikScreenshot(group = "CodeInputStates", name = "Focused and short Media", width = 390, height = 320)
@Composable
fun SelectCodeStatesMedia() = States(OldgeSkin.Media)

@ViddikScreenshot(group = "CodeInputStates", name = "Focused and short Crystal", width = 390, height = 320)
@Composable
fun SelectCodeStatesCrystal() = States(OldgeSkin.Crystal)
