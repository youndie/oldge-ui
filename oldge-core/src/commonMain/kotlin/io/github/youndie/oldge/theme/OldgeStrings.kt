package io.github.youndie.oldge.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import io.github.youndie.oldge.forms.MONTHS
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/**
 * The words the library's own controls say to a screen reader: the accessible names of the orbs and
 * actions a caller does not label. The defaults are the design system's Russian. An app in another
 * language passes its own once, to [OldgeTheme]'s `strings`, and every control below follows (B-71).
 *
 * A caller's own labels — a button's text, a field's label, an icon button's `contentDescription` —
 * are not here: the caller already names those.
 *
 * @param close the close orb of the Dialog's and the BottomSheet's title bar, the Snackbar's and the
 *   Balloon's close button, and the NavDrawer's scrim, which is announced as the close action.
 * @param clear the SearchBar's clear orb, shown once there is text.
 * @param showPassword the TextField's reveal orb while the password is hidden.
 * @param hidePassword the same orb while the password is shown.
 * @param progress a ProgressBar's name when it has no label (B-82).
 * @param stepDone a done Stepper step's tick.
 * @param page a PageDots dot: its 1-based number and the count.
 * @param day a DatePicker day, for a screen reader; the drawn calendar is the design system's.
 */
@Immutable
public class OldgeStrings(
    public val close: String = "Закрыть",
    public val clear: String = "Очистить",
    public val showPassword: String = "Показать пароль",
    public val hidePassword: String = "Скрыть пароль",
    public val progress: String = "Выполнение",
    public val stepDone: String = "готово",
    public val page: (number: Int, count: Int) -> String = { n, count -> "Страница $n из $count" },
    public val day: (LocalDate) -> String = { d -> "${d.day} ${MONTHS[d.month.number - 1].lowercase()} ${d.year}" },
)

/**
 * The [OldgeStrings] the controls below read; [OldgeTheme] provides its `strings`. Providing another
 * value over a subtree changes the words there only.
 */
public val LocalOldgeStrings: ProvidableCompositionLocal<OldgeStrings> =
    staticCompositionLocalOf { OldgeStrings() }
