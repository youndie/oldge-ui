package io.github.youndie.oldge.material

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.youndie.oldge.theme.OldgeSkin
import io.github.youndie.oldge.theme.OldgeTheme
import io.github.youndie.oldge.tokens.OldgeRadii
import io.github.youndie.viddik.annotations.ViddikScreenshot

// The material probes of scripts/probes/, rebuilt from the materials (B-07). Each fixture is
// compared with the Chrome render of its probe by viddikDesignParity; the layout here is the
// probe's CSS, number for number.

/** The probes' frame: 390 px wide, the skin's body, reduced motion, texture on (B-04). */
@Composable
internal fun Probe(
    skin: OldgeSkin,
    height: Int,
    content: @Composable () -> Unit,
) = OldgeTheme(skin = skin, reducedMotion = true) {
    Box(Modifier.size(390.dp, height.dp).skinBody()) { content() }
}

@Composable
private fun Modifier.at(
    x: Int,
    y: Int,
    w: Dp,
    h: Dp,
) = offset(x.dp, y.dp).size(w, h)

@Composable
internal fun BodyProbe(skin: OldgeSkin) = Probe(skin, 200) {}

@Composable
internal fun PanelProbe(skin: OldgeSkin) =
    Probe(skin, 96) {
        Box(Modifier.at(16, 16, 160.dp, 64.dp).panelSurface())
        Box(Modifier.at(200, 16, 160.dp, 64.dp).lcdGlass())
    }

@Composable
internal fun BezelProbe(skin: OldgeSkin) =
    Probe(skin, 76) {
        Box(Modifier.at(16, 16, 358.dp, 44.dp).bezel(OldgeRadii.xl))
    }

@Composable
internal fun OrbProbe(skin: OldgeSkin) =
    Probe(skin, 80) {
        val c = OldgeTheme.colors
        val cores =
            listOf(
                glossGradient(c.chromeHi, null, c.chromeLo),
                glossGradient(c.accentHi, c.accent, c.accentLo),
                glossGradient(c.bezelHi, null, c.bezelLo),
            )
        cores.forEachIndexed { i, core ->
            OrbMaterial(core, size = 48.dp, rim = 3.dp, modifier = Modifier.offset((16 + i * 64).dp, 16.dp))
        }
    }

@Composable
internal fun GlossProbe(skin: OldgeSkin) =
    Probe(skin, 76) {
        val c = OldgeTheme.colors
        val s = OldgeTheme.shadows
        Box(
            Modifier.at(16, 16, 160.dp, 44.dp).cssBox(
                OldgeRadii.sm,
                glossGradient(c.accentHi, c.accent, c.accentLo),
                1.dp,
                c.accentLo,
                s.raised,
                extraOuter = listOf(ring(c.accentRim)),
                gloss = c.gloss,
            ),
        )
        Box(
            Modifier
                .at(
                    200,
                    16,
                    160.dp,
                    44.dp,
                ).cssBox(
                    OldgeRadii.sm,
                    glossGradient(c.chromeHi, null, c.chromeLo),
                    1.dp,
                    c.edge,
                    s.raised,
                    gloss = c.gloss,
                ),
        )
    }

@Composable
internal fun LcdGlowProbe(skin: OldgeSkin) =
    Probe(skin, 88) {
        val t = OldgeTheme.type.readout.copy(color = OldgeTheme.colors.lcdInk)
        Box(Modifier.at(16, 16, 160.dp, 56.dp).lcdGlass()) {
            BasicText("12:47", Modifier.offset(12.dp, 10.dp), style = t.lcdGlow(), softWrap = false)
        }
        Box(Modifier.at(200, 16, 160.dp, 56.dp).lcdGlass()) {
            BasicText("12:47", Modifier.offset(12.dp, 10.dp), style = t, softWrap = false)
        }
    }

