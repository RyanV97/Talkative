package ryanv.talkative.common.network.serverbound

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.common.data.conditional.Conditional
import ryanv.talkative.common.network.NetworkHandler.TalkativePacket

class UpdateBranchConditionalPacket(val actorId: Int, val branchIndex: Int, val conditional: Conditional?) : TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readInt(), buf.readInt(), Conditional.deserialize(buf.readNbt()))

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return player.hasPermissions(3)
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(actorId)
        buf.writeInt(branchIndex)
        buf.writeNbt(conditional?.serialize())
    }
}