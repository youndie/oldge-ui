package io.github.youndie.oldge.theme

import androidx.compose.runtime.Composable

/** The platform's own "reduce motion" setting, read once by [OldgeTheme]; `false` where there is none. */
@Composable
internal expect fun platformReducedMotion(): Boolean
