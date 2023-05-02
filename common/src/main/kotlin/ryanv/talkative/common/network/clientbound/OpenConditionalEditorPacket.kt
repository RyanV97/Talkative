package ryanv.talkative.common.network.clientbound

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import ryanv.talkative.common.network.NetworkHandler.TalkativePacket

class OpenConditionalEditorPacket(val actorId: Int, val holderData: CompoundTag) : TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readInt(), buf.readNbt()!!)

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(actorId)
        buf.writeNbt(holderData)
    }
}