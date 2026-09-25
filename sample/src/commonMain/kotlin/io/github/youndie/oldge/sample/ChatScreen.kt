package io.github.youndie.oldge.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.containers.OldgeDivider
import io.github.youndie.oldge.containers.OldgeScreenBody
import io.github.youndie.oldge.feedback.OldgeAvatar
import io.github.youndie.oldge.feedback.OldgeAvatarSize
import io.github.youndie.oldge.feedback.OldgeAvatarStatus
import io.github.youndie.oldge.feedback.OldgeChatBubble
import io.github.youndie.oldge.feedback.OldgeChatSide
import io.github.youndie.oldge.feedback.OldgeComposer
import io.github.youndie.oldge.feedback.OldgeMessageStatus
import io.github.youndie.oldge.feedback.OldgeTypingIndicator
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.navigation.OldgeWindowAction
import io.github.youndie.oldge.navigation.OldgeWindowBar
import io.github.youndie.oldge.theme.OldgeTheme

/** One message of [ChatScreen]. */
public data class ChatMessage(
    val from: OldgeChatSide,
    val text: String,
    val time: String,
    val status: OldgeMessageStatus? = null,
)

private val preview =
    listOf(
        ChatMessage(OldgeChatSide.Them, "Привет! Скинула фото из поездки", "09:12"),
        ChatMessage(
            OldgeChatSide.Them,
            "Там 148 штук, но лучшие я отметила звёздочкой. Посмотри вечером, если будет время, и скажи какие печатать в альбом",
            "09:12",
        ),
        ChatMessage(OldgeChatSide.Me, "Уже смотрю. Горы — огонь", "09:14", OldgeMessageStatus.Read),
        ChatMessage(OldgeChatSide.Me, "Можно я возьму пару на обои?", "09:14", OldgeMessageStatus.Read),
    )

/**
 * The design system's ChatScreen stress page, from this library's components alone: the contact's
 * Avatar in the WindowBar, a run of messages grouped by author (the time, the status, the avatar and
 * the tail on the last of each run), «печатает», and the Composer where the bottom navigation would
 * be. What is sent is appended as mine, «отправлено», at 09:20. Its strings are the preview's.
 */
@Composable
public fun ChatScreen(modifier: Modifier = Modifier) {
    val s = OldgeTheme.spacing
    val messages = remember { mutableStateListOf<ChatMessage>().apply { addAll(preview) } }
    OldgeScreenBody(modifier) {
        Column(Modifier.fillMaxSize()) {
            OldgeWindowBar(
                "Анна Ким",
                subtitle = "в сети",
                onBack = {},
                lead = {
                    OldgeAvatar(
                        name = "Анна Ким",
                        size = OldgeAvatarSize.Small,
                        status = OldgeAvatarStatus.Online,
                    )
                },
                actions = listOf(OldgeWindowAction(OldgeIcons.More, "Ещё", {})),
            )
            // `justify-content: flex-end` in a scrolling column: a short lane sits at the bottom, a
            // long one fills the box and scrolls from its top.
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.BottomStart) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(start = s.space4, end = s.space4, top = s.space2, bottom = s.space4),
                    verticalArrangement = Arrangement.spacedBy(LANE_GAP),
                ) {
                    OldgeDivider(label = "Сегодня")
                    messages.forEachIndexed { i, m ->
                        val last = messages.getOrNull(i + 1)?.from != m.from
                        val them = m.from == OldgeChatSide.Them
                        OldgeChatBubble(
                            m.text,
                            m.from,
                            time = m.time.takeIf { last },
                            status = m.status.takeIf { last },
                            tail = last,
                            avatar =
                                if (!them) {
                                    null
                                } else if (last) {
                                    { OldgeAvatar(name = "Анна Ким", size = OldgeAvatarSize.Small) }
                                } else {
                                    { Spacer(Modifier.width(AVATAR_SPACE)) }
                                },
                        )
                    }
                    OldgeTypingIndicator(
                        name = "Анна",
                        avatar = { OldgeAvatar(name = "Анна Ким", size = OldgeAvatarSize.Small) },
                    )
                }
            }
            OldgeComposer({ messages += ChatMessage(OldgeChatSide.Me, it, "09:20", OldgeMessageStatus.Sent) })
        }
    }
}

private val LANE_GAP = 6.dp // css literal: ChatScreen preview, `.scroll { gap: 6 }`
private val AVATAR_SPACE = 32.dp // css literal: ChatScreen preview, the avatar's stand-in `width: 32`
