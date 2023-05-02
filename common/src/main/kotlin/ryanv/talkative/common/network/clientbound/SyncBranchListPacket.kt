package ryanv.talkative.common.network.clientbound

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.common.network.NetworkHandler.TalkativePacket

class SyncBranchListPacket(val tag: CompoundTag?) : TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readNbt())

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeNbt(tag)
    }
}