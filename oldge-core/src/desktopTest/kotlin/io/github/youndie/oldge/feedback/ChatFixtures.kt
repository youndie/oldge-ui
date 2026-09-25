package io.github.youndie.oldge.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/{ChatBubble,TypingIndicator,Composer}/preview.html, string for string.

@Composable
internal fun Anna() = OldgeAvatar(name = "Анна Ким", size = OldgeAvatarSize.Small)

/** `.og-demo` with `gap: 8`. */
@Composable
private fun ChatBubbleDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        Column(verticalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2)) {
            OldgeChatBubble(
                "Скинула фото из поездки, посмотри вечером",
                OldgeChatSide.Them,
                time = "09:12",
                avatar = { Anna() },
            )
            OldgeChatBubble(
                "Уже смотрю. Горы — огонь.",
                OldgeChatSide.Me,
                time = "09:14",
                status = OldgeMessageStatus.Read,
            )
            OldgeChatBubble(
                "Можно я возьму пару на обои?",
                OldgeChatSide.Me,
                time = "09:14",
                status = OldgeMessageStatus.Sent,
                tail = false,
            )
        }
    }

@Composable
private fun TypingIndicatorDemo(skin: OldgeSkin) =
    OldgeDemo(skin) { OldgeTypingIndicator(name = "Анна", avatar = { Anna() }) }

@Composable
private fun ComposerDemo(skin: OldgeSkin) = OldgeDemo(skin) { OldgeComposer({}, placeholder = "Сообщение") }

/** What the ChatBubble preview does not show: a group chat's author, a word too long to fit, their tail-less run. */
@Composable
private fun ChatBubbleStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        Column(verticalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space2)) {
            OldgeChatBubble("Кто едет в субботу?", OldgeChatSide.Them, author = "Иван Ли", tail = false)
            OldgeChatBubble(LINK, OldgeChatSide.Them, author = "Иван Ли", time = "10:02", avatar = {
                Anna()
            })
            OldgeChatBubble("Я!", OldgeChatSide.Me, time = "10:03", status = OldgeMessageStatus.Sent)
        }
    }

/** The dots frozen mid-hop, a third of the way through the loop: each at its own height. */
@Composable
private fun TypingIndicatorStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        CompositionLocalProvider(LocalOldgeLoopPhase provides MID_HOP) {
            OldgeTypingIndicator(name = "Анна", avatar = { Anna() })
        }
    }

/** What the Composer preview does not show: text in the field (the send orb lit), three lines, no attach orb. */
@Composable
private fun ComposerStates(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeComposer({}, value = "Уже выхожу")
        OldgeComposer({}, value = "Первая строка\nвторая строка\nи третья", onAttach = null)
    }

private const val MID_HOP = 0.25f
private const val LINK = "Ссылка: https://example.com/очень-длинный-путь-без-единого-пробела-до-самого-конца"

@ViddikScreenshot(group = "ChatBubbleStates", name = "Group long word Toxic", width = 390, height = 300)
@Composable
fun ChatBubbleStatesToxic() = ChatBubbleStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "ChatBubbleStates", name = "Group long word Media", width = 390, height = 300)
@Composable
fun ChatBubbleStatesMedia() = ChatBubbleStates(OldgeSkin.Media)

@ViddikScreenshot(group = "ChatBubbleStates", name = "Group long word Crystal", width = 390, height = 300)
@Composable
fun ChatBubbleStatesCrystal() = ChatBubbleStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "TypingIndicatorStates", name = "Mid hop Toxic", width = 390, height = 90)
@Composable
fun TypingIndicatorStatesToxic() = TypingIndicatorStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "TypingIndicatorStates", name = "Mid hop Media", width = 390, height = 90)
@Composable
fun TypingIndicatorStatesMedia() = TypingIndicatorStates(OldgeSkin.Media)

@ViddikScreenshot(group = "TypingIndicatorStates", name = "Mid hop Crystal", width = 390, height = 90)
@Composable
fun TypingIndicatorStatesCrystal() = TypingIndicatorStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "ComposerStates", name = "Text and three lines Toxic", width = 390, height = 220)
@Composable
fun ComposerStatesToxic() = ComposerStates(OldgeSkin.Toxic)

@ViddikScreenshot(group = "ComposerStates", name = "Text and three lines Media", width = 390, height = 220)
@Composable
fun ComposerStatesMedia() = ComposerStates(OldgeSkin.Media)

@ViddikScreenshot(group = "ComposerStates", name = "Text and three lines Crystal", width = 390, height = 220)
@Composable
fun ComposerStatesCrystal() = ComposerStates(OldgeSkin.Crystal)

@ViddikScreenshot(group = "ChatBubble", name = "Toxic", width = 390, height = 260)
@Composable
fun ChatBubbleToxic() = ChatBubbleDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "ChatBubble", name = "Media", width = 390, height = 260)
@Composable
fun ChatBubbleMedia() = ChatBubbleDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "ChatBubble", name = "Crystal", width = 390, height = 260)
@Composable
fun ChatBubbleCrystal() = ChatBubbleDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "TypingIndicator", name = "Toxic", width = 390, height = 90)
@Composable
fun TypingIndicatorToxic() = TypingIndicatorDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "TypingIndicator", name = "Media", width = 390, height = 90)
@Composable
fun TypingIndicatorMedia() = TypingIndicatorDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "TypingIndicator", name = "Crystal", width = 390, height = 90)
@Composable
fun TypingIndicatorCrystal() = TypingIndicatorDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "Composer", name = "Toxic", width = 390, height = 110)
@Composable
fun ComposerToxic() = ComposerDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Composer", name = "Media", width = 390, height = 110)
@Composable
fun ComposerMedia() = ComposerDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Composer", name = "Crystal", width = 390, height = 110)
@Composable
fun ComposerCrystal() = ComposerDemo(OldgeSkin.Crystal)
