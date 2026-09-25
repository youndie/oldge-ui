package io.github.youndie.oldge.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.type.OldgeText

// `.og-field`: the label, the control and the help line of TextField, Select and CodeInput.

/** `.og-field__label`: 0.8125rem / 1rem bold, `ink`. */
@Composable
internal fun FieldLabel(text: String) =
    OldgeText(text, style = OldgeTheme.type.label.copy(color = OldgeTheme.colors.ink))

/**
 * `.og-field__help`: 0.75rem / 1rem in `ink-muted`; an error is `danger`, bold, and led by the error
 * icon — colour, icon and word, never colour alone (the TextField and CodeInput READMEs).
 */
@Composable
internal fun FieldHelp(
    help: String?,
    error: String?,
) {
    val line = error ?: help ?: return
    val c = OldgeTheme.colors
    val tone = if (error != null) c.danger else c.inkMuted
    Row(
        horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (error != null) OldgeIcon(OldgeIcons.Error, contentDescription = null, size = HELP_ICON, tint = tone)
        val weight = if (error != null) FontWeight.Bold else FontWeight.Normal
        OldgeText(line, style = OldgeTheme.type.caption.copy(color = tone, fontWeight = weight))
    }
}

/** `.og-field { gap: 6px }`. */
internal val FIELD_GAP = 6.dp // css literal: bundle.css `.og-field { gap: 6px }`

private val HELP_ICON = 14.dp // css literal: bundle.js `TextField`, error `Icon size: 14`
