package me.woobb.skinchanger.skin

import org.bukkit.OfflinePlayer

public interface PlayerSkin : CustomSkin {
    public val holder: OfflinePlayer
}
