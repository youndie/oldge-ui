package io.github.youndie.oldge.feedback

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.actions.OldgeOrbButton
import io.github.youndie.oldge.actions.OldgeOrbSize
import io.github.youndie.oldge.actions.OldgeOrbTone
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.bezel
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.press.drawOldgeFocusRing
import io.github.youndie.oldge.press.oldgePopSoftIn
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.type.OldgeText

/**
 * The message field — the design system's `Composer`: in the skin's frame, the attach orb
 * ([onAttach]; null hides it), a field that grows, and the send orb, which calls [onSend] with the
 * text and empties the field. [value] with [onValueChange] makes the text the caller's; without it
 * the composer keeps its own.
 *
 * Its README's rules:
 * - the field grows to 120 px and scrolls past it;
 * - Enter sends, Shift+Enter breaks the line;
 * - the send orb is off on an empty field, and pops out lime once there is text.
 */
@Composable
public fun OldgeComposer(
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier,
    value: String? = null,
    onValueChange: (String) -> Unit = {},
    placeholder: String = "Сообщение",
    label: String = "Сообщение",
    onAttach: (() -> Unit)? = {},
) {
    val c = OldgeTheme.colors
    val type = OldgeTheme.type
    var own by remember { mutableStateOf("") }
    val text = value ?: own

    fun set(v: String) {
        if (value == null) own = v
        onValueChange(v)
    }

    fun send() {
        if (text.isBlank()) return
        onSend(text)
        set("")
    }
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val focus = c.focus
    val box = RoundedCornerShape(BOX_RADIUS)
    // `.og-composer__input`: 1rem / 1.25rem in `ink`, padded 9 0.
    val style = type.body.copy(fontSize = INPUT_SIZE, color = c.ink)
    Row(
        modifier
            .fillMaxWidth()
            .bezel(0.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
            .padding(horizontal = EDGE, vertical = BAR_TOP),
        horizontalArrangement =
            androidx.compose.foundation.layout.Arrangement
                .spacedBy(EDGE),
        verticalAlignment = Alignment.Bottom,
    ) {
        if (onAttach != null) {
            OldgeOrbButton(
                OldgeIcons.Attach,
                "Прикрепить",
                onAttach,
                size = OldgeOrbSize.Small,
                tone = OldgeOrbTone.Chrome,
            )
        }
        // `.og-composer__box`: a 20 px round `well` in a 1 px `edge` frame, sunken, 40 px at least,
        // 2 px clear above and below, padded 0 12; `:focus-within` draws the focus ring 2 px out.
        Box(
            Modifier
                .weight(1f)
                .padding(vertical = BOX_MARGIN)
                .drawWithContent {
                    drawContent()
                    if (focused) drawOldgeFocusRing(box, focus)
                }.cssBox(BOX_RADIUS, CssBackground.Solid(c.well), BORDER, c.edge, OldgeTheme.shadows.sunken)
                .defaultMinSize(minHeight = BOX_HEIGHT)
                .padding(horizontal = BORDER + OldgeTheme.spacing.space3, vertical = BORDER),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = text,
                onValueChange = { set(it) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .onPreviewKeyEvent { e ->
                            if (e.key == Key.Enter && !e.isShiftPressed) {
                                if (e.type == KeyEventType.KeyDown) send()
                                true
                            } else {
                                false
                            }
                        }.semantics { contentDescription = label },
                textStyle = style,
                interactionSource = source,
                cursorBrush = SolidColor(c.ink),
                decorationBox = { inner ->
                    Box(Modifier.padding(vertical = INPUT_PADDING)) {
                        if (text.isEmpty()) OldgeText(placeholder, style = style.copy(color = c.inkMuted), maxLines = 1)
                        // At least one line tall, as the textarea's content box is (B-17's 19 px line), and
                        // at most 120 px, past which it scrolls.
                        val line = with(LocalDensity.current) { style.lineHeight.toDp() }
                        Box(Modifier.defaultMinSize(minHeight = line).heightIn(max = MOST)) { inner() }
                    }
                },
            )
        }
        if (text.isBlank()) {
            key(
                "idle",
            ) { OldgeOrbButton(OldgeIcons.Send, "Отправить", {}, tone = OldgeOrbTone.Chrome, enabled = false) }
        } else {
            // `.og-composer__send { animation: og-pop-soft }`: a new orb, so it pops as the text appears.
            key("send") {
                Box(Modifier.oldgePopSoftIn()) {
                    OldgeOrbButton(OldgeIcons.Send, "Отправить", { send() }, tone = OldgeOrbTone.Accent)
                }
            }
        }
    }
}

private val BORDER = 1.dp
private val EDGE = 4.dp // css literal: bundle.css `.og-composer { gap: 4px; padding: 6px 4px }`
private val BAR_TOP = 6.dp // css literal: bundle.css `.og-composer { padding: 6px 4px calc(6px + …) }`
private val BOX_RADIUS = 20.dp // css literal: bundle.css `.og-composer__box { border-radius: 20px }`
private val BOX_HEIGHT = 40.dp // css literal: bundle.css `.og-composer__box { min-height: 40px }`
private val BOX_MARGIN = 2.dp // css literal: bundle.css `.og-composer__box { margin: 2px 0 }`
private val INPUT_PADDING = 9.dp // css literal: bundle.css `.og-composer__input { padding: 9px 0 }`
private val INPUT_SIZE = 16.sp // css literal: bundle.css `.og-composer__input { font: 400 1rem/1.25rem }`
private val MOST = 120.dp // css literal: bundle.css `.og-composer__input { max-height: 120px }`
