package io.github.youndie.oldge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A lime square, and nothing else. It exists so that the screenshot suite has a fixture before the
 * first component does: a viddik run over an empty registry proves nothing about whether viddik runs.
 * B-01 keeps it as the suite's canary; delete it once real fixtures exist and B-09 guards the set.
 */
@Composable
public fun OldgeCanary(modifier: Modifier = Modifier) {
    Box(modifier.size(24.dp).background(Color(0xFFA4FF1F)))
}
