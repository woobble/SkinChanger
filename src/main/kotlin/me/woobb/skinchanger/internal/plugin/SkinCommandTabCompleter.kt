package me.woobb.skinchanger.internal.plugin

import me.woobb.skinchanger.SkinChanger
import me.woobb.skinchanger.internal.Skins
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

internal class SkinCommandTabCompleter(
    private val skinChanger: SkinChanger,
) : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>,
    ): List<String> =
        buildList {
            if (args.size == 1) {
                addAll(listOf("set", "reset"))
            } else if (args.size == 2) {
                val onlinePlayers = Bukkit.getOnlinePlayers()
                if (args[0] == "set") {
                    addAll(onlinePlayers.map { it.name })
                    addAll(Skins.defaultSkins.filter { it.name != null }.map { "default:${it.name!!}" })
                } else if (args[0] == "reset") {
                    addAll(
                        onlinePlayers
                            .filter {
                                skinChanger.hasActiveSkin(it)
                            }.map {
                                it.name
                            },
                    )
                }
            } else if (args.size > 2 && args[0] == "set") {
                val target = args[1]
                val targetPlayer = Bukkit.getPlayer(target)
                if (targetPlayer != null && targetPlayer.isOnline) {
                    addAll(Skins.defaultSkins.filter { it.name != null }.map { "default:${it.name!!}" })
                }
            }
        }
}
