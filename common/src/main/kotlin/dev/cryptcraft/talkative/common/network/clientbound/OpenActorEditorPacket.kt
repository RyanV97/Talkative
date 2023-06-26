package dev.cryptcraft.talkative.common.network.clientbound

import net.minecraft.network.FriendlyByteBuf
import dev.cryptcraft.talkative.common.data.ActorData
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket

class OpenActorEditorPacket(val entityId: Int, val actorData: ActorData) : TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readInt(), ActorData.deserialize(buf.readNbt()!!))

    override fun encode(buf: FriendlyByteBuf) {
        actorData.validate()
        buf.writeInt(entityId)
        buf.writeNbt(actorData.serialize())
    }
}