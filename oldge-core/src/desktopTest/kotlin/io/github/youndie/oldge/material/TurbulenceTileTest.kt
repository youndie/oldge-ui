package io.github.youndie.oldge.material

import java.io.File
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * B-04: the Kotlin port of `feTurbulence` against the tiles Chrome renders from the design system's
 * own SVGs (`scripts/research/noise-tiles.mjs`, committed beside the PNGs as `grain.svg` and
 * `speckle.svg`). The parameters are read out of those SVGs, so a changed design system changes the
 * test's input rather than leaving a copied number behind.
 */
class TurbulenceTileTest {
    private val dir = File("src/desktopTest/snapshots/design/texture")

    @Test
    fun the_grain_matches_chrome_within_two_levels() {
        val result = compare("grain")
        println("grain: $result")
        assertTrue(result.maxDeviation <= 2, "grain: $result")
    }

    @Test
    fun the_speckle_matches_chrome_within_two_levels() {
        val result = compare("speckle")
        println("speckle: $result")
        assertTrue(result.maxDeviation <= 2, "speckle: $result")
    }

    /** The control: a different noise is nowhere near, so the ±2 above is not a tolerance that admits anything. */
    @Test
    fun a_different_base_frequency_is_far_from_chrome() {
        val svg = Svg.parse(File(dir, "grain.svg").readText())
        val chrome = alpha("grain")
        val other = turbulenceAlpha(svg.size, svg.baseFrequency + 0.05, svg.octaves, svg.slope, svg.intercept)
        val mean = chrome.indices.sumOf { abs(chrome[it] - other[it]) }.toDouble() / chrome.size
        assertTrue(mean > 20, "a wrong frequency was only $mean levels away on average")
    }

    private fun compare(name: String): Comparison {
        val svg = Svg.parse(File(dir, "$name.svg").readText())
        val chrome = alpha(name)
        assertEquals(svg.size * svg.size, chrome.size, "$name: the Chrome tile is not ${svg.size} px square")
        val ours = turbulenceAlpha(svg.size, svg.baseFrequency, svg.octaves, svg.slope, svg.intercept)
        val deviations = chrome.indices.map { abs(chrome[it] - ours[it]) }
        return Comparison(
            maxDeviation = deviations.max(),
            beyondTwo = deviations.count { it > 2 },
            exact = deviations.count { it == 0 },
            pixels = deviations.size,
        )
    }

    private fun alpha(name: String): IntArray {
        val image = ImageIO.read(File(dir, "${name}_chrome.png"))
        return IntArray(image.width * image.height) { i -> image.getRGB(i % image.width, i / image.width) ushr 24 }
    }

    private data class Comparison(
        val maxDeviation: Int,
        val beyondTwo: Int,
        val exact: Int,
        val pixels: Int,
    )

    private data class Svg(
        val size: Int,
        val baseFrequency: Double,
        val octaves: Int,
        val slope: Double,
        val intercept: Double,
    ) {
        companion object {
            fun parse(svg: String): Svg {
                fun attr(name: String) = Regex("$name='([^']+)'").find(svg)!!.groupValues[1]
                val matrix = attr("values").trim().split(Regex("\\s+")).map(String::toDouble)
                return Svg(
                    size = attr("width").toInt(),
                    baseFrequency = attr("baseFrequency").toDouble(),
                    octaves = attr("numOctaves").toInt(),
                    // The alpha row of a 4×5 colour matrix: A' = m[18]·A + m[19].
                    slope = matrix[18],
                    intercept = matrix[19],
                )
            }
        }
    }
}
