package ryanv.talkative.server.events

import dev.architectury.event.events.common.PlayerEvent
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.server.conversations.ConversationManager

object PlayerEventHandler {

    fun init() {
        PlayerEvent.PLAYER_QUIT.register(PlayerEventHandler::playerQuitEvent)
    }

    private fun playerQuitEvent(player: ServerPlayer) {
        if (ConversationManager.isInConversation(player))
            ConversationManager.endConversation(player)
    }

}