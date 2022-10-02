package ryanv.talkative.common.network.c2s

import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.common.network.TalkativePacket
import ryanv.talkative.server.ConversationManager
import java.util.function.Supplier

class FinishConversationPacket(): TalkativePacket {
    constructor(buf: FriendlyByteBuf): this()

    override fun encode(buf: FriendlyByteBuf) {
    }

    override fun process(ctx: Supplier<NetworkManager.PacketContext>) {
        val player = ctx.get().player
        if(player.level.isClientSide)
            return
        ConversationManager.endConversation(player as ServerPlayer)
    }
}