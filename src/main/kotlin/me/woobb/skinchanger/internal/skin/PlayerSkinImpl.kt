package me.woobb.skinchanger.internal.skin

import me.woobb.skinchanger.internal.utils.WrappedTextureProperty
import me.woobb.skinchanger.skin.PlayerSkin
import org.bukkit.OfflinePlayer

internal class PlayerSkinImpl(
    override val holder: OfflinePlayer,
    value: WrappedTextureProperty,
) : CustomSkinImpl(holder.name, holder.uniqueId, value),
    PlayerSkin {
    override fun toString(): String = "PlayerSkin(uuid=$uniqueId, holder=$holder, textures=$value)"
}