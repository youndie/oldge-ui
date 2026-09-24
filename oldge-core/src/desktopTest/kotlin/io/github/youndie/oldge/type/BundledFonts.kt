package io.github.youndie.oldge.type

import java.awt.Font
import java.io.File

/** The bundled font files, read from the module's own resources directory. */
internal object BundledFonts {
    private val dir = File("src/commonMain/composeResources/font")

    val all: List<String> =
        listOf(
            "dejavu_sans_condensed.ttf",
            "dejavu_sans_condensed_bold.ttf",
            "fira_sans_bold.ttf",
            "share_tech_mono.ttf",
            "pt_mono.ttf",
            "silkscreen.ttf",
            "tiny5.ttf",
        )

    fun awt(name: String): Font = Font.createFont(Font.TRUETYPE_FONT, File(dir, name))

    fun files(): List<String> =
        dir
            .list()
            .orEmpty()
            .filter { it.endsWith(".ttf") }
            .sorted()
}
