package io.github.youndie.oldge.type

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** B-75: [OldgeText] hands its layout to a caller that measures text, and draws the same either way. */
@OptIn(ExperimentalTestApi::class)
class OldgeTextLayoutTest {
    private val text = "Загрузка завершена, раздача продолжается в фоне"

    @Test
    fun the_layout_is_the_drawn_texts_and_the_callback_changes_nothing_drawn() =
        runComposeUiTest {
            var layout: TextLayoutResult? = null
            setContent {
                OldgeTheme {
                    Column {
                        OldgeText(text, Modifier.width(160.dp).testTag("plain"))
                        OldgeText(text, Modifier.width(160.dp).testTag("measured"), onTextLayout = { layout = it })
                    }
                }
            }
            val result = assertNotNull(layout, "onTextLayout was not called")
            assertTrue(result.lineCount > 1, "160 dp wraps the sentence, got ${result.lineCount} line")
            val bounds = onNodeWithTag("measured").getBoundsInRoot()
            with(density) {
                assertEquals((bounds.right - bounds.left).roundToPx(), result.size.width)
                assertEquals((bounds.bottom - bounds.top).roundToPx(), result.size.height)
            }
            val plain = onNodeWithTag("plain").captureToImage().toPixelMap()
            val measured = onNodeWithTag("measured").captureToImage().toPixelMap()
            assertEquals(plain.width to plain.height, measured.width to measured.height)
            for (x in 0 until plain.width) {
                for (y in 0 until plain.height) assertEquals(plain[x, y], measured[x, y], "pixel ($x, $y)")
            }
        }
}
