package me.woobb.skinchanger.internal.service

import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.protocol.player.*
import com.github.retrooper.packetevents.protocol.world.Difficulty
import com.github.retrooper.packetevents.protocol.world.Location
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.play.server.*
import com.google.common.hash.Hashing
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import me.woobb.skinchanger.internal.SkinChangerImpl
import me.woobb.skinchanger.internal.player.InternalPlayer
import me.woobb.skinchanger.internal.utils.broadcastPacket
import me.woobb.skinchanger.skin.CustomSkin
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

internal class CustomSkinManagementService(
    private val skinChanger: SkinChangerImpl,
) {
    fun setPlayerSkin(
        player: InternalPlayer,
        skin: CustomSkin?,
    ): Boolean {
        if (skin == null) return false
        if (player.activeSkin == skin) return false
        player.activeSkin = skin

        if (player.isJoining) {
            setSkinTextures(player, skin)
            return true
        }

        val targets =
            Bukkit.getOnlinePlayers().filter { it != player.bukkitPlayer }.map {
                skinChanger.packetEvents.playerManager.getUser(
                    it,
                )
            }

        removePlayerInfo(targets, player)
        setSkinTextures(player, skin)
        addPlayerInfo(targets, player)

        respawnPlayerForItself(player)
        setPlayerPosition(player)
        updateInventory(player)
        updateHealth(player)
        updateExperience(player)

        respawnPlayerForOthers(targets, player)
        updateEquipment(targets, player)
        updateHeadRotation(targets, player)

        return true
    }

    fun resetDefaultSkin(player: InternalPlayer): Boolean {
        if (player.activeSkin == null || player.activeSkin == player.defaultSkin) return false

        return setPlayerSkin(player, player.defaultSkin).also {
            player.activeSkin = null
        }
    }

    internal fun setSkinTextures(
        player: InternalPlayer,
        skin: CustomSkin,
    ) {
        player.gameProfile.properties =
            listOf(
                skin.value,
            )
    }

    internal fun removePlayerInfo(
        targets: Collection<User>,
        player: InternalPlayer,
    ) {
        val v1193 = player.clientVersion.isNewerThanOrEquals(ClientVersion.V_1_19_3)
        val removePlayerInfoPacket: PacketWrapper<*> =
            if (v1193) {
                WrapperPlayServerPlayerInfoRemove(player.uniqueId)
            } else {
                WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER)
            }

        removePlayerInfoPacket.broadcastPacket(targets + player.user)
    }

    @Suppress("DEPRECATION")
    internal fun addPlayerInfo(
        targets: Collection<User>,
        player: InternalPlayer,
    ) {
        val v1193 = player.clientVersion.isNewerThanOrEquals(ClientVersion.V_1_19_3)
        val addPlayerInfoPacket: PacketWrapper<*> =
            if (v1193) {
                WrapperPlayServerPlayerInfoUpdate(
                    EnumSet.of(
                        WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER,
                        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME,
                        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED,
                        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LATENCY,
                        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE,
                        WrapperPlayServerPlayerInfoUpdate.Action.INITIALIZE_CHAT,
                    ),
                    WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                        player.user.profile,
                        true,
                        player.ping,
                        GameMode.getById(player.bukkitPlayer.gameMode.value),
                        Component.text(player.bukkitPlayer.displayName),
                        player.remoteChatSession,
                    ),
                )
            } else {
                WrapperPlayServerPlayerInfo(
                    WrapperPlayServerPlayerInfo.Action.ADD_PLAYER,
                    WrapperPlayServerPlayerInfo.PlayerData(
                        Component.text(player.bukkitPlayer.displayName),
                        player.user.profile,
                        GameMode.getById(player.bukkitPlayer.gameMode.value),
                        player.ping,
                    ),
                )
            }

        addPlayerInfoPacket.broadcastPacket(targets + player.user)
    }

    @Suppress("DEPRECATION")
    internal fun respawnPlayerForItself(player: InternalPlayer) {
        val v1203 = player.clientVersion.isNewerThanOrEquals(ClientVersion.V_1_20_3)
        val world = player.bukkitPlayer.world
        val dimension = getDimension(world.environment.id)

        val respawnPacket =
            WrapperPlayServerRespawn(
                player.user.dimensionType,
                dimension,
                Difficulty.getById(world.difficulty.ordinal),
                Hashing.sha256().hashLong(world.seed).asLong(),
                GameMode.getById(player.bukkitPlayer.gameMode.value),
                null,
                false,
                false,
                0,
                null,
                null,
            )

        player.user.sendPacket(respawnPacket)

        if (v1203) {
            val startLoadingChunksPacket =
                WrapperPlayServerChangeGameState(
                    WrapperPlayServerChangeGameState.Reason.START_LOADING_CHUNKS,
                    0.0F,
                )
            player.user.sendPacket(startLoadingChunksPacket)
        }
    }

    internal fun setPlayerPosition(player: InternalPlayer) {
        val location = player.bukkitPlayer.location
        val setPositionPacket =
            WrapperPlayServerPlayerPositionAndLook(
                location.x,
                location.y,
                location.z,
                location.yaw,
                location.pitch,
                0,
                -1,
                false,
            )
        player.user.sendPacket(setPositionPacket)
    }

    internal fun respawnPlayerForOthers(
        targets: Collection<User>,
        player: InternalPlayer,
    ) {
        val v1202 = player.clientVersion.isNewerThanOrEquals(ClientVersion.V_1_20_2)
        val location = player.bukkitPlayer.location
        val destroyEntityPacket = WrapperPlayServerDestroyEntities(player.entityId)

        val spawnPlayerPacket =
            if (v1202) {
                WrapperPlayServerSpawnEntity(
                    player.entityId,
                    player.uniqueId,
                    EntityTypes.PLAYER,
                    Location(location.x, location.y, location.z, location.yaw, location.pitch),
                    player.bukkitPlayer.eyeLocation.yaw,
                    0,
                    null,
                )
            } else {
                WrapperPlayServerSpawnPlayer(
                    player.entityId,
                    player.uniqueId,
                    Location(location.x, location.y, location.z, location.yaw, location.pitch),
                )
            }

        val updateEntityMetadata =
            WrapperPlayServerEntityMetadata(
                player.entityId,
                listOf(EntityData(17, EntityDataTypes.BYTE, player.skinMask)),
            )

        destroyEntityPacket.broadcastPacket(targets)
        spawnPlayerPacket.broadcastPacket(targets)
        updateEntityMetadata.broadcastPacket(targets + player.user)
    }

    private fun updateInventory(player: InternalPlayer) {
        val heldItemChangePacket = WrapperPlayServerHeldItemChange(player.bukkitPlayer.inventory.heldItemSlot)
        player.user.sendPacket(heldItemChangePacket)
        player.bukkitPlayer.updateInventory()
    }

    private fun updateEquipment(
        targets: Collection<User>,
        player: InternalPlayer,
    ) {
        val v116 = player.clientVersion.isNewerThanOrEquals(ClientVersion.V_1_16)
        val equipmentSlots =
            EquipmentSlot.entries
                .filter { it != EquipmentSlot.BODY }
                .map {
                    it.getId(player.clientVersion)
                }.toSet()

        val equipmentPackets =
            if (v116) {
                val equipment =
                    equipmentSlots.map { slot ->
                        val item = player.bukkitPlayer.inventory.contents[slot] ?: ItemStack(Material.AIR)
                        Equipment(
                            EquipmentSlot.getById(player.clientVersion, slot),
                            SpigotConversionUtil.fromBukkitItemStack(item),
                        )
                    }
                listOf(WrapperPlayServerEntityEquipment(player.entityId, equipment))
            } else {
                equipmentSlots.map { slot ->
                    val item = player.bukkitPlayer.inventory.contents[slot] ?: ItemStack(Material.AIR)
                    WrapperPlayServerEntityEquipment(
                        player.entityId,
                        listOf(
                            Equipment(
                                EquipmentSlot.getById(player.clientVersion, slot),
                                SpigotConversionUtil.fromBukkitItemStack(item),
                            ),
                        ),
                    )
                }
            }

        equipmentPackets.forEach { packet ->
            packet.broadcastPacket(targets + player.user)
        }
    }

    private fun updateHealth(player: InternalPlayer) {
        val updateHealthPacket =
            WrapperPlayServerUpdateHealth(
                player.bukkitPlayer.health.toFloat(),
                player.bukkitPlayer.foodLevel,
                player.bukkitPlayer.saturation,
            )
        player.user.sendPacket(updateHealthPacket)
    }

    private fun updateExperience(player: InternalPlayer) {
        val updateExperiencePacket =
            WrapperPlayServerSetExperience(
                player.bukkitPlayer.exp,
                player.bukkitPlayer.level,
                player.bukkitPlayer.expToLevel,
            )
        player.user.sendPacket(updateExperiencePacket)
    }

    private fun updateHeadRotation(
        targets: Collection<User>,
        player: InternalPlayer,
    ) {
        val headRotationPacket =
            WrapperPlayServerEntityHeadLook(
                player.entityId,
                player.bukkitPlayer.eyeLocation.yaw,
            )
        headRotationPacket.broadcastPacket(targets)
    }

    private fun getDimension(environmentId: Int): String =
        when (environmentId) {
            0 -> "minecraft:overworld"
            1 -> "minecraft:the_end"
            -1 -> "minecraft:the_nether"
            else -> "minecraft:custom"
        }
}
