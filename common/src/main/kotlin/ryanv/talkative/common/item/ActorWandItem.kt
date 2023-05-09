package ryanv.talkative.common.item

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import ryanv.talkative.api.ActorEntity
import ryanv.talkative.common.data.ActorData
import ryanv.talkative.common.network.clientbound.OpenActorEditorPacket

class ActorWandItem: Item(Properties().tab(CreativeModeTab.TAB_TOOLS)) {

    override fun interactLivingEntity(itemStack: ItemStack?, player: Player?, livingEntity: LivingEntity?, interactionHand: InteractionHand?): InteractionResult {
        if(player!!.level.isClientSide || livingEntity is Player)
            return InteractionResult.FAIL

        val entity: ActorEntity = livingEntity as ActorEntity
        if (entity.getActorData() == null)
            entity.setActorData(ActorData())

        //ToDo: Replace with proper Server-Side check
        OpenActorEditorPacket(livingEntity.id, entity.getActorData()?.serialize(CompoundTag())).sendToPlayer(player as ServerPlayer)

        return InteractionResult.PASS
    }

}