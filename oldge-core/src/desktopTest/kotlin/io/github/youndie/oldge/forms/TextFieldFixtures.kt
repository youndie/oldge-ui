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

// reference/design-system/components/TextField/preview.html, string for string.

@Composable
private fun TextFieldDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeTextField("Имя профиля", "Павел", {}, help = "Видно только вам")
        OldgeTextField("Поиск", "", {}, icon = OldgeIcons.Search, placeholder = "Файлы, папки, теги")
        OldgeTextField("Эл. почта", "pavel@", {}, error = "Адрес неполный: нет домена")
    }

/** A source held focused, waiting a frame first for the reason `pressed()` does (B-12). */
@Composable
internal fun focused(): MutableInteractionSource {
    val source = remember { MutableInteractionSource() }
    LaunchedEffect(source) {
        withFrameNanos { }
        source.emit(FocusInteraction.Focus())
    }
    return source
}

@Composable
private fun TextFieldStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeTextField("Пароль", "secret", {}, reveal = true, interactionSource = focused())
        OldgeTextField("Комментарий", "", {}, placeholder = "Пара слов о файле", lines = 3, icon = OldgeIcons.Edit)
        OldgeTextField("Недоступно", "Только чтение", {}, enabled = false, help = "Управляется организацией")
    }

@ViddikScreenshot(group = "TextField", name = "Toxic", width = 390, height = 300)
@Composable
fun TextFieldToxic() = TextFieldDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "TextField", name = "Media", width = 390, height = 300)
@Composable
fun TextFieldMedia() = TextFieldDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "TextField", name = "Crystal", width = 390, height = 300)
@Composable
fun TextFieldCrystal() = TextFieldDemo(OldgeSkin.Crystal)

@ViddikScreenshot(
    group = "TextFieldStates",
    name = "Focused password multiline disabled Toxic",
    width = 390,
    height = 340,
)
@Composable
fun TextFieldStatesToxic() = TextFieldStates(OldgeSkin.Toxic)

@ViddikScreenshot(
    group = "TextFieldStates",
    name = "Focused password multiline disabled Media",
    width = 390,
    height = 340,
)
@Composable
fun TextFieldStatesMedia() = TextFieldStates(OldgeSkin.Media)

@ViddikScreenshot(
    group = "TextFieldStates",
    name = "Focused password multiline disabled Crystal",
    width = 390,
    height = 340,
)
@Composable
fun TextFieldStatesCrystal() = TextFieldStates(OldgeSkin.Crystal)

/**
 * scripts/probes/FieldProbe (B-53): the two shapes no preview shows, held to Chrome — a reveal field,
 * which the orb's 44 dp box makes 46 dp tall, and a three-line field, 84 dp.
 */
@Composable
private fun FieldProbe(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeTextField("Пароль", "secret", {}, reveal = true)
        OldgeTextField("Комментарий", "", {}, placeholder = "Пара слов о файле", lines = 3)
    }

@ViddikScreenshot(group = "FieldProbe", name = "Toxic", width = 390, height = 260)
@Composable
fun FieldProbeToxic() = FieldProbe(OldgeSkin.Toxic)

@ViddikScreenshot(group = "FieldProbe", name = "Media", width = 390, height = 260)
@Composable
fun FieldProbeMedia() = FieldProbe(OldgeSkin.Media)

@ViddikScreenshot(group = "FieldProbe", name = "Crystal", width = 390, height = 260)
@Composable
fun FieldProbeCrystal() = FieldProbe(OldgeSkin.Crystal)
