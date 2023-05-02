package ryanv.talkative.common.network

import me.shedaniel.architectury.networking.NetworkManager.PacketContext
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.api.ActorEntity
import ryanv.talkative.common.data.ServerActorData
import ryanv.talkative.common.data.conditional.Conditional
import ryanv.talkative.common.data.tree.BranchReference
import ryanv.talkative.common.network.NetworkHandler.TalkativePacket
import ryanv.talkative.common.network.clientbound.SyncBranchListPacket
import ryanv.talkative.common.network.clientbound.OpenActorEditorPacket
import ryanv.talkative.common.network.clientbound.OpenBranchEditorPacket
import ryanv.talkative.common.network.serverbound.*
import ryanv.talkative.common.util.FileUtil
import ryanv.talkative.common.util.NBTConstants
import ryanv.talkative.server.ConversationManager
import java.util.function.Supplier

object ServerPacketHandler {
    fun processPacket(packet: TalkativePacket.ServerboundTalkativePacket, ctx: Supplier<PacketContext>) {
        val context = ctx.get()
        if (!packet.permissionCheck(context.player as ServerPlayer)) {
            context.player.sendMessage(
                TextComponent("You have insufficient permissions to perform this action.")
                    .withStyle(ChatFormatting.DARK_RED),
                null
            )
            return
        }

        when (packet) {
            is AttachBranchPacket -> context.queue { processAttachBranch(packet, context) }
            is DialogResponsePacket -> context.queue { processDialogResponse(packet, context) }
            is FinishConversationPacket -> context.queue { processFinishConversation(context) }
            is UnAttachBranchPacket -> context.queue { processUnAttachBranch(packet, context) }
            is RequestBranchForEditPacket -> context.queue { processRequestBranchForEdit(packet, context) }
            is UpdateBranchPacket -> context.queue { processUpdateBranch(packet, context) }
            is UpdateConditionalPacket -> context.queue { processUpdateConditional(packet, context) }
            is RequestBranchListPacket -> context.queue { processRequestBranchList(context) }
        }
    }

    private fun processAttachBranch(packet: AttachBranchPacket, ctx: PacketContext) {
        val player = ctx.player
        val entity = player.level.getEntity(packet.entityId)
        if (entity is ActorEntity) {
            val branchRef = BranchReference(packet.path)
            (entity.actorData as ServerActorData).dialogBranches.add(branchRef)
            OpenActorEditorPacket(packet.entityId, entity.actorData.serialize(CompoundTag())).sendToPlayer(player as ServerPlayer)
        }
    }

    private fun processDialogResponse(packet: DialogResponsePacket, ctx: PacketContext) {
        val player = ctx.player as ServerPlayer
        if (ConversationManager.isInConversation(player)) {
            if (packet.responseId >= 0)
                ConversationManager.getConversation(player)?.onResponse(packet.responseId)
            else
                ConversationManager.getConversation(player)?.progressConversation()
        }
    }

    private fun processFinishConversation(ctx: PacketContext) {
        ConversationManager.endConversation(ctx.player as ServerPlayer)
    }

    private fun processUnAttachBranch(packet: UnAttachBranchPacket, ctx: PacketContext) {
        val player = ctx.player as ServerPlayer
        val entity = player.level.getEntity(packet.entityId)
        if (entity is ActorEntity) {
            (entity.actorData as ServerActorData).dialogBranches.removeAt(packet.index)
            OpenActorEditorPacket(packet.entityId, entity.actorData.serialize(CompoundTag())).sendToPlayer(player)
        }
    }

    private fun processRequestBranchForEdit(packet: RequestBranchForEditPacket, ctx: PacketContext) {
        val branchData = FileUtil.getBranchDataFromPath(packet.path)
        OpenBranchEditorPacket(packet.path, branchData).sendToPlayer(ctx.player as ServerPlayer)
    }

    private fun processUpdateBranch(packet: UpdateBranchPacket, ctx: PacketContext) {
        val player = ctx.player as ServerPlayer

        when (packet.action) {
            UpdateBranchPacket.UpdateAction.CREATE -> {
                FileUtil.createBranchAtPath(packet.path)
                SyncBranchListPacket(FileUtil.getBranchListCompound()).sendToPlayer(player)
            }

            UpdateBranchPacket.UpdateAction.MODIFY -> {
                packet.data?.let { FileUtil.saveBranchData(packet.path, it) }
            }

            UpdateBranchPacket.UpdateAction.DELETE -> {
                FileUtil.deleteBranchAtPath(packet.path)
                SyncBranchListPacket(FileUtil.getBranchListCompound()).sendToPlayer(player)
            }
        }
    }

    private fun processUpdateConditional(packet: UpdateConditionalPacket, ctx: PacketContext) {
        val player = ctx.player
        val branchPath = packet.holderData.getString(NBTConstants.CONDITIONAL_HOLDER_BRANCH)
        val conditional = Conditional.deserialize(packet.holderData.getCompound(NBTConstants.CONDITIONAL))
        //ToDO Update Conditional
    }

    private fun processRequestBranchList(ctx: PacketContext) {
        //ToDo Filter results for branches already attached?
        SyncBranchListPacket(FileUtil.getBranchListCompound()).sendToPlayer(ctx.player as ServerPlayer)
    }

}