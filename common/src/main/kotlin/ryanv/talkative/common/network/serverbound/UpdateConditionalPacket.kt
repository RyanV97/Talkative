package ryanv.talkative.common.network.serverbound

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.common.network.NetworkHandler.TalkativePacket

class UpdateConditionalPacket(val actorId: Int, val holderData: CompoundTag): TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf): this(buf.readInt(), buf.readNbt()!!)

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return player.hasPermissions(3)
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(actorId)
        buf.writeNbt(holderData)
    }
}