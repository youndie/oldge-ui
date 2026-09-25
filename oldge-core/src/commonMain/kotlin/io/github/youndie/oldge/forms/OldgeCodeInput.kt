package io.github.youndie.oldge.forms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.lcdGlow
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.tokens.OldgeShadow
import io.github.youndie.oldge.type.FontCoverage
import io.github.youndie.oldge.type.OldgeScriptText

/**
 * A one-time code on LCD cells — the design system's `CodeInput`: each digit glows over the unlit
 * «8» of its cell, and the active cell carries a blinking cursor, under the [label] and above a
 * [help] line or an [error].
 *
 * Its README's rules, and where they are kept:
 * - under the cells is **one real text field** — numeric, and a one-time code for autofill — so
 *   paste, the SMS autofill and a screen reader treat it as an ordinary field; the cells only draw
 *   what it holds;
 * - six digits are split by a dash into two threes, which is easier to compare;
 * - an error is the cells' `danger` border plus the icon and word below.
 *
 * Anything but digits is dropped, and input stops at [length]; [onComplete] is called when the last
 * digit arrives.
 */
@Composable
public fun OldgeCodeInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = DEFAULT_LENGTH,
    help: String? = null,
    error: String? = null,
    onComplete: (String) -> Unit = {},
    interactionSource: MutableInteractionSource? = null,
) {
    require(length >= 1) { "length must be at least 1, got $length" }
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val code = value.filter { it.isDigit() }.take(length)
    Column(modifier, verticalArrangement = Arrangement.spacedBy(FIELD_GAP)) {
        FieldLabel(label)
        Box {
            Cells(code, length, focused, error != null)
            BasicTextField(
                value = code,
                onValueChange = { raw ->
                    val next = raw.filter { it.isDigit() }.take(length)
                    onValueChange(next)
                    if (next.length == length && next != code) onComplete(next)
                },
                modifier =
                    Modifier
                        .matchParentSize()
                        .graphicsLayer { alpha = 0f }
                        .semantics {
                            contentDescription = label
                            contentType = ContentType.SmsOtpCode
                            if (error != null) error(error)
                        },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                interactionSource = source,
                cursorBrush = SolidColor(Color.Transparent),
            )
        }
        FieldHelp(help, error)
    }
}

/**
 * `.og-code__cells`: the cells 6 px apart, each `flex: 1` up to 46 px of content, with a dash after
 * the third of six. A cell is a `span`, content-box, so its box is 48 × 58 with the border.
 */
