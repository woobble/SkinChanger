package me.woobb.skinchanger.internal.mojang

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class MojangClientTest {
    @Test
    fun `test get player uuid`() {
        val mockEngine =
            MockEngine {
                respond(
                    content =
                        """
                        {
                            "id": "069a79f444e94726a5befca90e38aaf5",
                            "name": "Notch"
                        }
                        """.trimIndent(),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

        MojangClient(mockEngine).use {
            val uuid = runBlocking { it.getPlayerUUID(PLAYER1_NAME) }
            assertEquals(PLAYER1_UUID, uuid)
        }
    }

    @Test
    fun `test get player uuid with empty name`() {
        MojangClient().use {
            assertThrows<IllegalArgumentException> {
                runBlocking { it.getPlayerUUID("") }
            }
        }
    }

    @Test
    fun `test get player uuid not found`() {
        val mockEngine =
            MockEngine {
                respond(
                    content =
                        """
                        {
                          "path" : "/users/profiles/minecraft/ThisUserDoesNotExist",
                          "errorMessage" : "Couldn't find any profile with name ThisUserDoesNotExist"
                        }
                        """.trimIndent(),
                    status = HttpStatusCode.NotFound,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

        MojangClient(mockEngine).use {
            assertThrows<PlayerNotFoundException> {
                runBlocking { it.getPlayerUUID("ThisUserDoesNotExist") }
            }
        }
    }

    @Test
    fun `test get player uuid bad request`() {
        val mockEngine =
            MockEngine {
                respond(
                    content =
                        """
                        {
                          "path" : "/users/profiles/minecraft/ThisIsAVeryLongTestNameWhichWillFail",
                          "error" : "CONSTRAINT_VIOLATION",
                          "errorMessage" : "getProfileName.name: Invalid profile name, getProfileName.name: size must be between 1 and 25"
                        }
                        """.trimIndent(),
                    status = HttpStatusCode.BadRequest,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

        MojangClient(mockEngine).use {
            assertThrows<MojangException> {
                runBlocking { it.getPlayerUUID("ThisIsAVeryLongTestNameWhichWillFail") }
            }
        }
    }

    @Test
    fun `test get player uuid rate limit exceeded`() {
        val mockEngine =
            MockEngine {
                respond(
                    content = "",
                    status = HttpStatusCode.TooManyRequests,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

        MojangClient(mockEngine).use {
            assertThrows<RateLimitException> {
                runBlocking { it.getPlayerUUID(PLAYER1_NAME) }
            }
        }
    }

    @Test
    fun `test get player uuid different status code`() {
        val mockEngine =
            MockEngine {
                respond(
                    content = "",
                    status = HttpStatusCode.InternalServerError,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

        MojangClient(mockEngine).use {
            assertThrows<MojangException> {
                runBlocking { it.getPlayerUUID(PLAYER1_NAME) }
            }
        }
    }

    @Test
    fun `test get player uuids`() {
        val mockEngine =
            MockEngine {
                respond(
                    content =
                        """
                        [
                            {
                                "id": "069a79f444e94726a5befca90e38aaf5",
                                "name": "Notch"
                            },
                            {
                                "id": "853c80ef3c3749fdaa49938b674adae6",
                                "name": "jeb_"
                            }
                        ]
                        """.trimIndent(),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

        MojangClient(mockEngine).use {
            val uuids = runBlocking { it.getPlayerUUIDs(listOf(PLAYER1_NAME, PLAYER2_NAME)) }
            assertEquals(
                setOf(
                    PLAYER1_UUID,
                    PLAYER2_UUID,
                ),
                uuids,
            )
        }
    }

    @Test
    fun `test get player uuids with more than 10 names`() {
        MojangClient().use {
            assertThrows<IllegalArgumentException> {
                runBlocking { it.getPlayerUUIDs(List(11) { "Player$it" }) }
            }
        }
    }

    @Test
    fun `test get player uuids bad request`() {
        val mockEngine =
            MockEngine {
                respond(
                    content =
                        """
                        {
                          "path" : "/profiles/minecraft",
                          "error" : "CONSTRAINT_VIOLATION",
                          "errorMessage" : "Invalid profile name"
                        }
                        """.trimIndent(),
                    status = HttpStatusCode.BadRequest,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

        MojangClient(mockEngine).use {
            assertThrows<MojangException> {
                runBlocking { it.getPlayerUUIDs(listOf("Player1", "Player2")) }
            }
        }
    }

    @Test
    fun `test get player uuids rate limit exceeded`() {
        val mockEngine =
            MockEngine {
                respond(
                    content = "",
                    status = HttpStatusCode.TooManyRequests,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

        MojangClient(mockEngine).use {
            assertThrows<RateLimitException> {
                runBlocking { it.getPlayerUUIDs(listOf("Notch", "jeb_")) }
            }
        }
    }

    @Test
    fun `test get player uuids different status code`() {
        val mockEngine =
            MockEngine {
                respond(
                    content = "",
                    status = HttpStatusCode.InternalServerError,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

        MojangClient(mockEngine).use {
            assertThrows<MojangException> {
                runBlocking { it.getPlayerUUIDs(listOf("Notch", "jeb_")) }
            }
        }
    }

    @Test
    fun `test get player uuids empty`() {
        MojangClient().use {
            assertThrows<IllegalArgumentException> {
                runBlocking { it.getPlayerUUIDs(emptyList()) }
            }
        }
    }

    @Test
    fun `test get player profile`() {
        val mockEngine =
            MockEngine {
                respond(
                    content =
                        """
                        {
                          "id" : "069a79f444e94726a5befca90e38aaf5",
                          "name" : "Notch",
                          "properties" : [ {
                            "name" : "textures",
                            "value" : "ewogICJ0aW1lc3RhbXAiIDogMTczNDA0NzA4OTUzMywKICAicHJvZmlsZUlkIiA6ICIwNjlhNzlmNDQ0ZTk0NzI2YTViZWZjYTkwZTM4YWFmNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb3RjaCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yOTIwMDlhNDkyNWI1OGYwMmM3N2RhZGMzZWNlZjA3ZWE0Yzc0NzJmNjRlMGZkYzMyY2U1NTIyNDg5MzYyNjgwIgogICAgfQogIH0KfQ==",
                            "signature" : "EfbQD66T83EsIozic+jB8nOR/9RnlZT7DnwtZsDuqke0QCLH0JMNyRJgog4MbNr9+qt1TqfhwHGPYtGYtH67iWes6+I2jPcT5Ev37m8jS4YeIFBRtCzeAuXEiO4k0f4JDY+JaHpEcLbxhoN/qc4xnCUORT/xzNyGgWoMqtaJLXY65ZlmkpuQmIUqt2Q9UMYGV5Zh+UVBtE6BKrSDuELGrp++clVs7fjbPE3APii+LokVfLxJNw88t59OE6UZbJXCV3OqlUvVnAI1cJDG0+KgwWOAWqns0danInBVxFUIaflL/a0G1QfjWX8PuAjKBb3OiIrzh9H/4haevyR16C/e2r7IY68iIj7Q1h67mwQy089rJ4Vw+Ga869FBUY7Zu1IDtNB3OgnIywTihKXtY/VVcph+lhSe6S3m2mGBW9ns8pwn2J6JfrqhrnRYeGL35rQ0VZgbKhkiRPdTQN9i2FMMaij6iXlwV96YoTxRy7Gw5HxSsUFUPLJyZK6C+zoavesE678RgMB8cVxtC4/otJX16xtQu7f/e0j/V9k2jiG0G8rufOhl9u6KLvg1T0DbZh8JffZXRUuXfHV5IP3G/7YUhmNYfhw/xYd/hj4lBztydkvVPppHlhJikvRtoKzDySIsZNe/pIqKmAMuitGfzxgk2gUxNpunA3qBvJOlIyPCOkY="
                          } ],
                          "profileActions" : [ ]
                        }
                        """.trimIndent(),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

        MojangClient(mockEngine).use {
            val profile = runBlocking { it.getPlayerProfile(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5")) }
            assertEquals("Notch", profile.name)
            assertEquals(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"), profile.uniqueId)
            assertEquals(1, profile.properties.size)
            assertEquals("textures", profile.properties[0].name)
        }
    }

    @Test
    fun `test get player profile unsigned`() {
        val mockEngine =
            MockEngine {
                respond(
                    content =
                        """
                        {
                          "id" : "069a79f444e94726a5befca90e38aaf5",
                          "name" : "Notch",
                          "properties" : [ {
                            "name" : "textures",
                            "value" : "ewogICJ0aW1lc3RhbXAiIDogMTczNDA0NzA4OTUzMywKICAicHJvZmlsZUlkIiA6ICIwNjlhNzlmNDQ0ZTk0NzI2YTViZWZjYTkwZTM4YWFmNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb3RjaCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yOTIwMDlhNDkyNWI1OGYwMmM3N2RhZGMzZWNlZjA3ZWE0Yzc0NzJmNjRlMGZkYzMyY2U1NTIyNDg5MzYyNjgwIgogICAgfQogIH0KfQ=="
                          } ],
                          "profileActions" : [ ]
                        }
                        """.trimIndent(),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

        MojangClient(mockEngine).use {
            val profile = runBlocking { it.getPlayerProfile(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"), unsigned = true) }
            assertEquals("Notch", profile.name)
            assertEquals(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"), profile.uniqueId)
            assertEquals(1, profile.properties.size)
            assertEquals("textures", profile.properties[0].name)
        }
    }

    @Test
    fun `test get player profile not found`() {
        val mockEngine =
            MockEngine {
                respond(
                    content = "",
                    status = HttpStatusCode.NotFound,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

        MojangClient(mockEngine).use {
            assertThrows<ProfileNotFoundException> {
                runBlocking { it.getPlayerProfile(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5")) }
            }
        }
    }

    @Test
    fun `test get player profile rate limit exceeded`() {
        val mockEngine =
            MockEngine {
                respond(
                    content = "",
                    status = HttpStatusCode.TooManyRequests,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

        MojangClient(mockEngine).use {
            assertThrows<RateLimitException> {
                runBlocking { it.getPlayerProfile(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5")) }
            }
        }
    }

    @Test
    fun `test get player profile bad request`() {
        val mockEngine =
            MockEngine {
                respond(
                    content =
                        """
                        {
                          "path" : "/session/minecraft/profile/069a79f444e94726a5befca90e38aaf5",
                          "error" : "IllegalArgumentException",
                          "errorMessage" : "No profile with the name 069a79f444e94726a5befca90e38aaf5"
                        }
                        """.trimIndent(),
                    status = HttpStatusCode.BadRequest,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

        MojangClient(mockEngine).use {
            assertThrows<MojangException> {
                runBlocking { it.getPlayerProfile(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5")) }
            }
        }
    }

    @Test
    fun `test get player profile different status code`() {
        val mockEngine =
            MockEngine {
                respond(
                    content = "",
                    status = HttpStatusCode.InternalServerError,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

        MojangClient(mockEngine).use {
            assertThrows<MojangException> {
                runBlocking { it.getPlayerProfile(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5")) }
            }
        }
    }

    companion object {
        private const val PLAYER1_NAME = "notch"
        private const val PLAYER2_NAME = "jeb_"
        private val PLAYER1_UUID: UUID = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5")
        private val PLAYER2_UUID: UUID = UUID.fromString("853c80ef-3c37-49fd-aa49-938b674adae6")
    }
}
