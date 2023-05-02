package ryanv.talkative.common.network

import me.shedaniel.architectury.networking.NetworkManager
import me.shedaniel.architectury.utils.NbtType
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.LivingEntity
import ryanv.talkative.client.gui.TalkativeScreen
import ryanv.talkative.client.gui.dialog.DialogScreen
import ryanv.talkative.client.gui.editor.ActorEditorScreen
import ryanv.talkative.client.gui.editor.BranchDirectoryScreen
import ryanv.talkative.client.gui.editor.BranchEditorScreen
import ryanv.talkative.client.gui.editor.ConditionalEditorScreen
import ryanv.talkative.common.data.ServerActorData
import ryanv.talkative.common.data.tree.DialogBranch
import ryanv.talkative.common.network.NetworkHandler.TalkativePacket
import ryanv.talkative.common.network.clientbound.SyncBranchListPacket
import ryanv.talkative.common.network.clientbound.DialogPacket
import ryanv.talkative.common.network.clientbound.OpenActorEditorPacket
import ryanv.talkative.common.network.clientbound.OpenBranchEditorPacket
import ryanv.talkative.common.network.clientbound.OpenConditionalEditorPacket
import java.util.function.Supplier

object ClientPacketHandler {
    fun processPacket(packet: TalkativePacket.ClientboundTalkativePacket, ctx: Supplier<NetworkManager.PacketContext>) {
        val context = ctx.get()
        when (packet) {
            is DialogPacket -> context.queue { processDialog(packet) }
            is OpenActorEditorPacket -> context.queue { processOpenActorEditor(packet, context) }
            is OpenBranchEditorPacket -> context.queue { processOpenBranchEditor(packet) }
            is OpenConditionalEditorPacket -> context.queue { processOpenConditionalEditorPacket(packet) }
            is SyncBranchListPacket -> context.queue { processSyncBranchList(packet) }
        }
    }

    private fun processDialog(packet: DialogPacket) {
        var currentScreen = Minecraft.getInstance().screen

        if (currentScreen == null) {
            currentScreen = DialogScreen()
            Minecraft.getInstance().setScreen(currentScreen)
        }

        if (currentScreen is DialogScreen)
            currentScreen.loadDialog(packet.dialogLine!!, packet.responses, packet.exitNode)
    }

    private fun processOpenActorEditor(packet: OpenActorEditorPacket, ctx: NetworkManager.PacketContext) {
        ctx.player.level.getEntity(packet.id)?.let {
            if (it is LivingEntity && packet.tag != null)
                if (Minecraft.getInstance().screen is ActorEditorScreen)
                    (Minecraft.getInstance().screen as ActorEditorScreen).updateData(ServerActorData.deserialize(packet.tag))
                else
                    Minecraft.getInstance().setScreen(ActorEditorScreen(it, ServerActorData.deserialize(packet.tag)))
        }
    }

    private fun processOpenBranchEditor(packet: OpenBranchEditorPacket) {
        if (packet.data != null) {
            val branch = DialogBranch.deserialize(packet.data)
            if (branch != null) {
                var parent: TalkativeScreen? = null
                if (Minecraft.getInstance().screen is ActorEditorScreen)
                    parent = Minecraft.getInstance().screen as ActorEditorScreen

                Minecraft.getInstance().setScreen(BranchEditorScreen(parent, packet.path, branch))
            }
        }
    }

    private fun processOpenConditionalEditorPacket(packet: OpenConditionalEditorPacket) {
        Minecraft.getInstance()
            .setScreen(ConditionalEditorScreen(Minecraft.getInstance().screen, packet.actorId, packet.holderData))
    }

    private fun processSyncBranchList(packet: SyncBranchListPacket) {
        val list = packet.tag?.getList("branchList", NbtType.STRING)
        val screen = Minecraft.getInstance().screen
        if (screen is BranchDirectoryScreen)
            screen.loadBranchList(list)
    }
}