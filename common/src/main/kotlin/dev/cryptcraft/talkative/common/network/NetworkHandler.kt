package dev.cryptcraft.talkative.common.network

import dev.architectury.networking.NetworkChannel
import dev.architectury.networking.NetworkManager
import dev.cryptcraft.talkative.common.network.clientbound.*
import dev.cryptcraft.talkative.common.network.serverbound.*
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import java.util.function.Function
import java.util.function.Supplier

object NetworkHandler {
    private val CHANNEL: NetworkChannel = NetworkChannel.create(ResourceLocation(dev.cryptcraft.talkative.Talkative.MOD_ID, "network"))

    fun init() {
        //Serverbound
        register(::DialogResponsePacket)
        register(::ExitConversationPacket)
        register(::RequestEditActorPacket)
        register(::RequestBranchForEditPacket)
        register(::RequestBranchListPacket)
        register(::UpdateBranchPacket)
        register(::UpdateBranchConditionalPacket)
        register(::UpdateNodeConditionalPacket)
        register(::AttachBranchPacket)
        register(::UnAttachBranchPacket)
        register(::UpdateMarkersPacket)

        //Clientbound
        register(::DialogPacket)
        register(::OpenActorEditorPacket)
        register(::OpenBranchEditorPacket)
        register(::UpdateEditingActorDataPacket)
        register(::UpdateEditingBranchPacket)
        register(::SyncBranchListPacket)
        register(::SyncMarkerPacket)
    }

    private inline fun <reified T : TalkativePacket> register(decoder: Function<FriendlyByteBuf, T>) {
        CHANNEL.register(T::class.java, TalkativePacket::encode, decoder, ::handlePacket)
    }

    fun <T : TalkativePacket> handlePacket(packet: T, contextSupplier: Supplier<NetworkManager.PacketContext>) {
        val ctx = contextSupplier.get()
        ctx.queue {
            if (packet is TalkativePacket.ServerboundTalkativePacket && !packet.permissionCheck(ctx.player as ServerPlayer)) {
                //ToDo More formal system for warnings like this
                ctx.player.sendSystemMessage(Component.literal("You don't have permission for this action."))
                return@queue
            }
            println("Handling Packet: ${packet.javaClass.name} on Side: ${if (ctx.player.level.isClientSide) "Client" else "Server"}")
            packet.onReceived(ctx)
        }
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
                    CHANNEL.sendToPlayer(it, this)
                }
            }
        }
    }
}