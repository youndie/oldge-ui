package io.github.youndie.oldge.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/Badge/preview.html and Avatar/preview.html, string for string;
// each row is `display: flex; gap: 8px; flex-wrap: wrap; align-items: center`.

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Wrap(content: @Composable () -> Unit) =
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
        verticalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) { content() }

@Composable
private fun BadgeDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        Wrap {
            OldgeBadge("Черновик")
            OldgeBadge("Новое", tone = OldgeBadgeTone.Accent)
            OldgeBadge("12", tone = OldgeBadgeTone.Accent, count = true)
            OldgeBadge("Синхр.", tone = OldgeBadgeTone.Success)
            OldgeBadge("Мало места", tone = OldgeBadgeTone.Warning)
            OldgeBadge("Ошибка", tone = OldgeBadgeTone.Danger)
        }
    }

@Composable
private fun AvatarDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        Wrap {
            OldgeAvatar(name = "Анна Ким", size = OldgeAvatarSize.Small)
            OldgeAvatar(name = "Павел Вотяков", status = OldgeAvatarStatus.Online)
            OldgeAvatar(name = "Иван Ли", status = OldgeAvatarStatus.Busy)
            OldgeAvatar(name = "Мария Орлова", size = OldgeAvatarSize.Large, status = OldgeAvatarStatus.Away)
            OldgeAvatar(icon = OldgeIcons.User)
        }
    }

/**
 * What the preview does not show: an avatar with a picture (`src`), and the online lamp at the height
 * of its pulse, the loop frozen at half.
 */
@Composable
private fun AvatarStatesDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        // A stand-in for a photograph: its colours are the picture's, not the design's.
        val photo = remember { BrushPainter(Brush.linearGradient(listOf(Color(0xFFE0A070), Color(0xFF503020)))) }
        Wrap {
            OldgeAvatar(name = "Анна Ким", image = photo, status = OldgeAvatarStatus.Busy)
            OldgeAvatar(name = "Анна Ким", image = photo, size = OldgeAvatarSize.Large)
            CompositionLocalProvider(LocalOldgeLoopPhase provides HALF) {
                OldgeAvatar(name = "Павел Вотяков", size = OldgeAvatarSize.Large, status = OldgeAvatarStatus.Online)
            }
        }
    }

private const val HALF = 0.5f

@ViddikScreenshot(group = "Badge", name = "Toxic", width = 390, height = 90)
@Composable
fun BadgeToxic() = BadgeDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Badge", name = "Media", width = 390, height = 90)
@Composable
fun BadgeMedia() = BadgeDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Badge", name = "Crystal", width = 390, height = 90)
@Composable
fun BadgeCrystal() = BadgeDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "Avatar", name = "Toxic", width = 390, height = 100)
@Composable
fun AvatarToxic() = AvatarDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Avatar", name = "Media", width = 390, height = 100)
@Composable
fun AvatarMedia() = AvatarDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Avatar", name = "Crystal", width = 390, height = 100)
@Composable
fun AvatarCrystal() = AvatarDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "AvatarStates", name = "Picture and pulse Toxic", width = 390, height = 100)
@Composable
fun AvatarStatesToxic() = AvatarStatesDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "AvatarStates", name = "Picture and pulse Media", width = 390, height = 100)
@Composable
fun AvatarStatesMedia() = AvatarStatesDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "AvatarStates", name = "Picture and pulse Crystal", width = 390, height = 100)
@Composable
fun AvatarStatesCrystal() = AvatarStatesDemo(OldgeSkin.Crystal)
