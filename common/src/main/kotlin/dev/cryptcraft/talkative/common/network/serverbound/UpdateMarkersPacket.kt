package dev.cryptcraft.talkative.common.network.serverbound

import dev.architectury.networking.NetworkManager
import dev.cryptcraft.talkative.api.actor.ActorEntity
import dev.cryptcraft.talkative.api.actor.markers.Marker
import dev.cryptcraft.talkative.common.network.NetworkHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer

class UpdateMarkersPacket(private val entityId: Int, private val markers: ArrayList<Marker>) : NetworkHandler.TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readInt(), readMarkerList(buf))

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return player.hasPermissions(3)
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(entityId)
        writeMarkerList(buf, markers)
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        val entity = ctx.player.level.getEntity(entityId)
        if (entity is ActorEntity) {
            entity.getOrCreateActorData().markers = markers
        }
    }

    companion object {
        private fun writeMarkerList(buf: FriendlyByteBuf, markers: ArrayList<Marker>) {
            buf.writeInt(markers.size)
            markers.forEach {
                buf.writeNbt(it.serialize())
            }
        }

        private fun readMarkerList(buf: FriendlyByteBuf): ArrayList<Marker> {
            val list = ArrayList<Marker>()
            for (i in 0 until buf.readInt()) {
                list.add(Marker.deserialize(buf.readNbt()) ?: continue)
            }
            return list
        }
    }
}