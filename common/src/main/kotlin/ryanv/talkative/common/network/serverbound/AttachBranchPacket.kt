package ryanv.talkative.common.network.serverbound

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.common.network.NetworkHandler.TalkativePacket

class AttachBranchPacket(val entityId: Int, val path: String) : TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readInt(), buf.readUtf())

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return player.hasPermissions(3)
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(entityId)
        buf.writeUtf(path)
    }
}