package dev.cryptcraft.talkative.common.network.clientbound

import dev.architectury.networking.NetworkManager
import dev.cryptcraft.talkative.api.actor.ActorData
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket
import net.minecraft.network.FriendlyByteBuf

class OpenActorEditorPacket(private val entityId: Int, private val actorData: ActorData?) : TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readInt(), readData(buf))

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(entityId)
        if (actorData != null) {
            actorData.validate()
            buf.writeNbt(actorData.serialize())
        }
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        TalkativeClient.openEditorScreen(ctx.player.level.getEntity(entityId), actorData)
    }

    companion object {
        fun readData(buf: FriendlyByteBuf): ActorData? {
            return if (buf.readableBytes() > 0)
                ActorData.deserialize(buf.readNbt()!!)
            else
                null
        }
    }
}