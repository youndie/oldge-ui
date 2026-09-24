package io.github.youndie.oldge.type

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Every character the design system's previews put on screen, and every character a fixture here
 * renders (a file carrying `@ViddikScreenshot`), is drawable by the bundled faces of each family — so no glyph is ever the host's.
 *
 * What cannot be known statically is which family a given string is set in, so each family is held
 * to the whole set, and the exceptions are named one by one below with the reason. A new exception
 * is a design decision, not a way to get green.
 */
class DesignStringCoverageTest {
    private val strings: Set<Int> by lazy {
        (designStrings() + fixtureStrings()).flatMapTo(sortedSetOf()) { it.codePoints().toArray().asList() }
    }

    @Test
    fun the_design_system_and_the_fixtures_yield_a_non_trivial_character_set() {
        // Without this the tests below would pass over an empty scan.
        assertTrue(strings.size > 120, "scanned only ${strings.size} code points")
        assertTrue('Ж'.code in strings && 'щ'.code in strings, "the scan missed the Cyrillic of the previews")
    }

    @Test
    fun ui_draws_everything() =
        assertCovered("ui", listOf("dejavu_sans_condensed.ttf", "dejavu_sans_condensed_bold.ttf"), all = true)

    @Test
    fun title_draws_everything_but_its_named_exceptions() = assertCovered("title", listOf("fira_sans_bold.ttf"))

    @Test
    fun lcd_and_its_companion_draw_everything_but_their_named_exceptions() =
        assertCovered("lcd", listOf("share_tech_mono.ttf", "pt_mono.ttf"), union = true)

    @Test
    fun pixel_and_its_companion_draw_everything_but_their_named_exceptions() =
        assertCovered("pixel", listOf("silkscreen.ttf", "tiny5.ttf"), union = true)

    /**
     * Characters a family may lack, each with the reason. `⌘` appears once in the design system, in
     * Menu's shortcut hint «⌘O», which is set in `ui`.
     */
    private val exceptions: Map<String, Set<Int>> =
        mapOf(
            "title" to setOf('⌘'.code),
            "lcd" to setOf('⌘'.code),
            "pixel" to setOf('⌘'.code),
        )

    private fun assertCovered(
        family: String,
        files: List<String>,
        all: Boolean = false,
        union: Boolean = false,
    ) {
        val fonts = files.map(BundledFonts::awt)
        val missing =
            strings.filter { cp ->
                cp >= 0x20 &&
                    cp !in exceptions[family].orEmpty() &&
                    if (union) fonts.none { it.canDisplay(cp) } else fonts.any { !it.canDisplay(cp) }
            }
        assertEquals(
            emptyList(),
            missing.map { "U+%04X %s".format(it, String(Character.toChars(it))) },
            "$family (${files.joinToString()}) cannot draw these; a glyph drawn by the host differs on every machine",
        )
        if (all) {
            assertTrue(
                exceptions[family].isNullOrEmpty(),
                "$family is held to everything and may not have exceptions",
            )
        }
    }

    private fun designStrings(): List<String> =
        File("../reference/design-system/components")
            .walkTopDown()
            // The Cover is the artifact's own cover page, not a component, and is never rendered here.
            .filter { it.name == "preview.html" && it.parentFile.name != "Cover" }
            .toList()
            .also { assertEquals(59, it.size, "50 component previews and 9 pages, research §1.1") }
            .flatMap { literals(it.readText()) }

    private fun fixtureStrings(): List<String> =
        File("src/desktopTest/kotlin")
            .walkTopDown()
            .filter { it.extension == "kt" && "@ViddikScreenshot" in it.readText() }
            .flatMap { file ->
                Regex("\"((?:[^\"\\\\\\n]|\\\\.)*)\"").findAll(file.readText()).map { it.groupValues[1] }
            }.toList()

    private fun literals(source: String): List<String> =
        Regex("'((?:[^'\\\\\\n]|\\\\.)*)'|\"((?:[^\"\\\\\\n]|\\\\.)*)\"")
            .findAll(source)
            .map { it.groupValues[1] + it.groupValues[2] }
            .toList() +
            Regex(">([^<>]+)<").findAll(source).map { it.groupValues[1] }.toList()
}
