package dev.cryptcraft.talkative.common.network.serverbound

import dev.architectury.networking.NetworkManager
import dev.cryptcraft.talkative.api.actor.ActorEntity
import dev.cryptcraft.talkative.common.network.NetworkHandler
import dev.cryptcraft.talkative.common.network.clientbound.OpenActorEditorPacket
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import java.util.function.Supplier

class RequestEditActorPacket(private val entityId: Int) : NetworkHandler.TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readInt())

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return player.hasPermissions(3)
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(entityId)
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        val player = ctx.player as ServerPlayer
        val actorEntity = player.level.getEntity(entityId)
        if (actorEntity is ActorEntity)
            OpenActorEditorPacket(entityId, actorEntity.getOrCreateActorData()).sendToPlayer(player)
    }
}