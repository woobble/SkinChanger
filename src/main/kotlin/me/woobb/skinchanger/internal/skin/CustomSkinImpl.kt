package me.woobb.skinchanger.internal.skin

import me.woobb.skinchanger.internal.utils.WrappedTextureProperty
import me.woobb.skinchanger.skin.CustomSkin
import java.util.*

internal open class CustomSkinImpl(
    override val name: String? = null,
    uniqueId: UUID = UUID.randomUUID(),
    override val value: WrappedTextureProperty,
) : AbstractSkin(uniqueId),
    CustomSkin {
    override fun toString(): String = "CustomSkin(uuid=$uniqueId, textures=$value)"
}
