package me.woobb.skinchanger.event

import me.woobb.skinchanger.skin.Skin
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList

public class PlayerChangeSkinEvent internal constructor(
    public val target: Player,
    public val previousSkin: Skin?,
    override var skin: Skin,
    async: Boolean = false,
) : SkinEvent(async),
    Cancellable {
    private var cancelled = false

    override fun getHandlers(): HandlerList = handlerList

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }

    private companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}
