package io.github.youndie.oldge.actions

import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeColors
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * B-77: a Danger button is filled in its skin's `danger-*`, not the accent, and its label is the dark
 * `on-danger`, in every skin.
 */
@OptIn(ExperimentalTestApi::class)
class DangerButtonBehaviourTest {
    private fun distance(
        a: Color,
        b: Color,
    ) = (a.red - b.red) * (a.red - b.red) + (a.green - b.green) * (a.green - b.green) +
        (a.blue - b.blue) * (a.blue - b.blue)

    @Test
    fun the_fill_below_the_gloss_is_the_skins_danger_not_its_accent() {
        for (skin in OldgeSkin.entries) {
            runComposeUiTest {
                lateinit var c: OldgeColors
                setContent {
                    OldgeTheme(skin = skin) {
                        c = OldgeTheme.colors
                        Row {
                            OldgeButton("Удалить", {}, Modifier.testTag("danger"), variant = OldgeButtonVariant.Danger)
                        }
                    }
                }
                val px = onNodeWithTag("danger").captureToImage().toPixelMap()
                // Left of the label, below the gloss band (the top 36 %): the gradient between
                // `danger-fill` at 55 % and `danger-lo` at the bottom.
                val fill = px[px.width / 16, px.height * 3 / 4]
                val toDanger = minOf(distance(fill, c.dangerFill), distance(fill, c.dangerLo))
                val toAccent = minOf(distance(fill, c.accent), distance(fill, c.accentLo))
                assertTrue(toDanger < toAccent / 10, "$skin: the fill $fill is not the danger gradient")
            }
        }
    }

    @Test
    fun the_label_is_on_danger() {
        for (skin in OldgeSkin.entries) {
            runComposeUiTest {
                lateinit var c: OldgeColors
                setContent {
                    OldgeTheme(skin = skin) {
                        c = OldgeTheme.colors
                        Row {
                            OldgeButton("Ш", {}, Modifier.testTag("danger"), variant = OldgeButtonVariant.Danger)
                        }
                    }
                }
                val px = onNodeWithTag("danger").captureToImage().toPixelMap()
                // The darkest pixel is the glyph's ink; nothing else on the button is near black.
                var darkest = Color.White
                for (x in 0 until px.width) {
                    for (y in 0 until px.height) {
                        val p = px[x, y]
                        if (p.red + p.green + p.blue < darkest.red + darkest.green + darkest.blue) darkest = p
                    }
                }
                assertTrue(distance(darkest, c.onDanger) < 0.01f, "$skin: the label ink $darkest is not on-danger")
            }
        }
    }
}
