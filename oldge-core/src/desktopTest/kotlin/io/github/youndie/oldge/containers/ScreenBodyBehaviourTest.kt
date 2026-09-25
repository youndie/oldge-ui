package io.github.youndie.oldge.containers

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.theme.LocalOldgeContentColor
import io.github.youndie.oldge.theme.LocalOldgeTextStyle
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/** The rules of `.og-body` that are behaviour rather than look (B-50). */
@OptIn(ExperimentalTestApi::class)
class ScreenBodyBehaviourTest {
    @Test
    fun its_content_reads_ink_body_text_whatever_the_container_provides() =
        runComposeUiTest {
            var style = TextStyle.Default
            var content = Color.Unspecified
            var ink = Color.Unspecified
            var body = TextStyle.Default
            setContent {
                OldgeTheme(skin = OldgeSkin.Media) {
                    ink = OldgeTheme.colors.ink
                    body = OldgeTheme.type.body
                    // What a Banner or a pill hands its content: another colour and another size.
                    CompositionLocalProvider(
                        LocalOldgeTextStyle provides TextStyle(color = Color.Red, fontSize = 40.sp),
                        LocalOldgeContentColor provides Color.Red,
                    ) {
                        OldgeScreenBody {
                            style = LocalOldgeTextStyle.current
                            content = LocalOldgeContentColor.current
                        }
                    }
                }
            }
            waitForIdle()
            assertEquals(ink, style.color)
            assertEquals(body.fontSize, style.fontSize)
            assertEquals(body.lineHeight, style.lineHeight)
            assertEquals(ink, content)
        }

    @Test
    fun the_grain_is_there_unless_the_theme_turns_texture_off() {
        fun grab(texture: Boolean): List<Any> {
            var pixels = emptyList<Any>()
            runComposeUiTest {
                setContent {
                    OldgeTheme(skin = OldgeSkin.Toxic, texture = texture) {
                        OldgeScreenBody(Modifier.testTag("b").size(160.dp, 40.dp)) {}
                    }
                }
                val m = onNodeWithTag("b").captureToImage().toPixelMap()
                pixels = (0 until m.width).map { x -> m[x, 0] }
            }
            return pixels
        }
        // Along the top row the gradient is one colour, so only the grain can vary it.
        assertEquals(1, grab(texture = false).distinct().size, "texture off still drew grain")
        assertNotEquals(1, grab(texture = true).distinct().size, "texture on drew no grain")
    }
}
