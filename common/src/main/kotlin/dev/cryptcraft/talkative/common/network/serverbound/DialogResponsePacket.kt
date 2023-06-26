package dev.cryptcraft.talkative.common.network.serverbound

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket

class DialogResponsePacket(val responseId: Int) : TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readInt())

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return true
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(responseId)
    }
}