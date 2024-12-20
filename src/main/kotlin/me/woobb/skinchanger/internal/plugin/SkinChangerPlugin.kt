package me.woobb.skinchanger.internal.plugin

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.PacketEventsAPI
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import me.woobb.skinchanger.internal.SkinChangerImpl
import me.woobb.skinchanger.internal.utils.registerBukkitCommand
import org.bukkit.plugin.java.JavaPlugin

internal class SkinChangerPlugin : JavaPlugin() {
    private lateinit var skinChanger: SkinChangerImpl
    private lateinit var packetEvents: PacketEventsAPI<*>

    override fun onLoad() {
        packetEvents = SpigotPacketEventsBuilder.build(this)
        PacketEvents.setAPI(packetEvents)
        packetEvents.load()
    }

    override fun onEnable() {
        skinChanger = SkinChangerImpl(this)
        packetEvents.init()

        server.registerBukkitCommand(SkinCommand(skinChanger))
    }

    override fun onDisable() {
        skinChanger.close()
        packetEvents.terminate()
    }
}
