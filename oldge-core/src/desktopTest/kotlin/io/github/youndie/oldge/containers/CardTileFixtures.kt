package io.github.youndie.oldge.containers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.actions.OldgeButtonSize
import io.github.youndie.oldge.actions.OldgeButtonVariant
import io.github.youndie.oldge.actions.pressed
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/Card/preview.html and ActionTile/preview.html, string for
// string. The first card of the Card preview is pressable *and* has actions, which the Card README
// forbids and `OldgeCard` refuses; it is ported without the press, which draws the same at rest.

@Composable
private fun CardDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeCard(media = OldgeIcons.Image, title = "Отпуск 2006", subtitle = "148 фото · Черногория") {
            OldgeButton("Поделиться", {}, size = OldgeButtonSize.Small)
            OldgeButton("Открыть", {}, size = OldgeButtonSize.Small, variant = OldgeButtonVariant.Primary)
        }
        OldgeCard(
            bar = "Резервная копия",
            barIcon = OldgeIcons.Cloud,
            title = "Последняя — сегодня, 03:00",
            subtitle = "Следующая — завтра",
            text = "Сохранено 1 204 файла, ошибок нет.",
        )
        OldgeCard(
            sunken = true,
            title = "Журнал",
            subtitle = "Только чтение",
            text = "Утопленная карточка — для вывода, логов и справки.",
        )
    }

@Composable
private fun TileDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeActionTile("Создать резервную копию", OldgeIcons.Upload, {
        }, description = "Фото, контакты и заметки", tone = OldgeActionTileTone.Accent)
        OldgeActionTile("Восстановить из копии", OldgeIcons.Refresh, {}, tone = OldgeActionTileTone.Bezel)
        // `display: grid; grid-template-columns: 1fr 1fr; gap: 4px`.
        Row(horizontalArrangement = Arrangement.spacedBy(GRID_GAP)) {
            OldgeActionTile("Фото", OldgeIcons.Image, {}, Modifier.weight(1f), layout = OldgeActionTileLayout.Stack)
            OldgeActionTile("Документы", OldgeIcons.Doc, {}, Modifier.weight(1f), layout = OldgeActionTileLayout.Stack)
        }
    }

@Composable
private fun States(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeCard(media = OldgeIcons.Image, title = "Нажата целиком", onClick = {}, interactionSource = pressed())
        OldgeActionTile(
            "Нажата",
            OldgeIcons.Upload,
            {},
            description = "Панель под плиткой",
            interactionSource = pressed(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(GRID_GAP)) {
            OldgeActionTile("Хром", OldgeIcons.Doc, {
            }, Modifier.weight(1f), layout = OldgeActionTileLayout.Stack, interactionSource = pressed())
            OldgeActionTile("Акцент", OldgeIcons.Star, {
            }, Modifier.weight(1f), layout = OldgeActionTileLayout.Stack, tone = OldgeActionTileTone.Accent)
        }
    }

private val GRID_GAP = 4.dp

@ViddikScreenshot(group = "Card", name = "Toxic", width = 390, height = 546)
@Composable
fun CardToxic() = CardDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Card", name = "Media", width = 390, height = 546)
@Composable
fun CardMedia() = CardDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Card", name = "Crystal", width = 390, height = 546)
@Composable
fun CardCrystal() = CardDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "ActionTile", name = "Toxic", width = 390, height = 310)
@Composable
fun ActionTileToxic() = TileDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "ActionTile", name = "Media", width = 390, height = 310)
@Composable
fun ActionTileMedia() = TileDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "ActionTile", name = "Crystal", width = 390, height = 310)
@Composable
fun ActionTileCrystal() = TileDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "CardStates", name = "Pressed Toxic", width = 390, height = 460)
@Composable
fun CardStatesToxic() = States(OldgeSkin.Toxic)

@ViddikScreenshot(group = "CardStates", name = "Pressed Media", width = 390, height = 460)
@Composable
fun CardStatesMedia() = States(OldgeSkin.Media)

@ViddikScreenshot(group = "CardStates", name = "Pressed Crystal", width = 390, height = 460)
@Composable
fun CardStatesCrystal() = States(OldgeSkin.Crystal)
