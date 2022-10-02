package ryanv.talkative.common.events

import me.shedaniel.architectury.event.events.PlayerEvent
import ryanv.talkative.server.ConversationManager

class PlayerEventHandler {

    companion object {
        fun init() {
            PlayerEvent.PLAYER_QUIT.register {
                if(ConversationManager.isInConversation(it))
                    ConversationManager.endConversation(it)
            }
        }
    }
}