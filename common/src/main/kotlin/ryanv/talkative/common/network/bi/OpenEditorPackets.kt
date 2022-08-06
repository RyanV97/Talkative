package ryanv.talkative.common.network.bi

import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.client.TalkativeClient
import ryanv.talkative.common.consts.NBTConstants
import ryanv.talkative.common.data.tree.DialogBranch
import ryanv.talkative.common.data.tree.DialogNode
import ryanv.talkative.common.network.NetworkHandler
import ryanv.talkative.common.network.TalkativePacket
import ryanv.talkative.common.util.FileUtil
import java.util.function.Supplier

class OpenEditorPacket_S2C(val data: CompoundTag?): TalkativePacket {
    constructor(buf: FriendlyByteBuf): this(buf.readNbt())

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeNbt(data)
    }

    override fun process(ctx: Supplier<NetworkManager.PacketContext>) {
        val isClient = ctx.get().player.level.isClientSide()
        println(data)
        if(isClient) {
            if(data != null)
                TalkativeClient.openBranchEditor(DialogBranch.deserialize(data))
        }
    }
}

class OpenEditorPacket_C2S(val path: String): TalkativePacket {
    constructor(buf: FriendlyByteBuf): this(buf.readUtf())

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(path)
    }

    override fun process(ctx: Supplier<NetworkManager.PacketContext>) {
        val isClient = ctx.get().player.level.isClientSide()
        println(isClient)
        if (!isClient) {
            val branchData = FileUtil.getBranchDataFromPath(path)
            NetworkHandler.CHANNEL.sendToPlayer(ctx.get().player as ServerPlayer, OpenEditorPacket_S2C(branchData))
        }
    }
}