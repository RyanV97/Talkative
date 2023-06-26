package dev.cryptcraft.talkative.common.network.clientbound

import net.minecraft.network.FriendlyByteBuf
import dev.cryptcraft.talkative.common.data.tree.DialogBranch
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket

class OpenBranchEditorPacket(val path: String, val branch: DialogBranch) : TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readUtf(), DialogBranch.deserialize(buf.readNbt()!!)!!)

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(path)
        buf.writeNbt(branch.serialize())
    }
}