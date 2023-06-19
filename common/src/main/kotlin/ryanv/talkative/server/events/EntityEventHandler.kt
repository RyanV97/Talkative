package ryanv.talkative.server.events

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.InteractionEvent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import ryanv.talkative.api.ActorEntity
import ryanv.talkative.common.item.ActorWandItem
import ryanv.talkative.server.conversations.ConversationManager

object EntityEventHandler {

    fun init() {
        InteractionEvent.INTERACT_ENTITY.register(EntityEventHandler::entityInteractEvent)
    }

    private fun entityInteractEvent(player: Player, livingEntity: Entity, hand: InteractionHand): EventResult {
        if (player.level.isClientSide || player.mainHandItem.item is ActorWandItem || hand == InteractionHand.OFF_HAND)
            return EventResult.pass()

        val entity: ActorEntity = livingEntity as ActorEntity
        if (entity.getActorData() != null)
            ConversationManager.startConversation(player as ServerPlayer, entity)

        return EventResult.pass()
    }

}