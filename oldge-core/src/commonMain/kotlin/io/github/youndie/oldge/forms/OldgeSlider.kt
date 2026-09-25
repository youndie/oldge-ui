package io.github.youndie.oldge.forms

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.press.oldgePressScale
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.type.FontCoverage
import io.github.youndie.oldge.type.OldgeScriptText
import io.github.youndie.oldge.type.OldgeText
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * A slider — the design system's `Slider`: a sunken trough with an accent fill and a chrome ball,
 * [value] from 0 to 1, under a head with its [label] and [valueText] (how the value reads: «60%»).
 *
 * Its README asks for the native range input's keyboard and screen-reader behaviour, and this is
 * that input's: the arrow keys move by one step of a thousand, Page Up and Page Down by a tenth, Home
 * and End to the ends; a screen reader hears [label] and [valueText] and can set the value. A press
 * anywhere on the 44 dp strip moves the ball there and the ball swells to 1.25 while held.
 */
@Composable
public fun OldgeSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    valueText: String? = null,
    enabled: Boolean = true,
) {
    val c = OldgeTheme.colors
    val type = OldgeTheme.type
    val v = value.coerceIn(0f, 1f)
    val change by rememberUpdatedState(onValueChange)
    val source = remember { MutableInteractionSource() }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(HEAD_GAP)) {
        if (label != null || valueText != null) {
            // `.og-slider__head`: 0.8125rem / 1rem, the label bold, the value in the lcd face.
            Row(Modifier.fillMaxWidth().clearAndSetSemantics {}, horizontalArrangement = Arrangement.SpaceBetween) {
                OldgeText(label.orEmpty(), style = type.label.copy(color = c.ink))
                if (valueText != null) {
                    OldgeScriptText(
                        valueText,
                        type.label.copy(
                            fontFamily = type.families.lcd,
                            fontWeight = FontWeight.Normal,
                            color = c.inkMuted,
                        ),
                        type.families.lcdCompanion,
                        FontCoverage.shareTechMono,
                    )
                }
            }
        }
        BoxWithConstraints(
            Modifier
                .fillMaxWidth()
                .height(OldgeTheme.spacing.hitMin)
                .sliderSemantics(v, label, valueText, enabled) { change(it) }
                .focusable(enabled, source)
                .onKeyEvent { e ->
                    if (!enabled || e.type != KeyEventType.KeyDown) return@onKeyEvent false
                    val next =
                        when (e.key) {
                            Key.DirectionRight, Key.DirectionUp -> v + STEP
                            Key.DirectionLeft, Key.DirectionDown -> v - STEP
                            Key.PageUp -> v + PAGE
                            Key.PageDown -> v - PAGE
                            Key.MoveHome -> 0f
                            Key.MoveEnd -> 1f
                            else -> return@onKeyEvent false
                        }
                    change(next.coerceIn(0f, 1f))
                    true
                }.pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    val thumb = THUMB.toPx()
                    val inner = BORDER.toPx()

                    fun at(x: Float) = ((x - inner - thumb / 2) / (size.width - 2 * inner - thumb)).coerceIn(0f, 1f)
                    coroutineScope {
                        awaitEachGesture {
                            val down = awaitFirstDown()
                            val press = PressInteraction.Press(down.position)
                            launch { source.emit(press) }
                            change(at(down.position.x))
                            var released = false
                            while (!released) {
                                val event = awaitPointerEvent()
                                val pointer = event.changes.firstOrNull { it.id == down.id } ?: break
                                if (pointer.pressed) {
                                    change(at(pointer.position.x))
                                    pointer.consume()
                                } else {
                                    released = true
                                }
                            }
                            launch { source.emit(PressInteraction.Release(press)) }
                        }
                    }
                }.indication(source, OldgeIndication(RoundedCornerShape(OldgeRadii.sm), flash = false)),
            contentAlignment = Alignment.CenterStart,
        ) {
            Trough(v)
            // The thumb travels the track's content box: 1 px in, `margin-top: -10px` above it.
            val travel = maxWidth - BORDER * 2 - THUMB
            Box(
                Modifier
                    .offset(x = BORDER + travel * v)
                    .oldgePressScale(source, if (enabled) THUMB_PRESSED else 1f)
                    .size(THUMB)
                    .cssBox(
                        OldgeRadii.pill,
                        CssBackground.Radial(listOf(0f to c.chromeHi, THUMB_STOP to c.chromeLo), THUMB_X, THUMB_Y),
                        BORDER,
                        c.edge,
                        OldgeTheme.shadows.orb,
                    ),
            )
        }
    }
}

/** `::-webkit-slider-runnable-track`: 8 px, `accent` up to the value and `well` after it, sunken. */
@Composable
private fun Trough(v: Float) {
    val c = OldgeTheme.colors
    Box(
        Modifier
            .fillMaxWidth()
            .height(TRACK)
            .cssBox(
                OldgeRadii.pill,
                // `linear-gradient(90deg, accent 0 <fill>, well <fill> 100%)`: a hard stop.
                CssBackground.Linear(
                    listOf(0f to c.accent, v to c.accent, v to c.well, 1f to c.well),
                    angleDegrees = 90f,
                ),
                BORDER,
                c.edge,
                OldgeTheme.shadows.sunken,
            ),
    )
}

private fun Modifier.sliderSemantics(
    v: Float,
    label: String?,
    valueText: String?,
    enabled: Boolean,
    set: (Float) -> Unit,
): Modifier =
    // Plain semantics, not cleared: clearing would also drop the focus action `focusable` adds, and
    // the strip's children, the trough and the ball, say nothing of their own.
    this.then(
        Modifier.semantics {
            contentDescription = label ?: "Значение"
            if (valueText != null) stateDescription = valueText
            progressBarRangeInfo = ProgressBarRangeInfo(v, 0f..1f, steps = STEPS - 1)
            if (enabled) {
                setProgress { target ->
                    set(target.coerceIn(0f, 1f))
                    true
                }
            } else {
                disabled()
            }
        },
    )

private const val STEPS = 1000
private const val STEP = 1f / STEPS
private const val PAGE = 0.1f
private const val THUMB_PRESSED = 1.25f
private const val THUMB_STOP = 0.75f
private const val THUMB_X = 0.35f
private const val THUMB_Y = 0.3f
private val BORDER = 1.dp
private val HEAD_GAP = 2.dp // css literal: bundle.css `.og-slider { gap: 2px }`
private val TRACK = 8.dp // css literal: bundle.css `::-webkit-slider-runnable-track { height: 8px }`
private val THUMB: Dp = 26.dp // css literal: bundle.css `::-webkit-slider-thumb { width: 26px; height: 26px }`
