package me.woobb.skinchanger

import kotlinx.coroutines.CoroutineScope
import me.woobb.skinchanger.internal.SkinChangerImpl
import me.woobb.skinchanger.internal.Skins
import me.woobb.skinchanger.service.PlayerSkinService
import me.woobb.skinchanger.skin.Skin
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.io.Closeable

/**
 * Change the skin of a player to a desired skin.
 */
public interface SkinChanger :
    Closeable,
    CoroutineScope {
    /**
     * The service to manage player skins.
     */
    public val playerSkinService: PlayerSkinService

    /**
     * Sets the skin of the player to the desired skin.
     *
     * @param player The player whose skin to set.
     * @param skin The skin to set for the player.
     * @return `true` if the skin was set successfully, `false` otherwise.
     */
    public suspend fun setSkin(
        player: Player,
        skin: Skin,
    ): Boolean

    /**
     * Resets the skin of the player his default skin.
     *
     * @param player The player whose skin to reset.
     * @return `true` if the skin was reset successfully, `false` otherwise.
     */
    public fun resetDefaultSkin(player: Player): Boolean

    /**
     * Checks if the player has an active skin.
     *
     * @param player The player to check.
     * @return `true` if the player has an active skin, `false` otherwise.
     */
    public fun hasActiveSkin(player: Player): Boolean

    /**
     * Some predefined skins.
     */
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

/**
 * Creates a new instance of [SkinChanger].
 *
 * @param plugin The plugin instance.
 * @return A new instance of [SkinChanger].
 */
public fun SkinChanger(plugin: Plugin): SkinChanger = SkinChangerImpl(plugin)
