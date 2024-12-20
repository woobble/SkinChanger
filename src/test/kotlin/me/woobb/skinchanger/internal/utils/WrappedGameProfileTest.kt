package me.woobb.skinchanger.internal.utils

import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.protocol.player.UserProfile
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import me.woobb.skinchanger.utils.WrappedTextureProperty
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class PacketEventsTexturePropertyTest {
    @Test
    fun `test mojang property`() {
        val delegate = TextureProperty("name", "value", "signature")
        val property = PacketEventsTextureProperty(delegate)

        assertEquals("name", property.name)
        assertEquals("value", property.value)
        assertEquals("signature", property.signature)
    }
}

class TextureEventsUserProfileTest {
    @Test
    fun `test mojang game profile`() {
        val delegate = UserProfile(UUID.randomUUID(), "name")
        val gameProfile = PacketEventsUserProfile(delegate)

        assertEquals(delegate.uuid, gameProfile.uniqueId)
        assertEquals(delegate.name, gameProfile.name)
    }

    @Test
    fun `test change mojang game profile`() {
        val delegate = UserProfile(UUID.randomUUID(), "name")
        val gameProfile = PacketEventsUserProfile(delegate)

        gameProfile.uniqueId = UUID.randomUUID()
        gameProfile.name = "newName"

        assertEquals(delegate.uuid, gameProfile.uniqueId)
        assertEquals(delegate.name, gameProfile.name)
    }

    @Test
    fun `test mojang game profile empty properties`() {
        val delegate = UserProfile(UUID.randomUUID(), "name")
        val gameProfile = PacketEventsUserProfile(delegate)

        assertEquals(emptyList<WrappedTextureProperty>(), gameProfile.properties)
    }

    @Test
    fun `test mojang game profile properties`() {
        val delegate = UserProfile(UUID.randomUUID(), "name")
        val property = TextureProperty("name", "value", "signature")
        delegate.textureProperties.add(property)
        val gameProfile = PacketEventsUserProfile(delegate)

        assertEquals(1, gameProfile.properties.size)
        assertEquals("name", gameProfile.properties[0].name)
        assertEquals("value", gameProfile.properties[0].value)
        assertEquals("signature", gameProfile.properties[0].signature)
    }

    @Test
    fun `test change mojang game profile properties`() {
        val delegate = UserProfile(UUID.randomUUID(), "name")
        val property = TextureProperty("name", "value", "signature")
        delegate.textureProperties.add(property)
        val gameProfile = PacketEventsUserProfile(delegate)

        val newProperty = MojangProperty(Property("newName", "newValue", "newSignature"))
        gameProfile.properties = listOf(newProperty)

        assertEquals(1, gameProfile.properties.size)
        assertEquals("newName", gameProfile.properties[0].name)
        assertEquals("newValue", gameProfile.properties[0].value)
        assertEquals("newSignature", gameProfile.properties[0].signature)
    }
}

class MojangPropertyTest {
    @Test
    fun `test mojang property`() {
        val delegate = Property("name", "value", "signature")
        val property = MojangProperty(delegate)

        assertEquals("name", property.name)
        assertEquals("value", property.value)
        assertEquals("signature", property.signature)
    }

    @Test
    fun `test mojang property invalid delegate`() {
        assertThrows<IllegalArgumentException> {
            MojangProperty(Any())
        }
    }
}

class MojangGameProfileTest {
    @Test
    fun `test mojang game profile`() {
        val delegate = GameProfile(UUID.randomUUID(), "name")
        val gameProfile = MojangGameProfile(delegate)

        assertEquals(delegate.id, gameProfile.uniqueId)
        assertEquals(delegate.name, gameProfile.name)
    }

    @Test
    fun `test change mojang game profile`() {
        val delegate = GameProfile(UUID.randomUUID(), "name")
        val gameProfile = MojangGameProfile(delegate)

        gameProfile.uniqueId = UUID.randomUUID()
        gameProfile.name = "newName"

        assertEquals(delegate.id, gameProfile.uniqueId)
        assertEquals(delegate.name, gameProfile.name)
    }

    @Test
    fun `test mojang game profile empty properties`() {
        val delegate = GameProfile(UUID.randomUUID(), "name")
        val gameProfile = MojangGameProfile(delegate)

        assertEquals(emptyList<WrappedTextureProperty>(), gameProfile.properties)
    }

    @Test
    fun `test mojang game profile properties`() {
        val delegate = GameProfile(UUID.randomUUID(), "name")
        val property = Property("name", "value", "signature")
        delegate.properties.put("name", property)
        val gameProfile = MojangGameProfile(delegate)

        assertEquals(1, gameProfile.properties.size)
        assertEquals("name", gameProfile.properties[0].name)
        assertEquals("value", gameProfile.properties[0].value)
        assertEquals("signature", gameProfile.properties[0].signature)
    }

    @Test
    fun `test change mojang game profile properties`() {
        val delegate = GameProfile(UUID.randomUUID(), "name")
        val property = Property("name", "value", "signature")
        delegate.properties.put("name", property)
        val gameProfile = MojangGameProfile(delegate)

        val newProperty = MojangProperty(Property("newName", "newValue", "newSignature"))
        gameProfile.properties = listOf(newProperty)

        assertEquals(1, gameProfile.properties.size)
        assertEquals("newName", gameProfile.properties[0].name)
        assertEquals("newValue", gameProfile.properties[0].value)
        assertEquals("newSignature", gameProfile.properties[0].signature)
    }

    @Test
    fun `test mojang game profile invalid delegate`() {
        assertThrows<IllegalArgumentException> {
            MojangGameProfile(Any())
        }
    }
}
