package io.github.youndie.oldge.forms

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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

/**
 * B-85: a highlighted field, ringed in `accent-ink`, and one that is highlighted and in error, where
 * the error wins. Not a design system state, so there is no reference to hold it to.
 */
@Composable
private fun TextFieldHighlighted(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeTextField("Порт", "6881", {}, help = "По умолчанию 51413", highlighted = true)
        OldgeTextField("Порт", "68", {}, error = "Порт ниже 1024", highlighted = true)
    }

@ViddikScreenshot(group = "TextFieldHighlighted", name = "Toxic", width = 390, height = 220)
@Composable
fun TextFieldHighlightedToxic() = TextFieldHighlighted(OldgeSkin.Toxic)

@ViddikScreenshot(group = "TextFieldHighlighted", name = "Media", width = 390, height = 220)
@Composable
fun TextFieldHighlightedMedia() = TextFieldHighlighted(OldgeSkin.Media)

@ViddikScreenshot(group = "TextFieldHighlighted", name = "Crystal", width = 390, height = 220)
@Composable
fun TextFieldHighlightedCrystal() = TextFieldHighlighted(OldgeSkin.Crystal)

/**
 * B-89: a read-only path in a 220 dp field, cut at its end (the input's own), at its start and in its
 * middle. Not a design system state, so there is no reference to hold it to.
 */
@Composable
private fun TextFieldOverflow(skin: OldgeSkin) =
    OldgeDemo(skin) {
        val path = "/home/user/Downloads/iso/ubuntu-24.04"
        Column(Modifier.width(220.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OldgeTextField("Папка", path, {}, readOnly = true, showLabel = false)
            OldgeTextField("Папка", path, {}, readOnly = true, showLabel = false, overflow = TextOverflow.StartEllipsis)
            OldgeTextField(
                "Папка",
                path,
                {},
                readOnly = true,
                showLabel = false,
                overflow = TextOverflow.MiddleEllipsis,
            )
        }
    }

@ViddikScreenshot(group = "TextFieldOverflow", name = "Toxic", width = 390, height = 190)
@Composable
fun TextFieldOverflowToxic() = TextFieldOverflow(OldgeSkin.Toxic)

@ViddikScreenshot(group = "TextFieldOverflow", name = "Media", width = 390, height = 190)
@Composable
fun TextFieldOverflowMedia() = TextFieldOverflow(OldgeSkin.Media)

@ViddikScreenshot(group = "TextFieldOverflow", name = "Crystal", width = 390, height = 190)
@Composable
fun TextFieldOverflowCrystal() = TextFieldOverflow(OldgeSkin.Crystal)
