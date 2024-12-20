package me.woobb.skinchanger.internal.utils

import com.github.retrooper.packetevents.protocol.player.TextureProperty
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TexturePropertySerializerTest {
    private val json = Json { encodeDefaults = true }

    @Test
    fun `test TextureProperty serialization`() {
        val textureProperty = TextureProperty("test", "test", "test")
        val serialized = json.encodeToString(TexturePropertySerializer, textureProperty)
        val expected = """{"name":"${textureProperty.name}","value":"${textureProperty.value}","signature":"${textureProperty.signature}"}"""
        assertEquals(expected, serialized)
    }

    @Test
    fun `test TextureProperty deserialization`() {
        val textureProperty = TextureProperty("test", "test", "test")
        val jsonString = """{"name":"${textureProperty.name}","value":"${textureProperty.value}","signature":"${textureProperty.signature}"}"""
        val deserialized = json.decodeFromString(TexturePropertySerializer, jsonString)
        val expected = TextureProperty("test", "test", "test")
        assertEquals(expected.name, deserialized.name)
        assertEquals(expected.value, deserialized.value)
        assertEquals(expected.signature, deserialized.signature)
    }
}
