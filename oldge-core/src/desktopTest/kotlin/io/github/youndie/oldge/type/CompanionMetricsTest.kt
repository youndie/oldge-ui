package io.github.youndie.oldge.type

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * `OldgeScriptText` lays its line out on the primary face's metrics alone (B-49). That holds only
 * while each companion's ascent and descent, per em, sit inside its primary's: a companion that
 * reached past would make a mixed line taller than Compose's baseline arithmetic assumes, and its
 * Cyrillic would sit off Chrome's.
 */
class CompanionMetricsTest {
    @Test
    fun each_companion_sits_inside_its_primary_face() {
        val pairs =
            mapOf(
                "lcd: Share Tech Mono / PT Mono" to (BundledFontMetrics.shareTechMono to BundledFontMetrics.ptMono),
                "pixel: Silkscreen / Tiny5" to (BundledFontMetrics.silkscreen to BundledFontMetrics.tiny5),
            )
        val past =
            pairs.filter { (_, pair) ->
                val (primary, companion) = pair
                perEm(companion.ascent, companion) > perEm(primary.ascent, primary) ||
                    perEm(companion.descent, companion) > perEm(primary.descent, primary)
            }
        assertEquals(emptySet(), past.keys)
    }

    private fun perEm(
        units: Int,
        face: FontVerticalMetrics,
    ) = units.toFloat() / face.unitsPerEm
}
