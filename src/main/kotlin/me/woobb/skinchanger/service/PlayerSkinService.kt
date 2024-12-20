package me.woobb.skinchanger.service

import me.woobb.skinchanger.skin.PlayerSkin
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

public interface PlayerSkinService : SkinService<PlayerSkin> {
    public suspend fun getPlayerSkin(uniqueId: UUID): PlayerSkin?

    public suspend fun getPlayerSkin(playerName: String): PlayerSkin?

    public fun getPlayerSkin(player: Player): PlayerSkin

    public suspend fun getPlayerSkin(holder: OfflinePlayer): PlayerSkin?
}
