package me.woobb.skinchanger.internal.utils

import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.command.CommandMap

internal fun Server.registerBukkitCommand(
    command: Command,
    fallbackPrefix: String? = null,
): Boolean {
    val commandMap = javaClass.getDeclaredMethod("getCommandMap").invoke(this) as CommandMap

    return if (fallbackPrefix != null) {
        commandMap.register(fallbackPrefix, command)
    } else {
        commandMap.register(command.name, command)
    }
}
