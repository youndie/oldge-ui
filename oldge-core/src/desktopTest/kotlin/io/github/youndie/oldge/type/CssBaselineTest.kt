package io.github.youndie.oldge.type

import java.io.File
import java.io.RandomAccessFile
import kotlin.test.Test
import kotlin.test.assertEquals

class CssBaselineTest {
    /** The two cases the Icon and Divider references measured (B-11). */
    @Test
    fun blink_rounds_the_metrics_and_floors_the_half_leading() {
        // Silkscreen 10/12: ascent 10.3 → 10, descent 2.5 → 3, half-leading floor(−0.5) = −1.
        assertEquals(9f, cssBaseline(BundledFontMetrics.silkscreen, 10f, 12f))
        // DejaVu Sans Condensed 15/20: 13.92 → 14, 3.54 → 4, half-leading 1.
        assertEquals(15f, cssBaseline(BundledFontMetrics.dejavuSansCondensed, 15f, 20f))
    }

    /** The generated metrics are the files' own, read here without fontTools. */
    @Test
    fun the_generated_metrics_are_read_from_the_font_files() {
        val expected =
            mapOf(
                "dejavu_sans_condensed.ttf" to BundledFontMetrics.dejavuSansCondensed,
                "dejavu_sans_condensed_bold.ttf" to BundledFontMetrics.dejavuSansCondensedBold,
                "fira_sans_bold.ttf" to BundledFontMetrics.firaSansBold,
                "share_tech_mono.ttf" to BundledFontMetrics.shareTechMono,
                "pt_mono.ttf" to BundledFontMetrics.ptMono,
                "silkscreen.ttf" to BundledFontMetrics.silkscreen,
                "tiny5.ttf" to BundledFontMetrics.tiny5,
            )
        for ((file, m) in expected) {
            val read = readVerticalMetrics(File("src/commonMain/composeResources/font/$file"))
            assertEquals(Triple(m.ascent, m.descent, m.unitsPerEm), read, file)
        }
    }

    /** `head.unitsPerEm`, and OS/2 typo ascender/descender with USE_TYPO_METRICS, else hhea. */
    private fun readVerticalMetrics(file: File): Triple<Int, Int, Int> =
        RandomAccessFile(file, "r").use { f ->
            f.seek(4)
            val tables = f.readUnsignedShort()
            val offsets = HashMap<String, Long>()
            repeat(tables) {
                f.seek(12L + it * 16)
                val tag = ByteArray(4).also(f::readFully).decodeToString()
                f.skipBytes(4)
                offsets[tag] = f.readInt().toLong() and 0xFFFFFFFFL
            }

            fun short(
                table: String,
                at: Int,
            ): Int {
                f.seek(offsets.getValue(table) + at)
                return f.readShort().toInt()
            }
            val upm = short("head", 18) and 0xFFFF
            val useTypo = (short("OS/2", 62) and (1 shl 7)) != 0
            if (useTypo) {
                Triple(
                    short("OS/2", 68),
                    -short("OS/2", 70),
                    upm,
                )
            } else {
                Triple(short("hhea", 4), -short("hhea", 6), upm)
            }
        }
}
