package ryanv.talkative.common.network.clientbound

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import ryanv.talkative.common.network.NetworkHandler.TalkativePacket

class OpenActorEditorPacket(val id: Int, val tag: CompoundTag?) : TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readInt(), buf.readNbt())

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(id)
        buf.writeNbt(tag)
    }
}