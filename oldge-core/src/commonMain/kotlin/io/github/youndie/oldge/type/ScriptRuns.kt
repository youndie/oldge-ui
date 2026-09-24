package io.github.youndie.oldge.type

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle

/**
 * [text] with every run the design's own face cannot draw set in [companion] (research D6).
 *
 * The rule is **coverage, read from the face's cmap** ([FontCoverage]), not "is it Cyrillic":
 * kvadrant-ui split by script and a password-mask character that neither of its faces carried was
 * then drawn by the host. A run is a maximal sequence of code points with the same answer, so a
 * mixed readout such as «23,4 ГБ из 64» becomes five runs and every digit stays in the design's
 * face. The surrounding style's family is untouched: only the companion runs carry a family.
 *
 * A code point the companion cannot draw either is still given to the companion — the choice has to
 * go somewhere, and `DesignStringCoverageTest` is what fails when the design uses one.
 */
internal fun oldgeTextOf(
    text: String,
    companion: FontFamily,
    primaryCoverage: IntArray,
): AnnotatedString =
    buildAnnotatedString {
        var start = 0
        while (start < text.length) {
            val needsCompanion = !primaryCoverage.covers(text.codePointAtCompat(start))
            var end = start
            while (end < text.length && !primaryCoverage.covers(text.codePointAtCompat(end)) == needsCompanion) {
                end += charCountAt(text, end)
            }
            val run = text.substring(start, end)
            if (needsCompanion) withStyle(SpanStyle(fontFamily = companion)) { append(run) } else append(run)
            start = end
        }
    }

/** Whether [codePoint] falls inside one of the inclusive `[first, last]` pairs, by binary search. */
internal fun IntArray.covers(codePoint: Int): Boolean {
    var low = 0
    var high = size / 2 - 1
    while (low <= high) {
        val mid = (low + high) ushr 1
        when {
            codePoint < this[2 * mid] -> high = mid - 1
            codePoint > this[2 * mid + 1] -> low = mid + 1
            else -> return true
        }
    }
    return false
}

private fun String.codePointAtCompat(index: Int): Int {
    val high = this[index]
    if (high.isHighSurrogate() && index + 1 < length) {
        val low = this[index + 1]
        if (low.isLowSurrogate()) return ((high.code - 0xD800) shl 10) + (low.code - 0xDC00) + 0x10000
    }
    return high.code
}

private fun charCountAt(
    text: String,
    index: Int,
): Int = if (text[index].isHighSurrogate() && index + 1 < text.length && text[index + 1].isLowSurrogate()) 2 else 1
