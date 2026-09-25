package io.github.youndie.oldge.behaviour

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.youndie.oldge.actions.OldgeSegment
import io.github.youndie.oldge.actions.OldgeSegmented
import io.github.youndie.oldge.containers.OldgeActionTile
import io.github.youndie.oldge.containers.OldgeList
import io.github.youndie.oldge.containers.OldgeListItem
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * `hyphens: auto` (B-59). bundle.css asks for it on four texts: `.og-item__title` and
 * `.og-item__value`, `.og-tile__text` (the tile's title and its description inherit it) and
 * `.og-seg__opt`. The library passes `Hyphens.Auto` on each of them. Only Android's layout honours
 * it; the Skia paragraph that desktop and iOS draw with has no hyphenation (research §1.10), so what
 * is held here is the request, plus a canary on the platform limit.
 */
@OptIn(ExperimentalTestApi::class)
class HyphenationTest {
    @Test
    fun the_texts_the_css_hyphenates_ask_for_it_and_a_subtitle_does_not() {
        runComposeUiTest {
            setContent {
                OldgeTheme {
                    Column {
                        OldgeList {
                            row { OldgeListItem("Werbung", subtitle = "Untertitel", value = "Wert") }
                        }
                        OldgeActionTile("Kachel", OldgeIcons.Doc, {}, description = "Beschreibung")
                        OldgeSegmented(listOf(OldgeSegment(1, "Eins"), OldgeSegment(2, "Zwei")), 1, {}, "Wahl")
                    }
                }
            }

            fun hyphens(text: String): Hyphens {
                val results = mutableListOf<TextLayoutResult>()
                val get = onNodeWithText(text, useUnmergedTree = true).fetchSemanticsNode().config
                assertTrue(get.getOrNull(SemanticsActions.GetTextLayoutResult)?.action?.invoke(results) == true, text)
                return results
                    .single()
                    .layoutInput.style.hyphens
            }
            val asked = listOf("Werbung", "Wert", "Kachel", "Beschreibung", "Eins").associateWith { hyphens(it) }
            assertEquals(asked.mapValues { Hyphens.Auto }, asked)
            // The control: `.og-item__sub` does not hyphenate, so the check reads the style it claims to.
            assertEquals(Hyphens.Unspecified, hyphens("Untertitel"))
        }
    }

    /**
     * Desktop Skia breaks a German compound too narrow for its column exactly as it does without
     * hyphenation: `Hyphens.Auto` changes nothing. When it starts to, this fails — then EdgeNarrow's
     * «Personalisierte Wer- / bung» (B-39) can match Chrome, and research §1.10 is out of date.
     */
    @Test
    fun desktop_skia_does_not_hyphenate_yet() {
        fun lines(hyphens: Hyphens): List<Int> {
            var result: TextLayoutResult? = null
            runComposeUiTest {
                setContent {
                    BasicText(
                        COMPOUND,
                        Modifier.width(90.dp),
                        style = TextStyle(fontSize = 16.sp, hyphens = hyphens, localeList = LocaleList("de")),
                        onTextLayout = { result = it },
                    )
                }
                waitForIdle()
            }
            val r = result!!
            return (0 until r.lineCount).map { r.getLineEnd(it) }
        }
        val plain = lines(Hyphens.None)
        // The column does force breaks inside words, or the two could only agree.
        assertTrue(plain.size >= MANY_LINES, "the compound fits on ${plain.size} lines; nothing to hyphenate")
        assertEquals(plain, lines(Hyphens.Auto), "desktop Skia now hyphenates; revisit B-59")
    }
}

private const val COMPOUND = "Personalisierte Werbung anzeigen Donaudampfschifffahrtsgesellschaft"
private const val MANY_LINES = 5
