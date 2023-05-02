package ryanv.talkative.common.events

import me.shedaniel.architectury.event.events.InteractionEvent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import ryanv.talkative.api.ActorEntity
import ryanv.talkative.common.item.ActorWandItem
import ryanv.talkative.server.ConversationManager

object EntityEventHandler {

    fun init() {
        InteractionEvent.INTERACT_ENTITY.register(::entityInteractEvent)
    }

    private fun entityInteractEvent(player: Player, livingEntity: Entity, hand: InteractionHand): InteractionResult {
        if (player.level!!.isClientSide || player.mainHandItem.item is ActorWandItem || hand == InteractionHand.OFF_HAND)
            return InteractionResult.PASS

        val entity: ActorEntity = livingEntity as ActorEntity
        if (entity.actorData != null)
            ConversationManager.startConversation(player as ServerPlayer, entity)

        return InteractionResult.PASS
    }

}