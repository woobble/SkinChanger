package me.woobb.skinchanger.skin

import org.bukkit.OfflinePlayer

/**
 * Represents a player skin that can be applied to a player.
 */
public interface PlayerSkin : CustomSkin {
    /**
     * The holder of the skin.
     */
    public val holder: OfflinePlayer
}
