package io.github.youndie.oldge.forms

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.OldgeOrbButton
import io.github.youndie.oldge.actions.OldgeOrbSize
import io.github.youndie.oldge.actions.OldgeOrbTone
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.press.drawOldgeFocusRing
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.OldgeText

/**
 * A text field — the design system's `TextField`: the [label] above, a sunken well (white in Crystal,
 * black in the dark skins), and under it a [help] line or an [error].
 *
 * Its README's rules, and where they are kept:
 * - an error is the `danger` border and text **plus an icon** — never colour alone; it also marks the
 *   field invalid for a screen reader, with the message;
 * - the [placeholder] is an example of an answer, not a label;
 * - [reveal] makes it a password field with a round show/hide button inside, which says which it
 *   will do («Показать пароль» / «Скрыть пароль»);
 * - [lines] above 1 is the multiline field (`rows`);
 * - the height is a minimum, never a fixed one: at a large system font the text grows the field and
 *   nothing is cut.
 *
 * Focused, the well carries the focus ring whatever the input mode, as `:focus-within` does for an
 * input. Disabled, the whole field is drawn at 75 % and does not take input.
 */
@Composable
public fun OldgeTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    help: String? = null,
    error: String? = null,
    icon: ImageVector? = null,
    reveal: Boolean = false,
    lines: Int = 1,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
) {
    require(lines >= 1) { "lines must be at least 1, got $lines" }
    val c = OldgeTheme.colors
    val type = OldgeTheme.type
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    var shown by remember { mutableStateOf(false) }
    val multiline = lines > 1
    val shape = RoundedCornerShape(OldgeRadii.xs)
    val textStyle = type.body.copy(color = c.ink)
    Column(
        modifier.graphicsLayer { alpha = if (enabled) 1f else DISABLED_ALPHA },
        verticalArrangement = Arrangement.spacedBy(GAP),
    ) {
        OldgeText(label, style = type.label.copy(color = c.ink))
        Row(
            Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = OldgeTheme.spacing.hitMin)
                .cssBox(
                    OldgeRadii.xs,
                    CssBackground.Solid(c.well),
                    BORDER,
                    if (error != null) c.danger else c.edge,
                    OldgeTheme.shadows.sunken,
                ).drawWithContent {
                    drawContent()
                    if (focused) drawOldgeFocusRing(shape, c.focus)
                }.padding(
                    start = BORDER + OldgeTheme.spacing.space3,
                    end =
                        BORDER + if (reveal) REVEAL_END else OldgeTheme.spacing.space3,
                ),
            horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
            verticalAlignment = if (multiline) Alignment.Top else Alignment.CenterVertically,
        ) {
            if (icon != null) {
                // The box's colour is `ink-muted`, which the icon inherits; in a multiline field the
                // box aligns to `flex-start`, so the icon sits at its top.
                OldgeIcon(icon, contentDescription = null, size = ICON, tint = c.inkMuted)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier =
                    Modifier
                        .weight(1f)
                        .semantics {
                            contentDescription = label
                            if (error != null) error(error)
                        },
                enabled = enabled,
                textStyle = textStyle,
                keyboardOptions =
                    if (reveal && keyboardOptions == KeyboardOptions.Default) {
                        KeyboardOptions(keyboardType = KeyboardType.Password)
                    } else {
                        keyboardOptions
                    },
                keyboardActions = keyboardActions,
                singleLine = !multiline,
                minLines = lines,
                maxLines = lines,
                visualTransformation =
                    if (reveal &&
                        !shown
                    ) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                interactionSource = source,
                cursorBrush = SolidColor(c.ink),
                decorationBox = { inner ->
                    // `.og-field__input { padding: 11px 0; min-height: 42px }`.
                    Box(Modifier.padding(vertical = INPUT_PADDING)) {
                        if (value.isEmpty() && placeholder != null) {
                            OldgeText(placeholder, style = type.body.copy(color = c.inkMuted), maxLines = lines)
                        }
                        // The line is at least `line-height` tall, as the input's content box is:
                        // Compose's single-line field measured 19 px for a 20 px line, so the row
                        // centred a 41 px field in 44 and rounded the text a pixel low (B-17, «Павел»).
                        val line = with(LocalDensity.current) { textStyle.lineHeight.toDp() }
                        Box(Modifier.defaultMinSize(minHeight = line * lines)) { inner() }
                    }
                },
            )
            if (reveal) {
                OldgeOrbButton(
                    if (shown) OldgeIcons.EyeOff else OldgeIcons.Eye,
                    if (shown) "Скрыть пароль" else "Показать пароль",
                    { shown = !shown },
                    size = OldgeOrbSize.Small,
                    tone = OldgeOrbTone.Chrome,
                    enabled = enabled,
                )
            }
        }
        val line = error ?: help
        if (line != null) {
            val tone = if (error != null) c.danger else c.inkMuted
            Row(
                horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space1),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (error != null) OldgeIcon(OldgeIcons.Error, contentDescription = null, size = HELP_ICON, tint = tone)
                OldgeText(
                    line,
                    style =
                        type.caption.copy(
                            color = tone,
                            fontWeight =
                                if (error !=
                                    null
                                ) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                },
                        ),
                )
            }
        }
    }
}

private const val DISABLED_ALPHA = 0.75f
private val BORDER = 1.dp
private val GAP = 6.dp // css literal: bundle.css `.og-field { gap: 6px }`
private val ICON = 20.dp // css literal: bundle.js `TextField`, `Icon size: 20`
private val HELP_ICON = 14.dp // css literal: bundle.js `TextField`, error `Icon size: 14`
private val INPUT_PADDING = 11.dp // css literal: bundle.css `.og-field__input { padding: 11px 0 }`
private val REVEAL_END = 2.dp // css literal: bundle.css `.og-field__reveal { margin-right: -10px }` off `space-3`
