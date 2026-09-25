package io.github.youndie.oldge.harness

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.containers.OldgeScreenBody
import io.github.youndie.oldge.theme.LocalOldgeTextStyle
import io.github.youndie.oldge.theme.LocalOldgeTypography
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.theme.OldgeTypography
import io.github.youndie.oldge.type.OldgeText

/**
 * The design system's preview frame, `.og-demo` on [OldgeScreenBody]: a column at most 390 dp wide,
 * padding `space-4`, gap `space-3`, filling the fixture (whose size is the reference's, held by
 * `FixtureSizeTest`). Reduced motion, as the references are rendered (research §1.2); texture on,
 * as B-04 decided. Not [padded], the frame is the fixture's to draw: a preview that makes the demo
 * itself the scroll container (ListSection) puts its padding inside the scroll.
 */
@Composable
fun OldgeDemo(
    skin: OldgeSkin,
    padded: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) = OldgeTheme(skin = skin, reducedMotion = true, platformTextStyle = PortableTextStyle) {
    OldgeScreenBody(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .widthIn(
                    max = 390.dp,
                ).then(if (padded) Modifier.padding(OldgeTheme.spacing.space4) else Modifier),
            verticalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space3),
            content = content,
        )
    }
}

/**
 * A bare `<span>` of a preview: body text in the colour its container gives, as CSS text inherits
 * it (`on-balloon` in a Banner), else in `ink`.
 */
@Composable
fun DemoText(text: String) {
    val style = LocalOldgeTextStyle.current
    OldgeText(text, style = if (style.color.isSpecified) style else style.copy(color = OldgeTheme.colors.ink))
}

/**
 * The platform text style every golden and parity fixture pins, through `OldgeTheme`'s public
 * `platformTextStyle` (B-52): hinting off, antialiasing, subpixel positioning, so a golden means the
 * same on another operating system (kvadrant-ui's `portableTypography`, research §1.9). It is viddik's
 * `ViddikPlatformTextStyle` with subpixel positioning turned on: off, every advance is rounded and a
 * line drifts from Chrome's (research §1.10, 1.16 % → 0.74 % on Divider). Test-only: the library does
 * not override the platform's text rendering for its consumers.
 */
@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
val PortableTextStyle: androidx.compose.ui.text.PlatformTextStyle =
    androidx.compose.ui.text.PlatformTextStyle(
        spanStyle = null,
        paragraphStyle =
            androidx.compose.ui.text.PlatformParagraphStyle(
                fontRasterizationSettings =
                    androidx.compose.ui.text.FontRasterizationSettings(
                        smoothing = androidx.compose.ui.text.FontSmoothing.AntiAlias,
                        hinting = androidx.compose.ui.text.FontHinting.None,
                        subpixelPositioning = true,
                        autoHintingForced = false,
                    ),
            ),
    )
