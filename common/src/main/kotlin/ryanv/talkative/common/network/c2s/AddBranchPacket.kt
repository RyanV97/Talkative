package ryanv.talkative.common.network.c2s

import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.api.IActorEntity
import ryanv.talkative.common.data.tree.BranchReference
import ryanv.talkative.common.network.NetworkHandler
import ryanv.talkative.common.network.TalkativePacket
import ryanv.talkative.common.network.s2c.OpenActorEditorPacket
import java.util.function.Supplier

/**
 * Client to Server packet for adding a Branch to the given Actor's list.
 */
class AddBranchPacket(val id: Int, val path: String): TalkativePacket {
    constructor(buf: FriendlyByteBuf): this(buf.readInt(), buf.readUtf())

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(id)
        buf.writeUtf(path)
    }

    override fun process(ctx: Supplier<NetworkManager.PacketContext>) {
        val player = ctx.get().player
        val level = player.level
        if (!level.isClientSide) {
            val entity = level.getEntity(id)
            if(entity is IActorEntity) {
                val branchRef = BranchReference(path)
                entity.actorData.dialogBranches.add(branchRef)
                NetworkHandler.CHANNEL.sendToPlayer(player as ServerPlayer, OpenActorEditorPacket(id, entity.actorData.serialize(CompoundTag())))
            }
        }
    }
}