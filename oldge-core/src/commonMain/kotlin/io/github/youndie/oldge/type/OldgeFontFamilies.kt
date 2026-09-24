package io.github.youndie.oldge.type

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import io.github.youndie.oldge.resources.Res
import io.github.youndie.oldge.resources.dejavu_sans_condensed
import io.github.youndie.oldge.resources.dejavu_sans_condensed_bold
import io.github.youndie.oldge.resources.fira_sans_bold
import io.github.youndie.oldge.resources.pt_mono
import io.github.youndie.oldge.resources.share_tech_mono
import io.github.youndie.oldge.resources.silkscreen
import io.github.youndie.oldge.resources.tiny5
import org.jetbrains.compose.resources.Font

/**
 * The design system's four font families as bundled faces — research D6, a deviation from the
 * brief it names.
 *
 * | Family | The design says | Bundled |
 * |---|---|---|
 * | [ui] | Tahoma → Verdana → Segoe UI → DejaVu Sans | DejaVu Sans Condensed 400, 700 |
 * | [title] | Trebuchet MS → Tahoma → Segoe UI | Fira Sans 700 |
 * | [lcd] | Share Tech Mono → Lucida Console → ui-monospace | Share Tech Mono, [lcdCompanion] PT Mono |
 * | [pixel] | Silkscreen → Tahoma | Silkscreen, [pixelCompanion] Tiny5 |
 *
 * Tahoma, Verdana and Trebuchet MS may not be redistributed. Share Tech Mono and Silkscreen may,
 * but have no Cyrillic at all, so their text goes through [oldgeTextOf] with the companion; a
 * companion is never a second entry in the same [FontFamily], because Compose picks among weight
 * variants of one family and does not step to the next font for a missing glyph (research §1.6).
 */
@Immutable
internal class OldgeFontFamilies(
    val ui: FontFamily,
    val title: FontFamily,
    val lcd: FontFamily,
    val lcdCompanion: FontFamily,
    val pixel: FontFamily,
    val pixelCompanion: FontFamily,
)

/**
 * Loads the bundled faces. `@Composable` because compose-resources reads a font asynchronously and
 * needs a composition to hang the load on, on every target alike.
 */
@Composable
internal fun oldgeFontFamilies(): OldgeFontFamilies =
    OldgeFontFamilies(
        ui =
            FontFamily(
                Font(Res.font.dejavu_sans_condensed, FontWeight.Normal),
                Font(Res.font.dejavu_sans_condensed_bold, FontWeight.Bold),
            ),
        title = FontFamily(Font(Res.font.fira_sans_bold, FontWeight.Bold)),
        lcd = FontFamily(Font(Res.font.share_tech_mono, FontWeight.Normal)),
        lcdCompanion = FontFamily(Font(Res.font.pt_mono, FontWeight.Normal)),
        pixel = FontFamily(Font(Res.font.silkscreen, FontWeight.Normal)),
        pixelCompanion = FontFamily(Font(Res.font.tiny5, FontWeight.Normal)),
    )
