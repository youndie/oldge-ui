package io.github.youndie.oldge.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import io.github.youndie.oldge.actions.OldgeSegment
import io.github.youndie.oldge.actions.OldgeSegmented
import io.github.youndie.oldge.containers.OldgeScreenBody
import io.github.youndie.oldge.forms.OldgeSelect
import io.github.youndie.oldge.forms.OldgeSelectOption
import io.github.youndie.oldge.forms.OldgeSwitch
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme

/** The nine pages of the design system the sample hosts (B-37…B-40), in its own order. */
public enum class SamplePage(
    public val label: String,
) {
    Launcher("Главный экран"),
    Feed("Лента"),
    Settings("Настройки"),
    Auth("Вход"),
    Chat("Чат"),
    Inbox("Входящие"),
    Media("Медиа"),
    EdgeNarrow("Край: узкий экран"),
    EdgeScale("Край: шрифт 200 %"),
}

/**
 * The sample app, the same on the desktop, Android and iOS (B-41): the nine pages behind a page
 * choice, a skin switcher and a text-size switch, which sets the system font to 200 % for the page
 * under it as EdgeScale's fixture does. Settings' own skin choice is the app's.
 *
 * The switches are themselves this library's components, at the platform's own text size, so that
 * a page at 200 % never pushes them off the screen.
 */
@Composable
public fun OldgeSampleApp(initialPage: SamplePage = SamplePage.Launcher) {
    var skin by remember { mutableStateOf(OldgeSkin.Toxic) }
    var page by remember { mutableStateOf(initialPage) }
    var large by remember { mutableStateOf(false) }
    OldgeTheme(skin = skin) {
        val s = OldgeTheme.spacing
        OldgeScreenBody(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))) {
                Column(
                    Modifier.fillMaxWidth().padding(s.space3),
                    verticalArrangement = Arrangement.spacedBy(s.space2),
                ) {
                    OldgeSelect(
                        "Экран",
                        SamplePage.entries.map { OldgeSelectOption(it, it.label) },
                        page,
                        { page = it },
                        Modifier.fillMaxWidth(),
                    )
                    OldgeSegmented(
                        OldgeSkin.entries.map { OldgeSegment(it, it.name) },
                        skin,
                        { skin = it },
                        "Скин",
                        Modifier.fillMaxWidth(),
                    )
                    OldgeSwitch("Шрифт 200 %", large, { large = it })
                }
                val base = LocalDensity.current
                val scale = if (large || page == SamplePage.EdgeScale) LARGE else base.fontScale
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    CompositionLocalProvider(LocalDensity provides Density(base.density, scale)) {
                        Page(page, skin) { skin = it }
                    }
                }
            }
        }
    }
}

@Composable
private fun Page(
    page: SamplePage,
    skin: OldgeSkin,
    onSkinChange: (OldgeSkin) -> Unit,
) {
    val fill = Modifier.fillMaxSize()
    when (page) {
        SamplePage.Launcher -> LauncherScreen(fill)
        SamplePage.Feed -> FeedScreen(fill, more = true)
        SamplePage.Settings -> SettingsScreen(skin, onSkinChange, fill)
        SamplePage.Auth -> AuthScreen(fill)
        SamplePage.Chat -> ChatScreen(fill)
        SamplePage.Inbox -> InboxScreen(fill)
        SamplePage.Media -> MediaScreen(fill)
        SamplePage.EdgeNarrow -> EdgeNarrowScreen(fill)
        SamplePage.EdgeScale -> EdgeScaleScreen(fill)
    }
}

private const val LARGE = 2f
