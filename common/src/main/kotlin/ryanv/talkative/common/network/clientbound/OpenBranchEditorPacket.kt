package ryanv.talkative.common.network.clientbound

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import ryanv.talkative.common.network.NetworkHandler.TalkativePacket

class OpenBranchEditorPacket(val path: String, val data: CompoundTag?): TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf): this(buf.readUtf(), buf.readNbt())

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(path)
        buf.writeNbt(data)
    }
}