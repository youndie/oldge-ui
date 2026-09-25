package io.github.youndie.oldge.feedback

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.type.FontCoverage
import io.github.youndie.oldge.type.OldgeScriptText

// What the LCD family shares: the pixel tag over a readout or a meter, and text in the lcd face.

/** `.og-readout__tag`, `.og-meter__label`: `pixel-tag` in `lcd-dim`, upper case. */
@Composable
internal fun LcdTag(
    text: String,
    modifier: Modifier = Modifier,
) {
    val type = OldgeTheme.type
    OldgeScriptText(
        text.uppercase(),
        type.pixelTag.copy(color = OldgeTheme.colors.lcdDim),
        type.families.pixelCompanion,
        FontCoverage.silkscreen,
        modifier,
    )
}

/** Text in the lcd face, joined with its companion for the characters it lacks (research D6). */
@Composable
internal fun LcdText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    val type = OldgeTheme.type
    OldgeScriptText(
        text,
        style.copy(fontFamily = type.families.lcd),
        type.families.lcdCompanion,
        FontCoverage.shareTechMono,
        modifier,
    )
}
