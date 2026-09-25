package io.github.youndie.oldge.icons

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class OldgeIconsTest {
    /** The generated set is the bundle's table, name for name and in order (B-11). */
    @Test
    fun the_set_is_the_bundles() {
        val js = File("../reference/design-system/components/bundle.js").readText()
        val table = js.substring(js.indexOf("var PATHS = {"), js.indexOf("};", js.indexOf("var PATHS = {")))
        val names =
            Regex("""^\s*'?([a-z][a-z-]*)'?\s*:\s*'""", RegexOption.MULTILINE)
                .findAll(table)
                .map {
                    it.groupValues[1]
                }.toList()
        assertEquals(47, names.size, "research §1.1 counts 47 icons")
        assertEquals(names, OldgeIconPaths.keys.toList())
    }

    /** Every public constant resolves and is the path of its own name. */
    @Test
    fun every_constant_is_built_from_its_own_path() {
        val constants =
            OldgeIcons::class.java.declaredMethods.filter {
                it.name.startsWith("get") &&
                    it.returnType.simpleName == "ImageVector"
            }
        assertEquals(47, constants.size)
        for (getter in constants) {
            val vector = getter.invoke(OldgeIcons) as androidx.compose.ui.graphics.vector.ImageVector
            val name = vector.name.removePrefix("OldgeIcons.")
            val expected = name.split("-").joinToString("") { it.replaceFirstChar(Char::uppercase) }
            assertEquals("get$expected", getter.name, "$name is exposed under another name")
        }
    }
}
