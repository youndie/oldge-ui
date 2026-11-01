package io.github.youndie.oldge.actions

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.forms.OldgeCheckbox
import io.github.youndie.oldge.forms.OldgeSearchBar
import io.github.youndie.oldge.forms.OldgeSelect
import io.github.youndie.oldge.forms.OldgeSelectOption
import io.github.youndie.oldge.forms.OldgeSlider
import io.github.youndie.oldge.forms.OldgeSwitch
import io.github.youndie.oldge.forms.OldgeTextField
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.navigation.OldgeBalloon
import io.github.youndie.oldge.navigation.OldgeTab
import io.github.youndie.oldge.navigation.OldgeTabs
import io.github.youndie.oldge.theme.OldgeDensity
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/Button/preview.html and OrbButton/preview.html, string for
// string; each row is `display: flex; gap: 8px; flex-wrap: wrap; align-items: center`.

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DemoRow(content: @Composable () -> Unit) =
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
        verticalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) { content() }

@Composable
private fun ButtonDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        DemoRow {
            OldgeButton("Отправить", {}, variant = OldgeButtonVariant.Primary, icon = OldgeIcons.Upload)
            OldgeButton("Отмена", {})
            OldgeButton("Подробнее", {}, variant = OldgeButtonVariant.Plain)
        }
        DemoRow {
            OldgeButton("Обзор…", {}, size = OldgeButtonSize.Small, icon = OldgeIcons.Folder)
            OldgeIconButton(OldgeIcons.Search, "Поиск", {})
            OldgeButton("OK", {}, variant = OldgeButtonVariant.Primary, size = OldgeButtonSize.Small)
            OldgeButton("Недоступна", {}, enabled = false)
        }
        OldgeButton("Продолжить", {}, variant = OldgeButtonVariant.Primary, size = OldgeButtonSize.Large, block = true)
    }

@Composable
private fun OrbDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        DemoRow {
            OldgeOrbButton(OldgeIcons.Help, "Справка", {}, size = OldgeOrbSize.Small)
            OldgeOrbButton(OldgeIcons.Minus, "Свернуть", {}, size = OldgeOrbSize.Small)
            OldgeOrbButton(OldgeIcons.Close, "Закрыть", {}, size = OldgeOrbSize.Small)
            OldgeOrbButton(OldgeIcons.User, "Профиль", {})
            OldgeOrbButton(OldgeIcons.Refresh, "Обновить", {}, tone = OldgeOrbTone.Chrome)
            OldgeOrbButton(OldgeIcons.Plus, "Создать", {}, size = OldgeOrbSize.Large, tone = OldgeOrbTone.Accent)
        }
    }

/**
 * An interaction source held pressed, for the pressed-state goldens (reduced motion: no flash).
 *
 * The press waits one frame: viddik runs effects on an unconfined dispatcher, so an effect here
 * emits before the control's own `collectIsPressedAsState` has subscribed, and the interaction
 * flow keeps no replay — emitted at once, the press was dropped and the goldens showed the rest
 * state (found in B-12 by removing the press and watching every golden still pass).
 */
@Composable
internal fun pressed(): MutableInteractionSource {
    val source = remember { MutableInteractionSource() }
    LaunchedEffect(source) {
        withFrameNanos { }
        source.emit(PressInteraction.Press(Offset(20f, 20f)))
    }
    return source
}

@Composable
private fun ButtonStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        DemoRow {
            OldgeButton("Отправить", {}, variant = OldgeButtonVariant.Primary, interactionSource = pressed())
            OldgeButton("Отмена", {}, interactionSource = pressed())
            OldgeButton("Подробнее", {}, variant = OldgeButtonVariant.Plain, interactionSource = pressed())
        }
    }

@Composable
private fun OrbStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        DemoRow {
            OldgeOrbButton(OldgeIcons.User, "Профиль", {}, interactionSource = pressed())
            OldgeOrbButton(
                OldgeIcons.Refresh,
                "Обновить",
                {},
                tone = OldgeOrbTone.Chrome,
                interactionSource = pressed(),
            )
            OldgeOrbButton(OldgeIcons.Plus, "Создать", {}, tone = OldgeOrbTone.Accent, interactionSource = pressed())
            OldgeOrbButton(OldgeIcons.Close, "Закрыть", {}, enabled = false)
        }
    }

