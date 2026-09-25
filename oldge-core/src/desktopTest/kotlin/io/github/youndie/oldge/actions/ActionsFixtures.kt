package io.github.youndie.oldge.actions

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.viddik.annotations.ViddikScreenshot

// scripts/probes/ActionsProbe/preview.html, string for string (B-56): og-actions, which no component
// preview shows.

@Composable
private fun ActionsProbe(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeActions {
            OldgeButton("Сбросить", {})
            OldgeButton("Сохранить", {}, variant = OldgeButtonVariant.Primary)
        }
        // The Edge page's German pair in its 288 px column: they wrap, one per line.
        OldgeActions(Modifier.width(288.dp)) {
            OldgeButton("Änderungen verwerfen", {})
            OldgeButton("Speichern und fortfahren", {}, variant = OldgeButtonVariant.Primary)
        }
        OldgeActions {
            OldgeButton("Отмена", {}, size = OldgeButtonSize.Small)
            OldgeButton("Удалить навсегда", {}, size = OldgeButtonSize.Small)
            OldgeButton("Ок", {}, size = OldgeButtonSize.Small, variant = OldgeButtonVariant.Primary)
        }
    }

@ViddikScreenshot(group = "ActionsProbe", name = "Toxic", width = 390, height = 260)
@Composable
fun ActionsProbeToxic() = ActionsProbe(OldgeSkin.Toxic)

@ViddikScreenshot(group = "ActionsProbe", name = "Media", width = 390, height = 260)
@Composable
fun ActionsProbeMedia() = ActionsProbe(OldgeSkin.Media)

@ViddikScreenshot(group = "ActionsProbe", name = "Crystal", width = 390, height = 260)
@Composable
fun ActionsProbeCrystal() = ActionsProbe(OldgeSkin.Crystal)
