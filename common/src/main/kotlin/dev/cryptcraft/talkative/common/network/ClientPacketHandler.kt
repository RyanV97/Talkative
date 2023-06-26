package dev.cryptcraft.talkative.common.network

import dev.architectury.networking.NetworkManager
import dev.architectury.platform.Platform
import net.minecraft.client.Minecraft
import net.minecraft.nbt.Tag
import net.minecraft.world.entity.LivingEntity
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.client.gui.dialog.DialogScreen
import dev.cryptcraft.talkative.client.gui.editor.MainEditorScreen
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchDirectoryScreen
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchNodeEditorScreen
import dev.cryptcraft.talkative.common.network.NetworkHandler.TalkativePacket
import dev.cryptcraft.talkative.common.network.clientbound.*
import java.util.function.Supplier

object ClientPacketHandler {
    fun processPacket(packet: TalkativePacket.ClientboundTalkativePacket, ctx: Supplier<NetworkManager.PacketContext>) {
        val context = ctx.get()
        if (Platform.isDevelopmentEnvironment()) println("Client Received Packet: $packet")
        when (packet) {
            is DialogPacket -> context.queue { processDialog(packet) }
            is OpenActorEditorPacket -> context.queue { processOpenActorEditor(packet, context) }
            is OpenBranchEditorPacket -> context.queue { processOpenBranchEditor(packet) }
            is SyncBranchListPacket -> context.queue { processSyncBranchList(packet) }
            is UpdateActorDataScreenPacket -> context.queue { processUpdateActorDataScreen(packet) }
            is UpdateEditingBranchPacket -> context.queue { processUpdateEditingBranch(packet) }
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
        ctx.player.level.getEntity(packet.entityId)?.let {
            if (it is LivingEntity) {
                val screen = Minecraft.getInstance().screen
                TalkativeClient.editingActorData = packet.actorData

                if (screen !is MainEditorScreen)
                    Minecraft.getInstance().setScreen(MainEditorScreen(it))
            }
        }
    }

    private fun processOpenBranchEditor(packet: OpenBranchEditorPacket) {
        TalkativeClient.editingBranch = packet.branch
        TalkativeClient.editingBranchPath = packet.path
        Minecraft.getInstance().setScreen(BranchNodeEditorScreen(Minecraft.getInstance().screen))
    }

    private fun processSyncBranchList(packet: SyncBranchListPacket) {
        val list = packet.tag?.getList("branchList", Tag.TAG_STRING.toInt())
        val screen = Minecraft.getInstance().screen
        if (screen is BranchDirectoryScreen)
            screen.loadBranchList(list)
    }

    private fun processUpdateActorDataScreen(packet: UpdateActorDataScreenPacket) {
        TalkativeClient.editingActorData = packet.actorData
    }

    private fun processUpdateEditingBranch(packet: UpdateEditingBranchPacket) {
        TalkativeClient.editingBranchPath = packet.branchPath
        TalkativeClient.editingBranch = packet.branch
    }
}