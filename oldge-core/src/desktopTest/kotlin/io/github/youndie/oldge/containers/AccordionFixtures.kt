package io.github.youndie.oldge.containers

import androidx.compose.runtime.Composable
import io.github.youndie.oldge.actions.pressed
import io.github.youndie.oldge.harness.DemoText
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/Accordion/preview.html, string for string.

@Composable
private fun AccordionDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeAccordion("Приложения", icon = OldgeIcons.Grid) {
            OldgeList(bare = true) {
                row { OldgeListItem("Документы", icon = OldgeIcons.Doc, dense = true, onClick = {}) }
                row { OldgeListItem("Фотоальбом", icon = OldgeIcons.Image, dense = true, onClick = {}) }
                row { OldgeListItem("Музыка", icon = OldgeIcons.Note, dense = true, onClick = {}) }
            }
        }
        OldgeAccordion("Инструменты", icon = OldgeIcons.Gear, defaultOpen = false) { DemoText("Скрыто") }
        OldgeAccordion("Справка", icon = OldgeIcons.Help, defaultOpen = false) { DemoText("Скрыто") }
    }

/** What the preview does not show: a pressed header, a title too long for it, and a plain-text body. */
@Composable
private fun AccordionStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeAccordion("Нажата", icon = OldgeIcons.Star, defaultOpen = false, interactionSource = pressed()) {
            DemoText("Скрыто")
        }
        OldgeAccordion(
            "Резервное копирование и синхронизация устройств",
            icon = OldgeIcons.Cloud,
            defaultOpen = false,
        ) {
            DemoText("Скрыто")
        }
        OldgeAccordion("Заметки", icon = OldgeIcons.Doc) { DemoText("Тело — вставная панель под заголовком.") }
    }

@ViddikScreenshot(group = "AccordionStates", name = "Pressed long title text body Toxic", width = 390, height = 240)
@Composable
fun AccordionStatesToxic() = AccordionStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "AccordionStates", name = "Pressed long title text body Media", width = 390, height = 240)
@Composable
fun AccordionStatesMedia() = AccordionStates(OldgeSkin.Media)

@ViddikScreenshot(group = "AccordionStates", name = "Pressed long title text body Crystal", width = 390, height = 240)
@Composable
fun AccordionStatesCrystal() = AccordionStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "Accordion", name = "Toxic", width = 390, height = 340)
@Composable
fun AccordionToxic() = AccordionDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Accordion", name = "Media", width = 390, height = 340)
@Composable
fun AccordionMedia() = AccordionDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Accordion", name = "Crystal", width = 390, height = 340)
@Composable
fun AccordionCrystal() = AccordionDemo(OldgeSkin.Crystal)
