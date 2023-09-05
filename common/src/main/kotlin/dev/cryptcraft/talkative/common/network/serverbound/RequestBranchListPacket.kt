package dev.cryptcraft.talkative.common.network.serverbound

import dev.architectury.networking.NetworkManager
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import dev.cryptcraft.talkative.common.network.NetworkHandler
import dev.cryptcraft.talkative.common.network.clientbound.SyncBranchListPacket
import dev.cryptcraft.talkative.server.FileUtil

class RequestBranchListPacket() : NetworkHandler.TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this()

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return player.hasPermissions(3)
    }

    override fun encode(buf: FriendlyByteBuf) {}

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        //ToDo Filter results for branches already attached?
        SyncBranchListPacket(FileUtil.getBranchListCompound()).sendToPlayer(ctx.player as ServerPlayer)
    }
}