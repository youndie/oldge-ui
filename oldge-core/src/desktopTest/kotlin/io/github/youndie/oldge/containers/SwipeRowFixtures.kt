package io.github.youndie.oldge.containers

import androidx.compose.runtime.Composable
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/SwipeRow/preview.html, string for string.

@Composable
private fun SwipeRowDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeList {
            row {
                OldgeSwipeRow(
                    listOf(
                        OldgeSwipeAction(OldgeIcons.Folder, "Архив", {}),
                        OldgeSwipeAction(OldgeIcons.Trash, "Удалить", {}, OldgeSwipeTone.Danger),
                    ),
                    defaultOpen = true,
                ) {
                    OldgeListItem(
                        "Анна Ким",
                        subtitle = "Смахните влево",
                        icon = OldgeIcons.Mail,
                        value = "09:14",
                        onClick = {},
                    )
                }
            }
            row {
                OldgeSwipeRow(
                    listOf(
                        OldgeSwipeAction(OldgeIcons.Star, "Важное", {}, OldgeSwipeTone.Accent),
                        OldgeSwipeAction(OldgeIcons.Trash, "Удалить", {}, OldgeSwipeTone.Danger),
                    ),
                ) {
                    OldgeListItem(
                        "Иван Ли",
                        subtitle = "Потяните строку",
                        icon = OldgeIcons.Mail,
                        value = "чт",
                        onClick = {},
                    )
                }
            }
        }
    }

/** What the preview does not show: three actions open, one of each tone, and a single action open. */
@Composable
private fun SwipeRowStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeList {
            row {
                OldgeSwipeRow(
                    listOf(
                        OldgeSwipeAction(OldgeIcons.Star, "Важное", {}, OldgeSwipeTone.Accent),
                        OldgeSwipeAction(OldgeIcons.Folder, "Архив", {}),
                        OldgeSwipeAction(OldgeIcons.Trash, "Удалить", {}, OldgeSwipeTone.Danger),
                    ),
                    defaultOpen = true,
                ) { OldgeListItem("Три действия", subtitle = "Акцент, хром, опасное", icon = OldgeIcons.Mail) }
            }
            row {
                OldgeSwipeRow(listOf(OldgeSwipeAction(OldgeIcons.Folder, "Архив", {})), defaultOpen = true) {
                    OldgeListItem("Одно действие", icon = OldgeIcons.Mail, value = "пн")
                }
            }
        }
    }

@ViddikScreenshot(group = "SwipeRowStates", name = "Three and one open Toxic", width = 390, height = 200)
@Composable
fun SwipeRowStatesToxic() = SwipeRowStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "SwipeRowStates", name = "Three and one open Media", width = 390, height = 200)
@Composable
fun SwipeRowStatesMedia() = SwipeRowStates(OldgeSkin.Media)

@ViddikScreenshot(group = "SwipeRowStates", name = "Three and one open Crystal", width = 390, height = 200)
@Composable
fun SwipeRowStatesCrystal() = SwipeRowStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "SwipeRow", name = "Toxic", width = 390, height = 200)
@Composable
fun SwipeRowToxic() = SwipeRowDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "SwipeRow", name = "Media", width = 390, height = 200)
@Composable
fun SwipeRowMedia() = SwipeRowDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "SwipeRow", name = "Crystal", width = 390, height = 200)
@Composable
fun SwipeRowCrystal() = SwipeRowDemo(OldgeSkin.Crystal)
