package io.github.youndie.oldge.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * B-89: a read-only value too long for its field is cut as the caller asks while the field is at
 * rest, and shown whole once focused; a screen reader hears the whole value either way.
 */
@OptIn(ExperimentalTestApi::class)
class TextFieldOverflowTest {
    private val path = "/home/user/Downloads/iso/ubuntu-24.04"

    private fun same(
        a: PixelMap,
        b: PixelMap,
    ): Boolean =
        a.width == b.width && a.height == b.height &&
            (0 until a.width).all { x -> (0 until a.height).all { y -> a[x, y] == b[x, y] } }

    @Test
    fun a_read_only_value_is_cut_as_asked_at_rest_and_whole_when_focused() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column(Modifier.width(180.dp)) {
                        OldgeTextField("End", path, {}, Modifier.testTag("end"), readOnly = true, showLabel = false)
                        OldgeTextField(
                            "Middle",
                            path,
                            {},
                            Modifier.testTag("middle"),
                            readOnly = true,
                            showLabel = false,
                            overflow = TextOverflow.MiddleEllipsis,
                        )
                        for ((tag, cut) in listOf(
                            "ellipsis" to TextOverflow.Ellipsis,
                            "start" to TextOverflow.StartEllipsis,
                        )) {
                            OldgeTextField(
                                tag,
                                path,
                                {},
                                Modifier.testTag(tag),
                                readOnly = true,
                                showLabel = false,
                                overflow = cut,
                            )
                        }
                    }
                }
            }

            fun image(tag: String) = onNodeWithTag(tag).captureToImage().toPixelMap()
            // Four drawings: the input's own cut, and an ellipsis at the end, at the start and in the
            // middle. Left to Compose Desktop, the last three are drawn alike.
            val tags = listOf("end", "ellipsis", "start", "middle")
            for (a in tags) {
                for (b in tags) if (a < b) assertFalse(same(image(a), image(b)), "$a is drawn as $b")
            }
            onNodeWithContentDescription("Middle")
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString(path)))

            onNodeWithContentDescription("End").requestFocus()
            val endFocused = image("end")
            onNodeWithContentDescription("Middle").requestFocus()
            assertTrue(same(endFocused, image("middle")), "focused, the value is not shown whole")
            onNodeWithContentDescription("Middle")
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString(path)))
        }

    @Test
    fun an_editable_field_keeps_its_own_cut() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column(Modifier.width(180.dp)) {
                        OldgeTextField("A", path, {}, Modifier.testTag("plain"), showLabel = false)
                        OldgeTextField(
                            "B",
                            path,
                            {},
                            Modifier.testTag("asked"),
                            showLabel = false,
                            overflow = TextOverflow.MiddleEllipsis,
                        )
                    }
                }
            }
            val plain = onNodeWithTag("plain").captureToImage().toPixelMap()
            val asked = onNodeWithTag("asked").captureToImage().toPixelMap()
            assertTrue(same(plain, asked), "an editable field took the overflow")
        }
}
