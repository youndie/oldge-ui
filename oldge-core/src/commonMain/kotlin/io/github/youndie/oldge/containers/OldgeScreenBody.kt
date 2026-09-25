package io.github.youndie.oldge.containers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import io.github.youndie.oldge.material.skinBody
import io.github.youndie.oldge.theme.LocalOldgeContentColor
import io.github.youndie.oldge.theme.LocalOldgeTextStyle
import io.github.youndie.oldge.theme.OldgeTheme

/**
 * A screen's body — the design system's `.og-body`, the surface its README gives a screen container
 * (`og-textured`). It paints `body-hi` fading into `ground`, the skin's `glow` at the bottom-right
 * corner and, unless the theme turns texture off, the grain. Its content reads `ink` body text, as
 * the CSS class sets it, whatever the container around it provides.
 *
 * It is what an app's root stands on: usually `OldgeScreenBody(Modifier.fillMaxSize()) { … }` just
 * inside [io.github.youndie.oldge.theme.OldgeTheme].
 */
@Composable
public fun OldgeScreenBody(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val c = OldgeTheme.colors
    CompositionLocalProvider(
        LocalOldgeTextStyle provides OldgeTheme.type.body.copy(color = c.ink),
        LocalOldgeContentColor provides c.ink,
    ) {
        Box(modifier.skinBody(), content = content)
    }
}
