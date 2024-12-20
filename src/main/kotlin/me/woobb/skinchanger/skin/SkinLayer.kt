package me.woobb.skinchanger.skin

public interface SkinLayer {
    public val mask: Byte

    public val layers: Collection<SkinLayers>

    public operator fun contains(layer: SkinLayers): Boolean

    public operator fun plusAssign(layer: SkinLayers)

    public operator fun minusAssign(layer: SkinLayers)

    public operator fun plusAssign(layers: Collection<SkinLayers>)

    public operator fun minusAssign(layers: Collection<SkinLayers>)
}

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
