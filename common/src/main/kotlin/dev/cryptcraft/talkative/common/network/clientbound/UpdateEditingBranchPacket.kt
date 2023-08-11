package dev.cryptcraft.talkative.common.network.clientbound

import dev.architectury.networking.NetworkManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import dev.cryptcraft.talkative.api.tree.DialogBranch
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.common.network.NetworkHandler

class UpdateEditingBranchPacket(private val branchPath: String, private val branch: DialogBranch) : NetworkHandler.TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readUtf(), DialogBranch.deserialize(buf.readNbt()!!)!!)

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(branchPath)
        buf.writeNbt(branch.serialize(CompoundTag()))
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        TalkativeClient.editingBranchPath = branchPath
        TalkativeClient.editingBranch = branch
    }
}