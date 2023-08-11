package dev.cryptcraft.talkative.common.network.clientbound

import dev.architectury.networking.NetworkManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import dev.cryptcraft.talkative.api.actor.ActorData
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.common.network.NetworkHandler

class UpdateEditingActorDataPacket(private val actorData: ActorData) : NetworkHandler.TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(ActorData.deserialize(buf.readNbt()!!))

    override fun encode(buf: FriendlyByteBuf) {
        actorData.validate()
        buf.writeNbt(actorData.serialize(CompoundTag()))
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        TalkativeClient.editingActorData = actorData
    }
}