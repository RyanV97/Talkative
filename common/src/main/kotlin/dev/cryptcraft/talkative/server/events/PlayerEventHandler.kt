package dev.cryptcraft.talkative.server.events

import dev.architectury.event.events.common.PlayerEvent
import net.minecraft.server.level.ServerPlayer
import dev.cryptcraft.talkative.server.conversations.ConversationManager

object PlayerEventHandler {

    fun init() {
        PlayerEvent.PLAYER_QUIT.register(PlayerEventHandler::playerQuitEvent)
    }

    private fun playerQuitEvent(player: ServerPlayer) {
        if (ConversationManager.isInConversation(player))
            ConversationManager.endConversation(player)
    }

}