@ViddikScreenshot(group = "MaterialLcdGlow", name = "Toxic", width = 390, height = 88)
@Composable
fun MaterialLcdGlowToxic() = LcdGlowProbe(OldgeSkin.Toxic)

@ViddikScreenshot(group = "MaterialLcdGlow", name = "Media", width = 390, height = 88)
@Composable
fun MaterialLcdGlowMedia() = LcdGlowProbe(OldgeSkin.Media)

@ViddikScreenshot(group = "MaterialLcdGlow", name = "Crystal", width = 390, height = 88)
@Composable
fun MaterialLcdGlowCrystal() = LcdGlowProbe(OldgeSkin.Crystal)

private fun ring(color: androidx.compose.ui.graphics.Color) =
    io.github.youndie.oldge.tokens.OldgeShadow(
        inset = false,
        offsetX = 0.dp,
        offsetY = 0.dp,
        blur = 0.dp,
        spread = 1.dp,
        color = color,
    )

@ViddikScreenshot(group = "MaterialBody", name = "Toxic", width = 390, height = 200)
@Composable
fun MaterialBodyToxic() = BodyProbe(OldgeSkin.Toxic)

@ViddikScreenshot(group = "MaterialBody", name = "Media", width = 390, height = 200)
@Composable
fun MaterialBodyMedia() = BodyProbe(OldgeSkin.Media)

@ViddikScreenshot(group = "MaterialBody", name = "Crystal", width = 390, height = 200)
@Composable
fun MaterialBodyCrystal() = BodyProbe(OldgeSkin.Crystal)

@ViddikScreenshot(group = "MaterialPanel", name = "Toxic", width = 390, height = 96)
@Composable
fun MaterialPanelToxic() = PanelProbe(OldgeSkin.Toxic)

@ViddikScreenshot(group = "MaterialPanel", name = "Media", width = 390, height = 96)
@Composable
fun MaterialPanelMedia() = PanelProbe(OldgeSkin.Media)

@ViddikScreenshot(group = "MaterialPanel", name = "Crystal", width = 390, height = 96)
@Composable
fun MaterialPanelCrystal() = PanelProbe(OldgeSkin.Crystal)

@ViddikScreenshot(group = "MaterialBezel", name = "Toxic", width = 390, height = 76)
@Composable
fun MaterialBezelToxic() = BezelProbe(OldgeSkin.Toxic)

@ViddikScreenshot(group = "MaterialBezel", name = "Media", width = 390, height = 76)
@Composable
fun MaterialBezelMedia() = BezelProbe(OldgeSkin.Media)

@ViddikScreenshot(group = "MaterialBezel", name = "Crystal", width = 390, height = 76)
@Composable
fun MaterialBezelCrystal() = BezelProbe(OldgeSkin.Crystal)

@ViddikScreenshot(group = "MaterialOrb", name = "Toxic", width = 390, height = 80)
@Composable
fun MaterialOrbToxic() = OrbProbe(OldgeSkin.Toxic)

@ViddikScreenshot(group = "MaterialOrb", name = "Media", width = 390, height = 80)
@Composable
fun MaterialOrbMedia() = OrbProbe(OldgeSkin.Media)

@ViddikScreenshot(group = "MaterialOrb", name = "Crystal", width = 390, height = 80)
@Composable
fun MaterialOrbCrystal() = OrbProbe(OldgeSkin.Crystal)

@ViddikScreenshot(group = "MaterialGloss", name = "Toxic", width = 390, height = 76)
@Composable
fun MaterialGlossToxic() = GlossProbe(OldgeSkin.Toxic)

@ViddikScreenshot(group = "MaterialGloss", name = "Media", width = 390, height = 76)
@Composable
fun MaterialGlossMedia() = GlossProbe(OldgeSkin.Media)

@ViddikScreenshot(group = "MaterialGloss", name = "Crystal", width = 390, height = 76)
@Composable
fun MaterialGlossCrystal() = GlossProbe(OldgeSkin.Crystal)
