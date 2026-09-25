package io.github.youndie.oldge.forms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.actions.OldgeOrbButton
import io.github.youndie.oldge.actions.OldgeOrbSize
import io.github.youndie.oldge.actions.OldgeOrbTone
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.material.CssBackground
import io.github.youndie.oldge.material.cssBox
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.press.oldgePressScale
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.tokens.OldgeShadow
import io.github.youndie.oldge.type.FontCoverage
import io.github.youndie.oldge.type.OldgeScriptText
import io.github.youndie.oldge.type.OldgeText
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlinx.datetime.yearMonth
import kotlin.math.roundToInt
import kotlin.time.Clock

/**
 * A date picker — the design system's `DatePicker`: a month's calendar on an inset panel under a
 * pill header with the month and two round arrows.
 *
 * Its README's rules: the week starts on Monday, and the names are Russian; [today] is ringed in
 * `accent-ink`, and the [selected] day is a glossy accent ball that pops in when chosen; changing the
 * month slides the grid the way it went. [label] names the group for a screen reader, and each day is
 * named in full («24 сентябрь 2026», as the design system spells it).
 *
 * [today] is a parameter, defaulting to the system clock's date, so that a fixture can pin it — a
 * picker drawn from the clock is right on one day only.
 */
@Composable
public fun OldgeDatePicker(
    selected: LocalDate?,
    onSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    label: String = "Выбор даты",
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    var month by remember { mutableStateOf((selected ?: today).yearMonth) }
    var direction by remember { mutableStateOf(0) }
    val slide = remember { Animatable(1f) }
    LaunchedEffect(month) {
        if (direction != 0 && !motion.reduced) {
            slide.snapTo(0f)
            slide.animateTo(1f, tween(motion.base, easing = motion.out))
        }
    }
    Column(
        modifier
            .cssBox(OldgeRadii.lg, CssBackground.Solid(c.surface), BORDER, c.edge, OldgeTheme.shadows.raised)
            .padding(BORDER)
            // `.og-date { overflow: hidden }`: the header's square top is cut to the panel's corners.
            .clip(RoundedCornerShape(OldgeRadii.lg - BORDER))
            .semantics {
                contentDescription = label
                isTraversalGroup = true
            },
    ) {
        Header(month, { d ->
            direction = d
            month = month.plus(d)
        })
        Grid(
            month,
            selected,
            today,
            onSelect,
            Modifier.graphicsLayer {
                // `og-slide-l` / `og-slide-r`: 24 px in from the side it came from, fading in.
                val p = slide.value
                translationX = direction * SLIDE.toPx() * (1 - p)
                alpha = p
            },
        )
    }
}

/** `.og-date__head`: the pill gradient with its gloss, the month's name between the two orbs. */
@Composable
private fun Header(
    month: YearMonth,
    go: (Int) -> Unit,
) {
    val c = OldgeTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .cssBox(0.dp, CssBackground.Linear(listOf(0f to c.pillHi, 1f to c.pillLo)))
            .drawBehind {
                // `::before { inset: 0 0 auto; height: 36% }`, then the 1 px `edge` line under it all.
                drawRect(c.gloss, size = Size(size.width, (size.height - BORDER.toPx()) * GLOSS_HEIGHT))
                drawRect(
                    c.edge,
                    topLeft = Offset(0f, size.height - BORDER.toPx()),
                    size = Size(size.width, BORDER.toPx()),
                )
            }.padding(start = HEAD_X, end = HEAD_X, top = HEAD_Y, bottom = HEAD_Y + BORDER),
        horizontalArrangement = Arrangement.spacedBy(HEAD_GAP),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OldgeOrbButton(
            OldgeIcons.Back,
            "Предыдущий месяц",
            { go(-1) },
            size = OldgeOrbSize.Small,
            tone = OldgeOrbTone.Chrome,
        )
        OldgeText(
            "${MONTHS[month.month.number - 1]} ${month.year}",
            Modifier.weight(1f),
            // `font: 700 1rem/1.25rem var(--font-title)`.
            style =
                OldgeTheme.type.title.copy(
                    fontSize = TITLE_SIZE,
                    lineHeight = TITLE_LINE,
                    color = c.onPill,
                    textAlign = TextAlign.Center,
                ),
        )
        OldgeOrbButton(
            OldgeIcons.Chevron,
            "Следующий месяц",
            { go(1) },
            size = OldgeOrbSize.Small,
            tone = OldgeOrbTone.Chrome,
        )
    }
}