@ViddikScreenshot(group = "Button", name = "Toxic", width = 390, height = 200)
@Composable
fun ButtonToxic() = ButtonDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Button", name = "Media", width = 390, height = 200)
@Composable
fun ButtonMedia() = ButtonDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Button", name = "Crystal", width = 390, height = 200)
@Composable
fun ButtonCrystal() = ButtonDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "OrbButton", name = "Toxic", width = 390, height = 110)
@Composable
fun OrbButtonToxic() = OrbDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "OrbButton", name = "Media", width = 390, height = 110)
@Composable
fun OrbButtonMedia() = OrbDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "OrbButton", name = "Crystal", width = 390, height = 110)
@Composable
fun OrbButtonCrystal() = OrbDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "ButtonStates", name = "Pressed Toxic", width = 390, height = 76)
@Composable
fun ButtonStatesPressedToxic() = ButtonStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "ButtonStates", name = "Pressed Media", width = 390, height = 76)
@Composable
fun ButtonStatesPressedMedia() = ButtonStates(OldgeSkin.Media)

@ViddikScreenshot(group = "ButtonStates", name = "Pressed Crystal", width = 390, height = 76)
@Composable
fun ButtonStatesPressedCrystal() = ButtonStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "OrbButtonStates", name = "Pressed Toxic", width = 390, height = 80)
@Composable
fun OrbButtonStatesPressedToxic() = OrbStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "OrbButtonStates", name = "Pressed Media", width = 390, height = 80)
@Composable
fun OrbButtonStatesPressedMedia() = OrbStates(OldgeSkin.Media)

@ViddikScreenshot(group = "OrbButtonStates", name = "Pressed Crystal", width = 390, height = 80)
@Composable
fun OrbButtonStatesPressedCrystal() = OrbStates(OldgeSkin.Crystal)

// B-78: a toolbar's toggles — a plain icon button, one off, one on, one on beside a Primary. No
// preview shows a toggle, so no reference; the golden is this library's own.
@Composable
private fun IconToggles(skin: OldgeSkin) =
    OldgeDemo(skin) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OldgeIconButton(OldgeIcons.Search, "Поиск", {})
            OldgeIconButton(OldgeIcons.Info, "Подробности", {}, selected = false)
            OldgeIconButton(OldgeIcons.Info, "Подробности", {}, selected = true)
            OldgeIconButton(OldgeIcons.Gear, "Настройки", {}, selected = true)
            OldgeButton("Сохранить", {}, variant = OldgeButtonVariant.Primary)
        }
    }

// B-77: the destructive confirm. The Dialog reference shows it at rest; its sizes, press and
// disabled state have no reference, so these goldens are this library's own.
@Composable
private fun DangerButtons(skin: OldgeSkin) =
    OldgeDemo(skin) {
        DemoRow {
            OldgeButton("Удалить", {}, variant = OldgeButtonVariant.Danger, icon = OldgeIcons.Trash)
            OldgeButton("Стереть", {}, variant = OldgeButtonVariant.Danger, size = OldgeButtonSize.Small)
            OldgeButton("Удалить", {}, variant = OldgeButtonVariant.Danger, enabled = false)
        }
        DemoRow {
            OldgeButton("Отмена", {})
            OldgeButton(
                "Удалить",
                {},
                variant = OldgeButtonVariant.Danger,
                icon = OldgeIcons.Trash,
                interactionSource = pressed(),
            )
        }
    }

@ViddikScreenshot(group = "ButtonDanger", name = "Toxic", width = 390, height = 130)
@Composable
fun ButtonDangerToxic() = DangerButtons(OldgeSkin.Toxic)

@ViddikScreenshot(group = "ButtonDanger", name = "Media", width = 390, height = 130)
@Composable
fun ButtonDangerMedia() = DangerButtons(OldgeSkin.Media)

@ViddikScreenshot(group = "ButtonDanger", name = "Crystal", width = 390, height = 130)
@Composable
fun ButtonDangerCrystal() = DangerButtons(OldgeSkin.Crystal)

@ViddikScreenshot(group = "IconToggle", name = "Toxic", width = 390, height = 80)
@Composable
fun IconToggleToxic() = IconToggles(OldgeSkin.Toxic)

@ViddikScreenshot(group = "IconToggle", name = "Media", width = 390, height = 80)
@Composable
fun IconToggleMedia() = IconToggles(OldgeSkin.Media)

@ViddikScreenshot(group = "IconToggle", name = "Crystal", width = 390, height = 80)
@Composable
fun IconToggleCrystal() = IconToggles(OldgeSkin.Crystal)

