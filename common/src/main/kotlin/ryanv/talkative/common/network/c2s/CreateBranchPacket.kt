package ryanv.talkative.common.network.c2s

import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.common.network.TalkativePacket
import ryanv.talkative.common.network.bi.SyncBranchListPacket
import ryanv.talkative.common.util.FileUtil
import java.util.function.Supplier

class CreateBranchPacket(val path: String): TalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readUtf())

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(path)
    }

    override fun process(ctx: Supplier<NetworkManager.PacketContext>) {
        val isClient = ctx.get().player.level.isClientSide
        if (!isClient) {
            FileUtil.createBranchAtPath(path)
            SyncBranchListPacket.sendListToClient(ctx.get().player as ServerPlayer)
        }
    }
}