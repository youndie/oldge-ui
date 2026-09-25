package io.github.youndie.oldge.forms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
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
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.press.drawOldgeFocusRing
import io.github.youndie.oldge.press.oldgePopSoftIn
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.tokens.OldgeShadow
import io.github.youndie.oldge.type.OldgeText

/**
 * A search bar — the design system's `SearchBar`, Material's search bar: a sunken capsule in a
 * chrome ring with the magnifier, the field, a round «Очистить» button once there is text, and the
 * caller's [trailing] element (usually an Avatar).
 *
 * [value] with [onValueChange] makes the text the caller's; without it the bar keeps its own,
 * starting from [defaultValue]. Enter calls [onSubmit] with the text. [label] names the field for a
 * screen reader, and is [placeholder] unless given.
 *
 * Its README's rule: the magnifier wiggles when the bar takes focus, and the clear button pops in
 * once there is text.
 */
@Composable
public fun OldgeSearchBar(
    modifier: Modifier = Modifier,
    value: String? = null,
    onValueChange: (String) -> Unit = {},
    defaultValue: String = "",
    placeholder: String = "Поиск",
    label: String? = null,
    onSubmit: (String) -> Unit = {},
    trailing: (@Composable () -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    var own by remember { mutableStateOf(defaultValue) }
    val text = value ?: own

    fun set(v: String) {
        if (value == null) own = v
        onValueChange(v)
    }
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    // `og-wiggle` over `dur-slow` on `ease-out` each time the bar takes focus.
    val wiggle = remember { Animatable(1f) }
    LaunchedEffect(focused) {
        if (focused && !motion.reduced) {
            wiggle.snapTo(0f)
            wiggle.animateTo(1f, tween(motion.slow, easing = LinearEasing))
        }
    }
    val out = motion.out
    val focus = c.focus
    val pill = RoundedCornerShape(OldgeRadii.pill)
    // `.og-search__input`: 1rem / 1.25rem in `ink`.
    val style = OldgeTheme.type.body.copy(fontSize = INPUT_SIZE, color = c.ink)
    Row(
        modifier
            .fillMaxWidth()
            // `:focus-within`: the focus ring 3 px out.
            .drawWithContent {
                drawContent()
                if (focused) drawOldgeFocusRing(pill, focus, FOCUS_OFFSET)
            }.cssBox(
                OldgeRadii.pill,
                CssBackground.Solid(c.well),
                BORDER,
                c.edge,
                OldgeTheme.shadows.sunken + chromeRing(c.chromeLo),
            ).defaultMinSize(minHeight = HEIGHT)
            .padding(start = BORDER + OldgeTheme.spacing.space3, end = BORDER + END, top = BORDER, bottom = BORDER),
        horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OldgeIcon(
            OldgeIcons.Search,
            contentDescription = null,
            modifier = Modifier.graphicsLayer { rotationZ = wiggleTurn(wiggle.value, out) },
            size = GLASS,
            tint = if (focused) c.ink else c.inkMuted,
        )
        BasicTextField(
            value = text,
            onValueChange = { set(it) },
            modifier =
                Modifier
                    .weight(1f)
                    .onPreviewKeyEvent { e ->
                        if (e.key == Key.Enter && e.type == KeyEventType.KeyDown) {
                            onSubmit(text)
                            true
                        } else {
                            false
                        }
                    }.semantics { contentDescription = label ?: placeholder },
            textStyle = style,
            singleLine = true,
            interactionSource = source,
            cursorBrush = SolidColor(c.ink),
            decorationBox = { inner ->
                // `min-height: 44px`, the text centred in it; at least a line tall (B-17's 19 px line).
                val line = with(LocalDensity.current) { style.lineHeight.toDp() }
                // An `<input>`'s own padding, `1px 2px` from the browser's style sheet, sets the text in.
                Box(
                    Modifier.defaultMinSize(minHeight = INPUT_HEIGHT).padding(horizontal = INPUT_SIDE),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (text.isEmpty()) OldgeText(placeholder, style = style.copy(color = c.inkMuted), maxLines = 1)
                    Box(Modifier.defaultMinSize(minHeight = line)) { inner() }
                }
            },
        )
        // `.og-search__end`: the clear orb and the trailing element side by side, with no gap between.
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (text.isNotEmpty()) {
                // `.og-search__end > .og-orb { animation: og-pop-soft }`: a new orb, so it pops as text appears.
                key("clear") {
                    Box(Modifier.oldgePopSoftIn()) {
                        OldgeOrbButton(
                            OldgeIcons.Close,
                            "Очистить",
                            { set("") },
                            size = OldgeOrbSize.Small,
                            tone = OldgeOrbTone.Chrome,
                        )
                    }
                }
            }
            trailing?.invoke()
        }
    }
}

/**
 * `@keyframes og-wiggle` at [p]: 0, −14°, 10°, −5°, 0 at 0, 25, 50, 75 and 100 %, each interval on
 * [ease].
 */
private fun wiggleTurn(
    p: Float,
    ease: androidx.compose.animation.core.Easing,
): Float {
    val i = (p * (WIGGLE.size - 1)).toInt().coerceIn(0, WIGGLE.size - 2)
    val k = ease.transform((p * (WIGGLE.size - 1) - i).coerceIn(0f, 1f))
    return WIGGLE[i] + (WIGGLE[i + 1] - WIGGLE[i]) * k
}

/** `0 0 0 2px chrome-lo` after `shadow-sunken`: the chrome ring round the capsule. */
private fun chromeRing(chromeLo: androidx.compose.ui.graphics.Color) =
    listOf(OldgeShadow(false, 0.dp, 0.dp, 0.dp, RING, chromeLo))

private val WIGGLE = floatArrayOf(0f, -14f, 10f, -5f, 0f)
private val BORDER = 1.dp
private val RING = 2.dp // css literal: bundle.css `.og-search { box-shadow: …, 0 0 0 2px var(--chrome-lo) }`
private val HEIGHT = 48.dp // css literal: bundle.css `.og-search { min-height: 48px }`
private val INPUT_HEIGHT = 44.dp // css literal: bundle.css `.og-search__input { min-height: 44px }`
private val END = 4.dp // css literal: bundle.css `.og-search { padding: 0 4px 0 var(--space-3) }`
private val INPUT_SIDE = 2.dp // css literal: Chrome's UA style sheet, `input { padding: 1px 2px }`
private val GLASS = 20.dp // css literal: bundle.js `SearchBar`, `Icon size: 20`
private val FOCUS_OFFSET = 3.dp // css literal: bundle.css `.og-search:focus-within { outline-offset: 3px }`
private val INPUT_SIZE = 16.sp // css literal: bundle.css `.og-search__input { font: 400 1rem/1.25rem }`
