package dev.cryptcraft.talkative.server.events

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.InteractionEvent
import dev.cryptcraft.talkative.Talkative
import dev.cryptcraft.talkative.api.actor.ActorEntity
import dev.cryptcraft.talkative.common.network.clientbound.OpenActorEditorPacket
import dev.cryptcraft.talkative.server.conversations.ConversationManager
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player

object EntityEventHandler {
    fun init() {
        InteractionEvent.INTERACT_ENTITY.register(EntityEventHandler::entityInteractEvent)
    }

    private fun entityInteractEvent(player: Player, livingEntity: Entity, hand: InteractionHand): EventResult {
        if (player.level.isClientSide || hand == InteractionHand.OFF_HAND)
            return EventResult.pass()

        if (player.isHolding(Talkative.ACTOR_WAND.get())) {
            OpenActorEditorPacket(livingEntity.id, (livingEntity as ActorEntity).getOrCreateActorData()).sendToPlayer(player as ServerPlayer)
            return EventResult.interruptFalse()
        }
        else {
            val entity: ActorEntity = livingEntity as ActorEntity
            if (entity.getActorData() != null) {
                ConversationManager.startConversation(player as ServerPlayer, entity)
                return EventResult.interruptFalse()
            }
            return EventResult.pass()
        }
    }
}