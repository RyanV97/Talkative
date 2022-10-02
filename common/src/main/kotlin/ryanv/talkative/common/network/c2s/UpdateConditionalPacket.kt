package ryanv.talkative.common.network.c2s

import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import ryanv.talkative.api.IActorEntity
import ryanv.talkative.api.IConditional
import ryanv.talkative.common.consts.NBTConstants
import ryanv.talkative.common.data.conditional.Conditional
import ryanv.talkative.common.network.TalkativePacket
import ryanv.talkative.common.util.FileUtil
import java.util.function.Supplier

class UpdateConditionalPacket(val actorId: Int, val holderData: CompoundTag): TalkativePacket {
    constructor(buf: FriendlyByteBuf): this(buf.readInt(), buf.readNbt()!!)

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(actorId)
        buf.writeNbt(holderData)
    }

    override fun process(ctx: Supplier<NetworkManager.PacketContext>) {
        val player = ctx.get().player
        if(!player.level.isClientSide) {
            val type = IConditional.Type.valueOf(holderData.getString(NBTConstants.CONDITIONAL_HOLDER_TYPE))
            val branchPath = holderData.getString(NBTConstants.CONDITIONAL_HOLDER_BRANCH)
            val conditional = Conditional.deserialize(holderData.getCompound(NBTConstants.CONDITIONAL))

            when(type) {
                IConditional.Type.BRANCH -> {
                    val entity = player.level.getEntity(actorId)
                    if (entity is IActorEntity) {
                        val actor = entity as IActorEntity
                        actor.actorData.getBranchFromPath(branchPath)?.setConditional(conditional)
                    }
                }
                IConditional.Type.NODE -> {
                    val id = holderData.getInt(NBTConstants.CONDITIONAL_HOLDER_ID)
                    val branch = FileUtil.getBranchFromPath(branchPath)
                    branch?.nodes?.get(id)?.setConditional(conditional)
                }
            }
        }
    }
}