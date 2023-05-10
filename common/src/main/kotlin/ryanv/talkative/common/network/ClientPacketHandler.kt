package ryanv.talkative.common.network

import me.shedaniel.architectury.networking.NetworkManager
import me.shedaniel.architectury.utils.NbtType
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.LivingEntity
import ryanv.talkative.client.TalkativeClient
import ryanv.talkative.client.gui.TalkativeScreen
import ryanv.talkative.client.gui.dialog.DialogScreen
import ryanv.talkative.client.gui.editor.ActorEditorScreen
import ryanv.talkative.client.gui.editor.BranchDirectoryScreen
import ryanv.talkative.client.gui.editor.BranchEditorScreen
import ryanv.talkative.common.data.ActorData
import ryanv.talkative.common.data.tree.DialogBranch
import ryanv.talkative.common.network.NetworkHandler.TalkativePacket
import ryanv.talkative.common.network.clientbound.*
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
            if (it is LivingEntity && packet.tag != null) {
                TalkativeClient.editingActorData = ActorData.deserialize(packet.tag)
                Minecraft.getInstance().setScreen(ActorEditorScreen(it))
            }
        }
    }

    private fun processOpenBranchEditor(packet: OpenBranchEditorPacket) {
        if (packet.data != null) {
            val branch = DialogBranch.deserialize(packet.data)
            if (branch != null) {
                val screen = Minecraft.getInstance().screen
                var parent: TalkativeScreen? = null
                if (screen is ActorEditorScreen)
                    parent = screen
                Minecraft.getInstance().setScreen(BranchEditorScreen(parent, packet.path, branch))
            }
        }
    }

    private fun processSyncBranchList(packet: SyncBranchListPacket) {
        val list = packet.tag?.getList("branchList", NbtType.STRING)
        val screen = Minecraft.getInstance().screen
        if (screen is BranchDirectoryScreen)
            screen.loadBranchList(list)
    }

    private fun processUpdateActorDataScreen(packet: UpdateActorDataScreenPacket) {
        TalkativeClient.editingActorData = packet.actorData
    }
}