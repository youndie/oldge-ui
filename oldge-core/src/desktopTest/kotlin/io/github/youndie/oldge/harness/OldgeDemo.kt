package io.github.youndie.oldge.harness

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.material.skinBody
import io.github.youndie.oldge.theme.LocalOldgeTextStyle
import io.github.youndie.oldge.theme.LocalOldgeTypography
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.theme.OldgeTypography
import io.github.youndie.oldge.type.OldgeText
import io.github.youndie.viddik.core.ViddikPlatformTextStyle

/**
 * The design system's preview frame, `.og-demo` on the skin's body: a column at most 390 dp wide,
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
) = OldgeTheme(skin = skin, reducedMotion = true) {
    PortableText {
        Box(Modifier.fillMaxSize().skinBody()) {
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
}

/** A bare `<span>` of a preview: body text in `ink`. */
@Composable
fun DemoText(text: String) = OldgeText(text, style = LocalOldgeTextStyle.current.copy(color = OldgeTheme.colors.ink))

/**
 * Every type style with hinting and smoothing pinned through viddik's `ViddikPlatformTextStyle`, so a
 * golden means the same on another operating system (kvadrant-ui's `portableTypography`, research
 * §1.9). Test-only: the library does not override the platform's text rendering for its consumers.
 */
@Composable
fun PortableText(content: @Composable () -> Unit) {
    val t = OldgeTheme.type
    val portable =
        OldgeTypography(
            display = t.display.pinned(),
            heading = t.heading.pinned(),
            title = t.title.pinned(),
            body = t.body.pinned(),
            bodyStrong = t.bodyStrong.pinned(),
            button = t.button.pinned(),
            label = t.label.pinned(),
            caption = t.caption.pinned(),
            readout = t.readout.pinned(),
            readoutSm = t.readoutSm.pinned(),
            pixelTag = t.pixelTag.pinned(),
            families = t.families,
        )
    CompositionLocalProvider(
        LocalOldgeTypography provides portable,
        LocalOldgeTextStyle provides portable.body,
        content = content,
    )
}

@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
private val SubpixelPlatformTextStyle =
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

private fun TextStyle.pinned(): TextStyle = copy(platformStyle = SubpixelPlatformTextStyle)
