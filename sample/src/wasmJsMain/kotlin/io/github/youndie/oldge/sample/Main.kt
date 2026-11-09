package io.github.youndie.oldge.sample

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

/** The sample in a browser, the GitHub Pages site: the same `OldgeSampleApp` as the desktop window. */
@OptIn(ExperimentalComposeUiApi::class)
public fun main() {
    ComposeViewport(document.body!!) { OldgeSampleApp() }
}
