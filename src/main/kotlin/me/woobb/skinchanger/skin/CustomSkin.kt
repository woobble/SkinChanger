package me.woobb.skinchanger.skin

import me.woobb.skinchanger.internal.utils.Holder
import me.woobb.skinchanger.internal.utils.WrappedTextureProperty

/**
 * Represents a custom skin that can be applied to a player.
 */
public interface CustomSkin :
    Skin,
    Holder<WrappedTextureProperty> {
    /**
     * The custom name of the skin.
     */
    public val name: String?
}
