package dev.cryptcraft.talkative.common.network.serverbound

import dev.architectury.networking.NetworkManager
import dev.cryptcraft.talkative.api.actor.ActorEntity
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import dev.cryptcraft.talkative.api.conditional.Conditional
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket
import dev.cryptcraft.talkative.common.network.clientbound.UpdateEditingActorDataPacket

class UpdateBranchConditionalPacket(private val actorId: Int, private val branchIndex: Int, private val conditional: Conditional?) : TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readInt(), buf.readInt(), Conditional.deserialize(buf.readNbt()))

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return player.hasPermissions(3)
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(actorId)
        buf.writeInt(branchIndex)
        buf.writeNbt(conditional?.serialize())
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        val level = ctx.player.level
        val actorEntity = level.getEntity(actorId) as ActorEntity

        actorEntity.getActorData()?.let { actorData ->
            actorData.dialogBranches[branchIndex].setConditional(conditional)
            UpdateEditingActorDataPacket(actorData).sendToPlayer(ctx.player as ServerPlayer)
        }
    }
}