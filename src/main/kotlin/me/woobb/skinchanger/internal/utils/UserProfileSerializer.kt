@file:UseContextualSerialization(UserProfile::class)

package me.woobb.skinchanger.internal.utils

import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.protocol.player.UserProfile
import kotlinx.serialization.KSerializer
import kotlinx.serialization.UseContextualSerialization
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import java.util.*

internal object UserProfileSerializer : KSerializer<UserProfile> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("com.github.retrooper.packetevents.protocol.player.UserProfile") {
            element("id", UUIDSerializer.descriptor)
            element<String>("name")
            element("properties", ListSerializer(TexturePropertySerializer).descriptor)
        }

    override fun deserialize(decoder: Decoder): UserProfile {
        var uuid: UUID? = null
        var name: String? = null
        var textureProperties: List<TextureProperty>? = null

        decoder.decodeStructure(descriptor) {
            loop@ while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> uuid = decodeSerializableElement(descriptor, 0, UUIDSerializer)
                    1 -> name = decodeStringElement(descriptor, 1)
                    2 -> textureProperties = decodeSerializableElement(descriptor, 2, ListSerializer(TexturePropertySerializer))
                    CompositeDecoder.DECODE_DONE -> break@loop
                    else -> error("Unexpected index: $index")
                }
            }
        }

        require(uuid != null) { "UUID is required" }
        require(name != null) { "Name is required" }
        require(textureProperties != null) { "Texture properties are required" }

        return UserProfile(uuid, name, textureProperties)
    }

    override fun serialize(
        encoder: Encoder,
        value: UserProfile,
    ) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, UUIDSerializer, value.uuid)
            encodeStringElement(descriptor, 1, value.name)
            encodeSerializableElement(descriptor, 2, ListSerializer(TexturePropertySerializer), value.textureProperties)
        }
    }
}
