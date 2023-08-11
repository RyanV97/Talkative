package dev.cryptcraft.talkative.client

import dev.cryptcraft.talkative.client.gui.DataScreen
import dev.cryptcraft.talkative.client.gui.editor.MainEditorScreen
import dev.cryptcraft.talkative.api.actor.ActorData
import dev.cryptcraft.talkative.api.tree.DialogBranch
import dev.cryptcraft.talkative.client.gui.dialog.DialogScreen
import dev.cryptcraft.talkative.common.network.clientbound.OpenActorEditorPacket
import it.unimi.dsi.fastutil.ints.Int2ReferenceLinkedOpenHashMap
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.EntityHitResult

object TalkativeClient {
    var editingActorData: ActorData? = null
        set(value) {
            field = value
            val screen = Minecraft.getInstance().screen
            if (screen is DataScreen) screen.refresh()
        }

    var editingBranch: DialogBranch? = null
        set(value) {
            field = value
            val screen = Minecraft.getInstance().screen
            if (screen is DataScreen) screen.refresh()
        }

    var editingBranchPath: String? = null

    fun onReceiveDialog(dialogLine: Component?, responses: Int2ReferenceLinkedOpenHashMap<Component>?, isExitNode: Boolean) {
        //ToDo Maybe change this into a
        var currentScreen = Minecraft.getInstance().screen

        if (currentScreen == null) {
            currentScreen = DialogScreen()
            Minecraft.getInstance().setScreen(currentScreen)
        }

        if (currentScreen is DialogScreen)
            currentScreen.loadDialog(dialogLine!!, responses, isExitNode)
    }

    fun openEditor() {
        val hitResult = Minecraft.getInstance().hitResult
        if (hitResult is EntityHitResult)
            OpenActorEditorPacket(hitResult.entity.id).sendToServer()
        else
            openEditorScreen(null)
    }

    fun openEditorScreen(actorEntity: LivingEntity?) {
        Minecraft.getInstance().setScreen(MainEditorScreen(actorEntity))
    }
}