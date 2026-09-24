package io.github.youndie.oldge.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.youndie.oldge.OldgeCanary

public fun main(): Unit =
    application {
        Window(onCloseRequest = ::exitApplication, title = "oldge-ui") {
            OldgeCanary()
        }
    }
