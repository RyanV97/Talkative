package ryanv.talkative.common.network.c2s

import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import ryanv.talkative.common.data.tree.DialogBranch
import ryanv.talkative.common.network.TalkativePacket
import ryanv.talkative.common.util.FileUtil
import java.util.function.Supplier

class UpdateBranchPacket(val path: String, val data: CompoundTag): TalkativePacket {
    constructor(buf: FriendlyByteBuf): this(buf.readUtf(), buf.readNbt()!!)

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(path)
        buf.writeNbt(data)
    }

    override fun process(ctx: Supplier<NetworkManager.PacketContext>) {
        val isClient = ctx.get().player.level.isClientSide()
        if(!isClient) {
            FileUtil.saveBranchData(path, data)
        }
    }
}