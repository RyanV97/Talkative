package dev.cryptcraft.talkative.common.network.serverbound

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import dev.cryptcraft.talkative.common.data.conditional.Conditional
import dev.cryptcraft.talkative.common.network.NetworkHandler

class UpdateNodeConditionalPacket(val branchPath: String, val nodeId: Int, val conditional: Conditional?) : NetworkHandler.TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readUtf(), buf.readInt(), Conditional.deserialize(buf.readNbt()))

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return player.hasPermissions(3)
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(branchPath)
        buf.writeInt(nodeId)
        buf.writeNbt(conditional?.serialize())
    }
}