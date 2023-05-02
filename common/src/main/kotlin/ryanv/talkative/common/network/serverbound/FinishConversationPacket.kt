package ryanv.talkative.common.network.serverbound

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.common.network.NetworkHandler.TalkativePacket

class FinishConversationPacket(): TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf): this()

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return true
    }

    override fun encode(buf: FriendlyByteBuf) {}
}