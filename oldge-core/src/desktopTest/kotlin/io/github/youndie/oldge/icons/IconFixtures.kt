package io.github.youndie.oldge.icons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import io.github.youndie.oldge.harness.PortableTextStyle
import io.github.youndie.oldge.material.skinBody
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.type.OldgeText
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/Icon/preview.html: `og-demo og-demo--row og-demo--wide`, a
// wrapping row with a 12 px gap, each icon 28 px over its name in `400 10px/12px var(--font-pixel)`
// in ink-muted, in a 56 px column.

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IconDemo(skin: OldgeSkin) =
    OldgeTheme(skin = skin, reducedMotion = true, platformTextStyle = PortableTextStyle) {
        Box(Modifier.fillMaxSize().skinBody()) {
            FlowRow(
                Modifier.padding(OldgeTheme.spacing.space4),
                horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space3),
                verticalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space3),
            ) {
                val label = OldgeTheme.type.pixelTag.copy(letterSpacing = 0.em, color = OldgeTheme.colors.inkMuted)
                for ((name, _) in OldgeIconPaths) {
                    Column(
                        Modifier.width(56.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space1),
                    ) {
                        OldgeIcon(
                            oldgeIcon(name, OldgeIconPaths.getValue(name)),
                            contentDescription = null,
                            size = 28.dp,
                        )
                        OldgeText(name, style = label, softWrap = false)
                    }
                }
            }
        }
    }

@ViddikScreenshot(group = "Icon", name = "Toxic", width = 390, height = 580)
@Composable
fun IconToxic() = IconDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Icon", name = "Media", width = 390, height = 580)
@Composable
fun IconMedia() = IconDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Icon", name = "Crystal", width = 390, height = 580)
@Composable
fun IconCrystal() = IconDemo(OldgeSkin.Crystal)
