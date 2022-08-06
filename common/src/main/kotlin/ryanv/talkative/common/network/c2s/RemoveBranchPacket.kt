package ryanv.talkative.common.network.c2s

import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.api.IActorEntity
import ryanv.talkative.common.data.tree.BranchReference
import ryanv.talkative.common.network.NetworkHandler
import ryanv.talkative.common.network.TalkativePacket
import ryanv.talkative.common.network.s2c.OpenActorUIPacket
import java.util.function.Supplier

class RemoveBranchPacket(val id: Int, val index: Int): TalkativePacket {
    constructor(buf: FriendlyByteBuf): this(buf.readInt(), buf.readInt())

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(id)
        buf.writeInt(index)
    }

    override fun process(ctx: Supplier<NetworkManager.PacketContext>) {
        val player = ctx.get().player
        val level = player.level
        if (!level.isClientSide) {
            val entity = level.getEntity(id)
            if(entity is IActorEntity) {
                entity.actorData.dialogBranches.removeAt(index)
                NetworkHandler.CHANNEL.sendToPlayer(player as ServerPlayer, OpenActorUIPacket(id, entity.actorData.serialize(CompoundTag())))
            }
        }
    }
}