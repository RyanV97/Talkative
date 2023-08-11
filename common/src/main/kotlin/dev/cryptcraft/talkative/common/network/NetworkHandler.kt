package dev.cryptcraft.talkative.common.network

import dev.architectury.networking.NetworkChannel
import dev.architectury.networking.NetworkManager
import dev.architectury.platform.Platform
import dev.architectury.utils.Env
import dev.cryptcraft.talkative.common.network.clientbound.*
import dev.cryptcraft.talkative.common.network.serverbound.*
import dev.cryptcraft.talkative.common.network.serverbound.RequestBranchForEditPacket
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Function
import java.util.function.Supplier

object NetworkHandler {
    private val CHANNEL: NetworkChannel = NetworkChannel.create(ResourceLocation(dev.cryptcraft.talkative.Talkative.MOD_ID, "network"))

    fun init() {
        //Serverbound
        register(DialogResponsePacket::class.java, ::DialogResponsePacket)
        register(FinishConversationPacket::class.java, ::FinishConversationPacket)
        register(RequestEditActorPacket::class.java, ::RequestEditActorPacket)
        register(RequestBranchForEditPacket::class.java, ::RequestBranchForEditPacket)
        register(RequestBranchListPacket::class.java, ::RequestBranchListPacket)
        register(UpdateBranchPacket::class.java, ::UpdateBranchPacket)
        register(UpdateBranchConditionalPacket::class.java, ::UpdateBranchConditionalPacket)
        register(UpdateNodeConditionalPacket::class.java, ::UpdateNodeConditionalPacket)
        register(AttachBranchPacket::class.java, ::AttachBranchPacket)
        register(UnAttachBranchPacket::class.java, ::UnAttachBranchPacket)

        //Clientbound
        register(DialogPacket::class.java, ::DialogPacket)
        register(OpenActorEditorPacket::class.java, ::OpenActorEditorPacket)
        register(OpenBranchEditorPacket::class.java, ::OpenBranchEditorPacket)
        register(UpdateEditingActorDataPacket::class.java, ::UpdateEditingActorDataPacket)
        register(UpdateEditingBranchPacket::class.java, ::UpdateEditingBranchPacket)
        register(SyncBranchListPacket::class.java, ::SyncBranchListPacket)
    }

    private fun <T : TalkativePacket> register(packetClass: Class<T>, decoder: Function<FriendlyByteBuf, T>) {
        CHANNEL.register(packetClass, TalkativePacket::encode, decoder, ::handlePacket)
    }

    fun <T : TalkativePacket> handlePacket(packet: T, contextSupplier: Supplier<NetworkManager.PacketContext>) {
        val ctx = contextSupplier.get()
        if (packet is TalkativePacket.ServerboundTalkativePacket && !packet.permissionCheck(ctx.player as ServerPlayer)) {
            //ToDo More formal system for warnings like this
            ctx.player.sendSystemMessage(Component.literal("You don't have permission for this action."))
            return
        }
        packet.onReceived(ctx)
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