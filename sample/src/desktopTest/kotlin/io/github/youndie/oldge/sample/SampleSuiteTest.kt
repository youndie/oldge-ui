package io.github.youndie.oldge.sample

import io.github.youndie.viddik.generated.GeneratedViddikRegistry
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The sample's screenshot suite checked as a set, as oldge-core's `ScreenshotSuiteTest` checks its own
 * (B-09, B-40). Every fixture has its golden and its page reference, and every golden and every
 * reference has a fixture. oldge-core counts a page as built from this module's goldens, so a golden
 * here must stand for a fixture.
 */
class SampleSuiteTest {
    private val snapshots = File("src/desktopTest/snapshots")
    private val design = File(snapshots, "design")

    private val fixtures =
        GeneratedViddikRegistry.components
            .map { "${it.group}_${it.name}".replace(Regex("[^A-Za-z0-9_.-]"), "_") }
            .toSet()

    private fun pngs(dir: File) =
        dir
            .listFiles { f -> f.isFile && f.name.endsWith(".png") }
            .orEmpty()
            .map { it.name.removeSuffix(".png") }
            .toSet()

    @Test
    fun every_page_fixture_has_its_golden_and_its_reference_and_nothing_is_left_over() {
        assertTrue(
            fixtures.size >= NINE_PAGES_THREE_SKINS,
            "only ${fixtures.size} fixtures: KSP found fewer than the pages",
        )
        assertEquals(emptySet(), fixtures - pngs(snapshots), "fixtures with no golden")
        assertEquals(emptySet(), pngs(snapshots) - fixtures, "goldens with no fixture")
        assertEquals(emptySet(), fixtures - pngs(design), "fixtures with no page reference")
        assertEquals(emptySet(), pngs(design) - fixtures, "page references no fixture is compared with")
    }
}

private const val NINE_PAGES_THREE_SKINS = 27
