package me.woobb.skinchanger

import com.github.retrooper.packetevents.PacketEventsAPI
import kotlinx.coroutines.CoroutineScope
import me.woobb.skinchanger.internal.SkinChangerImpl
import me.woobb.skinchanger.internal.Skins
import me.woobb.skinchanger.service.PlayerSkinService
import me.woobb.skinchanger.skin.Skin
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.io.Closeable
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

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
        public val Efe: Skin by lazy { Skins.Efe }
        public val Makena: Skin by lazy { Skins.Makena }
        public val Ari: Skin by lazy { Skins.Ari }
        public val Kai: Skin by lazy { Skins.Kai }
        public val Alex: Skin by lazy { Skins.Alex }
        public val Noor: Skin by lazy { Skins.Noor }
        public val Steve: Skin by lazy { Skins.Steve }
        public val Sunny: Skin by lazy { Skins.Sunny }
        public val Zuri: Skin by lazy { Skins.Zuri }
    }
}

/**
 * Creates a new instance of [SkinChanger].
 *
 * @param plugin The plugin instance.
 * @return A new instance of [SkinChanger].
 */
public suspend fun SkinChanger(plugin: Plugin): SkinChanger = SkinChangerImpl(plugin, coroutineContext)

/**
 * Creates a new instance of [SkinChanger].
 *
 * @param plugin The plugin instance.
 * @param callContext The coroutine context.
 * @return A new instance of [SkinChanger].
 */
public fun SkinChanger(
    plugin: Plugin,
    callContext: CoroutineContext,
): SkinChanger = SkinChangerImpl(plugin, callContext)

/**
 * Creates a new instance of [SkinChanger].
 *
 * @param plugin The plugin instance.
 * @param packetEvents The PacketEvents API instance.
 * @return A new instance of [SkinChanger].
 */
public suspend fun SkinChanger(
    plugin: Plugin,
    packetEvents: PacketEventsAPI<Plugin>,
): SkinChanger = SkinChangerImpl(plugin, coroutineContext, packetEvents)

/**
 * Creates a new instance of [SkinChanger].
 *
 * @param plugin The plugin instance.
 * @param packetEvents The PacketEvents API instance.
 * @param callContext The coroutine context.
 * @return A new instance of [SkinChanger].
 */
public fun SkinChanger(
    plugin: Plugin,
    packetEvents: PacketEventsAPI<Plugin>,
    callContext: CoroutineContext,
): SkinChanger = SkinChangerImpl(plugin, callContext, packetEvents)
