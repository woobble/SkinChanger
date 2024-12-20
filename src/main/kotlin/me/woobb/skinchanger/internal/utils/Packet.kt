package me.woobb.skinchanger.internal.utils

import com.github.retrooper.packetevents.protocol.player.User
import com.github.retrooper.packetevents.wrapper.PacketWrapper

internal fun <T : PacketWrapper<T>> PacketWrapper<T>.broadcastPacket(users: Iterable<User>) {
    users.forEach { user ->
        user.sendPacket(this)
    }
}
