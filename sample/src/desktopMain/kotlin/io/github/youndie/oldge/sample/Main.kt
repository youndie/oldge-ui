package io.github.youndie.oldge.sample

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

/** The sample app on the desktop, in a phone-sized window (B-41). */
public fun main(): Unit =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "oldge-ui",
            state = rememberWindowState(size = DpSize(WIDTH.dp, HEIGHT.dp)),
        ) { OldgeSampleApp() }
    }

private const val WIDTH = 420
private const val HEIGHT = 900
