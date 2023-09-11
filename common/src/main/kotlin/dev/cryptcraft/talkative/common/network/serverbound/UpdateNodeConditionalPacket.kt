package dev.cryptcraft.talkative.common.network.serverbound

import dev.architectury.networking.NetworkManager
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import dev.cryptcraft.talkative.api.conditional.Conditional
import dev.cryptcraft.talkative.common.network.NetworkHandler
import dev.cryptcraft.talkative.server.FileUtil

class UpdateNodeConditionalPacket(private val branchPath: String, private val nodeId: Int, private val conditional: Conditional?) : NetworkHandler.TalkativePacket.ServerboundTalkativePacket {
    constructor(buf: FriendlyByteBuf) : this(buf.readUtf(), buf.readInt(), Conditional.deserialize(buf.readNbt()))

    override fun permissionCheck(player: ServerPlayer): Boolean {
        return player.hasPermissions(2)
    }

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(branchPath)
        buf.writeInt(nodeId)
        buf.writeNbt(conditional?.serialize())
    }

    override fun onReceived(ctx: NetworkManager.PacketContext) {
        FileUtil.getBranchFromPath(branchPath)?.let { branch ->
            branch.getNode(nodeId)?.setConditional(conditional)
            FileUtil.saveBranchData(branchPath, branch.serialize())
            //Disabled to see if updating the conditional locally is enough
            //UpdateEditingBranchPacket(packet.branchPath, branch).sendToPlayer(ctx.player as ServerPlayer)
        }
    }
}