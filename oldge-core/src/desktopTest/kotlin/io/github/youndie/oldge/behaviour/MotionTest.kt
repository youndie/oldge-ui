package io.github.youndie.oldge.behaviour

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.actions.OldgeButtonVariant
import io.github.youndie.oldge.actions.OldgeFab
import io.github.youndie.oldge.containers.OldgeAccordion
import io.github.youndie.oldge.containers.OldgeBottomSheet
import io.github.youndie.oldge.containers.OldgeDialog
import io.github.youndie.oldge.feedback.OldgeBanner
import io.github.youndie.oldge.feedback.OldgeChatBubble
import io.github.youndie.oldge.feedback.OldgeChatSide
import io.github.youndie.oldge.feedback.OldgeEmptyState
import io.github.youndie.oldge.feedback.OldgeMeter
import io.github.youndie.oldge.feedback.OldgeProgressBar
import io.github.youndie.oldge.feedback.OldgeProgressStatus
import io.github.youndie.oldge.feedback.OldgeReadout
import io.github.youndie.oldge.feedback.OldgeSnackbar
import io.github.youndie.oldge.forms.OldgeCheckbox
import io.github.youndie.oldge.forms.OldgeDatePicker
import io.github.youndie.oldge.forms.OldgeSwitch
import io.github.youndie.oldge.harness.PortableTextStyle
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.navigation.OldgeBalloon
import io.github.youndie.oldge.navigation.OldgeCategory
import io.github.youndie.oldge.navigation.OldgeCategoryTabs
import io.github.youndie.oldge.navigation.OldgeDrawerEntry
import io.github.youndie.oldge.navigation.OldgeMenu
import io.github.youndie.oldge.navigation.OldgeMenuEntry
import io.github.youndie.oldge.navigation.OldgeNavDrawer
import io.github.youndie.oldge.navigation.OldgeTab
import io.github.youndie.oldge.navigation.OldgeTabs
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeDurations
import kotlinx.datetime.LocalDate
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The design system's motion, which parity cannot see: references are rendered with reduced motion
 * (research §1.2), so an entrance that stopped moving, or moved and landed somewhere else, would pass
 * every golden (B-44). Each entrance runs on a clock driven by hand, and:
 *
 * - halfway through its duration it is neither where it started nor where it ends;
 * - once over, it is where reduced motion puts it at once;
 * - its halfway frame is a golden of its own (`snapshots/motion/`), so an easing that changes shows.
 *
 * The table in `docs/services/oldge-core.md` maps every `@keyframes` of `bundle.css` to the scene here
 * or the test elsewhere that holds it.
 */
@OptIn(ExperimentalTestApi::class)
class MotionTest {
    private class Scene(
        val name: String,
        val ms: Int,
        val width: Int,
        val height: Int,
        val trigger: (ComposeUiTest.(MutableState<Boolean>) -> Unit)? = null,
        // Where the "halfway" frame is taken; the middle of the run unless that is a step's edge.
        val mid: Int = ms / 2,
        val content: @Composable (MutableState<Boolean>) -> Unit,
    )

    private val entries =
        listOf(
            OldgeDrawerEntry.Item("inbox", OldgeIcons.Mail, "Входящие"),
            OldgeDrawerEntry.Item("sent", OldgeIcons.Upload, "Отправленные"),
            OldgeDrawerEntry.Item("trash", OldgeIcons.Trash, "Корзина"),
        )