/**
 * `.og-date__grid`: seven equal columns 2 px apart inside `space-2`, the weekday row, blanks up to
 * the first day's weekday, then the days. Chrome's `1fr` columns are fractional (≈ 46.9 px here);
 * each cell is placed at its column's rounded position, as Chrome snaps the painted box.
 */
@Composable
private fun Grid(
    month: YearMonth,
    selected: LocalDate?,
    today: LocalDate,
    onSelect: (LocalDate) -> Unit,
    modifier: Modifier,
) {
    val type = OldgeTheme.type
    val c = OldgeTheme.colors
    val first = month.firstDay
    val blanks = first.dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal
    val days = month.numberOfDays
    // `font: 400 0.625rem/0.75rem var(--font-pixel); letter-spacing: .06em`.
    val weekday =
        type.pixelTag.copy(
            fontSize = WEEKDAY_SIZE,
            lineHeight = WEEKDAY_LINE,
            letterSpacing = WEEKDAY_TRACKING.em,
            fontWeight = FontWeight.Normal,
            color = c.inkMuted,
        )
    Layout(
        content = {
            for (w in WEEKDAYS) {
                Box(Modifier.padding(vertical = WEEKDAY_PADDING), contentAlignment = Alignment.Center) {
                    OldgeScriptText(w, weekday, type.families.pixelCompanion, FontCoverage.silkscreen)
                }
            }
            for (d in 1..days) {
                val date = LocalDate(month.year, month.month, d)
                Day(date, date == selected, date == today) { onSelect(date) }
            }
        },
        modifier = modifier.fillMaxWidth().padding(OldgeTheme.spacing.space2),
    ) { measurables, constraints ->
        val gap = GRID_GAP.toPx()
        val column = (constraints.maxWidth - gap * (COLUMNS - 1)) / COLUMNS
        val cell = minOf(DAY_MAX.toPx(), column).roundToInt()
        val heads = measurables.take(COLUMNS).map { it.measure(Constraints(maxWidth = column.roundToInt())) }
        val cells = measurables.drop(COLUMNS).map { it.measure(Constraints(minWidth = cell, maxWidth = cell)) }
        val headRow = heads.maxOf { it.height }
        val rowHeight = cells.maxOf { it.height }
        val rows = (blanks + days + COLUMNS - 1) / COLUMNS
        val height = headRow + (gap * rows).roundToInt() + rowHeight * rows
        layout(constraints.maxWidth, height) {
            fun left(col: Int) = col * (column + gap)
            heads.forEachIndexed { i, p -> p.place((left(i) + (column - p.width) / 2).roundToInt(), 0) }
            cells.forEachIndexed { i, p ->
                val slot = blanks + i
                val row = slot / COLUMNS
                val col = slot % COLUMNS
                val y = headRow + (gap + rowHeight) * row + gap
                p.place((left(col) + (column - p.width) / 2).roundToInt(), y.roundToInt())
            }
        }
    }
}

/**
 * `.og-date__day`: a pill up to 44 px wide and at least 40 high, the day in body text; today ringed
 * inside with 2 px of `accent-ink` and bold; chosen, the glossy accent ball in its `accent-rim`, which
 * pops in, and whose ring replaces today's. Pressed, it squashes to 0.85.
 */
