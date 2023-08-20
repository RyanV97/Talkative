package dev.cryptcraft.talkative.common.network.clientbound

import dev.architectury.networking.NetworkManager
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.FriendlyByteBuf

class SyncBranchListPacket(private val branchListData: CompoundTag?) : TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readNbt())

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeNbt(branchListData)
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        val list = branchListData?.getList("branchList", Tag.TAG_STRING.toInt())
        TalkativeClient.syncBranchList(list)
    }
}