package io.github.youndie.oldge.theme

import androidx.compose.runtime.Composable

/** A desktop JVM exposes no reduce-motion setting to read, so motion is on unless the caller says otherwise. */
@Composable
internal actual fun platformReducedMotion(): Boolean = false
