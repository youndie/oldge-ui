package io.github.youndie.oldge.actions

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.icons.OldgeIcons
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
