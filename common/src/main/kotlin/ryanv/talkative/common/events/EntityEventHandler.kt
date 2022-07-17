package ryanv.talkative.common.events

import me.shedaniel.architectury.event.events.InteractionEvent
import net.minecraft.world.InteractionResult
import ryanv.talkative.api.IActorEntity
import ryanv.talkative.client.TalkativeClient
import ryanv.talkative.common.item.ActorWandItem

class EntityEventHandler {

    companion object {
        fun init() {
            InteractionEvent.INTERACT_ENTITY.register { player, livingEntity, hand ->
                val entity: IActorEntity = livingEntity as IActorEntity
                if(entity.actorData != null) {
                    if (player.level.isClientSide()) {
                        if (player.mainHandItem.item is ActorWandItem)
                            return@register InteractionResult.PASS
                        TalkativeClient.openDialogScreen(entity, player)
                    }
                }
                InteractionResult.PASS
            }
        }
    }

}