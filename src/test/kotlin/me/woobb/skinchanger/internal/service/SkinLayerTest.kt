package me.woobb.skinchanger.internal.service

import me.woobb.skinchanger.internal.skin.SkinLayerImpl
import me.woobb.skinchanger.internal.skin.fromMask
import me.woobb.skinchanger.internal.skin.toMask
import me.woobb.skinchanger.skin.SkinLayers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SkinLayerTest {
    @Test
    fun `test skin-layer contains`() {
        val layer = SkinLayers.CAPE
        val skinLayer = SkinLayerImpl(listOf(layer))
        assertTrue(layer in skinLayer)
    }

    @Test
    fun `test skin-layer add single layer`() {
        val layer = SkinLayers.CAPE
        val skinLayer = SkinLayerImpl()
        skinLayer += layer
        assertTrue(layer in skinLayer)
    }

    @Test
    fun `test skin-layer remove single layer`() {
        val layer = SkinLayers.CAPE
        val skinLayer = SkinLayerImpl(listOf(layer))
        skinLayer -= layer
        assertFalse(layer in skinLayer)
    }

    @Test
    fun `test skin-layer add multiple layers`() {
        val layers = listOf(SkinLayers.CAPE, SkinLayers.LEFT_SLEEVE)
        val skinLayer = SkinLayerImpl()
        skinLayer += layers
        assertTrue(SkinLayers.CAPE in skinLayer)
        assertTrue(SkinLayers.LEFT_SLEEVE in skinLayer)
    }

    @Test
    fun `test skin-layer remove multiple layers`() {
        val layers = listOf(SkinLayers.CAPE, SkinLayers.LEFT_SLEEVE)
        val skinLayer = SkinLayerImpl(layers)
        skinLayer -= layers
        assertFalse(SkinLayers.CAPE in skinLayer)
        assertFalse(SkinLayers.LEFT_SLEEVE in skinLayer)
    }

    @Test
    fun `test get layers from mask`() {
        val mask: Byte = 0x05 // CAPE and LEFT_SLEEVE enabled
        val layers = SkinLayerImpl(mask).layers
        assertEquals(2, layers.size)
        assertTrue(layers.contains(SkinLayers.CAPE))
        assertTrue(layers.contains(SkinLayers.LEFT_SLEEVE))
    }
}

class SkinLayersTest {
    @Test
    fun `test fromMask single mask`() {
        val mask: Byte = 0x01 // CAPE enabled
        val layers = SkinLayers.fromMask(mask)
        assertEquals(1, layers.size)
        assertTrue(layers.contains(SkinLayers.CAPE))
    }

    @Test
    fun `test fromMask multiple masks`() {
        val mask: Byte = 0x05 // CAPE and LEFT_SLEEVE enabled
        val layers = SkinLayers.fromMask(mask)
        assertEquals(2, layers.size)
        assertTrue(layers.contains(SkinLayers.CAPE))
        assertTrue(layers.contains(SkinLayers.LEFT_SLEEVE))
    }

    @Test
    fun `test toMask single layer`() {
        val layers = listOf(SkinLayers.CAPE)
        val mask = SkinLayers.toMask(layers)
        assertEquals(0x01, mask)
    }

    @Test
    fun `test toMask multiple layers`() {
        val layers = listOf(SkinLayers.CAPE, SkinLayers.LEFT_SLEEVE)
        val mask = SkinLayers.toMask(layers)
        assertEquals(0x05, mask)
    }
}
