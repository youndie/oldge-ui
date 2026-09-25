package io.github.youndie.oldge.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import io.github.youndie.oldge.press.OldgeIndication
import io.github.youndie.oldge.tokens.OldgeColors
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.oldge.tokens.OldgeShadows
import io.github.youndie.oldge.tokens.OldgeSpacing

/**
 * The oldge-ui theme: a skin, the type ramp, the motion and three switches, provided to everything
 * below it. A component reads every value through [OldgeTheme]'s accessors (research D7).
 *
 * @param skin the three skins of the design system; a skin is a set of token values, nothing else.
 * @param reducedMotion every duration zero and every loop still. Defaults to the platform's setting
 *   where the platform has one (Android's animator scale, iOS's Reduce Motion, the browser's
 *   `prefers-reduced-motion`); on desktop there is none and the default is `false`.
 * @param texture the grain on screen bodies and the material of the frame; the design system's
 *   `data-og-texture="off"`, for weak devices and power saving.
 * @param pressFlash the gloss flash from the touch point; the design system's `data-og-press="none"`.
 * @param platformTextStyle applied to every style of [OldgeTheme.type]: the platform's text
 *   rendering (hinting, smoothing, subpixel positioning) pinned, for screenshots that mean the same
 *   on another machine — viddik's `ViddikPlatformTextStyle`, say. `null`, the default, leaves the
 *   platform's own, which is what an app wants. The faces stay the design system's either way.
 */
@Composable
public fun OldgeTheme(
    skin: OldgeSkin = OldgeSkin.Toxic,
    reducedMotion: Boolean = platformReducedMotion(),
    texture: Boolean = true,
    pressFlash: Boolean = true,
    platformTextStyle: PlatformTextStyle? = null,
    content: @Composable () -> Unit,
) {
    val typography = oldgeTypography(platformTextStyle)
    CompositionLocalProvider(
        LocalOldgeSkin provides skin,
        LocalOldgeTypography provides typography,
        LocalOldgeMotion provides OldgeMotion(reducedMotion),
        LocalOldgeSwitches provides OldgeSwitches(texture = texture, pressFlash = pressFlash),
        LocalOldgeTextStyle provides typography.body,
        LocalOldgeContentColor provides skin.colors.ink,
        // The press flash and the focus ring reach every clickable through this (B-10).
        LocalIndication provides OldgeIndication(),
        content = content,
    )
}

/** What [OldgeTheme] provides, read from inside it. */
public object OldgeTheme {
    public val skin: OldgeSkin
        @Composable @ReadOnlyComposable
        get() = LocalOldgeSkin.current

    public val colors: OldgeColors
        @Composable @ReadOnlyComposable
        get() = LocalOldgeSkin.current.colors

    public val shadows: OldgeShadows
        @Composable @ReadOnlyComposable
        get() = LocalOldgeSkin.current.shadows

    public val type: OldgeTypography
        @Composable @ReadOnlyComposable
        get() = LocalOldgeTypography.current

    public val motion: OldgeMotion
        @Composable @ReadOnlyComposable
        get() = LocalOldgeMotion.current

    /** Skin-independent: the same object whatever the skin. */
    public val spacing: OldgeSpacing get() = OldgeSpacing

    /** Skin-independent: the same object whatever the skin. */
    public val radii: OldgeRadii get() = OldgeRadii

    public val texture: Boolean
        @Composable @ReadOnlyComposable
        get() = LocalOldgeSwitches.current.texture

    public val pressFlash: Boolean
        @Composable @ReadOnlyComposable
        get() = LocalOldgeSwitches.current.pressFlash
}

/** The text style a component's text inherits: `body` under [OldgeTheme]. */
public val LocalOldgeTextStyle: androidx.compose.runtime.ProvidableCompositionLocal<TextStyle> =
    staticCompositionLocalOf { TextStyle.Default }

/** The colour a component's text and icons inherit: `ink` under [OldgeTheme]. */
public val LocalOldgeContentColor: androidx.compose.runtime.ProvidableCompositionLocal<Color> =
    staticCompositionLocalOf { Color.Unspecified }

internal class OldgeSwitches(
    val texture: Boolean,
    val pressFlash: Boolean,
)

private fun missing(what: String): Nothing = error("$what read outside OldgeTheme")

internal val LocalOldgeSkin = staticCompositionLocalOf<OldgeSkin> { missing("OldgeTheme.skin") }
internal val LocalOldgeTypography = staticCompositionLocalOf<OldgeTypography> { missing("OldgeTheme.type") }
internal val LocalOldgeMotion = staticCompositionLocalOf<OldgeMotion> { missing("OldgeTheme.motion") }
internal val LocalOldgeSwitches = staticCompositionLocalOf<OldgeSwitches> { missing("OldgeTheme's switches") }
