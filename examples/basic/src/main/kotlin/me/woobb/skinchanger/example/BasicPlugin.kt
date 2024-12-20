package me.woobb.skinchanger.example

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import me.woobb.skinchanger.SkinChanger
import me.woobb.skinchanger.event.PlayerChangeSkinEvent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class BasicPlugin :
    JavaPlugin(),
    Listener,
    CommandExecutor,
    CoroutineScope {
    private lateinit var skinChanger: SkinChanger

    private val executor = Executors.newFixedThreadPool(1)
    override val coroutineContext: CoroutineContext by lazy {
        executor.asCoroutineDispatcher() + CoroutineName("Basic-Plugin")
    }

    override fun onLoad() {
    }

    override fun onEnable() {
        this.skinChanger = SkinChanger(this, coroutineContext)

        server.pluginManager.registerEvents(this, this)
        getCommand("skin")?.setExecutor(this)
    }

    @EventHandler
    fun onSkinChange(event: PlayerChangeSkinEvent) {
        event.target.sendMessage("Your skin got changed to ${event.skin}.")
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>,
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only player are allowed to use this command!")
            return false
        }
        if (args.isEmpty()) {
            sender.sendMessage("/skin <skinName>/reset")
            return false
        }

        if (args[0] == "reset") {
            skinChanger.resetDefaultSkin(sender)
        } else {
            val skinName = args[0]
            launch {
                try {
                    val skin = skinChanger.playerSkinService.getPlayerSkin(skinName)

                    skinChanger.setSkin(sender, skin)
                } catch (e: Exception) {
                    sender.sendMessage("Skin $skinName not found.")
                    return@launch
                }
            }
        }

        return true
    }

    override fun onDisable() {
        executor.shutdownNow()
        this.skinChanger.close()
    }
}
