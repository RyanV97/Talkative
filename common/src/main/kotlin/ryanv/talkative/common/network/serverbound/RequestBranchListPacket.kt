package ryanv.talkative.common.network.serverbound

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.common.network.NetworkHandler

class RequestBranchListPacket() : NetworkHandler.TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this()

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return player.hasPermissions(3)
    }

    override fun encode(buf: FriendlyByteBuf) {}
}