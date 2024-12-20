package me.woobb.skinchanger.internal.mojang

import java.util.*

public open class MojangException : RuntimeException {
    internal constructor(message: String) : super(message)
    internal constructor(message: String, cause: Throwable) : super(message, cause)
}

public class PlayerNotFoundException(
    public val playerName: String,
) : MojangException("Player not found: $playerName")

public class ProfileNotFoundException(
    public val id: UUID,
) : MojangException("Profile not found: $id")

public class RateLimitException : MojangException("Rate limit exceeded")