    private val scenes =
        listOf(
            Scene("fab-in", SLOW, 120, 120) { OldgeFab(OldgeIcons.Plus, "Создать", {}) },
            Scene("window-in-dialog", SLOW, 390, 260) {
                OldgeDialog("Удалить файл?") {
                    androidx.compose.foundation.text
                        .BasicText("Отменить нельзя.")
                }
            },
            Scene("sheet-in", SLOW, 390, 400) {
                OldgeBottomSheet(
                    "Отправить файл",
                    onClose = {},
                ) {
                    androidx.compose.foundation.text
                        .BasicText("Контакту")
                }
            },
            Scene("snack-in", SLOW, 390, 100) { OldgeSnackbar("Файл удалён", actionLabel = "Отменить", inline = true) },
            Scene("drawer-in", SLOW, 390, 420) { OldgeNavDrawer(entries, "inbox", {}, "Почта") },
            Scene("banner-in", SLOW, 390, 160) {
                OldgeBanner(
                    title = "Мало места",
                    boxed = true,
                ) {
                    androidx.compose.foundation.text
                        .BasicText("Осталось 1,2 ГБ.")
                }
            },
            Scene("balloon-in", SLOW, 390, 160) { OldgeBalloon("Копия создана", text = "Сохранено 1 204 файла.") },
            Scene("balloon-in-bubble", SLOW, 390, 90) { OldgeChatBubble("Уже смотрю", OldgeChatSide.Me) },
            Scene("draw", SLOW, 390, 130) {
                OldgeCategoryTabs(
                    listOf(
                        OldgeCategory("fav", OldgeIcons.Star, "Избранное"),
                        OldgeCategory("doc", OldgeIcons.Doc, "Документы"),
                        OldgeCategory("music", OldgeIcons.Note, "Музыка"),
                        OldgeCategory("photo", OldgeIcons.Image, "Фото"),
                    ),
                    "doc",
                    {},
                )
            },
            // The panel is keyed by the selected tab, so switching tabs is what brings a new one in.
            Scene("fade-tabs-panel", BASE, 390, 140, trigger = { it.value = true }) { flag ->
                OldgeTabs(
                    listOf(OldgeTab("gen", "Общие"), OldgeTab("acc", "Доступ")),
                    if (flag.value) "acc" else "gen",
                    {},
                    "Раздел",
                ) {
                    androidx.compose.foundation.text
                        .BasicText(if (flag.value) "Кто видит папку" else "Имя и значок")
                }
            },
            // Full, so that every segment enters and the stagger runs its whole length.
            Scene(
                "seg-in",
                FAST + METER_STAGGER * METER_SEGMENTS,
                390,
                80,
            ) { OldgeMeter(1f, "Хранилище", "64 / 64 ГБ") },
            Scene("window-in-menu", BASE, 390, 260) {
                OldgeMenu(
                    true,
                    {},
                    listOf(OldgeMenuEntry.Item("open", "Открыть"), OldgeMenuEntry.Item("share", "Поделиться")),
                    {},
                ) {
                    androidx.compose.foundation.text
                        .BasicText("Ещё")
                }
            },
            Scene(
                "unfold",
                SLOW,
                390,
                220,
                trigger = { onNodeWithText("Недавние", useUnmergedTree = true).performClick() },
            ) {
                OldgeAccordion(
                    "Недавние",
                    defaultOpen = false,
                ) {
                    androidx.compose.foundation.text
                        .BasicText("Отчёт за квартал")
                }
            },
            Scene(
                "slide-l",
                BASE,
                390,
                370,
                trigger = { onNodeWithContentDescription("Следующий месяц").performClick() },
            ) { OldgeDatePicker(LocalDate(2026, 9, 24), {}, today = LocalDate(2026, 9, 24)) },
            Scene(
                "flicker",
                BASE,
                390,
                100,
                trigger = { it.value = true },
            ) { flag -> OldgeReadout(if (flag.value) "0:41" else "0:42") },
            Scene("hop", SLOW, 390, 220) { OldgeEmptyState("Папка пуста", icon = OldgeIcons.Folder) },
            Scene("pop", BASE, 390, 60, trigger = { it.value = true }) { flag ->
                OldgeCheckbox("Запомнить меня", flag.value, {})
            },
            Scene("pop-soft", BASE, 390, 90, trigger = { it.value = true }) { flag ->
                OldgeProgressBar(
                    1f,
                    label = "Копирование",
                    detail = "Готово",
                    status = if (flag.value) OldgeProgressStatus.Done else OldgeProgressStatus.None,
                )
            },
            // Coming on, the lamp blinks twice (`steps(1) 2`): dim, lit, dim, lit. Halfway is the edge of the
            // second dim run, so the frame is taken in its middle, 5/8 through. The lamp is an 8 px word, so
            // the switch is framed alone, or it is under the threshold of a wider frame.
            Scene("blink", BASE * 2, 64, 40, trigger = { it.value = true }, mid = BASE * 2 * 5 / 8) { flag ->
                OldgeSwitch("Wi-Fi", flag.value, {}, showLabel = false)
            },
            // The glint runs once over a press held down; reduced motion holds the same press with none.
            Scene(
                "shine",
                OldgeDurations.press,
                200,
                70,
                trigger = { onNodeWithText("Отправить", useUnmergedTree = true).performTouchInput { down(center) } },
            ) { OldgeButton("Отправить", {}, variant = OldgeButtonVariant.Primary) },
        )

