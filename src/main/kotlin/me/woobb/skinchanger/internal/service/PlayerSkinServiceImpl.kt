package me.woobb.skinchanger.internal.service

import me.woobb.skinchanger.internal.SkinChangerImpl
import me.woobb.skinchanger.internal.skin.PlayerSkinImpl
import me.woobb.skinchanger.internal.utils.gameProfile
import me.woobb.skinchanger.service.PlayerSkinService
import me.woobb.skinchanger.skin.PlayerSkin
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

internal class PlayerSkinServiceImpl(
    internal val skinChanger: SkinChangerImpl,
) : AbstractSkinService<PlayerSkin>(),
    PlayerSkinService {
    private val skinCache: MutableMap<UUID, PlayerSkin> = ConcurrentHashMap()

    override suspend fun getPlayerSkin(uniqueId: UUID): PlayerSkin {
        val player = Bukkit.getPlayer(uniqueId)
        if (player != null && player.isOnline) {
            return getPlayerSkin(player)
        }

        return fetchPlayerSkin(uniqueId)
    }

    override suspend fun getPlayerSkin(playerName: String): PlayerSkin {
        val player = Bukkit.getPlayer(playerName)
        if (player != null && player.isOnline) {
            return getPlayerSkin(player)
        }

        val uniqueId = skinChanger.mojangClient.getPlayerUUID(playerName)
        return fetchPlayerSkin(uniqueId)
    }

    override fun getPlayerSkin(player: Player): PlayerSkin {
        skinCache[player.uniqueId]?.let {
            return it
        }
        val textureProperty =
            player.gameProfile.properties.find { it.name == "textures" } ?: throw IllegalArgumentException("Texture property not found")

        return PlayerSkinImpl(
            player,
            textureProperty,
        ).also { skinCache[player.uniqueId] = it }
    }

    override suspend fun getPlayerSkin(holder: OfflinePlayer): PlayerSkin {
        val player = holder.player
        if (player != null && holder.isOnline) {
            return getPlayerSkin(player)
        }

        return fetchPlayerSkin(holder.uniqueId)
    }

    private suspend fun fetchPlayerSkin(uniqueId: UUID): PlayerSkin {
        skinCache[uniqueId]?.let {
            return it
        }

        val playerProfile = skinChanger.mojangClient.getPlayerProfile(uniqueId)
        return PlayerSkinImpl(
            Bukkit.getOfflinePlayer(uniqueId),
            playerProfile.properties.find { it.name == "textures" } ?: throw IllegalArgumentException("Texture property not found"),
        ).also {
            skinCache[uniqueId] = it
        }
    }

    companion object {
        private val STEVE = UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7")
    }
}
