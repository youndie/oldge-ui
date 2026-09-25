package io.github.youndie.oldge.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.youndie.oldge.containers.OldgeDivider
import io.github.youndie.oldge.theme.OldgeTheme

// The desktop sample until B-41 gives it screens: the one component there is so far.
public fun main(): Unit =
    application {
        Window(onCloseRequest = ::exitApplication, title = "oldge-ui") {
            OldgeTheme {
                Column(Modifier.padding(OldgeTheme.spacing.space4)) {
                    OldgeDivider(label = "или")
                }
            }
        }
    }
