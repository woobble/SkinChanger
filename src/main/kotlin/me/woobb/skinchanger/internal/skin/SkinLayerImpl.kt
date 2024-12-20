package me.woobb.skinchanger.internal.skin

import me.woobb.skinchanger.skin.SkinLayer
import me.woobb.skinchanger.skin.SkinLayers
import me.woobb.skinchanger.skin.SkinLayers.entries
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

public class SkinLayerImpl internal constructor(
    mask: Byte = 0x00,
) : SkinLayer {
    public override var mask: Byte = mask
        private set

    public override val layers: Collection<SkinLayers>
        get() = SkinLayers.fromMask(mask)

    internal constructor(layers: List<SkinLayers>) : this(SkinLayers.toMask(layers))

    public override operator fun contains(layer: SkinLayers): Boolean = mask and layer.mask != 0x00.toByte()

    public override operator fun plusAssign(layer: SkinLayers) {
        mask = mask or layer.mask
    }

    public override operator fun minusAssign(layer: SkinLayers) {
        mask = mask and layer.mask.inv()
    }

    public override operator fun plusAssign(layers: Collection<SkinLayers>) {
        for (layer in layers) {
            mask = mask or layer.mask
        }
    }

    public override operator fun minusAssign(layers: Collection<SkinLayers>) {
        for (layer in layers) {
            mask = mask and layer.mask.inv()
        }
    }
}

@JvmName("fromMask")
internal fun SkinLayers.Companion.fromMask(mask: Byte): Collection<SkinLayers> {
    val layers = mutableSetOf<SkinLayers>()
    for (layer in entries) {
        if (layer.mask and mask != 0x00.toByte()) {
            layers.add(layer)
        }
    }
    return layers
}

@JvmName("fromMask")
internal fun SkinLayers.Companion.toMask(layers: Iterable<SkinLayers>): Byte {
    var mask = 0x00.toByte()
    for (layer in layers) {
        mask = mask or layer.mask
    }
    return mask
}
