package dev.cryptcraft.talkative.common.network.clientbound

import dev.architectury.networking.NetworkManager
import dev.cryptcraft.talkative.api.actor.markers.Marker
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.common.network.NetworkHandler
import net.minecraft.network.FriendlyByteBuf

class SyncMarkerPacket(private val entityId: Int, private val marker: Marker?) : NetworkHandler.TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readInt(), Marker.deserialize(buf.readNbt()))

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(entityId)
        buf.writeNbt(marker?.serialize())
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        TalkativeClient.syncMarker(entityId, marker)
    }
}