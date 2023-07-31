package dev.cryptcraft.talkative.common.item

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import dev.cryptcraft.talkative.api.ActorEntity
import dev.cryptcraft.talkative.common.network.clientbound.OpenActorEditorPacket

class ActorWandItem: Item(Properties().tab(CreativeModeTab.TAB_TOOLS)) {

    //ToDo: Open Editor on regular Right Click

    override fun interactLivingEntity(itemStack: ItemStack, player: Player, livingEntity: LivingEntity, interactionHand: InteractionHand): InteractionResult {
        if (player.level.isClientSide || livingEntity is Player)
            return InteractionResult.FAIL

        val actorEntity = livingEntity as ActorEntity
        OpenActorEditorPacket(livingEntity.id, actorEntity.getOrCreateActorData()).sendToPlayer(player as ServerPlayer)

        return InteractionResult.PASS
    }

}