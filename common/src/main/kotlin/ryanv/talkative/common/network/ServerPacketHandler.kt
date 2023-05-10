package ryanv.talkative.common.network

import me.shedaniel.architectury.networking.NetworkManager.PacketContext
import me.shedaniel.architectury.platform.Platform
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.api.ActorEntity
import ryanv.talkative.common.data.tree.BranchReference
import ryanv.talkative.common.network.NetworkHandler.TalkativePacket
import ryanv.talkative.common.network.clientbound.*
import ryanv.talkative.common.network.serverbound.*
import ryanv.talkative.common.util.FileUtil
import ryanv.talkative.server.conversations.ConversationManager
import java.util.function.Supplier

object ServerPacketHandler {
    fun processPacket(packet: TalkativePacket.ServerboundTalkativePacket, ctx: Supplier<PacketContext>) {
        val context = ctx.get()
        if (Platform.isDevelopmentEnvironment()) println("Server Received Packet: $packet")

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
            is UpdateBranchConditionalPacket -> context.queue { processUpdateBranchConditional(packet, context) }
            is UpdateNodeConditionalPacket -> context.queue { processUpdateNodeConditional(packet, context) }
            is RequestBranchListPacket -> context.queue { processRequestBranchList(context) }
        }
    }

    private fun processAttachBranch(packet: AttachBranchPacket, ctx: PacketContext) {
        val player = ctx.player
        val entity = player.level.getEntity(packet.entityId)
        if (entity is ActorEntity) {
            val branchRef = BranchReference(packet.path)
            entity.getActorData()?.let { actorData ->
                actorData.dialogBranches.add(branchRef)
                OpenActorEditorPacket(packet.entityId, actorData).sendToPlayer(player as ServerPlayer)
            }
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
            entity.getActorData()?.let { actorData ->
                actorData.dialogBranches.removeAt(packet.index)
                OpenActorEditorPacket(packet.entityId, actorData).sendToPlayer(player)
            }
        }
    }

    private fun processRequestBranchForEdit(packet: RequestBranchForEditPacket, ctx: PacketContext) {
        FileUtil.getBranchFromPath(packet.path)?.let { branch ->
            OpenBranchEditorPacket(packet.path, branch).sendToPlayer(ctx.player as ServerPlayer)
        }
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

    private fun processUpdateBranchConditional(packet: UpdateBranchConditionalPacket, ctx: PacketContext) {
        val level = ctx.player.level
        val actorEntity = level.getEntity(packet.actorId) as ActorEntity

        actorEntity.getActorData()?.let { actorData ->
            actorData.dialogBranches[packet.branchIndex].setConditional(packet.conditional)
            UpdateActorDataScreenPacket(actorData).sendToPlayer(ctx.player as ServerPlayer)
        }
    }

    private fun processUpdateNodeConditional(packet: UpdateNodeConditionalPacket, ctx: PacketContext) {
        FileUtil.getBranchFromPath(packet.branchPath)?.let { branch ->
            branch.getNode(packet.nodeId)?.setConditional(packet.conditional)
            FileUtil.saveBranchData(packet.branchPath, branch.serialize())
            UpdateEditingBranchPacket(packet.branchPath, branch).sendToPlayer(ctx.player as ServerPlayer)
        }
    }

    private fun processRequestBranchList(ctx: PacketContext) {
        //ToDo Filter results for branches already attached?
        SyncBranchListPacket(FileUtil.getBranchListCompound()).sendToPlayer(ctx.player as ServerPlayer)
    }

}