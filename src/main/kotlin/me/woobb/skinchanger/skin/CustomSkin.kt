package me.woobb.skinchanger.skin

import me.woobb.skinchanger.internal.skin.CustomSkinImpl
import me.woobb.skinchanger.internal.utils.Holder
import me.woobb.skinchanger.utils.WrappedTextureProperty
import java.util.*

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

/**
 * Creates a new custom skin with the given name and textures.
 *
 * @param name The custom name of the skin.
 * @param textures The textures of the skin.
 * @return The created custom skin.
 */
public fun CustomSkin(
    name: String,
    textures: WrappedTextureProperty,
): CustomSkin =
    CustomSkin(
        name,
        UUID.randomUUID(),
        textures,
    )

/**
 * Creates a new custom skin with the given name, unique identifier, and textures.
 *
 * @param name The custom name of the skin.
 * @param uuid The unique identifier of the skin.
 * @param textures The textures of the skin.
 * @return The created custom skin.
 */
public fun CustomSkin(
    name: String,
    uuid: UUID,
    textures: WrappedTextureProperty,
): CustomSkin =
    CustomSkinImpl(
        name,
        uuid,
        textures,
    )
