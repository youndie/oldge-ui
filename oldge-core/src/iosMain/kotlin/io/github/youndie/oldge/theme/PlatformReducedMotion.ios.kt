package io.github.youndie.oldge.theme

import androidx.compose.runtime.Composable
import platform.UIKit.UIAccessibilityIsReduceMotionEnabled

/** iOS's Settings → Accessibility → Motion → Reduce Motion. */
@Composable
internal actual fun platformReducedMotion(): Boolean = UIAccessibilityIsReduceMotionEnabled()
