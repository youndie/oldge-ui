package io.github.youndie.oldge.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.feedback.LcdText
import io.github.youndie.oldge.feedback.loopPhase
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.material.premultipliedLerp
import io.github.youndie.oldge.press.oldgePopIn
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.tokens.OldgeShadow
import io.github.youndie.oldge.type.OldgeText

/**
 * A wizard's steps — the design system's `Stepper`, Material's stepper in the manner of the
 * mid-2000s wizards: numbered orbs joined by a trough, [current] the index of the step being done.
 *
 * Its README's rules: a done step is a frame orb with a tick; the current one is the accent with a
 * pulsing ring, and the trough up to it fills; three to five steps, each labelled with one word.
 * The ring is a loop: it stands still under reduced motion.
 */
@Composable
public fun OldgeStepper(
    steps: List<String>,
    current: Int,
    modifier: Modifier = Modifier,
    label: String = "Шаги",
) {
    require(steps.size in MIN_STEPS..MAX_STEPS) { "a stepper has $MIN_STEPS to $MAX_STEPS steps, got ${steps.size}" }
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    // `.og-step + .og-step::after { transform: scaleX(0 or 1); transition: dur-slow ease-out }`, one per join.
    val fills =
        (1 until steps.size).map { i ->
            val filled = i <= current
            animateFloatAsState(
                if (filled) 1f else 0f,
                tween(motion.slow, easing = motion.out),
                label = "oldge step join",
            ).value
        }
    val well = c.well
    val edge = c.edge
    val accent = c.accent
    val sunken = OldgeTheme.shadows.sunken
    Row(
        modifier
            .fillMaxWidth()
            .semantics { contentDescription = label }
            .drawBehind {
                val col = size.width / steps.size
                val gap = JOIN_GAP.toPx()
                for (i in 1 until steps.size) {
                    // From 20 px past the centre of the step before to 20 px short of this one's.
                    val left = col * (i - 1) + col / 2 + gap
                    val width = col - 2 * gap
                    drawTrough(Offset(left, TROUGH_TOP.toPx()), Size(width, TROUGH.toPx()), well, edge, sunken)
                    val f = fills[i - 1]
                    if (f > 0f) {
                        drawRoundRect(
                            accent,
                            Offset(left, FILL_TOP.toPx()),
                            Size(width * f, FILL.toPx()),
                            CornerRadius(FILL_RADIUS.toPx()),
                        )
                    }
                }
            },
    ) {
        steps.forEachIndexed { i, step ->
            val state =
                when {
                    i < current -> StepState.Done
                    i == current -> StepState.Current
                    else -> StepState.Todo
                }
            Step(step, i, state, Modifier.weight(1f))
        }
    }
}

private enum class StepState { Done, Current, Todo }

/** The `well` trough: a 6 px bar in a 1 px `edge` frame, `shadow-sunken`, 2 px round. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTrough(
    at: Offset,
    size: Size,
    well: Color,
    edge: Color,
    sunken: List<OldgeShadow>,
) {
    val r = CornerRadius(FILL_RADIUS.toPx())
    val b = 1.dp.toPx()
    drawRoundRect(edge, at, size, r)
    drawRoundRect(
        well,
        Offset(at.x + b, at.y + b),
        Size(size.width - 2 * b, size.height - 2 * b),
        CornerRadius((r.x - b).coerceAtLeast(0f)),
    )
    // `shadow-sunken`: 1 px of its dark inset along the top and left, its light one along the bottom and right.
    for (s in sunken) {
        val dx = s.offsetX.toPx()
        val dy = s.offsetY.toPx()
        val inner = Size(size.width - 2 * b, size.height - 2 * b)
        if (dy > 0) drawRect(s.color, Offset(at.x + b, at.y + b), Size(inner.width, dy))
        if (dy < 0) drawRect(s.color, Offset(at.x + b, at.y + b + inner.height + dy), Size(inner.width, -dy))
        if (dx > 0) drawRect(s.color, Offset(at.x + b, at.y + b), Size(dx, inner.height))
        if (dx < 0) drawRect(s.color, Offset(at.x + b + inner.width + dx, at.y + b), Size(-dx, inner.height))
    }
}

/**
 * `.og-step`: the orb over the one-word label at 0.75rem / 1rem, 6 px apart, centred, in
 * `ink-muted`; the current step's label is bold in `ink`.
 */
