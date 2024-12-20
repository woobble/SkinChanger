package me.woobb.skinchanger.internal.player

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.UserDisconnectEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatSessionUpdate
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings
import me.woobb.skinchanger.internal.SkinChangerImpl
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

internal class PlayerService(
    private val skinChanger: SkinChangerImpl,
) : MutableMap<UUID, InternalPlayer> by ConcurrentHashMap(),
    PacketListener,
    Listener {
    override fun onUserDisconnect(event: UserDisconnectEvent) {
        remove(event.user.uuid)
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun onPacketReceive(event: PacketReceiveEvent) {
        if (event.packetType == PacketType.Play.Client.CHAT_SESSION_UPDATE) {
            val packet = WrapperPlayClientChatSessionUpdate(event)
            this[event.user.uuid]?.remoteChatSession = packet.chatSession
        } else if (event.packetType == PacketType.Play.Client.CLIENT_SETTINGS ||
            event.packetType == PacketType.Configuration.Client.CLIENT_SETTINGS
        ) {
            val player = this[event.user.uuid] ?: return
            val packet = WrapperPlayClientSettings(event)

            player.internalSkinMask = packet.skinMask to player.internalSkinMask.second
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        this[event.player.uniqueId]?.playerState = InternalPlayerState.PLAY
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerLogin(event: PlayerLoginEvent) {
        val player = event.player
        this[event.player.uniqueId] =
            InternalPlayer(
                skinChanger,
                player,
                skinChanger.playerSkinService.getPlayerSkin(player),
            )
    }
}
