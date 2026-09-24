package io.github.youndie.oldge.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.tokens.OldgeFontFamilyKey
import io.github.youndie.oldge.tokens.OldgeTypeStyle
import io.github.youndie.oldge.tokens.OldgeTypeStyles
import io.github.youndie.oldge.type.OldgeFontFamilies
import io.github.youndie.oldge.type.oldgeFontFamilies

/**
 * The design system's eleven type styles as Compose text styles.
 *
 * Sizes are in sp at `1rem = 16.sp` (research D5), so they follow the system font setting the way
 * the design's rem do — the EdgeScale stress screen is the test of it. Every style sets its line
 * height, and centres the text in it without trimming, which is how a browser lays out a CSS
 * `line-height`; the parity fixtures of B-08 are where that choice is measured.
 *
 * `lcd` and `pixel` styles carry the design's own face; their Cyrillic goes through the companion
 * face (research D6), which the components apply with the families held here.
 */
@Immutable
public class OldgeTypography internal constructor(
    public val display: TextStyle,
    public val heading: TextStyle,
    public val title: TextStyle,
    public val body: TextStyle,
    public val bodyStrong: TextStyle,
    public val button: TextStyle,
    public val label: TextStyle,
    public val caption: TextStyle,
    public val readout: TextStyle,
    public val readoutSm: TextStyle,
    public val pixelTag: TextStyle,
    internal val families: OldgeFontFamilies,
)

/** The bundled faces, resolved; `@Composable` because compose-resources loads a font in a composition. */
@Composable
internal fun oldgeTypography(): OldgeTypography {
    val families = oldgeFontFamilies()

    fun style(s: OldgeTypeStyle) = s.toTextStyle(families.of(s.family))
    return OldgeTypography(
        display = style(OldgeTypeStyles.display),
        heading = style(OldgeTypeStyles.heading),
        title = style(OldgeTypeStyles.title),
        body = style(OldgeTypeStyles.body),
        bodyStrong = style(OldgeTypeStyles.bodyStrong),
        button = style(OldgeTypeStyles.button),
        label = style(OldgeTypeStyles.label),
        caption = style(OldgeTypeStyles.caption),
        readout = style(OldgeTypeStyles.readout),
        readoutSm = style(OldgeTypeStyles.readoutSm),
        pixelTag = style(OldgeTypeStyles.pixelTag),
        families = families,
    )
}

/** CSS px per rem: the browser default the design system's rem are written against. */
private const val PX_PER_REM = 16f

internal fun OldgeTypeStyle.toTextStyle(fontFamily: FontFamily): TextStyle =
    TextStyle(
        fontFamily = fontFamily,
        fontSize = (sizeRem * PX_PER_REM).sp,
        lineHeight = (lineHeightRem * PX_PER_REM).sp,
        fontWeight = FontWeight(weight),
        letterSpacing = letterSpacingEm.em,
        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
    )

private fun OldgeFontFamilies.of(key: OldgeFontFamilyKey): FontFamily =
    when (key) {
        OldgeFontFamilyKey.Ui -> ui
        OldgeFontFamilyKey.Title -> title
        OldgeFontFamilyKey.Lcd -> lcd
        OldgeFontFamilyKey.Pixel -> pixel
    }
