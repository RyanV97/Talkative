package dev.cryptcraft.talkative.common.network.clientbound

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import dev.cryptcraft.talkative.common.data.tree.DialogBranch
import dev.cryptcraft.talkative.common.network.NetworkHandler

class UpdateEditingBranchPacket(val branchPath: String, val branch: DialogBranch) : NetworkHandler.TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readUtf(), DialogBranch.deserialize(buf.readNbt()!!)!!)

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(branchPath)
        buf.writeNbt(branch.serialize(CompoundTag()))
    }
}