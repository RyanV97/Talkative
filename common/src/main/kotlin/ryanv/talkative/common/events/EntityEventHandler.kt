package ryanv.talkative.common.events

import me.shedaniel.architectury.event.events.InteractionEvent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import ryanv.talkative.api.IActorEntity
import ryanv.talkative.common.data.tree.DialogNode
import ryanv.talkative.common.item.ActorWandItem
import ryanv.talkative.common.network.NetworkHandler
import ryanv.talkative.common.network.s2c.DialogPacket
import ryanv.talkative.common.network.s2c.OpenActorEditorPacket
import ryanv.talkative.common.util.FileUtil
import ryanv.talkative.server.ConversationManager

class EntityEventHandler {

    companion object {
        fun init() {
            InteractionEvent.INTERACT_ENTITY.register { player, livingEntity, hand ->
                if(player.level.isClientSide || player.mainHandItem.item is ActorWandItem || hand == InteractionHand.OFF_HAND)
                    return@register InteractionResult.PASS
                val entity: IActorEntity = livingEntity as IActorEntity
                if(entity.actorData != null) {
                    ConversationManager.startConversation(player as ServerPlayer, entity)
                }
                InteractionResult.PASS
            }
        }
    }

}