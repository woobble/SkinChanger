package me.woobb.skinchanger

import kotlinx.coroutines.CoroutineScope
import me.woobb.skinchanger.internal.SkinChangerImpl
import me.woobb.skinchanger.internal.Skins
import me.woobb.skinchanger.service.PlayerSkinService
import me.woobb.skinchanger.skin.Skin
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.io.Closeable

public interface SkinChanger :
    Closeable,
    CoroutineScope {
    public val playerSkinService: PlayerSkinService

    public suspend fun setSkin(
        player: Player,
        skin: Skin,
    ): Boolean

    public fun resetDefaultSkin(player: Player): Boolean

    public fun hasActiveSkin(player: Player): Boolean

    public companion object {
        public val Efe: Skin = Skins.Efe
        public val Makena: Skin = Skins.Makena
        public val Ari: Skin = Skins.Ari
        public val Kai: Skin = Skins.Kai
        public val Alex: Skin = Skins.Alex
        public val Noor: Skin = Skins.Noor
        public val Steve: Skin = Skins.Steve
        public val Sunny: Skin = Skins.Sunny
        public val Zuri: Skin = Skins.Zuri
    }
}

public fun SkinChanger(plugin: Plugin): SkinChanger = SkinChangerImpl(plugin)
