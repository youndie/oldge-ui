package io.github.youndie.oldge.tokens

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * No hand-written Kotlin in the library spells a value the token layer holds (research D4, B-05):
 * no colour literal at all, no dp literal equal to a spacing or radius token, no duration equal to a
 * duration token. Generated files are the one place such literals live.
 */
class NoTokenLiteralsTest {
    private val generated = setOf("tokens/OldgeTokens.kt", "type/FontCoverage.kt")
    private val root = File("src/commonMain/kotlin/io/github/youndie/oldge")

    private val lengths = (OldgeTokenIndex.spacing.values + OldgeTokenIndex.radius.values).map { it.value }.toSet()
    private val durations = OldgeTokenIndex.duration.values.toSet()

    @Test
    fun hand_written_sources_spell_no_token_value() {
        val offences =
            root
                .walkTopDown()
                .filter { it.extension == "kt" && it.relativeTo(root).invariantSeparatorsPath !in generated }
                .flatMap { file ->
                    file.readLines().withIndex().flatMap { (i, line) ->
                        val code = line.substringBefore("//")
                        buildList {
                            if (Regex("""Color\(0x""").containsMatchIn(code)) add("colour literal")
                            Regex("""\b(\d+(?:\.\d+)?)\.dp\b""").findAll(code).forEach {
                                if (it.groupValues[1].toFloat() in lengths) add("${it.value} equals a length token")
                            }
                            Regex("""(?:tween\(|durationMillis\s*=\s*)(\d+)""").findAll(code).forEach {
                                if (it.groupValues[1].toInt() in
                                    durations
                                ) {
                                    add("${it.groupValues[1]} ms equals a duration token")
                                }
                            }
                        }.map { "${file.relativeTo(root)}:${i + 1}: $it" }
                    }
                }.toList()
        assertEquals(emptyList(), offences)
    }

    /** The control: the scan finds the generated file's own literals when it is not excluded. */
    @Test
    fun the_scan_sees_the_generated_file() {
        val text = File(root, "tokens/OldgeTokens.kt").readText()
        assert(Regex("""Color\(0x""").containsMatchIn(text)) { "the generated file holds no colour literal to find" }
    }
}
