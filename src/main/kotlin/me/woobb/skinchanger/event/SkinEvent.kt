package me.woobb.skinchanger.event

import me.woobb.skinchanger.skin.Skin
import org.bukkit.event.Event

public abstract class SkinEvent internal constructor(
    async: Boolean = false,
) : Event(async) {
    public abstract val skin: Skin
}
