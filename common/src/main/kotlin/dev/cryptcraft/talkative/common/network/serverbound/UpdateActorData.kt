package dev.cryptcraft.talkative.common.network.serverbound

import dev.architectury.networking.NetworkManager
import dev.cryptcraft.talkative.api.actor.ActorData
import dev.cryptcraft.talkative.api.actor.ActorEntity
import dev.cryptcraft.talkative.common.network.NetworkHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer

class UpdateActorData(private val entityId: Int, private val actorData: ActorData?) : NetworkHandler.TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readInt(), ActorData.deserialize(buf.readNbt()))

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return player.hasPermissions(3)
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(entityId)
        buf.writeNbt(actorData?.serialize())
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        if (actorData == null) return

        val entity = ctx.player.level.getEntity(entityId)
        if (entity is ActorEntity) entity.setActorData(actorData)
    }
}