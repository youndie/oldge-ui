package io.github.youndie.oldge.behaviour

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.tokens.OldgeColors
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The design system's own contrast claims (B-43), computed from the generated tokens per skin. Each
 * pair is one its README or `tokens.json` states: `ink` and `ink-muted` hold 4.5:1 on the body and
 * its panels, `edge` 3:1 to them, and so on. A pair that falls short is not re-coloured here; the
 * design system's checklist says it is kept and reported, and [KNOWN] is where it is reported. The
 * set is compared whole, so a new shortfall fails, and so does a known one that has been fixed.
 */
class ContrastTest {
    private class Claim(
        val fore: String,
        val back: String,
        val least: Double,
        val pick: (OldgeColors) -> Pair<Color, Color>,
    )

    private val claims =
        buildList {
            val bodies =
                listOf<Pair<String, (OldgeColors) -> Color>>(
                    "body-hi" to { it.bodyHi },
                    "ground" to { it.ground },
                    "surface" to { it.surface },
                    "surface-2" to { it.surface2 },
                    "well" to { it.well },
                )
            // README «Цвет»: «Обе пары держат ≥4.5:1 на body-hi, ground, surface, surface-2, well».
            for ((b, back) in bodies) {
                add(Claim("ink", b, TEXT) { it.ink to back(it) })
                add(Claim("ink-muted", b, TEXT) { it.inkMuted to back(it) })
                // tokens.json `edge`: «≥3:1 к ground, body-hi, surface, surface-2, well».
                add(Claim("edge", b, NON_TEXT) { it.edge to back(it) })
            }
            // `focus` and `accent-rim`: «≥3:1 на всех фонах тела» — the body's two ends.
            for ((b, back) in bodies.take(2)) {
                add(Claim("focus", b, NON_TEXT) { it.focus to back(it) })
                add(Claim("accent-rim", b, NON_TEXT) { it.accentRim to back(it) })
            }
            // `on-bezel`: «≥4.5:1 на всех трёх остановках».
            add(Claim("on-bezel", "bezel-hi", TEXT) { it.onBezel to it.bezelHi })
            add(Claim("on-bezel", "bezel", TEXT) { it.onBezel to it.bezel })
            add(Claim("on-bezel", "bezel-lo", TEXT) { it.onBezel to it.bezelLo })
            // `on-pill`: «≥4.5:1 на обеих остановках».
            add(Claim("on-pill", "pill-hi", TEXT) { it.onPill to it.pillHi })
            add(Claim("on-pill", "pill-lo", TEXT) { it.onPill to it.pillLo })
            // `on-accent`: «≥4.5:1 на всех остановках».
            add(Claim("on-accent", "accent-hi", TEXT) { it.onAccent to it.accentHi })
            add(Claim("on-accent", "accent", TEXT) { it.onAccent to it.accent })
            add(Claim("on-accent", "accent-lo", TEXT) { it.onAccent to it.accentLo })
            // `lcd-dim`: «Подписи и единицы на lcd — ≥4.5:1»; `meter-low`: «≥3:1 на lcd».
            add(Claim("lcd-dim", "lcd", TEXT) { it.lcdDim to it.lcd })
            add(Claim("meter-low", "lcd", NON_TEXT) { it.meterLow to it.lcd })
            // `balloon-mark`: «Иконка в Balloon (≥3:1 на balloon)».
            add(Claim("balloon-mark", "balloon", NON_TEXT) { it.balloonMark to it.balloon })
        }

    @Test
    fun the_design_systems_contrast_claims_hold_but_for_the_known_shortfalls() {
        val measured =
            OldgeSkin.entries.flatMap { skin ->
                claims.map { c ->
                    val (fore, back) = c.pick(skin.colors)
                    Triple("${skin.name}: ${c.fore} on ${c.back}", ratio(fore, back), c.least)
                }
            }
        val short = measured.filter { (_, ratio, least) -> ratio + EPSILON < least }
        assertEquals(
            KNOWN,
            short.map { it.first }.toSet(),
            short.joinToString("\n") { (pair, ratio, least) -> "$pair ${"%.2f".format(ratio)}:1, claimed $least:1" },
        )
        // The claims were all read: three skins of every pair above.
        assertEquals(OldgeSkin.entries.size * claims.size, measured.size)
    }

    /** WCAG 2's contrast ratio, the foreground composited over the background first when translucent. */
    private fun ratio(
        fore: Color,
        back: Color,
    ): Double {
        val a = fore.compositeOver(back).luminance().toDouble()
        val b = back.luminance().toDouble()
        return (maxOf(a, b) + OFFSET) / (minOf(a, b) + OFFSET)
    }
}

/** Pairs that fall short of the design system's own claim, each reported in an item. */
private val KNOWN = emptySet<String>()

private const val TEXT = 4.5
private const val NON_TEXT = 3.0
private const val OFFSET = 0.05
private const val EPSILON = 1e-9
