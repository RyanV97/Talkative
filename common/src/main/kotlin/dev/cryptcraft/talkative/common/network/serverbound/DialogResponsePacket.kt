package dev.cryptcraft.talkative.common.network.serverbound

import dev.architectury.networking.NetworkManager
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket
import dev.cryptcraft.talkative.server.conversations.ConversationManager
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer

class DialogResponsePacket(private val responseId: Int) : TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readInt())

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return true
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(responseId)
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        val player = ctx.player as ServerPlayer
        if (ConversationManager.isInConversation(player)) {
            if (responseId >= 0)
                ConversationManager.getConversation(player)?.onResponse(responseId)
            else
                ConversationManager.getConversation(player)?.progressConversation()
        }
    }
}