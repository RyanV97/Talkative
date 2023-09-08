package dev.cryptcraft.talkative.common.network

import dev.architectury.networking.NetworkChannel
import dev.architectury.networking.NetworkManager
import dev.cryptcraft.talkative.Talkative
import dev.cryptcraft.talkative.common.network.clientbound.*
import dev.cryptcraft.talkative.common.network.serverbound.*
import dev.cryptcraft.talkative.mixin.entity.ChunkMapAccessor
import dev.cryptcraft.talkative.mixin.entity.TrackedEntityAccessor
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import java.util.function.Function
import java.util.function.Supplier

object NetworkHandler {
    private val CHANNEL: NetworkChannel = NetworkChannel.create(ResourceLocation(dev.cryptcraft.talkative.Talkative.MOD_ID, "network"))

    fun init() {
        //Serverbound
        register(::DialogResponsePacket)
        register(::ExitConversationPacket)
        register(::RequestBranchForEditPacket)
        register(::RequestBranchListPacket)
        register(::RequestEditActorPacket)
        register(::UpdateActorData)
        register(::UpdateBranchPacket)
        register(::UpdateNodeConditionalPacket)

        //Clientbound
        register(::DialogPacket)
        register(::OpenActorEditorPacket)
        register(::OpenBranchEditorPacket)
        register(::StartDialogPacket)
        register(::SyncBranchListPacket)
        register(::SyncMarkerPacket)
        register(::UpdateEditingActorDataPacket)
        register(::UpdateEditingBranchPacket)
    }

    private inline fun <reified T : TalkativePacket> register(decoder: Function<FriendlyByteBuf, T>) {
        CHANNEL.register(T::class.java, TalkativePacket::encode, decoder, ::handlePacket)
    }

    private fun <T : TalkativePacket> handlePacket(packet: T, contextSupplier: Supplier<NetworkManager.PacketContext>) {
        val ctx = contextSupplier.get()
        ctx.queue {
            if (packet is TalkativePacket.ServerboundTalkativePacket && !packet.permissionCheck(ctx.player as ServerPlayer)) {
                //ToDo Configurable Permissions
                //ToDo More formal system for warnings like this
                ctx.player.sendSystemMessage(Component.literal("You don't have permission for this action."))
                return@queue
            }
            Talkative.LOGGER.debug("Handling Packet: ${packet.javaClass.name} on Side: ${if (ctx.player.level.isClientSide) "Client" else "Server"}")
            packet.onReceived(ctx)
        }
    }

    @JvmStatic
    fun getTrackingPlayers(entity: Entity): ArrayList<ServerPlayer> {
        val list = ArrayList<ServerPlayer>()
        val trackedEntity = ((entity.level as ServerLevel).chunkSource.chunkMap as ChunkMapAccessor).entityMap[entity.id]

        if (trackedEntity != null) {
            val tracking = (trackedEntity as TrackedEntityAccessor).seenBy
            for (connection in tracking) {
                list.add(connection.player)
            }
        }
        return list
    }

    interface TalkativePacket {
        fun encode(buf: FriendlyByteBuf)
        fun onReceived(ctx: NetworkManager.PacketContext)

        interface ServerboundTalkativePacket : TalkativePacket {
            fun sendToServer() {
                CHANNEL.sendToServer(this)
            }

            fun permissionCheck(player: ServerPlayer): Boolean
        }

        interface ClientboundTalkativePacket : TalkativePacket {
            fun sendToPlayer(player: ServerPlayer) {
                CHANNEL.sendToPlayer(player, this)
            }

            fun sendToPlayers(players: Collection<ServerPlayer>) {
                players.forEach {
                    sendToPlayer(it)
                }
            }

            fun sendToTrackingPlayers(entity: Entity) {
                sendToPlayers(getTrackingPlayers(entity))
            }
        }
    }
}