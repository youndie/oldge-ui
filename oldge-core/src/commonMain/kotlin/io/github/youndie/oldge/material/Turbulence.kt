package io.github.youndie.oldge.material

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * The alpha channel of the design system's noise tiles, generated the way Chrome generates them —
 * research §1.3, D8, measured in B-04.
 *
 * The design system draws its grain and bezel speckle as an SVG `feTurbulence type="fractalNoise"`
 * followed by an `feColorMatrix` that maps the noise's alpha to `slope * a + intercept`, used as a
 * mask. This is the SVG 1.1 specification's reference implementation of `feTurbulence` (seed 0,
 * `stitchTiles="stitch"`), plus the three things Chrome does that the specification does not say,
 * each found by measuring against a Chrome render rather than by reading Blink:
 *
 * 1. **Stitching is over the filter region, not the tile.** An SVG filter's default region is the
 *    element's box grown by 10 % on every side, so a 160 px tile stitches over 192 px — and the base
 *    frequency is adjusted to that width, 0.85 becoming 163/192. Chrome rounds the region down to
 *    whole pixels: the 128 px speckle stitches over 153, not 153.6.
 * 2. **The noise is sampled at `(x + 1, y + 1)`**, not at the pixel's corner or centre.
 * 3. **The noise is quantised to 8 bits before the colour matrix**, which matters where the matrix
 *    is steep: the speckle's slope of 9 turns one level of noise into nine of mask.
 *
 * With those, the 160 px grain matches Chrome 153 within ±2 per pixel everywhere and the 128 px
 * speckle matches exactly (`TurbulenceTileTest`).
 *
 * @return [size]² alpha values, row-major, 0…255.
 */
internal fun turbulenceAlpha(
    size: Int,
    baseFrequency: Double,
    octaves: Int,
    slope: Double,
    intercept: Double,
): IntArray {
    val region = floor(size * FILTER_REGION_SCALE)
    val out = IntArray(size * size)
    for (y in 0 until size) {
        for (x in 0 until size) {
            val noise = turbulence(x + SAMPLE_OFFSET, y + SAMPLE_OFFSET, baseFrequency, octaves, region)
            val alpha8 = (((noise + 1) / 2).coerceIn(0.0, 1.0) * 255).roundToInt() / 255.0
            out[y * size + x] = ((slope * alpha8 + intercept).coerceIn(0.0, 1.0) * 255).roundToInt()
        }
    }
    return out
}

/** The default SVG filter region: the box plus 10 % on each side. */
private const val FILTER_REGION_SCALE = 1.2

/** Measured against Chrome in B-04; see [turbulenceAlpha]. */
private const val SAMPLE_OFFSET = 1.0

private const val BLOCK_SIZE = 0x100
private const val BLOCK_MASK = 0xff
private const val PERLIN_N = 0x1000

/** The alpha channel's index in the specification's four-channel gradient table. */
private const val ALPHA_CHANNEL = 3

private fun turbulence(
    px: Double,
    py: Double,
    baseFrequency: Double,
    octaves: Int,
    tile: Double,
): Double {
    val frequency = stitchedFrequency(baseFrequency, tile)
    var width = (tile * frequency + 0.5).toInt()
    var wrap = PERLIN_N + width
    var sum = 0.0
    var vx = px * frequency
    var vy = py * frequency
    var ratio = 1.0
    repeat(octaves) {
        sum += noise2(vx, vy, width, wrap) / ratio
        vx *= 2
        vy *= 2
        ratio *= 2
        width *= 2
        wrap = 2 * wrap - PERLIN_N
    }
    return sum
}

/** The frequency nearest to [base] that fits a whole number of lattice cells in [tile]. */
private fun stitchedFrequency(
    base: Double,
    tile: Double,
): Double {
    val low = floor(tile * base) / tile
    val high = ceil(tile * base) / tile
    return if (base / low < high / base) low else high
}

