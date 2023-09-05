package dev.cryptcraft.talkative.common.network.serverbound

import dev.architectury.networking.NetworkManager
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket
import dev.cryptcraft.talkative.common.network.clientbound.OpenBranchEditorPacket
import dev.cryptcraft.talkative.server.FileUtil

class RequestBranchForEditPacket(private val branchPath: String) : TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf): this(buf.readUtf())

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return player.hasPermissions(3)
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(branchPath)
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        FileUtil.getBranchFromPath(branchPath)?.let { branch ->
            OpenBranchEditorPacket(branchPath, branch).sendToPlayer(ctx.player as ServerPlayer)
        }
    }
}