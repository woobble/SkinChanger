package me.woobb.skinchanger.skin

/**
 * Represents the Minecraft skin layers.
 */
public interface SkinLayer {
    /**
     * The mask of the skin layers.
     */
    public val mask: Byte

    /**
     * The collection of skin layers.
     */
    public val layers: Collection<SkinLayers>

    /**
     * Checks if the skin layer contains the specified layer.
     *
     * @param layer The skin layer to check.
     * @return True if the skin layer contains the specified layer, false otherwise.
     */
    public operator fun contains(layer: SkinLayers): Boolean

    /**
     * Adds the specified layer to the skin layer.
     *
     * @param layer The skin layer to add.
     */
    public operator fun plusAssign(layer: SkinLayers)

    /**
     * Removes the specified layer from the skin layer.
     *
     * @param layer The skin layer to remove.
     */
    public operator fun minusAssign(layer: SkinLayers)

    /**
     * Adds the specified layers to the skin layer.
     *
     * @param layers The skin layers to add.
     */
    public operator fun plusAssign(layers: Collection<SkinLayers>)

    /**
     * Removes the specified layers from the skin layer.
     *
     * @param layers The skin layers to remove.
     */
    public operator fun minusAssign(layers: Collection<SkinLayers>)
}

/**
 * Enum representing the Minecraft skin layers.
 *
 * @property mask The mask of the skin layer.
 */
public enum class SkinLayers(
    public val mask: Byte,
) {
    CAPE(0x01),
    JACKET(0x02),
    LEFT_SLEEVE(0x04),
    RIGHT_SLEEVE(0x08),
    LEFT_PANTS_LEG(0x10),
    RIGHT_PANTS_LEG(0x20),
    HAT(0x40),
    ;

    internal companion object
}
