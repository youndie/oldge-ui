package io.github.youndie.oldge.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeColors
import kotlin.test.Test
import kotlin.test.assertEquals

/** B-85: a highlighted field's well is ringed in `accent-ink`; an error still rings it in `danger`. */
@OptIn(ExperimentalTestApi::class)
class TextFieldHighlightedTest {
    @Test
    fun a_highlighted_well_is_ringed_in_accent_ink_and_an_error_wins() {
        for (skin in OldgeSkin.entries) {
            runComposeUiTest {
                lateinit var c: OldgeColors
                setContent {
                    OldgeTheme(skin = skin) {
                        c = OldgeTheme.colors
                        Column(Modifier.width(240.dp)) {
                            OldgeTextField("A", "6881", {}, Modifier.testTag("plain"), showLabel = false)
                            OldgeTextField(
                                "B",
                                "6881",
                                {},
                                Modifier.testTag("lit"),
                                showLabel = false,
                                highlighted = true,
                            )
                            OldgeTextField(
                                "C",
                                "68",
                                {},
                                Modifier.testTag("bad"),
                                showLabel = false,
                                error = "x",
                                highlighted = true,
                            )
                        }
                    }
                }

                // The middle of the well's left border: a straight run of the 1 dp border, clear of
                // the corners and of the sunken shadow, which is drawn inside it.
                fun border(tag: String): Color {
                    val node = onNodeWithTag(tag)
                    val px = node.captureToImage().toPixelMap()
                    return px[0, WELL_MID]
                }
                assertEquals(c.edge, border("plain"), "$skin: plain")
                assertEquals(c.accentInk, border("lit"), "$skin: highlighted")
                assertEquals(c.danger, border("bad"), "$skin: highlighted with an error")
            }
        }
    }

    private companion object {
        /** Half the 44 dp well, at the test's density of 1. */
        const val WELL_MID = 22
    }
}
