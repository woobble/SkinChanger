package me.woobb.skinchanger.internal.utils

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class UUIDSerializerTest {
    private val json = Json { encodeDefaults = true }

    @Test
    fun `test UUID serialization`() {
        val uuid = TestUUID(UUID.randomUUID())
        val serialized = json.encodeToString(TestUUID.serializer(), uuid)
        val expected = """{"uuid":"${uuid.uuid.toString().replace("-", "")}"}"""
        assertEquals(expected, serialized)
    }

    @Test
    fun `test UUID deserialization`() {
        val uuid = UUID.randomUUID()
        val jsonString = """{"uuid":"${uuid.toString().replace("-", "")}"}"""
        val deserialized = json.decodeFromString(TestUUID.serializer(), jsonString)
        val expected = TestUUID(uuid)
        assertEquals(expected, deserialized)
    }

    @Serializable
    private data class TestUUID(
        @Serializable(with = UUIDSerializer::class) val uuid: UUID,
    )
}
