package io.github.youndie.oldge.theme

import io.github.youndie.oldge.tokens.OldgeColors
import io.github.youndie.oldge.tokens.OldgeShadows

/**
 * The design system's three skins. A skin changes the frame, the body and the LCD glass; the
 * components and the lime accent stay (the design system's README, the section on skins).
 */
public enum class OldgeSkin(
    public val colors: OldgeColors,
    public val shadows: OldgeShadows,
) {
    /** Near-black green body, graphite frame, green neon on the displays. The default. */
    Toxic(OldgeColors.Toxic, OldgeShadows.Toxic),

    /** Royal blue with a light-blue glow, a glossy blue frame, a cold blue display. */
    Media(OldgeColors.Media, OldgeShadows.Media),

    /** Light: translucent mint-grey plastic, a dark smoked frame, grey-green liquid crystal. */
    Crystal(OldgeColors.Crystal, OldgeShadows.Crystal),
}
