package io.github.youndie.oldge.actions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/Fab/preview.html and Segmented/preview.html, string for string.

@Composable
private fun FabRow(content: @Composable () -> Unit) =
    // `display: flex; gap: 12px; align-items: center`.
    Row(
        horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space3),
        verticalAlignment = Alignment.CenterVertically,
    ) { content() }

@Composable
private fun FabDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        FabRow {
            OldgeFab(OldgeIcons.Plus, "Создать", {}, expanded = false)
            OldgeExtendedFab("Написать", OldgeIcons.Edit, {})
            OldgeFab(OldgeIcons.Refresh, "Обновить", {}, tone = OldgeFabTone.Chrome)
        }
    }

@Composable
private fun FabStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        FabRow {
            OldgeFab(OldgeIcons.Plus, "Создать", {}, expanded = true)
            OldgeFab(OldgeIcons.Plus, "Создать", {}, interactionSource = pressed())
            OldgeExtendedFab("Написать", OldgeIcons.Edit, {}, tone = OldgeFabTone.Chrome, interactionSource = pressed())
        }
    }

private val periods =
    listOf(OldgeSegment("day", "День"), OldgeSegment("week", "Неделя"), OldgeSegment("month", "Месяц"))

@Composable
private fun SegmentedDemo(skin: OldgeSkin) = OldgeDemo(skin) { OldgeSegmented(periods, "week", {}, label = "Период") }

@Composable
private fun SegmentedStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeSegmented(
            listOf(
                OldgeSegment("list", "Список", OldgeIcons.Menu),
                OldgeSegment("grid", "Плитка", OldgeIcons.Grid),
            ),
            "grid",
            {},
            label = "Вид",
        )
        OldgeSegmented(periods, "day", {}, label = "Период")
    }

@ViddikScreenshot(group = "Fab", name = "Toxic", width = 390, height = 110)
@Composable
fun FabToxic() = FabDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Fab", name = "Media", width = 390, height = 110)
@Composable
fun FabMedia() = FabDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Fab", name = "Crystal", width = 390, height = 110)
@Composable
fun FabCrystal() = FabDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "FabStates", name = "Expanded and pressed Toxic", width = 390, height = 110)
@Composable
fun FabStatesToxic() = FabStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "FabStates", name = "Expanded and pressed Media", width = 390, height = 110)
@Composable
fun FabStatesMedia() = FabStates(OldgeSkin.Media)

@ViddikScreenshot(group = "FabStates", name = "Expanded and pressed Crystal", width = 390, height = 110)
@Composable
fun FabStatesCrystal() = FabStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "Segmented", name = "Toxic", width = 390, height = 80)
@Composable
fun SegmentedToxic() = SegmentedDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Segmented", name = "Media", width = 390, height = 80)
@Composable
fun SegmentedMedia() = SegmentedDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Segmented", name = "Crystal", width = 390, height = 80)
@Composable
fun SegmentedCrystal() = SegmentedDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "SegmentedStates", name = "Icons and first Toxic", width = 390, height = 140)
@Composable
fun SegmentedStatesToxic() = SegmentedStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "SegmentedStates", name = "Icons and first Media", width = 390, height = 140)
@Composable
fun SegmentedStatesMedia() = SegmentedStates(OldgeSkin.Media)

@ViddikScreenshot(group = "SegmentedStates", name = "Icons and first Crystal", width = 390, height = 140)
@Composable
fun SegmentedStatesCrystal() = SegmentedStates(OldgeSkin.Crystal)