@Composable
private fun Cells(
    code: String,
    length: Int,
    focused: Boolean,
    invalid: Boolean,
) {
    val c = OldgeTheme.colors
    val type = OldgeTheme.type
    // `font: 400 2rem/1 var(--font-lcd)`: the lcd face, 2rem on a line of the same height.
    val digitStyle = type.readout.copy(fontSize = DIGIT, lineHeight = DIGIT, letterSpacing = 0.sp)
    val dash = length == DEFAULT_LENGTH
    Layout(
        content = {
            for (i in 0 until length) {
                val active = focused && (i == code.length || (i == length - 1 && code.length == length))
                Cell(code.getOrNull(i), active, invalid, digitStyle)
            }
            if (dash) {
                OldgeScriptText(
                    "–",
                    digitStyle.copy(fontSize = DASH, lineHeight = DASH, color = c.inkMuted),
                    type.families.lcdCompanion,
                    FontCoverage.shareTechMono,
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
    ) { measurables, constraints ->
        val gap = GAP.roundToPx()
        val dashPlaceable = if (dash) measurables.last().measure(Constraints()) else null
        val items = length + if (dash) 1 else 0
        val free = constraints.maxWidth - gap * (items - 1) - (dashPlaceable?.width ?: 0)
        val cell = minOf(CELL.roundToPx(), free / length).coerceAtLeast(0)
        val cells = measurables.take(length).map { it.measure(Constraints.fixedWidth(cell)) }
        val height = cells.maxOf { it.height }
        layout(constraints.maxWidth, height) {
            var x = 0
            cells.forEachIndexed { i, p ->
                p.place(x, 0)
                x += p.width + gap
                if (dashPlaceable != null && i == DASH_AFTER) {
                    dashPlaceable.place(x, (height - dashPlaceable.height) / 2)
                    x += dashPlaceable.width + gap
                }
            }
        }
    }
}

/**
 * `.og-code__cell`: `lcd` glass, `edge` border (`danger` on error, `focus` and a 2 px focus ring when
 * active), sunken; the ghost «8» in `lcd-ghost` under the digit in `lcd-ink` with the LCD glow; the
 * active cell's cursor a 3 px bar 9 px from the bottom, 30 % in from each side, blinking once a
 * second.
 */
@Composable
private fun Cell(
    digit: Char?,
    active: Boolean,
    invalid: Boolean,
    style: TextStyle,
) {
    val c = OldgeTheme.colors
    val type = OldgeTheme.type
    val motion = OldgeTheme.motion
    val border =
        when {
            active -> c.focus
            invalid -> c.danger
            else -> c.edge
        }
    val ring = if (active) listOf(OldgeShadow(false, 0.dp, 0.dp, 0.dp, RING, c.focus)) else emptyList()
    val blink = remember { Animatable(1f) }
    LaunchedEffect(active, motion.reduced) {
        if (active && !motion.reduced) {
            blink.animateTo(0f, infiniteRepeatable(tween(BLINK_MS, easing = LinearEasing), RepeatMode.Restart))
        } else {
            blink.snapTo(1f)
        }
    }
    Box(
        Modifier
            // A `span`: content-box, so its 56 px minimum is inside the 1 px border.
            .defaultMinSize(minHeight = CELL_HEIGHT + BORDER * 2)
            .cssBox(OldgeRadii.xs, CssBackground.Solid(c.lcd), BORDER, border, OldgeTheme.shadows.sunken, ring)
            .drawWithContent {
                drawContent()
                // `og-blink 1s steps(1)`: at .2 for the first half of each second, then full. Under
                // reduced motion the one 1 ms run ends and the cursor stays lit.
                if (active) {
                    val bar = CURSOR.toPx()
                    val w = size.width * (1 - 2 * CURSOR_SIDE)
                    val dim = !motion.reduced && blink.value > 0.5f
                    drawRect(
                        c.lcdInk,
                        Offset(size.width * CURSOR_SIDE, size.height - CURSOR_BOTTOM.toPx() - bar),
                        Size(w, bar),
                        alpha = if (dim) BLINK_LOW else 1f,
                    )
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        OldgeScriptText("8", style.copy(color = c.lcdGhost), type.families.lcdCompanion, FontCoverage.shareTechMono)
        if (digit != null) {
            OldgeScriptText(
                digit.toString(),
                style.copy(color = c.lcdInk).lcdGlow(),
                type.families.lcdCompanion,
                FontCoverage.shareTechMono,
            )
        }
    }
}

private const val DEFAULT_LENGTH = 6
private const val DASH_AFTER = 2
private const val BLINK_MS = 1000
private const val CURSOR_SIDE = 0.3f
private const val BLINK_LOW = 0.2f
private val BORDER = 1.dp
private val GAP = 6.dp // css literal: bundle.css `.og-code__cells { gap: 6px }`
private val CELL = 48.dp // css literal: bundle.css `.og-code__cell { max-width: 46px }`, plus its 1 px border each side
private val CELL_HEIGHT = 56.dp // css literal: bundle.css `.og-code__cell { min-height: 56px }`
private val DIGIT = 32.sp // css literal: bundle.css `.og-code__cell { font: 400 2rem/1 }`
private val DASH = 20.sp // css literal: bundle.css `.og-code__dash { font: 400 1.25rem/1 }`
private val RING = 2.dp // css literal: bundle.css `.og-code__cell--active { box-shadow: …, 0 0 0 2px var(--focus) }`
private val CURSOR = 3.dp // css literal: bundle.css `.og-code__cell--active::after { height: 3px }`
private val CURSOR_BOTTOM = 9.dp // css literal: bundle.css `.og-code__cell--active::after { bottom: 9px }`
