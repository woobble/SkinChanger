package me.woobb.skinchanger.internal.utils

import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.protocol.player.UserProfile
import com.google.common.collect.Multimap
import me.woobb.skinchanger.utils.WrappedGameProfile
import me.woobb.skinchanger.utils.WrappedTextureProperty
import org.bukkit.entity.Player
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.util.*

internal class PacketEventsTextureProperty(
    private val delegate: TextureProperty,
) : WrappedTextureProperty {
    override val name: String
        get() = delegate.name
    override val value: String
        get() = delegate.value
    override val signature: String?
        get() = delegate.signature
}

internal class PacketEventsUserProfile(
    private val delegate: UserProfile,
) : WrappedGameProfile {
    override var uniqueId: UUID
        get() = delegate.uuid
        set(value) {
            delegate.uuid = value
        }
    override var name: String
        get() = delegate.name
        set(value) {
            delegate.name = value
        }
    override var properties: List<WrappedTextureProperty>
        get() = delegate.textureProperties.map { PacketEventsTextureProperty(it) }
        set(value) {
            delegate.textureProperties.clear()
            delegate.textureProperties.addAll(
                value.map {
                    TextureProperty(it.name, it.value, it.signature)
                },
            )
        }
}

internal class MojangProperty(
    private val delegate: Any,
) : WrappedTextureProperty {
    init {
        require(delegate::class.qualifiedName == MOJANG_PROPERTY_CLASS.name) {
            "Delegated object is not a Mojang Property"
        }
    }

    override val name: String
        get() = MOJANG_NAME_FIELD.get(delegate) as String
    override val value: String
        get() = MOJANG_VALUE_FIELD.get(delegate) as String

    override val signature: String?
        get() = MOJANG_SIGNATURE_FIELD.get(delegate) as String?

    companion object {
        val MOJANG_PROPERTY_CLASS: Class<*> = Class.forName("com.mojang.authlib.properties.Property")
        val MOJANG_PROPERTY_CONSTRUCTOR: Constructor<*> =
            MOJANG_PROPERTY_CLASS.getDeclaredConstructor(
                String::class.java,
                String::class.java,
                String::class.java,
            )
        val MOJANG_NAME_FIELD: Field = MOJANG_PROPERTY_CLASS.getDeclaredField("name").also { it.isAccessible = true }
        val MOJANG_VALUE_FIELD: Field = MOJANG_PROPERTY_CLASS.getDeclaredField("value").also { it.isAccessible = true }
        val MOJANG_SIGNATURE_FIELD: Field = MOJANG_PROPERTY_CLASS.getDeclaredField("signature").also { it.isAccessible = true }

        fun createMojangProperty(
            name: String,
            value: String,
            signature: String?,
        ): Any = MOJANG_PROPERTY_CONSTRUCTOR.newInstance(name, value, signature)
    }
}

internal class MojangGameProfile(
    private val delegate: Any,
) : WrappedGameProfile {
    @Suppress("UNCHECKED_CAST")
    private val mojangProperties = MOJANG_PROPERTIES_FIELD.get(delegate) as Multimap<String, Any>

    init {
        require(delegate::class.qualifiedName == MOJANG_GAME_PROFILE_CLASS.name) {
            "Delegated object is not a Mojang GameProfile"
        }
    }

    override var uniqueId: UUID
        get() = MOJANG_UUID_FIELD.get(delegate) as UUID
        set(value) {
            MOJANG_UUID_FIELD.set(delegate, value)
        }

    override var name: String
        get() = MOJANG_NAME_FIELD.get(delegate) as String
        set(value) {
            MOJANG_NAME_FIELD.set(delegate, value)
        }

    override var properties: List<WrappedTextureProperty>
        get() {
            return mojangProperties.entries().map { entry ->
                MojangProperty(entry.value!!)
            }
        }
        set(value) {
            mojangProperties.clear()
            value.forEach { property ->
                val mojangProperty =
                    MojangProperty.createMojangProperty(
                        property.name,
                        property.value,
                        property.signature,
                    )

                mojangProperties.put(property.name, mojangProperty)
            }
        }

    companion object {
        val MOJANG_GAME_PROFILE_CLASS: Class<*> = Class.forName("com.mojang.authlib.GameProfile")
        val MOJANG_UUID_FIELD: Field = MOJANG_GAME_PROFILE_CLASS.getDeclaredField("id").also { it.isAccessible = true }
        val MOJANG_NAME_FIELD: Field = MOJANG_GAME_PROFILE_CLASS.getDeclaredField("name").also { it.isAccessible = true }
        val MOJANG_PROPERTIES_FIELD: Field = MOJANG_GAME_PROFILE_CLASS.getDeclaredField("properties").also { it.isAccessible = true }
    }
}

internal fun WrappedGameProfile(mojangGameProfile: Any): WrappedGameProfile = MojangGameProfile(mojangGameProfile)

internal val Player.gameProfile: WrappedGameProfile
    get() {
        val gameProfileField =
            handle::class.java.superclass.declaredMethods
                .find {
                    it.returnType.name == MojangGameProfile.MOJANG_GAME_PROFILE_CLASS.name
                }.also { it?.isAccessible = true } ?: error("Cannot find game profile method")

        return WrappedGameProfile(gameProfileField.invoke(handle))
    }
