package io.github.youndie.oldge.theme

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** B-76: a skin's id is its persistence key, and a released id never changes. */
class OldgeSkinIdTest {
    /** The released ids, as literals: changing one breaks every stored choice, and this test. */
    @Test
    fun the_released_ids_stay_what_they_are() {
        assertEquals("toxic", OldgeSkin.Toxic.id)
        assertEquals("media", OldgeSkin.Media.id)
        assertEquals("crystal", OldgeSkin.Crystal.id)
    }

    @Test
    fun every_skin_is_found_by_its_id_and_the_ids_are_distinct_and_lowercase() {
        for (skin in OldgeSkin.entries) {
            assertEquals(skin, OldgeSkin.byId(skin.id))
            assertEquals(skin.id.lowercase(), skin.id)
        }
        assertEquals(
            OldgeSkin.entries.size,
            OldgeSkin.entries
                .map { it.id }
                .toSet()
                .size,
        )
    }

    @Test
    fun an_unknown_id_is_no_skin() {
        assertNull(OldgeSkin.byId("nope"))
        assertNull(OldgeSkin.byId("Toxic"), "the name is not the id")
    }
}