/** The specification's `noise2` for one channel, with stitching; the tile is square here. */
private fun noise2(
    vx: Double,
    vy: Double,
    width: Int,
    wrap: Int,
): Double {
    val tx = vx + PERLIN_N
    val ty = vy + PERLIN_N
    // Stitching compares the lattice index before it is masked, as the specification does.
    val bx0 = tx.toInt().stitch(width, wrap) and BLOCK_MASK
    val bx1 = (tx.toInt() + 1).stitch(width, wrap) and BLOCK_MASK
    val by0 = ty.toInt().stitch(width, wrap) and BLOCK_MASK
    val by1 = (ty.toInt() + 1).stitch(width, wrap) and BLOCK_MASK
    val rx0 = tx - tx.toInt()
    val ry0 = ty - ty.toInt()
    val rx1 = rx0 - 1
    val ry1 = ry0 - 1
    val i = Lattice.selector[bx0]
    val j = Lattice.selector[bx1]
    val b00 = Lattice.selector[i + by0]
    val b10 = Lattice.selector[j + by0]
    val b01 = Lattice.selector[i + by1]
    val b11 = Lattice.selector[j + by1]
    val sx = rx0 * rx0 * (3 - 2 * rx0)
    val sy = ry0 * ry0 * (3 - 2 * ry0)
    val g = Lattice.gradient
    val a = lerp(sx, rx0 * g[2 * b00] + ry0 * g[2 * b00 + 1], rx1 * g[2 * b10] + ry0 * g[2 * b10 + 1])
    val b = lerp(sx, rx0 * g[2 * b01] + ry1 * g[2 * b01 + 1], rx1 * g[2 * b11] + ry1 * g[2 * b11 + 1])
    return lerp(sy, a, b)
}

private fun Int.stitch(
    width: Int,
    wrap: Int,
): Int = if (this >= wrap) this - width else this

private fun lerp(
    t: Double,
    a: Double,
    b: Double,
): Double = a + t * (b - a)

/** The specification's `init(0)`: the lattice permutation and the alpha channel's gradients. */
private object Lattice {
    val selector = IntArray(BLOCK_SIZE + BLOCK_SIZE + 2)

    /** The alpha channel's gradients, as `[x0, y0, x1, y1, ...]`. */
    val gradient = DoubleArray(2 * (BLOCK_SIZE + BLOCK_SIZE + 2))

    init {
        val all = Array(4) { DoubleArray(2 * BLOCK_SIZE) }
        var seed = 1L // setup_seed(0)
        for (k in 0 until 4) {
            for (i in 0 until BLOCK_SIZE) {
                selector[i] = i
                for (j in 0 until 2) {
                    seed = random(seed)
                    all[k][2 * i + j] = ((seed % (BLOCK_SIZE + BLOCK_SIZE)) - BLOCK_SIZE).toDouble() / BLOCK_SIZE
                }
                val s = sqrt(all[k][2 * i] * all[k][2 * i] + all[k][2 * i + 1] * all[k][2 * i + 1])
                all[k][2 * i] /= s
                all[k][2 * i + 1] /= s
            }
        }
        for (i in BLOCK_SIZE - 1 downTo 1) {
            val k = selector[i]
            seed = random(seed)
            val j = (seed % BLOCK_SIZE).toInt()
            selector[i] = selector[j]
            selector[j] = k
        }
        for (i in 0 until BLOCK_SIZE + 2) {
            selector[BLOCK_SIZE + i] = selector[i]
        }
        for (i in 0 until BLOCK_SIZE + BLOCK_SIZE + 2) {
            val src = i % BLOCK_SIZE
            gradient[2 * i] = all[ALPHA_CHANNEL][2 * src]
            gradient[2 * i + 1] = all[ALPHA_CHANNEL][2 * src + 1]
        }
    }

    /** Park and Miller's minimal standard generator, as the specification writes it. */
    private fun random(seed: Long): Long {
        var result = RAND_A * (seed % RAND_Q) - RAND_R * (seed / RAND_Q)
        if (result <= 0) result += RAND_M
        return result
    }

    private const val RAND_M = 2147483647L
    private const val RAND_A = 16807L
    private const val RAND_Q = 127773L
    private const val RAND_R = 2836L
}
