package ryanv.talkative.common.network.clientbound

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import ryanv.talkative.common.data.ActorData
import ryanv.talkative.common.network.NetworkHandler

class UpdateActorDataScreenPacket(val actorData: ActorData) : NetworkHandler.TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(ActorData.deserialize(buf.readNbt()!!))

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeNbt(actorData.serialize(CompoundTag()))
    }
}