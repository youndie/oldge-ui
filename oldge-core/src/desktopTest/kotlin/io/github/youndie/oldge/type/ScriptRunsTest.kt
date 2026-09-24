package io.github.youndie.oldge.type

import androidx.compose.ui.text.font.FontFamily
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScriptRunsTest {
    // Covers ASCII only: 0x20..0x7E.
    private val asciiOnly = intArrayOf(0x20, 0x7E)
    private val companion = FontFamily.Monospace

    @Test
    fun a_mixed_readout_becomes_alternating_runs_and_digits_stay_in_the_primary_face() {
        val text = oldgeTextOf("23,4 ГБ из 64", companion, asciiOnly)
        val companionRuns = text.spanStyles.map { text.text.substring(it.start, it.end) }
        assertEquals(listOf("ГБ", "из"), companionRuns)
        assertTrue(text.spanStyles.all { it.item.fontFamily == companion })
    }

    @Test
    fun a_string_the_primary_face_covers_carries_no_span_at_all() {
        assertTrue(oldgeTextOf("SYNC 5G", companion, asciiOnly).spanStyles.isEmpty())
    }

    @Test
    fun a_code_point_outside_the_basic_plane_is_one_run_not_two_halves() {
        val emoji = String(Character.toChars(0x1F600))
        val text = oldgeTextOf("a${emoji}b", companion, asciiOnly)
        assertEquals(listOf(emoji), text.spanStyles.map { text.text.substring(it.start, it.end) })
    }

    @Test
    fun coverage_is_inclusive_at_both_ends_of_every_range() {
        val ranges = intArrayOf(0x20, 0x7E, 0x410, 0x44F)
        assertTrue(ranges.covers(0x20))
        assertTrue(ranges.covers(0x7E))
        assertTrue(ranges.covers(0x410))
        assertTrue(ranges.covers(0x44F))
        assertFalse(ranges.covers(0x7F))
        assertFalse(ranges.covers(0x1F))
        assertFalse(ranges.covers(0x450))
    }
}
