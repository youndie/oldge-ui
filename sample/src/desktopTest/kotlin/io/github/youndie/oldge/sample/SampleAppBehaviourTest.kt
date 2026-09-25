package io.github.youndie.oldge.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test

/** The sample app's own switches (B-41): the page choice, and the skin shared with Settings. */
@OptIn(ExperimentalTestApi::class)
class SampleAppBehaviourTest {
    @Test
    fun the_page_choice_shows_the_page_and_a_skin_chosen_in_settings_is_the_apps() =
        runComposeUiTest {
            setContent { Box(Modifier.size(420.dp, 900.dp)) { OldgeSampleApp() } }
            onNodeWithText("Центр задач", useUnmergedTree = true).assertExists()
            // The page choice opens its menu and takes Settings.
            onNodeWithText(SamplePage.Launcher.label, useUnmergedTree = true).performClick()
            onNodeWithText(SamplePage.Settings.label, useUnmergedTree = true).performClick()
            onNodeWithText("Синхронизировать через", useUnmergedTree = true).assertExists()

            // Choosing Crystal in Settings' own Segmented makes it the app's: the app's switcher says so too.
            val crystal =
                hasText("Crystal") and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)
            onAllNodes(crystal)[1].performClick()
            onAllNodes(crystal)[0].assertIsSelected()
            onAllNodes(crystal)[1].assertIsSelected()
        }

    /**
     * The app's feed is longer than the design's page (B-64): its last post starts out of view, and
     * scrolling brings it in. The design's FeedScreen, which the parity fixture renders, has no such
     * post.
     */
    @Test
    fun the_apps_feed_scrolls_to_posts_beyond_the_designs_two() {
        runComposeUiTest {
            setContent { Box(Modifier.size(420.dp, 900.dp)) { OldgeSampleApp() } }
            onNodeWithText(SamplePage.Launcher.label, useUnmergedTree = true).performClick()
            onNodeWithText(SamplePage.Feed.label, useUnmergedTree = true).performClick()
            onNodeWithText(
                LAST_POST,
                useUnmergedTree = true,
            ).assertIsNotDisplayed().performScrollTo().assertIsDisplayed()
        }
        runComposeUiTest {
            setContent { Box(Modifier.size(390.dp, 760.dp)) { OldgeTheme { FeedScreen() } } }
            onNodeWithText("Отпуск 2006", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithText(LAST_POST, useUnmergedTree = true).assertDoesNotExist()
        }
    }
}

private const val LAST_POST = "Сборник на диск"
