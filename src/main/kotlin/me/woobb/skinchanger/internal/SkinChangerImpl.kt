package me.woobb.skinchanger.internal

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.PacketEventsAPI
import com.github.retrooper.packetevents.event.PacketListenerPriority
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import me.woobb.skinchanger.SkinChanger
import me.woobb.skinchanger.event.PlayerChangeSkinEvent
import me.woobb.skinchanger.internal.mojang.MojangClient
import me.woobb.skinchanger.internal.player.PlayerService
import me.woobb.skinchanger.internal.service.CustomSkinManagementService
import me.woobb.skinchanger.internal.service.PlayerSkinServiceImpl
import me.woobb.skinchanger.skin.CustomSkin
import me.woobb.skinchanger.skin.Skin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class SkinChangerImpl(
    private val plugin: Plugin,
    internal val packetEvents: PacketEventsAPI<*> = PacketEvents.getAPI(),
) : SkinChanger,
    CoroutineScope {
    internal val mojangClient = MojangClient()
    internal val playerService = PlayerService(this)
    override val playerSkinService = PlayerSkinServiceImpl(this)
    internal val customSkinManagementService = CustomSkinManagementService(this)

    private val executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2)
    override val coroutineContext: CoroutineContext by lazy {
        CoroutineName("SkinChanger") + executorService.asCoroutineDispatcher() + SupervisorJob()
    }

    init {
        Runtime.getRuntime().addShutdownHook(Thread { close() })

        packetEvents.eventManager.registerListener(playerService, PacketListenerPriority.NORMAL)

        plugin.server.pluginManager.registerEvents(playerService, plugin)
    }

    override suspend fun setSkin(
        player: Player,
        skin: Skin,
    ): Boolean =
        suspendCoroutine { continuation ->
            val internalPlayer =
                playerService[player.uniqueId] ?: run {
                    continuation.resumeWithException(IllegalArgumentException("Player not found"))
                    return@suspendCoroutine
                }

            val changeSkinEvent = PlayerChangeSkinEvent(player, internalPlayer.activeSkin, skin, !Bukkit.isPrimaryThread())
            plugin.server.pluginManager.callEvent(changeSkinEvent)
            if (changeSkinEvent.isCancelled) {
                continuation.resume(false)
                return@suspendCoroutine
            }

            val newSkin = changeSkinEvent.skin
            when (newSkin) {
                is CustomSkin -> customSkinManagementService.setPlayerSkin(internalPlayer, newSkin)
                else -> throw IllegalArgumentException("Unsupported skin type: ${skin::class.simpleName}")
            }
            continuation.resume(true)
        }

    override fun resetDefaultSkin(player: Player): Boolean =
        playerService[player.uniqueId]?.let {
            customSkinManagementService.resetDefaultSkin(it)
        } == true

    override fun hasActiveSkin(player: Player): Boolean = playerService[player.uniqueId]?.activeSkin != null

    override fun close() {
        executorService.shutdown()
        executorService.awaitTermination(5000, TimeUnit.SECONDS)
        mojangClient.close()
    }
}