@Composable
private fun Day(
    date: LocalDate,
    chosen: Boolean,
    today: Boolean,
    onClick: () -> Unit,
) {
    val c = OldgeTheme.colors
    val motion = OldgeTheme.motion
    val source = remember { MutableInteractionSource() }
    val pop = remember { Animatable(1f) }
    LaunchedEffect(chosen, motion.reduced) {
        if (chosen && !motion.reduced) {
            pop.snapTo(0f)
            pop.animateTo(1f, tween(motion.base, easing = motion.spring))
        }
    }
    val shape = RoundedCornerShape(OldgeRadii.pill)
    val shadows =
        when {
            chosen -> listOf(OldgeShadow(false, 0.dp, 0.dp, 0.dp, RING, c.accentRim)) + OldgeTheme.shadows.orb
            today -> listOf(OldgeShadow(true, 0.dp, 0.dp, 0.dp, RING, c.accentInk))
            else -> emptyList()
        }
    val background =
        if (chosen) {
            CssBackground.Radial(listOf(0f to c.accentHi, BALL_STOP to c.accentLo), BALL_X, BALL_Y)
        } else {
            CssBackground.Solid(Color.Transparent)
        }
    Box(
        Modifier
            .graphicsLayer {
                // `og-pop-soft`: from 0.6 and 0.3 opacity.
                val p = pop.value
                val s = POP_FROM + (1 - POP_FROM) * p
                scaleX = s
                scaleY = s
                alpha = (POP_ALPHA + (1 - POP_ALPHA) * p).coerceIn(0f, 1f)
            }.oldgePressScale(source, PRESSED_SCALE)
            .defaultMinSize(minHeight = DAY_MIN_HEIGHT)
            .cssBox(OldgeRadii.pill, background, shadows = shadows)
            .selectable(
                chosen,
                source,
                OldgeIndication(shape, flash = false, ringOffset = 0.dp),
                role = Role.Button,
                onClick = onClick,
            ).semantics {
                contentDescription = "${date.day} ${MONTHS[date.month.number - 1].lowercase()} ${date.year}"
            },
        contentAlignment = Alignment.Center,
    ) {
        val bold = chosen || today
        OldgeText(
            date.day.toString(),
            style =
                OldgeTheme.type.body.copy(
                    color = if (chosen) c.onAccent else c.ink,
                    fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
                ),
        )
    }
}

private fun YearMonth.plus(months: Int): YearMonth = firstDay.plus(DatePeriod(months = months)).yearMonth

/** bundle.js `MONTHS` and `WD`, verbatim. */
private val MONTHS =
    listOf(
        "Январь",
        "Февраль",
        "Март",
        "Апрель",
        "Май",
        "Июнь",
        "Июль",
        "Август",
        "Сентябрь",
        "Октябрь",
        "Ноябрь",
        "Декабрь",
    )
private val WEEKDAYS = listOf("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС")

private const val COLUMNS = 7
private const val GLOSS_HEIGHT = 0.36f
private const val BALL_STOP = 0.8f
private const val BALL_X = 0.35f
private const val BALL_Y = 0.3f
private const val PRESSED_SCALE = 0.85f
private const val POP_FROM = 0.6f
private const val POP_ALPHA = 0.3f
private const val WEEKDAY_TRACKING = 0.06f
private val BORDER = 1.dp
private val HEAD_X = 4.dp // css literal: bundle.css `.og-date__head { padding: 2px 4px }`
private val HEAD_Y = 2.dp // css literal: bundle.css `.og-date__head { padding: 2px 4px }`
private val HEAD_GAP = 4.dp // css literal: bundle.css `.og-date__head { gap: 4px }`
private val GRID_GAP = 2.dp // css literal: bundle.css `.og-date__grid { gap: 2px }`
private val DAY_MAX = 44.dp // css literal: bundle.css `.og-date__day { max-width: 44px }`
private val DAY_MIN_HEIGHT = 40.dp // css literal: bundle.css `.og-date__day { min-height: 40px }`
private val RING = 2.dp // css literal: bundle.css `.og-date__day--today { box-shadow: inset 0 0 0 2px }`
private val WEEKDAY_PADDING = 4.dp // css literal: bundle.css `.og-date__wd { padding: 4px 0 }`
private val SLIDE = 24.dp // css literal: bundle.css `@keyframes og-slide-l { from { transform: translateX(24px) } }`
private val TITLE_SIZE = 16.sp // css literal: bundle.css `.og-date__title { font: 700 1rem/1.25rem }`
private val TITLE_LINE = 20.sp // css literal: bundle.css `.og-date__title { font: 700 1rem/1.25rem }`
private val WEEKDAY_SIZE = 10.sp // css literal: bundle.css `.og-date__wd { font: 400 0.625rem/0.75rem }`
private val WEEKDAY_LINE = 12.sp // css literal: bundle.css `.og-date__wd { font: 400 0.625rem/0.75rem }`
