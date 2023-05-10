package ryanv.talkative.common.network

import me.shedaniel.architectury.networking.NetworkChannel
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.Talkative
import ryanv.talkative.common.network.clientbound.*
import ryanv.talkative.common.network.serverbound.*
import ryanv.talkative.common.network.serverbound.RequestBranchForEditPacket

object NetworkHandler {
    private val CHANNEL: NetworkChannel = NetworkChannel.create(ResourceLocation(Talkative.MOD_ID, "network"))

    fun init() {
        //Serverbound
        CHANNEL.register(AttachBranchPacket::class.java, AttachBranchPacket::encode, ::AttachBranchPacket, ServerPacketHandler::processPacket)
        CHANNEL.register(DialogResponsePacket::class.java, DialogResponsePacket::encode, ::DialogResponsePacket, ServerPacketHandler::processPacket)
        CHANNEL.register(FinishConversationPacket::class.java, FinishConversationPacket::encode, ::FinishConversationPacket,ServerPacketHandler::processPacket)
        CHANNEL.register(RequestBranchForEditPacket::class.java, RequestBranchForEditPacket::encode, ::RequestBranchForEditPacket, ServerPacketHandler::processPacket)
        CHANNEL.register(UpdateBranchPacket::class.java, UpdateBranchPacket::encode, ::UpdateBranchPacket, ServerPacketHandler::processPacket)
        CHANNEL.register(UpdateBranchConditionalPacket::class.java, UpdateBranchConditionalPacket::encode, ::UpdateBranchConditionalPacket, ServerPacketHandler::processPacket)
        CHANNEL.register(UpdateNodeConditionalPacket::class.java, UpdateNodeConditionalPacket::encode, ::UpdateNodeConditionalPacket, ServerPacketHandler::processPacket)
        CHANNEL.register(UnAttachBranchPacket::class.java, UnAttachBranchPacket::encode, ::UnAttachBranchPacket, ServerPacketHandler::processPacket)
        CHANNEL.register(RequestBranchListPacket::class.java, RequestBranchListPacket::encode, ::RequestBranchListPacket, ServerPacketHandler::processPacket)

        //Clientbound
        CHANNEL.register(DialogPacket::class.java, DialogPacket::encode, ::DialogPacket, ClientPacketHandler::processPacket)
        CHANNEL.register(OpenActorEditorPacket::class.java, OpenActorEditorPacket::encode, ::OpenActorEditorPacket, ClientPacketHandler::processPacket)
        CHANNEL.register(OpenBranchEditorPacket::class.java, OpenBranchEditorPacket::encode, ::OpenBranchEditorPacket, ClientPacketHandler::processPacket)
        CHANNEL.register(UpdateActorDataScreenPacket::class.java, UpdateActorDataScreenPacket::encode, ::UpdateActorDataScreenPacket, ClientPacketHandler::processPacket)
        CHANNEL.register(UpdateEditingBranchPacket::class.java, UpdateEditingBranchPacket::encode, ::UpdateEditingBranchPacket, ClientPacketHandler::processPacket)
        CHANNEL.register(SyncBranchListPacket::class.java, SyncBranchListPacket::encode, ::SyncBranchListPacket, ClientPacketHandler::processPacket)
    }

    interface TalkativePacket {
        fun encode(buf: FriendlyByteBuf)

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