package io.github.youndie.oldge.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.youndie.oldge.harness.DemoText
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/CategoryTabs/preview.html and Tabs/preview.html, string for string.

internal val categories =
    listOf(
        OldgeCategory("fav", OldgeIcons.Star, "Избранное"),
        OldgeCategory("doc", OldgeIcons.Doc, "Документы"),
        OldgeCategory("music", OldgeIcons.Note, "Музыка"),
        OldgeCategory("photo", OldgeIcons.Image, "Фото"),
        OldgeCategory("video", OldgeIcons.Film, "Видео"),
        OldgeCategory("more", OldgeIcons.Plus, "Ещё"),
    )

internal val folderTabs =
    listOf(OldgeTab("gen", "Общие"), OldgeTab("share", "Доступ", badge = "2"), OldgeTab("hist", "История"))

private val folderPages =
    mapOf(
        "gen" to "Имя, значок и размер папки.",
        "share" to "Кому доступна папка и по какой ссылке.",
        "hist" to "Кто и когда менял содержимое.",
    )

/** `.og-demo` with `padding: 0`: the row's own padding is the frame. */
@Composable
private fun CategoryTabsDemo(skin: OldgeSkin) =
    OldgeDemo(skin, padded = false) {
        var value by remember { mutableStateOf("doc") }
        OldgeCategoryTabs(categories, value, { value = it })
    }

@Composable
private fun TabsDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        var value by remember { mutableStateOf("gen") }
        OldgeTabs(folderTabs, value, { value = it }, label = "Свойства папки") { DemoText(folderPages.getValue(value)) }
    }

/** What the CategoryTabs preview does not show: the first category current, and the last. */
@Composable
private fun CategoryTabsStates(skin: OldgeSkin) =
    OldgeDemo(skin, padded = false) {
        OldgeCategoryTabs(categories, "fav", {})
        OldgeCategoryTabs(categories.take(4), "photo", {})
    }

/** What the Tabs preview does not show: five tabs with icons, the middle one current, a badge on another. */
@Composable
private fun TabsStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeTabs(
            listOf(
                OldgeTab("a", "Файлы", OldgeIcons.Folder),
                OldgeTab("b", "Фото", OldgeIcons.Image, badge = "5"),
                OldgeTab("c", "Звук", OldgeIcons.Note),
                OldgeTab("d", "Видео", OldgeIcons.Film),
                OldgeTab("e", "Ещё", OldgeIcons.Plus),
            ),
            "c",
            {},
            label = "Медиатека",
        ) { DemoText("Три альбома, 42 записи.") }
    }

@ViddikScreenshot(group = "CategoryTabsStates", name = "First and last current Toxic", width = 390, height = 260)
@Composable
fun CategoryTabsStatesToxic() = CategoryTabsStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "CategoryTabsStates", name = "First and last current Media", width = 390, height = 260)
@Composable
fun CategoryTabsStatesMedia() = CategoryTabsStates(OldgeSkin.Media)

@ViddikScreenshot(group = "CategoryTabsStates", name = "First and last current Crystal", width = 390, height = 260)
@Composable
fun CategoryTabsStatesCrystal() = CategoryTabsStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "TabsStates", name = "Five with icons Toxic", width = 390, height = 190)
@Composable
fun TabsStatesToxic() = TabsStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "TabsStates", name = "Five with icons Media", width = 390, height = 190)
@Composable
fun TabsStatesMedia() = TabsStates(OldgeSkin.Media)

@ViddikScreenshot(group = "TabsStates", name = "Five with icons Crystal", width = 390, height = 190)
@Composable
fun TabsStatesCrystal() = TabsStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "CategoryTabs", name = "Toxic", width = 390, height = 130)
@Composable
fun CategoryTabsToxic() = CategoryTabsDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "CategoryTabs", name = "Media", width = 390, height = 130)
@Composable
fun CategoryTabsMedia() = CategoryTabsDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "CategoryTabs", name = "Crystal", width = 390, height = 130)
@Composable
fun CategoryTabsCrystal() = CategoryTabsDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "Tabs", name = "Toxic", width = 390, height = 190)
@Composable
fun TabsToxic() = TabsDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Tabs", name = "Media", width = 390, height = 190)
@Composable
fun TabsMedia() = TabsDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Tabs", name = "Crystal", width = 390, height = 190)
@Composable
fun TabsCrystal() = TabsDemo(OldgeSkin.Crystal)
