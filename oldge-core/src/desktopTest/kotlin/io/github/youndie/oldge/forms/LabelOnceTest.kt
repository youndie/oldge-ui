package io.github.youndie.oldge.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test

/**
 * B-81: a Select's and a CodeInput's label is heard once, as the control's name, as a TextField's is
 * since B-72; the visible label above the control is drawn but kept out of the tree.
 */
@OptIn(ExperimentalTestApi::class)
class LabelOnceTest {
    @Test
    fun a_selects_label_is_heard_once() =
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    OldgeSelect(
                        "Город",
                        listOf(OldgeSelectOption("msk", "Москва"), OldgeSelectOption("spb", "Петербург")),
                        "msk",
                        {},
                    )
                }
            }
            onAllNodes(hasContentDescription("Город") or hasText("Город"), useUnmergedTree = true).assertCountEquals(1)
        }

    @Test
    fun a_code_inputs_label_is_heard_once() =
        runComposeUiTest {
            setContent { OldgeTheme { Column { OldgeCodeInput("Код из СМС", "12", {}) } } }
            onAllNodes(
                hasContentDescription("Код из СМС") or hasText("Код из СМС"),
                useUnmergedTree = true,
            ).assertCountEquals(1)
        }
}
