package io.github.youndie.oldge.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.Density
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
    fontScale: Float = 1f,
    screen: @Composable (Modifier) -> Unit,
) {
    // A page's root font of 200 % is the system font at 200 % (research §1.7): `rem` grows, `px` does not.
    val base = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(base.density, fontScale)) {
        OldgeTheme(skin = skin, reducedMotion = true, platformTextStyle = PortableTextStyle) {
            OldgeScreenBody(Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxSize().clip(RoundedCornerShape(OldgeTheme.radii.xl))) {
                    screen(Modifier.fillMaxSize())
                }
            }
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

@ViddikScreenshot(group = "InboxScreen", name = "Toxic", width = 390, height = 760)
@Composable
fun InboxScreenToxic() = Phone(OldgeSkin.Toxic) { InboxScreen(it) }

@ViddikScreenshot(group = "InboxScreen", name = "Media", width = 390, height = 760)
@Composable
fun InboxScreenMedia() = Phone(OldgeSkin.Media) { InboxScreen(it) }

@ViddikScreenshot(group = "InboxScreen", name = "Crystal", width = 390, height = 760)
@Composable
fun InboxScreenCrystal() = Phone(OldgeSkin.Crystal) { InboxScreen(it) }

@ViddikScreenshot(group = "MediaScreen", name = "Toxic", width = 390, height = 760)
@Composable
fun MediaScreenToxic() = Phone(OldgeSkin.Toxic) { MediaScreen(it) }

@ViddikScreenshot(group = "MediaScreen", name = "Media", width = 390, height = 760)
@Composable
fun MediaScreenMedia() = Phone(OldgeSkin.Media) { MediaScreen(it) }

@ViddikScreenshot(group = "MediaScreen", name = "Crystal", width = 390, height = 760)
@Composable
fun MediaScreenCrystal() = Phone(OldgeSkin.Crystal) { MediaScreen(it) }

@ViddikScreenshot(group = "EdgeNarrow", name = "Toxic", width = 320, height = 680)
@Composable
fun EdgeNarrowToxic() = Phone(OldgeSkin.Toxic) { EdgeNarrowScreen(it) }

@ViddikScreenshot(group = "EdgeNarrow", name = "Media", width = 320, height = 680)
@Composable
fun EdgeNarrowMedia() = Phone(OldgeSkin.Media) { EdgeNarrowScreen(it) }

@ViddikScreenshot(group = "EdgeNarrow", name = "Crystal", width = 320, height = 680)
@Composable
fun EdgeNarrowCrystal() = Phone(OldgeSkin.Crystal) { EdgeNarrowScreen(it) }

@ViddikScreenshot(group = "EdgeScale", name = "Toxic", width = 390, height = 860)
@Composable
fun EdgeScaleToxic() = Phone(OldgeSkin.Toxic, fontScale = 2f) { EdgeScaleScreen(it) }

@ViddikScreenshot(group = "EdgeScale", name = "Media", width = 390, height = 860)
@Composable
fun EdgeScaleMedia() = Phone(OldgeSkin.Media, fontScale = 2f) { EdgeScaleScreen(it) }

@ViddikScreenshot(group = "EdgeScale", name = "Crystal", width = 390, height = 860)
@Composable
fun EdgeScaleCrystal() = Phone(OldgeSkin.Crystal, fontScale = 2f) { EdgeScaleScreen(it) }