    @Test
    fun every_entrance_moves_halfway_and_lands_where_reduced_motion_puts_it() {
        val wrong = mutableListOf<String>()
        for (scene in scenes) {
            val (start, mid, end) = frames(scene, reduced = false)
            val still = frames(scene, reduced = true).third
            if (differs(start, end) < MOVED) wrong += "${scene.name}: the start is the end, nothing moved"
            if (differs(mid, end) < MOVED) wrong += "${scene.name}: halfway it has already landed"
            if (differs(end, still) >
                LANDED
            ) {
                wrong +=
                    "${scene.name}: it lands ${"%.2f".format(
                        differs(end, still) * 100,
                    )} % off where reduced motion puts it"
            }
            golden(scene.name, mid)?.let { wrong += it }
        }
        assertEquals(emptyList(), wrong)
    }

    /** The first frame, the halfway frame and one well past the end, of the last root (a popup's, if any). */
    private fun frames(
        scene: Scene,
        reduced: Boolean,
    ): Triple<ImageBitmap, ImageBitmap, ImageBitmap> {
        lateinit var result: Triple<ImageBitmap, ImageBitmap, ImageBitmap>
        runComposeUiTest {
            mainClock.autoAdvance = false
            val flag = mutableStateOf(false)
            setContent {
                OldgeTheme(reducedMotion = reduced, platformTextStyle = PortableTextStyle) {
                    Box(Modifier.size(scene.width.dp, scene.height.dp)) { scene.content(flag) }
                }
            }
            mainClock.advanceTimeBy(SETTLE)
            scene.trigger?.invoke(this, flag)

            fun grab() = onAllNodes(isRoot()).let { it[it.fetchSemanticsNodes().size - 1] }.captureToImage()
            mainClock.advanceTimeByFrame()
            val start = grab()
            mainClock.advanceTimeBy(scene.mid.toLong())
            val mid = grab()
            mainClock.advanceTimeBy((scene.ms * 3 - scene.mid).toLong())
            result = Triple(start, mid, grab())
        }
        return result
    }

    /** The share of pixels that differ by more than a channel tolerance. */
    private fun differs(
        a: ImageBitmap,
        b: ImageBitmap,
    ): Float {
        if (a.width != b.width || a.height != b.height) return 1f
        val p = a.toPixelMap()
        val q = b.toPixelMap()
        var n = 0
        for (y in 0 until a.height) {
            for (x in 0 until a.width) {
                val u = p[x, y]
                val v = q[x, y]
                val d =
                    maxOf(
                        kotlin.math.abs(u.red - v.red),
                        kotlin.math.abs(u.green - v.green),
                        kotlin.math.abs(u.blue - v.blue),
                    )
                if (d * FULL > CHANNEL) n++
            }
        }
        return n.toFloat() / (a.width * a.height)
    }

    /**
     * The halfway frame held to `snapshots/motion/<name>.png`. A missing golden is written and reported,
     * so that a deleted one never passes silently: the next run compares against it.
     */
    private fun golden(
        name: String,
        mid: ImageBitmap,
    ): String? {
        val file = File(MOTION, "$name.png")
        if (!file.isFile) {
            file.parentFile.mkdirs()
            ImageIO.write(mid.toAwtImage(), "png", file)
            return "$name: halfway golden recorded at ${file.path}; run again to compare"
        }
        val kept = ImageIO.read(file)
        val bitmap = kept.toComposeImageBitmap()
        val off = differs(bitmap, mid)
        return if (off > GOLDEN) "$name: halfway ${"%.2f".format(off * 100)} % off its golden" else null
    }
}

private val MOTION = File("src/desktopTest/snapshots/motion")
private val SLOW = OldgeDurations.slow
private val BASE = OldgeDurations.base
private val FAST = OldgeDurations.fast
private const val METER_STAGGER = 28 // css literal: bundle.css `.og-meter__seg` animation-delay `* 28ms`
private const val METER_SEGMENTS = 20
private const val SETTLE = 32L
private const val FULL = 255
private const val CHANNEL = 16
private const val MOVED = 0.001f
private const val LANDED = 0.001f
private const val GOLDEN = 0.0005f
