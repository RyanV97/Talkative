package ryanv.talkative.common.item

import net.minecraft.client.Minecraft
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import ryanv.talkative.Talkative
import ryanv.talkative.api.IActorEntity
import ryanv.talkative.client.gui.TreeEditorScreen
import ryanv.talkative.common.data.Actor
import ryanv.talkative.mixin.entity.LivingEntityActorDataMixin

class ActorWandItem: Item(Properties().tab(CreativeModeTab.TAB_TOOLS)) {

    override fun interactLivingEntity(itemStack: ItemStack?, player: Player?, livingEntity: LivingEntity?, interactionHand: InteractionHand?): InteractionResult {
        if(player!!.level.isClientSide || livingEntity is Player)
            return InteractionResult.FAIL

        var entity: IActorEntity? = livingEntity as IActorEntity
        if(entity?.actorData == null)
            entity?.actorData = Actor()

        Minecraft.getInstance().setScreen(TreeEditorScreen())

        return InteractionResult.PASS
    }

}