package ryanv.talkative.common.item

import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import ryanv.talkative.Talkative
import ryanv.talkative.api.IActorEntity
import ryanv.talkative.client.gui.editor.ActorEditorScreen
import ryanv.talkative.common.data.Actor
import ryanv.talkative.common.network.NetworkHandler

class ActorWandItem: Item(Properties().tab(CreativeModeTab.TAB_TOOLS)) {

    override fun interactLivingEntity(itemStack: ItemStack?, player: Player?, livingEntity: LivingEntity?, interactionHand: InteractionHand?): InteractionResult {
        if(player!!.level.isClientSide || livingEntity is Player)
            return InteractionResult.FAIL

        val entity: IActorEntity = livingEntity as IActorEntity
        if(entity.actorData == null)
            entity.actorData = Actor()

        //Replace with proper Server-Side check and packet
        val buf = FriendlyByteBuf(Unpooled.buffer())
        buf.writeNbt(entity.actorData.serialize(CompoundTag()))
        NetworkManager.sendToPlayer(player as ServerPlayer?, NetworkHandler.Client_OpenActorUI, buf)

        return InteractionResult.PASS
    }

}