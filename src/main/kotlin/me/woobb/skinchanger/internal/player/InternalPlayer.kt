package me.woobb.skinchanger.internal.player

import com.github.retrooper.packetevents.protocol.chat.RemoteChatSession
import com.github.retrooper.packetevents.protocol.player.ClientVersion
import com.github.retrooper.packetevents.protocol.player.User
import me.woobb.skinchanger.internal.SkinChangerImpl
import me.woobb.skinchanger.internal.utils.WrappedGameProfile
import me.woobb.skinchanger.internal.utils.gameProfile
import me.woobb.skinchanger.skin.PlayerSkin
import me.woobb.skinchanger.skin.Skin
import org.bukkit.entity.Player
import java.util.*

internal data class InternalPlayer(
    val skinChanger: SkinChangerImpl,
    val bukkitPlayer: Player,
    var defaultSkin: PlayerSkin,
) {
    internal var internalSkinMask: Pair<Byte, Byte?> = 0x00.toByte() to null
    var activeSkin: Skin? = null
    var playerState: InternalPlayerState = InternalPlayerState.LOGIN
    var remoteChatSession: RemoteChatSession? = null

    val user: User
        get() = skinChanger.packetEvents.playerManager.getUser(bukkitPlayer)

    val clientVersion: ClientVersion
        get() = user.clientVersion

    val uniqueId: UUID
        get() = user.uuid

    val entityId: Int
        get() = user.entityId

    val isJoining: Boolean
        get() = playerState == InternalPlayerState.LOGIN

    val gameProfile: WrappedGameProfile
        get() =
            if (isJoining) {
                bukkitPlayer.gameProfile
            } else {
                WrappedGameProfile(user.profile)
            }

    val skinMask: Byte
        get() = internalSkinMask.second ?: internalSkinMask.first

    val ping: Int
        get() = skinChanger.packetEvents.playerManager.getPing(bukkitPlayer)
}

internal enum class InternalPlayerState {
    LOGIN,
    PLAY,
}
