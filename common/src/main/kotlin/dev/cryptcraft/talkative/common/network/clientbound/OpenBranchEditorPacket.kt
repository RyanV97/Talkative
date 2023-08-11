package dev.cryptcraft.talkative.common.network.clientbound

import dev.architectury.networking.NetworkManager
import net.minecraft.network.FriendlyByteBuf
import dev.cryptcraft.talkative.api.tree.DialogBranch
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchNodeEditorScreen
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket
import net.minecraft.client.Minecraft

class OpenBranchEditorPacket(private val branchPath: String, private val branch: DialogBranch) : TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readUtf(), DialogBranch.deserialize(buf.readNbt()!!)!!)

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(branchPath)
        buf.writeNbt(branch.serialize())
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        TalkativeClient.editingBranch = branch
        TalkativeClient.editingBranchPath = branchPath
        Minecraft.getInstance().setScreen(BranchNodeEditorScreen(Minecraft.getInstance().screen))
    }
}