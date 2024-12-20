package me.woobb.skinchanger.service

import me.woobb.skinchanger.skin.PlayerSkin
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

/**
 * Represents a service that provides player skins.
 */
public interface PlayerSkinService : SkinService<PlayerSkin> {
    /**
     * Gets the player skin of the player with the given unique identifier.
     *
     * @param uniqueId The unique identifier of the player.
     * @return The player skin of the player with the given unique identifier.
     * @throws IllegalArgumentException If the player with the given unique identifier does not exist.
     */
    public suspend fun getPlayerSkin(uniqueId: UUID): PlayerSkin

    /**
     * Gets the player skin of the player with the given name.
     *
     * @param playerName The name of the player.
     * @return The player skin of the player with the given name.
     * @throws IllegalArgumentException If the player with the given name does not exist.
     */
    public suspend fun getPlayerSkin(playerName: String): PlayerSkin

    /**
     * Gets the player skin of the given player.
     *
     * @param player The player.
     * @return The player skin of the given player.
     * @throws IllegalArgumentException If the player does not have a skin.
     */
    public fun getPlayerSkin(player: Player): PlayerSkin

    /**
     * Gets the player skin of the given offline player.
     *
     * @param holder The offline player.
     * @return The player skin of the given offline player.
     * @throws IllegalArgumentException If the offline player does not exist.
     */
    public suspend fun getPlayerSkin(holder: OfflinePlayer): PlayerSkin
}