// B-80: a desktop toolbar under OldgeDensity.Pointer — square icon buttons and a toggle, the search
// bar with text (so its clear orb shows), and the three button sizes. The design system has no
// pointer sizes, so no reference; the golden is this library's own.
@Composable
private fun PointerToolbar(skin: OldgeSkin) =
    OldgeDemo(skin, density = OldgeDensity.Pointer) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            OldgeIconButton(OldgeIcons.Plus, "Добавить", {})
            OldgeIconButton(OldgeIcons.Info, "Подробности", {}, selected = true)
            OldgeIconButton(OldgeIcons.Gear, "Настройки", {}, size = OldgeButtonSize.Small)
            OldgeSearchBar(Modifier.weight(1f), defaultValue = "ubuntu")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OldgeButton("Отмена", {}, size = OldgeButtonSize.Small)
            OldgeButton("Сохранить", {}, variant = OldgeButtonVariant.Primary)
            OldgeButton("Готово", {}, size = OldgeButtonSize.Large)
        }
    }

@ViddikScreenshot(group = "PointerDensity", name = "Toxic", width = 390, height = 120)
@Composable
fun PointerDensityToxic() = PointerToolbar(OldgeSkin.Toxic)

@ViddikScreenshot(group = "PointerDensity", name = "Media", width = 390, height = 120)
@Composable
fun PointerDensityMedia() = PointerToolbar(OldgeSkin.Media)

@ViddikScreenshot(group = "PointerDensity", name = "Crystal", width = 390, height = 120)
@Composable
fun PointerDensityCrystal() = PointerToolbar(OldgeSkin.Crystal)

// B-83: a settings form under OldgeDensity.Pointer — a text field, a select, a switch, a checkbox,
// a slider and a balloon with its close. The library's own golden, as PointerDensity's is.
@Composable
private fun PointerForm(skin: OldgeSkin) =
    OldgeDemo(skin, density = OldgeDensity.Pointer) {
        OldgeTextField("Папка загрузок", "/home/pavel/Downloads", {}, readOnly = true)
        OldgeSelect(
            "Шифрование",
            listOf(OldgeSelectOption("on", "Предпочитать"), OldgeSelectOption("off", "Выключено")),
            "on",
            {},
        )
        OldgeSwitch("Запускать при входе", true, {})
        OldgeCheckbox("Показывать скорость в заголовке", true, {})
        OldgeSlider(0.4f, {}, label = "Лимит отдачи", valueText = "400 КБ/с")
        OldgeBalloon("Настройки сохранены", onClose = {})
    }

@ViddikScreenshot(group = "PointerForm", name = "Toxic", width = 390, height = 420)
@Composable
fun PointerFormToxic() = PointerForm(OldgeSkin.Toxic)

@ViddikScreenshot(group = "PointerForm", name = "Media", width = 390, height = 420)
@Composable
fun PointerFormMedia() = PointerForm(OldgeSkin.Media)

@ViddikScreenshot(group = "PointerForm", name = "Crystal", width = 390, height = 420)
@Composable
fun PointerFormCrystal() = PointerForm(OldgeSkin.Crystal)

// B-84: Segmented and Tabs under OldgeDensity.Pointer — a settings row with a 32 dp field and a
// segmented choice level with it, and a details panel's tabs. The library's own golden.
@Composable
private fun PointerChoices(skin: OldgeSkin) =
    OldgeDemo(skin, density = OldgeDensity.Pointer) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OldgeTextField("Порт", "51413", {}, Modifier.width(120.dp), showLabel = false)
            OldgeSegmented(
                listOf(OldgeSegment("auto", "Авто"), OldgeSegment("manual", "Вручную")),
                "manual",
                {},
                "Выбор порта",
                Modifier.width(200.dp),
            )
        }
        OldgeTabs(
            listOf(OldgeTab("files", "Файлы"), OldgeTab("peers", "Пиры"), OldgeTab("info", "Сведения")),
            "peers",
            {},
            "Детали",
        )
    }

@ViddikScreenshot(group = "PointerChoices", name = "Toxic", width = 390, height = 150)
@Composable
fun PointerChoicesToxic() = PointerChoices(OldgeSkin.Toxic)

@ViddikScreenshot(group = "PointerChoices", name = "Media", width = 390, height = 150)
@Composable
fun PointerChoicesMedia() = PointerChoices(OldgeSkin.Media)

@ViddikScreenshot(group = "PointerChoices", name = "Crystal", width = 390, height = 150)
@Composable
fun PointerChoicesCrystal() = PointerChoices(OldgeSkin.Crystal)
