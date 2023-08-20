package dev.cryptcraft.talkative.common.item

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class ActorWandItem: Item(Properties().tab(CreativeModeTab.TAB_TOOLS)) {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        //ToDo Shit's fucked >:(
        val itemStack = player.getItemInHand(usedHand)
//        if (level.isClientSide || !player.hasPermissions(3))
//            return InteractionResultHolder.pass(itemStack)
//
//        OpenActorEditorPacket(-1, null).sendToPlayer(player as ServerPlayer)
        return InteractionResultHolder.consume(itemStack)
    }
}