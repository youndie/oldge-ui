package io.github.youndie.oldge.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import io.github.youndie.oldge.harness.OldgeDemo
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.viddik.annotations.ViddikScreenshot

// reference/design-system/components/Spinner/preview.html and Skeleton/preview.html, string for
// string. The references are rendered with reduced motion, where the loops stand still; the
// `…States` goldens freeze them mid-loop through `LocalOldgeLoopPhase` instead.

@Composable
private fun SpinnerDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        // `display: flex; gap: 24px; align-items: flex-end`.
        Row(
            horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space5),
            verticalAlignment = Alignment.Bottom,
        ) {
            OldgeSpinner(size = OldgeSpinnerSize.Small)
            OldgeSpinner()
            OldgeSpinner(size = OldgeSpinnerSize.Large, label = "Загружаю…")
        }
    }

@Composable
private fun SkeletonDemo(skin: OldgeSkin) =
    OldgeDemo(skin) {
        OldgeSkeleton(variant = OldgeSkeletonVariant.Row)
        OldgeSkeleton(variant = OldgeSkeletonVariant.Card)
        OldgeSkeleton(lines = 4, index = 5)
    }

/** The loops at a third of their period: the disc turned, the scanners out, the dials stepped. */
@Composable
private fun Moving(skin: OldgeSkin) =
    OldgeDemo(skin) {
        CompositionLocalProvider(LocalOldgeLoopPhase provides FROZEN) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(OldgeTheme.spacing.space5),
                verticalAlignment = Alignment.Bottom,
            ) {
                OldgeSpinner(size = OldgeSpinnerSize.Large, label = "Загружаю…")
                OldgeSkeleton(variant = OldgeSkeletonVariant.Circle)
            }
            OldgeSkeleton(variant = OldgeSkeletonVariant.Card)
            OldgeSkeleton(lines = 4)
        }
    }

private const val FROZEN = 0.33f

@ViddikScreenshot(group = "Spinner", name = "Toxic", width = 390, height = 130)
@Composable
fun SpinnerToxic() = SpinnerDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Spinner", name = "Media", width = 390, height = 130)
@Composable
fun SpinnerMedia() = SpinnerDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Spinner", name = "Crystal", width = 390, height = 130)
@Composable
fun SpinnerCrystal() = SpinnerDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "Skeleton", name = "Toxic", width = 390, height = 420)
@Composable
fun SkeletonToxic() = SkeletonDemo(OldgeSkin.Toxic)

@ViddikScreenshot(group = "Skeleton", name = "Media", width = 390, height = 420)
@Composable
fun SkeletonMedia() = SkeletonDemo(OldgeSkin.Media)

@ViddikScreenshot(group = "Skeleton", name = "Crystal", width = 390, height = 420)
@Composable
fun SkeletonCrystal() = SkeletonDemo(OldgeSkin.Crystal)

@ViddikScreenshot(group = "SkeletonStates", name = "Mid loop Toxic", width = 390, height = 460)
@Composable
fun SkeletonStatesToxic() = Moving(OldgeSkin.Toxic)

@ViddikScreenshot(group = "SkeletonStates", name = "Mid loop Media", width = 390, height = 460)
@Composable
fun SkeletonStatesMedia() = Moving(OldgeSkin.Media)

@ViddikScreenshot(group = "SkeletonStates", name = "Mid loop Crystal", width = 390, height = 460)
@Composable
fun SkeletonStatesCrystal() = Moving(OldgeSkin.Crystal)
