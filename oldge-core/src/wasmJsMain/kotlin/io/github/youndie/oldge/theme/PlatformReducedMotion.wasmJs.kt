package io.github.youndie.oldge.theme

import androidx.compose.runtime.Composable
import kotlinx.browser.window

/** The browser's `prefers-reduced-motion: reduce`. */
@Composable
internal actual fun platformReducedMotion(): Boolean = window.matchMedia("(prefers-reduced-motion: reduce)").matches
