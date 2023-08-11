package dev.cryptcraft.talkative.common.network.clientbound

import dev.architectury.networking.NetworkManager
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchDirectoryScreen
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket
import net.minecraft.client.Minecraft
import net.minecraft.nbt.Tag

class SyncBranchListPacket(private val branchListData: CompoundTag?) : TalkativePacket.ClientboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readNbt())

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeNbt(branchListData)
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        val list = branchListData?.getList("branchList", Tag.TAG_STRING.toInt())
        val screen = Minecraft.getInstance().screen
        if (screen is BranchDirectoryScreen)
            screen.loadBranchList(list)
    }
}