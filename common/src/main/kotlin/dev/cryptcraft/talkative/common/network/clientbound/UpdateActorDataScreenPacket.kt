package dev.cryptcraft.talkative.common.network.clientbound

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import dev.cryptcraft.talkative.common.data.ActorData
import dev.cryptcraft.talkative.common.network.NetworkHandler

class UpdateActorDataScreenPacket(val actorData: ActorData) : NetworkHandler.TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(ActorData.deserialize(buf.readNbt()!!))

    override fun encode(buf: FriendlyByteBuf) {
        actorData.validate()
        buf.writeNbt(actorData.serialize(CompoundTag()))
    }
}