package me.woobb.skinchanger.internal.plugin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.woobb.skinchanger.SkinChanger
import me.woobb.skinchanger.internal.Skins
import me.woobb.skinchanger.internal.mojang.MojangException
import me.woobb.skinchanger.internal.mojang.PlayerNotFoundException
import me.woobb.skinchanger.internal.mojang.RateLimitException
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

internal class SkinCommand(
    private val skinChanger: SkinChanger,
) : BukkitCommand(
        "skin",
        "Change your skin to a player's skin",
        "/skin [reset/set] <target/skin> <skin>",
        emptyList(),
    ),
    CoroutineScope by skinChanger {
    private val tabCompleter = SkinCommandTabCompleter(skinChanger)

    init {
        permission = "skinchanger.command.skin"
    }

    override fun execute(
        sender: CommandSender,
        commandLabel: String,
        args: Array<out String>?,
    ): Boolean {
        if (!testPermission(sender)) {
            return false
        }
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}I'm sorry, but you must be a player to perform this command.")
            return false
        }

        if (args == null || args.isEmpty()) {
            sender.sendMessage("${ChatColor.RED}Usage: $usage")
            return false
        }

        when (args[0].lowercase()) {
            "set" -> {
                if (args.size < 2) {
                    sender.sendMessage("${ChatColor.RED}Usage: $usage")
                    return false
                }

                val skin = if (args.size > 2) args[2] else args[1]
                val target = if (args.size > 2) args[1] else sender.name

                val targetPlayer = Bukkit.getPlayer(target)
                if (targetPlayer == null || !targetPlayer.isOnline) {
                    sender.sendMessage("${ChatColor.RED}Player ${ChatColor.YELLOW}$target ${ChatColor.RED}is not online.")
                    return false
                }

                try {
                    launch {
                        val skin =
                            (
                                if (skin.startsWith("default:")) {
                                    val skinName = skin.removePrefix("default:")

                                    Skins.defaultSkins.find { it.name == skinName }
                                } else {
                                    skinChanger.playerSkinService.getPlayerSkin(skin)
                                } ?: error("Skin not found")
                            )

                        if (skinChanger.setSkin(targetPlayer, skin)) {
                            sender.sendMessage("${ChatColor.GREEN}Successfully set skin for player ${ChatColor.YELLOW}$target.")
                        } else {
                            sender.sendMessage("${ChatColor.RED}Failed to set skin for player ${ChatColor.YELLOW}$target.")
                        }
                    }
                } catch (e: PlayerNotFoundException) {
                    sender.sendMessage("${ChatColor.RED}Skin for player ${ChatColor.YELLOW}$skin ${ChatColor.RED}not found.")
                } catch (e: RateLimitException) {
                    sender.sendMessage("${ChatColor.RED}Rate limit exceeded. Please try again later.")
                } catch (e: MojangException) {
                    sender.sendMessage("${ChatColor.RED}An error occurred while fetching skin for player ${ChatColor.YELLOW}$skin.")
                }
            }

            "reset" -> {
                val target = if (args.size > 1) args[1] else sender.name
                val targetPlayer = Bukkit.getPlayer(target)
                if (targetPlayer == null || !targetPlayer.isOnline) {
                    sender.sendMessage("${ChatColor.RED}Player ${ChatColor.YELLOW}$target ${ChatColor.RED}is not online.")
                    return false
                }

                if (!skinChanger.hasActiveSkin(targetPlayer)) {
                    sender.sendMessage("${ChatColor.RED}Player ${ChatColor.YELLOW}$target ${ChatColor.RED}does not have a custom skin.")
                    return false
                }

                if (skinChanger.resetDefaultSkin(targetPlayer)) {
                    sender.sendMessage("${ChatColor.GREEN}Successfully reset skin for player ${ChatColor.YELLOW}$target.")
                } else {
                    sender.sendMessage("${ChatColor.RED}Failed to reset skin for player ${ChatColor.YELLOW}$target.")
                }
            }
        }

        return false
    }

    override fun tabComplete(
        sender: CommandSender,
        alias: String,
        args: Array<String>,
    ): List<String> = tabCompleter.onTabComplete(sender, this, alias, args)
}
