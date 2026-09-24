package io.github.youndie.oldge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.youndie.oldge.tokens.OldgeColors
import io.github.youndie.oldge.tokens.OldgeSpacing

/**
 * A lime square, and nothing else. It exists so that the screenshot suite has a fixture before the
 * first component does: a viddik run over an empty registry proves nothing about whether viddik runs.
 * B-01 keeps it as the suite's canary; delete it once real fixtures exist and B-09 guards the set.
 */
@Composable
public fun OldgeCanary(modifier: Modifier = Modifier) {
    // Token values, not literals, so the canary obeys the rule every component does (B-05).
    Box(modifier.size(OldgeSpacing.space5).background(OldgeColors.Toxic.accent))
}
