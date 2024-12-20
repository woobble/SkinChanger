package me.woobb.skinchanger.internal.utils

import org.bukkit.entity.Player

internal val Player.handle: Any
    get() =
        this::class.java
            .getDeclaredMethod("getHandle")
            .apply { isAccessible = true }
            .invoke(this) ?: error("Cannot get handle")

// internal val Property.asTextureProperty: TextureProperty
//    get() {
//        val nameField = Property::class.java.getDeclaredField("name").apply { isAccessible = true } ?: error("Cannot find name field")
//        val valueField = Property::class.java.getDeclaredField("value").apply { isAccessible = true } ?: error("Cannot find value field")
//        val signatureField =
//            Property::class.java.getDeclaredField("signature").apply { isAccessible = true } ?: error("Cannot find signature field")
//
//        return TextureProperty(
//            nameField.get(this) as String,
//            valueField.get(this) as String,
//            signatureField.get(this) as String,
//        )
//    }
