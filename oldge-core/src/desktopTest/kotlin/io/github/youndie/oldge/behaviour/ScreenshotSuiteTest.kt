package io.github.youndie.oldge.behaviour

import io.github.youndie.viddik.generated.GeneratedViddikRegistry
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * What guards the screenshot suite itself, in both directions (B-09).
 *
 * `viddikVerify` compares each fixture with its golden and `viddikDesignParity` each fixture with its
 * reference; neither says anything about the set. A fixture with no golden verifies green over
 * nothing, a golden with no fixture guards nothing, a reference nobody compares is decoration, and a
 * component nobody built is simply absent. This checks the set against the registry viddik's
 * processor emitted and against the design system's own list of components (research §1.1).
 */
class ScreenshotSuiteTest {
    private val snapshots = File("src/desktopTest/snapshots")
    private val design = File(snapshots, "design")
    private val designSystem = File("../reference/design-system/components")

    /**
     * Components and pages of the design system with a preview (and so references) but no parity
     * fixture yet. Every component item removes its names; B-40 ends the list empty. A name here
     * that has a fixture fails the suite, so the list cannot outlive the work.
     *
     * The nine pages are built in `sample` (B-37…B-40), not here; they stay on this list until the
     * page items decide where their fixtures live.
     */
    private val notYetBuilt =
        setOf(
            // Components (B-11…B-34).
            "Accordion",
            "ActionTile",
            "Avatar",
            "Badge",
            "Balloon",
            "Banner",
            "BottomNav",
            "BottomSheet",
            "Card",
            "CategoryTabs",
            "ChatBubble",
            "Composer",
            "Dialog",
            "EmptyState",
            "List",
            "ListItem",
            "ListSection",
            "Menu",
            "Meter",
            "NavDrawer",
            "PageDots",
            "Panel",
            "ProgressBar",
            "PullRefresh",
            "Readout",
            "SearchBar",
            "Skeleton",
            "Snackbar",
            "Spinner",
            "Stepper",
            "SwipeRow",
            "Tabs",
            "Tooltip",
            "TypingIndicator",
            "WindowBar",
            // Pages (B-37…B-40).
            "AuthScreen",
            "ChatScreen",
            "EdgeNarrow",
            "EdgeScale",
            "FeedScreen",
            "InboxScreen",
            "Launcher",
            "MediaScreen",
            "SettingsScreen",
        )

    /** Exported by the bundle without a preview of its own, so there is no reference to hold it to. */
    private val exportedWithoutPreview = setOf("ChipGroup")

    private val skins = listOf("Toxic", "Media", "Crystal")

    /** viddik's rule: group and name joined by `_`, every character outside `[A-Za-z0-9_.-]` replaced by `_`. */
    private fun stem(
        group: String,
        name: String,
    ) = "${group}_$name".replace(Regex("[^A-Za-z0-9_.-]"), "_")

    private val fixtures = GeneratedViddikRegistry.components.map { stem(it.group, it.name) }.toSet()
    private val fixtureGroups = GeneratedViddikRegistry.components.map { it.group }.toSet()

    private fun pngs(dir: File) =
        dir
            .listFiles { f -> f.isFile && f.name.endsWith(".png") }
            .orEmpty()
            .map { it.name.removeSuffix(".png") }
            .toSet()

    /** Previewed components and pages of the design system, `Cover` excepted. */
    private val previewed =
        designSystem
            .listFiles { f -> f.isDirectory && File(f, "preview.html").isFile && f.name != "Cover" }
            .orEmpty()
            .map { it.name }
            .toSet()

    /** References rendered from this repository's own probes (scripts/probes), which have no component. */
    private val probeReferences: Set<String> by lazy {
        val manifest =
            Json
                .parseToJsonElement(
                    File(design, "manifest.json").readText(),
                ).jsonObject
                .getValue("references")
                .jsonObject
        manifest.filterValues { it.jsonObject.containsKey("source") }.keys
    }

    @Test
    fun the_suite_is_not_empty() {
        assertTrue(
            GeneratedViddikRegistry.components.isNotEmpty(),
            "the registry is empty: KSP found no @ViddikScreenshot",
        )
        assertTrue(
            previewed.size == 59,
            "found ${previewed.size} previews in the design system, research §1.1 counts 59",
        )
    }

    @Test
    fun every_fixture_has_a_golden_and_every_golden_has_a_fixture() {
        val goldens = pngs(snapshots)
        assertEquals(
            emptySet(),
            fixtures - goldens,
            "fixtures with no golden: they verify green because there is nothing to compare to",
        )
        assertEquals(
            emptySet(),
            goldens - fixtures,
            "goldens with no fixture: left behind by a deleted or renamed fixture, guarding nothing",
        )
    }

    @Test
    fun every_reference_has_a_fixture_unless_its_component_is_not_built_yet() {
        val orphans =
            (pngs(design) - fixtures).filterNot { it.substringBeforeLast('_') in notYetBuilt }
        assertEquals(emptySet(), orphans.toSet(), "references no fixture is compared with")
    }

    @Test
    fun every_parity_fixture_has_its_reference() {
        val references = pngs(design)
        val componentFixtures =
            GeneratedViddikRegistry.components.filter {
                it.group in previewed ||
                    stem(it.group, it.name) in probeReferences
            }
        val missing = componentFixtures.map { stem(it.group, it.name) }.filterNot { it in references }
        assertEquals(
            emptyList(),
            missing,
            "parity fixtures with no reference: they score MISSING_REFERENCE in a report nobody gates on",
        )
    }

    @Test
    fun every_previewed_component_has_a_fixture_per_skin_or_is_on_the_not_yet_built_list() {
        val unbuilt = previewed.filterNot { it in fixtureGroups || it in notYetBuilt }
        assertEquals(
            emptyList(),
            unbuilt.sorted(),
            "components of the design system with no parity fixture and not on the not-yet-built list",
        )
        val partial =
            previewed.filter { it in fixtureGroups }.flatMap { c ->
                skins.map { stem(c, it) }.filterNot {
                    it in
                        fixtures
                }
            }
        assertEquals(emptyList(), partial, "components built in some skins only")
    }

    @Test
    fun the_not_yet_built_list_names_only_unbuilt_components_of_the_design_system() {
        assertEquals(
            emptySet(),
            notYetBuilt - previewed,
            "names on the not-yet-built list the design system does not have",
        )
        assertEquals(
            emptySet(),
            notYetBuilt.intersect(fixtureGroups),
            "built, so take these off the not-yet-built list",
        )
    }

    @Test
    fun the_only_export_without_a_preview_is_the_known_one() {
        val exports =
            Regex("""export (?:declare )?(?:function|const) ([A-Za-z]+)""")
                .findAll(File(designSystem, "index.d.ts").readText())
                .map { it.groupValues[1] }
                .toSet()
        assertEquals(51, exports.size, "research §1.1 counts 51 exported components")
        assertEquals(
            exportedWithoutPreview,
            exports - previewed,
            "an exported component without a preview has no reference to be held to",
        )
    }
}
