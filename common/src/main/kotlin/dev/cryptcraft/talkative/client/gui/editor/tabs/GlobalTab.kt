package dev.cryptcraft.talkative.client.gui.editor.tabs

import dev.cryptcraft.talkative.client.gui.editor.MainEditorScreen
import dev.cryptcraft.talkative.client.gui.editor.branch.EditBranchesScreen
import dev.cryptcraft.talkative.client.gui.widgets.TalkativeButton
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class GlobalTab(x: Int, y: Int, width: Int, height: Int, parentScreen: MainEditorScreen) : EditorTab(x, y, width, height, parentScreen, Component.literal("Global Settings")) {
    val editBranchesButton: TalkativeButton = addChild(TalkativeButton(x + 10, y + 10, 100, 20, Component.literal("Edit Branches"), { openBranchScreen() }))

    override fun refresh() {}

    private fun openBranchScreen() {
        Minecraft.getInstance().setScreen(EditBranchesScreen(parentScreen))
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}
}
