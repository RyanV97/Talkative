package dev.cryptcraft.talkative.common.network.serverbound

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket

class RequestBranchForEditPacket(val path: String) : TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf): this(buf.readUtf())

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return player.hasPermissions(3)
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(path)
    }
}