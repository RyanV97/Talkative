package dev.cryptcraft.talkative.client

import dev.cryptcraft.talkative.api.actor.ActorData
import dev.cryptcraft.talkative.api.actor.markers.Marker
import dev.cryptcraft.talkative.api.actor.markers.MarkerEntity
import dev.cryptcraft.talkative.api.tree.DialogBranch
import dev.cryptcraft.talkative.client.gui.EditorScreen
import dev.cryptcraft.talkative.client.gui.dialog.DialogScreen
import dev.cryptcraft.talkative.client.gui.editor.MainEditorScreen
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchNodeEditorScreen
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchSelectionScreen
import dev.cryptcraft.talkative.common.network.clientbound.DialogPacket
import net.minecraft.client.Minecraft
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity

object TalkativeClient {
    lateinit var minecraft: Minecraft
    var editingActorEntity: LivingEntity? = null
        set(value) {
            field = value
            refreshEditorScreen()
        }

    var editingActorData: ActorData? = null
        set(value) {
            field = value
            refreshEditorScreen()
        }

    var editingBranch: DialogBranch? = null
        set(value) {
            field = value
            refreshEditorScreen()
        }

    var editingBranchPath: String? = null

    fun init() {
        minecraft = Minecraft.getInstance()
    }

    private fun refreshEditorScreen() {
        val screen = minecraft.screen
        if (screen is EditorScreen) screen.refresh()
    }

    fun onReceiveDialog(dialogLine: Component, responses: ArrayList<DialogPacket.ResponseData>?, isExitNode: Boolean) {
        var currentScreen = minecraft.screen

        if (currentScreen !is DialogScreen) {
            currentScreen = DialogScreen()
            minecraft.setScreen(currentScreen)
        }

        currentScreen.receiveDialog(dialogLine, responses, isExitNode)
    }

    fun openEditorScreen(actorEntity: Entity?, actorData: ActorData?) {
        if (actorEntity != null && actorEntity !is LivingEntity) return

        editingActorEntity = actorEntity as LivingEntity?
        editingActorData = actorData

        if (minecraft.screen !is MainEditorScreen)
            minecraft.setScreen(MainEditorScreen())
    }

    fun syncBranchList(list: ListTag?) {
        val screen = minecraft.screen
        if (screen is BranchSelectionScreen)
            screen.loadBranchList(list)
    }

    fun openBranchEditor(branchPath: String, branch: DialogBranch) {
        editingBranch = branch
        editingBranchPath = branchPath
        minecraft.setScreen(BranchNodeEditorScreen(minecraft.screen))
    }

    fun syncMarker(entityId: Int, marker: Marker?) {
        (minecraft.level?.getEntity(entityId) as MarkerEntity).setMarker(marker)
    }
}