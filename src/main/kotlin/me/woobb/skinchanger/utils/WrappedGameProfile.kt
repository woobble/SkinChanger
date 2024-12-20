package me.woobb.skinchanger.utils

import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.protocol.player.UserProfile
import me.woobb.skinchanger.internal.utils.PacketEventsTextureProperty
import me.woobb.skinchanger.internal.utils.PacketEventsUserProfile
import java.util.*

/**
 * Represents a wrapped game profile.
 *
 * @see WrappedTextureProperty
 */
public interface WrappedGameProfile {
    public var uniqueId: UUID
    public var name: String
    public var properties: List<WrappedTextureProperty>
}

/**
 * Represents a wrapped texture property.
 */
public interface WrappedTextureProperty {
    public val name: String
    public val value: String
    public val signature: String?
}

/**
 * Creates a new wrapped texture property with the given name, value, and signature.
 */
public fun WrappedTextureProperty(
    name: String,
    value: String,
    signature: String?,
): WrappedTextureProperty = PacketEventsTextureProperty(TextureProperty(name, value, signature))

/**
 * Creates a new wrapped game profile with the given unique identifier, name, and properties.
 */
internal fun WrappedGameProfile(packetEventsUserProfile: UserProfile): WrappedGameProfile = PacketEventsUserProfile(packetEventsUserProfile)
