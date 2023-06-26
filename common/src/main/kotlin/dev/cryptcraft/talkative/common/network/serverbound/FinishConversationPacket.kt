package dev.cryptcraft.talkative.common.network.serverbound

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket

class FinishConversationPacket(): TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf): this()

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return true
    }

    override fun encode(buf: FriendlyByteBuf) {}
}