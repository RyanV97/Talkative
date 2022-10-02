package ryanv.talkative.common.network.s2c

import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import ryanv.talkative.client.TalkativeClient
import ryanv.talkative.common.network.TalkativePacket
import java.util.function.Supplier

class OpenConditionalEditorPacket(val actorId: Int, private val holderData: CompoundTag): TalkativePacket {
    constructor(buf: FriendlyByteBuf): this(buf.readInt(), buf.readNbt()!!)

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(actorId)
        buf.writeNbt(holderData)
    }

    override fun process(ctx: Supplier<NetworkManager.PacketContext>) {
        TalkativeClient.openConditionalEditor(actorId, holderData)
    }
}