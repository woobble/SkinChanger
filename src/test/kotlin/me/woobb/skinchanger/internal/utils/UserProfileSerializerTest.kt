package me.woobb.skinchanger.internal.utils

import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.protocol.player.UserProfile
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class UserProfileSerializerTest {
    private val json = Json { encodeDefaults = true }

    @Test
    fun `test UserProfile serialization`() {
        val userProfile =
            UserProfile(
                UUID.randomUUID(),
                "test",
                listOf(
                    TextureProperty("test", "test", "test"),
                ),
            )
        val serialized = json.encodeToString(UserProfileSerializer, userProfile)
        val expected = """{"id":"${userProfile.uuid.toString().replace(
            "-",
            "",
        )}","name":"${userProfile.name}","properties":[{"name":"test","value":"test","signature":"test"}]}"""
        assertEquals(expected, serialized)
    }

    @Test
    fun `test UserProfile deserialization`() {
        val uuid = UUID.randomUUID()
        val jsonString = """{"id":"${uuid.toString().replace(
            "-",
            "",
        )}","name":"test","properties":[{"name":"test","value":"test","signature":"test"}]}"""
        val deserialized = json.decodeFromString(UserProfileSerializer, jsonString)
        val expected =
            UserProfile(
                uuid,
                "test",
                listOf(
                    TextureProperty("test", "test", "test"),
                ),
            )
        assertEquals(expected.uuid, deserialized.uuid)
        assertEquals(expected.name, deserialized.name)
        assertEquals(expected.textureProperties[0].name, deserialized.textureProperties[0].name)
        assertEquals(expected.textureProperties[0].value, deserialized.textureProperties[0].value)
        assertEquals(expected.textureProperties[0].signature, deserialized.textureProperties[0].signature)
    }
}
