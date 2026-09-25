package io.github.youndie.oldge.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.OldgeOrbButton
import io.github.youndie.oldge.actions.OldgeOrbTone
import io.github.youndie.oldge.icons.OldgeIcon
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.navigation.OldgePageDots
import io.github.youndie.oldge.navigation.OldgeWindowAction
import io.github.youndie.oldge.navigation.OldgeWindowBar
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeColors
import io.github.youndie.oldge.type.OldgeText
import kotlin.math.abs

/** One photograph of [MediaScreen]: its caption, and the skin colour its stand-in is lit with. */
private class Shot(
    val caption: String,
    val light: (OldgeColors) -> Color,
)

private val shots =
    listOf(
        Shot("Озеро на рассвете") { it.bezelHi },
        Shot("Серпантин") { it.accentLo },
        Shot("Старый город") { it.pillLo },
        Shot("Ночной порт") { it.bezel },
        Shot("Пляж") { it.chromeLo },
    )

/**
 * The design system's MediaScreen stress page: no frame, the WindowBar laid over the picture
 * (`overlay`), a horizontal swipe to the next photograph, a tap that hides the interface, and PageDots
 * over the photo. The photograph is the page's stand-in, `.media`: an ellipse of a skin colour over
 * black, with the image glyph in its middle. The caption and its scrim are the page's `.media__bottom`.
 * Its strings are the page's.
 */
@Composable
public fun MediaScreen(
    modifier: Modifier = Modifier,
    initialIndex: Int = 1,
) {
    val c = OldgeTheme.colors
    val type = OldgeTheme.type
    var index by remember { mutableIntStateOf(initialIndex) }
    var liked by remember { mutableStateOf(false) }
    var ui by remember { mutableStateOf(true) }
    val shot = shots[index]
    val light = shot.light(c)
    Box(modifier.background(Color.Black)) {
        // `.media`: `radial-gradient(120% 80% at 30% 20%, <colour>, #000 75%)`, filling the phone; a
        // tap toggles the interface and a swipe of more than 40 px turns the page.
        Box(
            Modifier
                .fillMaxSize()
                .drawBehind {
                    val centre = Offset(size.width * GLOW_X, size.height * GLOW_Y)
                    val rx = size.width * GLOW_RX
                    val ry = size.height * GLOW_RY
                    withTransform({ scale(1f, ry / rx, pivot = centre) }) {
                        drawRect(
                            Brush.radialGradient(0f to light, GLOW_FADE to Color.Black, center = centre, radius = rx),
                            topLeft = Offset(0f, centre.y - (centre.y) * rx / ry),
                            size = Size(size.width, size.height * rx / ry),
                        )
                    }
                }.clickable(remember { MutableInteractionSource() }, indication = null) { ui = !ui }
                .pointerInput(Unit) {
                    var dx = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { dx = 0f },
                        onDragEnd = {
                            if (abs(dx) > SWIPE.toPx()) {
                                index = (index + if (dx < 0) 1 else -1).coerceIn(0, shots.lastIndex)
                            }
                        },
                    ) { _, amount -> dx += amount }
                },
            contentAlignment = Alignment.Center,
        ) {
            OldgeIcon(
                OldgeIcons.Image,
                contentDescription = null,
                size = GLYPH,
                tint = Color.White.copy(alpha = GLYPH_ALPHA),
            )
        }
        if (ui) {
            OldgeWindowBar(
                "Отпуск 2006",
                Modifier.align(Alignment.TopStart),
                subtitle = "${index + 1} из 148",
                onBack = {},
                actions = listOf(OldgeWindowAction(OldgeIcons.More, "Ещё", {})),
                overlay = true,
            )
            // `.media__bottom`: padded 48 16 20 over a scrim from clear to 75 % black, 4 px apart.
            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0f), Color.Black.copy(alpha = SCRIM))),
                    ).padding(start = SIDE, end = SIDE, top = SCRIM_TOP, bottom = SCRIM_BOTTOM),
                verticalArrangement = Arrangement.spacedBy(GAP),
            ) {
                // `.media__cap`: the title face at 1.0625rem / 1.375rem in white with a 2 px shadow, its
                // place and date under it in the body face at 0.75rem / 1rem, at 0.85 opacity.
                Column {
                    OldgeText(
                        shot.caption,
                        style = type.title.copy(color = Color.White, shadow = Shadow(Color.Black, Offset(0f, 1f), 2f)),
                    )
                    OldgeText(
                        "Черногория · 14 июля 2006",
                        style =
                            type.caption.copy(
                                color = Color.White.copy(alpha = PLACE_ALPHA),
                                shadow = Shadow(Color.Black, Offset(0f, 1f), 2f),
                            ),
                    )
                }
                OldgePageDots(shots.size, index, { index = it }, onDark = true)
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ACTS_GAP, Alignment.CenterHorizontally),
                ) {
                    OldgeOrbButton(
                        OldgeIcons.Heart,
                        if (liked) "Убрать из избранного" else "В избранное",
                        { liked = !liked },
                        Modifier.semantics { stateDescription = if (liked) "в избранном" else "не в избранном" },
                        tone = if (liked) OldgeOrbTone.Accent else OldgeOrbTone.Bezel,
                    )
                    OldgeOrbButton(OldgeIcons.Share, "Поделиться", {})
                    OldgeOrbButton(OldgeIcons.Download, "Скачать", {})
                }
            }
        }
    }
}

private const val GLOW_X = 0.3f
private const val GLOW_Y = 0.2f
private const val GLOW_RX = 1.2f
private const val GLOW_RY = 0.8f
private const val GLOW_FADE = 0.75f
private const val GLYPH_ALPHA = 0.28f
private const val SCRIM = 0.75f
private const val PLACE_ALPHA = 0.85f
private val GLYPH = 96.dp // css literal: MediaScreen preview, `Icon size: 96`
private val SWIPE = 40.dp // css literal: MediaScreen preview, `Math.abs(dx) > 40`
private val SIDE = 16.dp // css literal: MediaScreen preview, `.media__bottom { padding: 48px 16px 20px }`
private val SCRIM_TOP = 48.dp // css literal: MediaScreen preview, `.media__bottom { padding: 48px 16px 20px }`
private val SCRIM_BOTTOM = 20.dp // css literal: MediaScreen preview, `.media__bottom { padding: 48px 16px 20px }`
private val GAP = 4.dp // css literal: MediaScreen preview, `.media__bottom { gap: 4px }`
private val ACTS_GAP = 12.dp // css literal: MediaScreen preview, `.media__acts { gap: 12px }`
