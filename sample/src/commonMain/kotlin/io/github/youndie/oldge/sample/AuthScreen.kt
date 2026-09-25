package io.github.youndie.oldge.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import io.github.youndie.oldge.actions.OldgeButton
import io.github.youndie.oldge.actions.OldgeButtonSize
import io.github.youndie.oldge.actions.OldgeButtonVariant
import io.github.youndie.oldge.actions.OldgeSegment
import io.github.youndie.oldge.actions.OldgeSegmented
import io.github.youndie.oldge.containers.OldgeDivider
import io.github.youndie.oldge.containers.OldgeScreenBody
import io.github.youndie.oldge.feedback.OldgeReadout
import io.github.youndie.oldge.feedback.OldgeReadoutSize
import io.github.youndie.oldge.forms.OldgeCheckbox
import io.github.youndie.oldge.forms.OldgeCodeInput
import io.github.youndie.oldge.forms.OldgeTextField
import io.github.youndie.oldge.icons.OldgeIcons
import io.github.youndie.oldge.navigation.OldgeWindowAction
import io.github.youndie.oldge.navigation.OldgeWindowBar
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.type.OldgeText

/** How [AuthScreen] signs in: a password, or a code sent by SMS. */
public enum class AuthMode { Password, Code }

/**
 * The design system's AuthScreen stress page, from this library's components alone: a password with
 * reveal and an error, the switch to an SMS code with a resend timer, a passkey and an e-mail link.
 * Its strings are the preview's.
 */
@Composable
public fun AuthScreen(
    modifier: Modifier = Modifier,
    initialMode: AuthMode = AuthMode.Password,
) {
    val s = OldgeTheme.spacing
    val c = OldgeTheme.colors
    val type = OldgeTheme.type
    var mode by remember { mutableStateOf(initialMode) }
    var code by remember { mutableStateOf("4829") }
    var email by remember { mutableStateOf("pavel@example.com") }
    var password by remember { mutableStateOf("hunter22") }
    var remember by remember { mutableStateOf(true) }
    OldgeScreenBody(modifier) {
        Column(Modifier.fillMaxSize()) {
            OldgeWindowBar(
                "Вход",
                icon = OldgeIcons.Key,
                actions = listOf(OldgeWindowAction(OldgeIcons.Help, "Справка", {})),
            )
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = s.space4, end = s.space4, top = s.space4, bottom = s.space4),
                verticalArrangement = Arrangement.spacedBy(s.space3),
            ) {
                OldgeText("С возвращением", style = type.display.copy(color = c.ink))
                OldgeText("Войдите, чтобы продолжить синхронизацию.", style = type.body.copy(color = c.inkMuted))
                OldgeSegmented(
                    listOf(OldgeSegment(AuthMode.Password, "Пароль"), OldgeSegment(AuthMode.Code, "Код")),
                    mode,
                    { mode = it },
                    "Способ входа",
                    Modifier.fillMaxWidth(),
                )
                when (mode) {
                    AuthMode.Password -> {
                        OldgeTextField("Эл. почта", email, { email = it }, Modifier.fillMaxWidth())
                        OldgeTextField(
                            "Пароль",
                            password,
                            { password = it },
                            Modifier.fillMaxWidth(),
                            error = "Неверный пароль. Осталось 2 попытки",
                            reveal = true,
                        )
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OldgeCheckbox("Запомнить", remember, { remember = it })
                            OldgeButton(
                                "Забыли пароль?",
                                {},
                                variant = OldgeButtonVariant.Plain,
                                size = OldgeButtonSize.Small,
                            )
                        }
                    }

                    AuthMode.Code -> {
                        OldgeCodeInput(
                            "Код из СМС",
                            code,
                            { code = it },
                            Modifier.fillMaxWidth(),
                            help = "Отправили на +382 •• ••• 41",
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(s.space2),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OldgeReadout("0:42", label = "Повтор через", size = OldgeReadoutSize.Small)
                            OldgeButton(
                                "Отправить ещё раз",
                                {},
                                variant = OldgeButtonVariant.Plain,
                                size = OldgeButtonSize.Small,
                                enabled = false,
                            )
                        }
                    }
                }
                OldgeButton(
                    "Войти",
                    {},
                    Modifier.fillMaxWidth(),
                    variant = OldgeButtonVariant.Primary,
                    size = OldgeButtonSize.Large,
                    block = true,
                )
                OldgeDivider(label = "или")
                OldgeButton("Войти по ключу доступа", {}, Modifier.fillMaxWidth(), icon = OldgeIcons.Key, block = true)
                OldgeButton(
                    "Прислать ссылку на почту",
                    {},
                    Modifier.fillMaxWidth(),
                    icon = OldgeIcons.Mail,
                    block = true,
                )
                // `<p class="og-muted">` at 0.8125rem, centred, 4 px below the gap: the text and a
                // plain button on one line, sharing its baseline.
                Row(
                    Modifier.fillMaxWidth().padding(top = s.space1),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    OldgeText(
                        "Нет аккаунта? ",
                        Modifier.alignByBaseline(),
                        style =
                            type.label.copy(
                                fontWeight = type.body.fontWeight,
                                color = c.inkMuted,
                                textAlign = TextAlign.Center,
                            ),
                    )
                    OldgeButton(
                        "Создать",
                        {},
                        Modifier.alignByBaseline(),
                        variant = OldgeButtonVariant.Plain,
                        size = OldgeButtonSize.Small,
                    )
                }
            }
        }
    }
}
