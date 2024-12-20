package me.woobb.skinchanger.skin

import me.woobb.skinchanger.internal.utils.Holder
import me.woobb.skinchanger.internal.utils.WrappedTextureProperty

public interface CustomSkin :
    Skin,
    Holder<WrappedTextureProperty> {
    public val name: String?
}
