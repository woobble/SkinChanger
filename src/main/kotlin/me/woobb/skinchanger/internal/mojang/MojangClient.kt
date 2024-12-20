@file:Suppress("ktlint:standard:no-wildcard-imports")

package me.woobb.skinchanger.internal.mojang

import com.github.retrooper.packetevents.protocol.player.UserProfile
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.apache5.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import me.woobb.skinchanger.internal.utils.UUIDSerializer
import me.woobb.skinchanger.internal.utils.UserProfileSerializer
import me.woobb.skinchanger.internal.utils.WrappedGameProfile
import java.util.*

internal class MojangClient(
    engine: HttpClientEngine = Apache5.create(),
) : AutoCloseable {
    // add serializer for UUID and UserProfile
    private val json =
        Json {
            ignoreUnknownKeys = true
            serializersModule =
                SerializersModule {
                    contextual(UserProfileSerializer)
                }
        }

    private companion object {
        const val API_MOJANG = "https://api.mojang.com"
        const val SESSIONSERVER_MOJANG = "https://sessionserver.mojang.com"
    }

    private val httpClient =
        HttpClient(engine) {
            engine {
                pipelining = true
                if (this is Apache5EngineConfig) {
                    followRedirects = true
                    socketTimeout = 10_000
                    connectTimeout = 10_000
                    connectionRequestTimeout = 20_000
                }
            }
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
                exponentialDelay()
            }
            install(ContentNegotiation) {
                json(json)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }
            install(HttpCache)
            install(Logging)
            CurlUserAgent()
        }

    private suspend inline fun <reified T> handleResponse(
        response: HttpResponse,
        notFound: () -> Throwable,
    ): T {
        if (!response.status.isSuccess()) {
            when (response.status) {
                HttpStatusCode.NotFound -> throw notFound()
                HttpStatusCode.TooManyRequests -> throw RateLimitException()
                HttpStatusCode.BadRequest -> {
                    val error = response.body<ErrorResponse>()
                    throw MojangException(error.errorMessage)
                }
                else -> throw MojangException("Request failed with status: ${response.status}")
            }
        }
        return response.body()
    }

    suspend fun getPlayerUUID(playerName: String): UUID {
        if (playerName.isEmpty()) {
            throw IllegalArgumentException("Player name cannot be empty")
        }

        val response =
            httpClient.get {
                contentType(ContentType.Application.Json)
                url("$API_MOJANG/users/profiles/minecraft/$playerName")
            }

        return handleResponse<QueryUUIDResponse>(response) {
            PlayerNotFoundException(playerName)
        }.id
    }

    suspend fun getPlayerUUIDs(playerNames: Collection<String>): Set<UUID> {
        require(playerNames.isNotEmpty()) { "Player names cannot be empty" }
        require(playerNames.size <= 10) { "Cannot query more than 10 players at once" }
        val response =
            httpClient.post {
                contentType(ContentType.Application.Json)
                url("$API_MOJANG/profiles/minecraft")
                setBody(playerNames)
            }

        return handleResponse<List<QueryUUIDResponse>>(response) {
            PlayerNotFoundException(playerNames.joinToString())
        }.map { it.id }.toSet()
    }

    suspend fun getPlayerProfile(
        uuid: UUID,
        unsigned: Boolean = false,
    ): WrappedGameProfile {
        val response =
            httpClient.get {
                contentType(ContentType.Application.Json)
                url("$SESSIONSERVER_MOJANG/session/minecraft/profile/${uuid.toString().replace("-", "")}?unsigned=$unsigned")
            }

        return WrappedGameProfile(
            handleResponse<UserProfile>(response) {
                ProfileNotFoundException(uuid)
            },
        )
    }

    override fun close() {
        httpClient.close()
    }
}

@Serializable
private data class ErrorResponse(
    val path: String,
    val error: String? = null,
    val errorMessage: String,
)

@Serializable
private data class QueryUUIDResponse(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    var demo: Boolean? = null,
    var legacy: Boolean? = null,
)
