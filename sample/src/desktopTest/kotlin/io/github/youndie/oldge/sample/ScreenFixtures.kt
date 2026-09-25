package io.github.youndie.oldge.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.PlatformTextStyle
import io.github.youndie.oldge.containers.OldgeScreenBody
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.viddik.annotations.ViddikScreenshot

/**
 * The page previews' `.phone`, as the reference is photographed in it: the page's body behind, the
 * screen clipped to `radius-xl`. The phone's `shadow-window` falls outside the photographed box but
 * in its corners; it is the mock-up's, not the screen's, and is not drawn. Reduced motion, as the
 * references are rendered, and text pinned through the theme, as oldge-core's fixtures pin it
 * (B-52), so parity reads against the same floor.
 */
@Composable
fun Phone(
    skin: OldgeSkin,
    screen: @Composable (Modifier) -> Unit,
) = OldgeTheme(skin = skin, reducedMotion = true, platformTextStyle = PortableTextStyle) {
    OldgeScreenBody(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().clip(RoundedCornerShape(OldgeTheme.radii.xl))) {
            screen(Modifier.fillMaxSize())
        }
    }
}

/** oldge-core's harness value (its `OldgeDemo.kt`, research §1.9 and §1.10): viddik's pin with subpixel positioning on. */
@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
val PortableTextStyle: PlatformTextStyle =
    PlatformTextStyle(
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

@ViddikScreenshot(group = "AuthScreen", name = "Toxic", width = 390, height = 760)
@Composable
fun AuthScreenToxic() = Phone(OldgeSkin.Toxic) { AuthScreen(it) }

@ViddikScreenshot(group = "AuthScreen", name = "Media", width = 390, height = 760)
@Composable
fun AuthScreenMedia() = Phone(OldgeSkin.Media) { AuthScreen(it) }

@ViddikScreenshot(group = "AuthScreen", name = "Crystal", width = 390, height = 760)
@Composable
fun AuthScreenCrystal() = Phone(OldgeSkin.Crystal) { AuthScreen(it) }

@ViddikScreenshot(group = "ChatScreen", name = "Toxic", width = 390, height = 760)
@Composable
fun ChatScreenToxic() = Phone(OldgeSkin.Toxic) { ChatScreen(it) }

@ViddikScreenshot(group = "ChatScreen", name = "Media", width = 390, height = 760)
@Composable
fun ChatScreenMedia() = Phone(OldgeSkin.Media) { ChatScreen(it) }

@ViddikScreenshot(group = "ChatScreen", name = "Crystal", width = 390, height = 760)
@Composable
fun ChatScreenCrystal() = Phone(OldgeSkin.Crystal) { ChatScreen(it) }
