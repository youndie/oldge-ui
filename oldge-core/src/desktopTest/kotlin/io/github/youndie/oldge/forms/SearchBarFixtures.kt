package io.github.youndie.oldge.forms

import androidx.compose.runtime.Composable
import io.github.youndie.oldge.feedback.OldgeAvatar
import io.github.youndie.oldge.feedback.OldgeAvatarSize
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/SearchBar/preview.html, string for string.

@Composable
private fun Pavel() = OldgeAvatar(name = "Павел Вотяков", size = OldgeAvatarSize.Small)

@Composable
private fun SearchBarDemo(skin: OldgeSkin) =
    OldgeDemo(
        skin,
    ) { OldgeSearchBar(placeholder = "Файлы, папки, теги", defaultValue = "отчёт", trailing = { Pavel() }) }

/** What the preview does not show: focused (the magnifier's wiggle at rest), empty and with text. */
@Composable
private fun SearchBarStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeSearchBar(placeholder = "Файлы, папки, теги", interactionSource = focused())
        OldgeSearchBar(placeholder = "Файлы, папки, теги", defaultValue = "отчёт", interactionSource = focused())
        OldgeSearchBar(placeholder = "Поиск по почте")
    }

@ViddikScreenshot(group = "SearchBar", name = "Toxic", width = 390, height = 90)
@Composable
fun SearchBarToxic() = SearchBarDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "SearchBar", name = "Media", width = 390, height = 90)
@Composable
fun SearchBarMedia() = SearchBarDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "SearchBar", name = "Crystal", width = 390, height = 90)
@Composable
fun SearchBarCrystal() = SearchBarDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "SearchBarStates", name = "Focused empty and with text Toxic", width = 390, height = 220)
@Composable
fun SearchBarStatesToxic() = SearchBarStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "SearchBarStates", name = "Focused empty and with text Media", width = 390, height = 220)
@Composable
fun SearchBarStatesMedia() = SearchBarStates(OldgeSkin.Media)

@ViddikScreenshot(group = "SearchBarStates", name = "Focused empty and with text Crystal", width = 390, height = 220)
@Composable
fun SearchBarStatesCrystal() = SearchBarStates(OldgeSkin.Crystal)
