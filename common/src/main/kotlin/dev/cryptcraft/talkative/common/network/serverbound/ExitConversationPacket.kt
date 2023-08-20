package dev.cryptcraft.talkative.common.network.serverbound

import dev.architectury.networking.NetworkManager
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket
import dev.cryptcraft.talkative.server.conversations.ConversationManager

class ExitConversationPacket(): TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf): this()

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return true
    }

    override fun encode(buf: FriendlyByteBuf) {}

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        ConversationManager.endConversation(ctx.player as ServerPlayer)
    }
}