package io.github.youndie.oldge.theme

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import io.github.youndie.oldge.harness.PortableTextStyle
import kotlin.test.Test
import kotlin.test.assertEquals

/** `OldgeTheme(platformTextStyle = …)` reaches every style of the typography, and none without it (B-52). */
@OptIn(ExperimentalTestApi::class)
class PlatformTextStyleTest {
    @Test
    fun the_theme_pins_every_type_style_and_leaves_them_alone_by_default() {
        assertEquals(everyStyle(PortableTextStyle).mapValues { PortableTextStyle }, everyStyle(PortableTextStyle))
        assertEquals(everyStyle(null).mapValues { null }, everyStyle(null))
    }

    private fun everyStyle(pinned: PlatformTextStyle?): Map<String, PlatformTextStyle?> {
        var styles = emptyMap<String, PlatformTextStyle?>()
        runComposeUiTest {
            setContent {
                OldgeTheme(platformTextStyle = pinned) {
                    val t = OldgeTheme.type
                    styles =
                        mapOf<String, TextStyle>(
                            "display" to t.display,
                            "heading" to t.heading,
                            "title" to t.title,
                            "body" to t.body,
                            "bodyStrong" to t.bodyStrong,
                            "button" to t.button,
                            "label" to t.label,
                            "caption" to t.caption,
                            "readout" to t.readout,
                            "readoutSm" to t.readoutSm,
                            "pixelTag" to t.pixelTag,
                            "the inherited text style" to LocalOldgeTextStyle.current,
                        ).mapValues { it.value.platformStyle }
                }
            }
            waitForIdle()
        }
        return styles
    }
}
