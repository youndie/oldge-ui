package io.github.youndie.oldge.forms

import androidx.compose.runtime.Composable
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.viddik.annotations.ViddikScreenshot
import kotlinx.datetime.LocalDate

// reference/design-system/components/DatePicker/preview.html, string for string. The reference was
// rendered with the clock pinned to 2026-09-24T12:00 (B-03), so today is pinned to the same day: the
// preview's value is today, and the one cell is both.

private val TODAY = LocalDate(2026, 9, 24)

@Composable
private fun DatePickerDemo(skin: OldgeSkin) = OldgeDemo(skin) { OldgeDatePicker(TODAY, {}, today = TODAY) }

/** Today and the chosen day apart, in a month that starts on a Monday (June 2026): no leading blanks. */
@Composable
private fun DatePickerStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeDatePicker(LocalDate(2026, 6, 3), {}, today = LocalDate(2026, 6, 15))
    }

@ViddikScreenshot(group = "DatePicker", name = "Toxic", width = 390, height = 370)
@Composable
fun DatePickerToxic() = DatePickerDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "DatePicker", name = "Media", width = 390, height = 370)
@Composable
fun DatePickerMedia() = DatePickerDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "DatePicker", name = "Crystal", width = 390, height = 370)
@Composable
fun DatePickerCrystal() = DatePickerDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "DatePickerStates", name = "Today apart Toxic", width = 390, height = 400)
@Composable
fun DatePickerStatesToxic() = DatePickerStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "DatePickerStates", name = "Today apart Media", width = 390, height = 400)
@Composable
fun DatePickerStatesMedia() = DatePickerStates(OldgeSkin.Media)

@ViddikScreenshot(group = "DatePickerStates", name = "Today apart Crystal", width = 390, height = 400)
@Composable
fun DatePickerStatesCrystal() = DatePickerStates(OldgeSkin.Crystal)
