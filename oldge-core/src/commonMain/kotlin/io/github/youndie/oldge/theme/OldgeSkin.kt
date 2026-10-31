package io.github.youndie.oldge.theme

import io.github.youndie.oldge.tokens.OldgeColors
import io.github.youndie.oldge.tokens.OldgeShadows

/**
 * The design system's three skins. A skin changes the frame, the body and the LCD glass; the
 * components and the lime accent stay (the design system's README, the section on skins).
 *
 * To remember a user's choice, store [id] and read it back with [byId] (B-76). The id never changes
 * once a skin is released; the enum constant's `name` is source, and a rename would turn every stored
 * choice into an unknown one.
 */
public enum class OldgeSkin(
    public val id: String,
    public val colors: OldgeColors,
    public val shadows: OldgeShadows,
) {
    /** Near-black green body, graphite frame, green neon on the displays. The default. */
    Toxic("toxic", OldgeColors.Toxic, OldgeShadows.Toxic),

    /** Royal blue with a light-blue glow, a glossy blue frame, a cold blue display. */
    Media("media", OldgeColors.Media, OldgeShadows.Media),

    /** Light: translucent mint-grey plastic, a dark smoked frame, grey-green liquid crystal. */
    Crystal("crystal", OldgeColors.Crystal, OldgeShadows.Crystal),
    ;

    public companion object {
        /** The skin whose [id] this is, or `null` for an id no skin has (one stored by a newer release, say). */
        public fun byId(id: String): OldgeSkin? = entries.firstOrNull { it.id == id }
    }
}
