package ryanv.talkative.common.network.s2c

import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.LivingEntity
import ryanv.talkative.client.TalkativeClient
import ryanv.talkative.common.data.Actor
import ryanv.talkative.common.network.TalkativePacket
import java.util.function.Supplier

class OpenActorEditorPacket(val id: Int, val tag: CompoundTag?): TalkativePacket {
    constructor(buf: FriendlyByteBuf): this(buf.readInt(), buf.readNbt())

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(id)
        buf.writeNbt(tag)
    }

    override fun process(ctx: Supplier<NetworkManager.PacketContext>) {
        val level = ctx.get().player.level
        val entity = level.getEntity(id)
        if(level.isClientSide && entity is LivingEntity && tag != null)
            TalkativeClient.openActorEditor(entity, Actor.deserialize(tag))
    }
}