@Composable
private fun Step(
    label: String,
    index: Int,
    state: StepState,
    modifier: Modifier,
) {
    val c = OldgeTheme.colors
    Column(
        // `aria-current="step"`: the current step is the selected one.
        modifier.semantics(mergeDescendants = true) { selected = state == StepState.Current },
        verticalArrangement = Arrangement.spacedBy(LABEL_GAP),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Orb(index, state)
        OldgeText(
            label,
            style =
                OldgeTheme.type.caption.copy(
                    color = if (state == StepState.Current) c.ink else c.inkMuted,
                    fontWeight = if (state == StepState.Current) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                ),
        )
    }
}

/**
 * `.og-step__orb`: 32 px, a 160° chrome ring 2 px wide with `shadow-orb`, round a core of
 * `surface-2` holding the number in the lcd face at 14 px. Done: `bezel-hi` to `bezel-lo` with a
 * tick that pops in, named «готово». Current: `accent-hi` to `accent-lo` with `og-pulse-ring`, a
 * ring of `accent-hi` growing to 10 px and fading out every `dur-loop` on `ease-out`.
 */
@Composable
private fun Orb(
    index: Int,
    state: StepState,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val pulse = if (state == StepState.Current) loopPhase(PULSE_MS) else null
    val ring =
        pulse?.let { p ->
            val k = motion.out.transform(p)
            listOf(OldgeShadow(false, 0.dp, 0.dp, 0.dp, RING * k, premultipliedLerp(c.accentHi, Color.Transparent, k)))
        } ?: emptyList()
    val (core, ink) =
        when (state) {
            StepState.Done -> CssBackground.Linear(listOf(0f to c.bezelHi, 1f to c.bezelLo)) to c.onBezel
            StepState.Current -> CssBackground.Linear(listOf(0f to c.accentHi, 1f to c.accentLo)) to c.onAccent
            StepState.Todo -> CssBackground.Solid(c.surface2) to c.ink
        }
    Box(
        Modifier
            .size(ORB)
            .cssBox(
                OldgeRadii.pill,
                CssBackground.Linear(listOf(0f to c.chromeHi, 1f to c.chromeLo), ORB_ANGLE),
                shadows = OldgeTheme.shadows.orb,
            ).padding(ORB_RIM)
            .cssBox(OldgeRadii.pill, core, shadows = ring),
        contentAlignment = Alignment.Center,
    ) {
        if (state == StepState.Done) {
            OldgeIcon(
                OldgeIcons.Check,
                contentDescription = "готово",
                modifier = Modifier.oldgePopIn(),
                size = TICK,
                tint = ink,
            )
        } else {
            LcdText("${index + 1}", OldgeTheme.type.readoutSm.copy(fontSize = DIGIT, lineHeight = DIGIT, color = ink))
        }
    }
}

private const val MIN_STEPS = 3
private const val MAX_STEPS = 5
private const val PULSE_MS = 1200
private const val ORB_ANGLE = 160f
private val ORB = 32.dp // css literal: bundle.css `.og-step__orb { width: 32px; height: 32px }`
private val ORB_RIM = 2.dp // css literal: bundle.css `.og-step__orb { padding: 2px }`
private val TICK = 16.dp // css literal: bundle.js `Stepper`, `Icon size: 16`
private val DIGIT = 14.sp // css literal: bundle.css `.og-step__core { font: 400 14px/1 var(--font-lcd) }`
private val RING = 10.dp // css literal: bundle.css `@keyframes og-pulse-ring { 100% { box-shadow: 0 0 0 10px } }`
private val LABEL_GAP = 6.dp // css literal: bundle.css `.og-step { gap: 6px }`
private val JOIN_GAP = 20.dp // css literal: bundle.css `.og-step + .og-step::before { left: calc(-50% + 20px) }`
private val TROUGH_TOP = 13.dp // css literal: bundle.css `.og-step + .og-step::before { top: 13px }`
private val TROUGH = 6.dp // css literal: bundle.css `.og-step + .og-step::before { height: 6px }`
private val FILL_TOP = 15.dp // css literal: bundle.css `.og-step + .og-step::after { top: 15px }`
private val FILL = 4.dp // css literal: bundle.css `.og-step + .og-step::after { height: 4px }`
private val FILL_RADIUS = 2.dp // css literal: bundle.css `.og-step + .og-step::before { border-radius: 2px }`
