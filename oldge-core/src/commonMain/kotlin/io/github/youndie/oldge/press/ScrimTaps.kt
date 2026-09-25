package io.github.youndie.oldge.press

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

/**
 * A scrim's taps, and nothing for a screen reader (B-43): the design system's scrims are plain `div`s
 * with a click handler (`og-scrim`, `og-sheet-scrim`, `og-drawer-scrim`), not controls, and an
 * accessible close is the overlay's own button. A `clickable` here was announced as an unnamed
 * button covering the screen. The taps are still taken, so nothing behind the scrim receives them.
 */
@Composable
internal fun Modifier.oldgeScrimTaps(onTap: () -> Unit): Modifier {
    val tap by rememberUpdatedState(onTap)
    return pointerInput(Unit) { detectTapGestures { tap() } }
}
