package ryanv.talkative.common.network.c2s

import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.common.network.TalkativePacket
import ryanv.talkative.common.util.FileUtil
import ryanv.talkative.server.ConversationManager
import java.util.function.Supplier

class DialogResponsePacket(private val responseId: Int): TalkativePacket {
    constructor(buf: FriendlyByteBuf): this(buf.readInt())

    override fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(responseId)
    }

    override fun process(ctx: Supplier<NetworkManager.PacketContext>) {
        val isClient = ctx.get().player.level.isClientSide
        if (!isClient) {
            val player = ctx.get().player as ServerPlayer
            if(ConversationManager.isInConversation(player)) {
                val branch = FileUtil.getBranchFromPath(ConversationManager.getConversation(player)!!.branchPath)
                if (branch != null && branch.nodes.contains(responseId)) {
                    val node = branch.nodes[responseId]
                    //ToDo: Continue progressing to next node (via coniditonals eventually)
                }
            }
        }
    }

}