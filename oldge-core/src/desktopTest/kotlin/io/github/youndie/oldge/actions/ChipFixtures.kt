package io.github.youndie.oldge.actions

import androidx.compose.runtime.Composable
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/Chip/preview.html, string for string.

@Composable
private fun ChipDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeChipGroup("Фильтр") {
            OldgeFilterChip("Документы", true, {})
            OldgeFilterChip("Фото", false, {})
            OldgeFilterChip("Музыка", false, {})
            OldgeAssistChip("Новый фильтр", {}, icon = OldgeIcons.Plus)
        }
        OldgeChipGroup("Теги") {
            for (tag in listOf("Работа", "Отчёты", "2026")) OldgeInputChip(tag, {}, "Убрать $tag")
        }
    }

@Composable
private fun ChipStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeChipGroup("Нажатые") {
            OldgeFilterChip("Документы", true, {}, interactionSource = pressed())
            OldgeFilterChip("Фото", false, {}, icon = OldgeIcons.Image, interactionSource = pressed())
            OldgeAssistChip("Новый фильтр", {}, icon = OldgeIcons.Plus, interactionSource = pressed())
        }
        OldgeChipGroup("Лента", scroll = true) {
            for (tag in listOf("Работа", "Отчёты", "2026", "Архив", "Черновики", "Входящие")) {
                OldgeInputChip(tag, {}, "Убрать $tag")
            }
        }
    }

@ViddikScreenshot(group = "Chip", name = "Toxic", width = 390, height = 156)
@Composable
fun ChipToxic() = ChipDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Chip", name = "Media", width = 390, height = 156)
@Composable
fun ChipMedia() = ChipDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Chip", name = "Crystal", width = 390, height = 156)
@Composable
fun ChipCrystal() = ChipDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "ChipStates", name = "Pressed and scrolling Toxic", width = 390, height = 170)
@Composable
fun ChipStatesToxic() = ChipStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "ChipStates", name = "Pressed and scrolling Media", width = 390, height = 170)
@Composable
fun ChipStatesMedia() = ChipStates(OldgeSkin.Media)

@ViddikScreenshot(group = "ChipStates", name = "Pressed and scrolling Crystal", width = 390, height = 170)
@Composable
fun ChipStatesCrystal() = ChipStates(OldgeSkin.Crystal)
