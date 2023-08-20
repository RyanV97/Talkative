package dev.cryptcraft.talkative.common.network.serverbound

import dev.architectury.networking.NetworkManager
import dev.cryptcraft.talkative.api.actor.ActorEntity
import dev.cryptcraft.talkative.api.tree.BranchReference
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket
import dev.cryptcraft.talkative.common.network.clientbound.OpenActorEditorPacket
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer

class AttachBranchPacket(private val entityId: Int, private val path: String) : TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readInt(), buf.readUtf())

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return player.hasPermissions(3)
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(entityId)
        buf.writeUtf(path)
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        val player = ctx.player
        val entity = player.level.getEntity(entityId)
        if (entity is ActorEntity) {
            val branchRef = BranchReference(path)
            entity.getActorData()?.let { actorData ->
                actorData.dialogBranches.add(branchRef)
                OpenActorEditorPacket(entityId, actorData).sendToPlayer(player as ServerPlayer)
            }
        }
    }
}