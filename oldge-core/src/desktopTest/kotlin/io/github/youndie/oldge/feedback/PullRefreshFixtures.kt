package io.github.youndie.oldge.feedback

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.containers.OldgeList
import io.github.youndie.oldge.containers.OldgeListItem
import io.github.youndie.oldge.containers.OldgePanel
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/PullRefresh/preview.html: `refreshing` starts true, so the
// reference is the refreshing state under reduced motion.

@Composable
internal fun Rows() =
    OldgeList(bare = true) {
        row {
            OldgeListItem(
                "Потяните вниз",
                subtitle = "Диск раскрутится и обновит список",
                icon = OldgeIcons.Refresh,
            )
        }
        row { OldgeListItem("Отчёт за квартал", icon = OldgeIcons.Doc, value = "09:14") }
        row { OldgeListItem("Скан.png", icon = OldgeIcons.Image, value = "вчера") }
    }

/** An `.og-panel` 220 px high round the pull, as the preview has it: the bare `div`, no body padding. */
@Composable
private fun PullRefreshDemo(
    skin: OldgeSkin,
    refreshing: Boolean = true,
) = OldgeDemo(skin) {
    OldgePanel(Modifier.height(PANEL), flush = true) {
        OldgePullRefresh(refreshing, {}, height = SCROLL) { Rows() }
    }
}

private val PANEL = 220.dp
private val SCROLL = 164.dp

@ViddikScreenshot(group = "PullRefresh", name = "Toxic", width = 390, height = 260)
@Composable
fun PullRefreshToxic() = PullRefreshDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "PullRefresh", name = "Media", width = 390, height = 260)
@Composable
fun PullRefreshMedia() = PullRefreshDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "PullRefresh", name = "Crystal", width = 390, height = 260)
@Composable
fun PullRefreshCrystal() = PullRefreshDemo(OldgeSkin.Crystal)

// At rest: the disc folded away and the list at the top of the panel.
@ViddikScreenshot(group = "PullRefreshStates", name = "At rest Toxic", width = 390, height = 260)
@Composable
fun PullRefreshStatesToxic() = PullRefreshDemo(OldgeSkin.Toxic, refreshing = false)

@ViddikScreenshot(group = "PullRefreshStates", name = "At rest Media", width = 390, height = 260)
@Composable
fun PullRefreshStatesMedia() = PullRefreshDemo(OldgeSkin.Media, refreshing = false)

@ViddikScreenshot(group = "PullRefreshStates", name = "At rest Crystal", width = 390, height = 260)
@Composable
fun PullRefreshStatesCrystal() = PullRefreshDemo(OldgeSkin.Crystal, refreshing = false)
