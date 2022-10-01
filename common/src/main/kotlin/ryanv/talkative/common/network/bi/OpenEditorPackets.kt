package ryanv.talkative.common.network.bi

import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.client.TalkativeClient
import ryanv.talkative.common.data.tree.DialogBranch
import ryanv.talkative.common.network.NetworkHandler
import ryanv.talkative.common.network.TalkativePacket
import ryanv.talkative.common.util.FileUtil
import java.util.function.Supplier

class OpenBranchEditorPacket_S2C(val path: String, val data: CompoundTag?): TalkativePacket {
    constructor(buf: FriendlyByteBuf): this(buf.readUtf(), buf.readNbt())

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(path)
        buf.writeNbt(data)
    }

    override fun process(ctx: Supplier<NetworkManager.PacketContext>) {
        val isClient = ctx.get().player.level.isClientSide()
        if(isClient) {
            if (data != null) {
                val branch = DialogBranch.deserialize(data)
                if(branch != null)
                    TalkativeClient.openBranchEditor(path, branch)
            }
        }
    }
}

class OpenBranchEditorPacket_C2S(val path: String): TalkativePacket {
    constructor(buf: FriendlyByteBuf): this(buf.readUtf())

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(path)
    }

    override fun process(ctx: Supplier<NetworkManager.PacketContext>) {
        val isClient = ctx.get().player.level.isClientSide()
        if (!isClient) {
            val branchData = FileUtil.getBranchDataFromPath(path)
            NetworkHandler.CHANNEL.sendToPlayer(ctx.get().player as ServerPlayer, OpenBranchEditorPacket_S2C(path, branchData))
        }
    }
}