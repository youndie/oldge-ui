package io.github.youndie.oldge.harness

import io.github.youndie.viddik.generated.GeneratedViddikRegistry
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * A fixture with a design reference is the reference's size (B-08). The size is written into the
 * annotation, because an annotation argument must be a constant; this is what keeps the copy honest.
 * A mismatch would otherwise surface as a SIZE_MISMATCH line in a parity report nobody gates on.
 */
class FixtureSizeTest {
    private val manifest =
        Json
            .parseToJsonElement(File("src/desktopTest/snapshots/design/manifest.json").readText())
            .jsonObject
            .getValue("references")
            .jsonObject

    @Test
    fun every_fixture_with_a_reference_has_the_references_size() {
        val checked =
            GeneratedViddikRegistry.components.mapNotNull { fixture ->
                val stem = "${fixture.group}_${fixture.name}".replace(Regex("[^A-Za-z0-9_.-]"), "_")
                val reference = manifest[stem]?.jsonObject ?: return@mapNotNull null
                val expected =
                    reference.getValue("width").jsonPrimitive.int to reference.getValue("height").jsonPrimitive.int
                assertEquals(
                    expected,
                    fixture.width to fixture.height,
                    "$stem: fixture size against design/manifest.json",
                )
                stem
            }
        assertTrue(checked.size >= 3, "only ${checked.size} fixtures have a reference: $checked")
    }
}
