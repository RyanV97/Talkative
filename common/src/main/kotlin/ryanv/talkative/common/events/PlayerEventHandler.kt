package ryanv.talkative.common.events

import me.shedaniel.architectury.event.events.PlayerEvent
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.server.ConversationManager

object PlayerEventHandler {

    fun init() {
        PlayerEvent.PLAYER_QUIT.register(::playerQuitEvent)
    }

    private fun playerQuitEvent(player: ServerPlayer) {
        if(ConversationManager.isInConversation(player))
            ConversationManager.endConversation(player)
    }

}