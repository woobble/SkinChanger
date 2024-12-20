@file:UseContextualSerialization(TextureProperty::class)

package me.woobb.skinchanger.internal.utils

import com.github.retrooper.packetevents.protocol.player.TextureProperty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.UseContextualSerialization
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*

internal object TexturePropertySerializer : KSerializer<TextureProperty> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("com.github.retrooper.packetevents.protocol.player.TextureProperty") {
            element<String>("name")
            element<String>("value")
            element<String?>("signature")
        }

    override fun serialize(
        encoder: Encoder,
        value: TextureProperty,
    ) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name)
            encodeStringElement(descriptor, 1, value.value)
            if (value.signature != null) {
                encodeStringElement(descriptor, 2, value.signature ?: "")
            }
        }
    }

    override fun deserialize(decoder: Decoder): TextureProperty {
        var name: String? = null
        var value: String? = null
        var signature: String? = null

        decoder.decodeStructure(descriptor) {
            loop@ while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> name = decodeStringElement(descriptor, 0)
                    1 -> value = decodeStringElement(descriptor, 1)
                    2 -> signature = decodeStringElement(descriptor, 2)
                    CompositeDecoder.DECODE_DONE -> break@loop
                    else -> error("Unexpected index: $index")
                }
            }
        }

        require(name != null) { "Property name is required" }
        require(value != null) { "Property value is required" }

        return TextureProperty(name, value, signature)
    }
}
