package dev.cryptcraft.talkative.common.network.serverbound

import dev.architectury.networking.NetworkManager
import dev.cryptcraft.talkative.api.actor.ActorEntity
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket
import dev.cryptcraft.talkative.common.network.clientbound.OpenActorEditorPacket

class UnAttachBranchPacket(private val entityId: Int, private val branchIndex: Int) : TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readInt(), buf.readInt())

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return player.hasPermissions(3)
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(entityId)
        buf.writeInt(branchIndex)
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        val player = ctx.player as ServerPlayer
        val entity = player.level.getEntity(entityId)
        if (entity is ActorEntity) {
            entity.getActorData()?.let { actorData ->
                actorData.dialogBranches.removeAt(branchIndex)
                OpenActorEditorPacket(entityId, actorData).sendToPlayer(player)
            }
        }
    }
}