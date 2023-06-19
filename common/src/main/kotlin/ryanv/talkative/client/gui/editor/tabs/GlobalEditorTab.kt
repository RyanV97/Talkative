package ryanv.talkative.client.gui.editor.tabs

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import ryanv.talkative.client.gui.editor.branch.BranchDirectoryScreen
import ryanv.talkative.client.gui.editor.MainEditorScreen
import ryanv.talkative.client.gui.editor.branch.EditBranchesScreen

class GlobalEditorTab(x: Int, y: Int, width: Int, height: Int, parentScreen: MainEditorScreen) : EditorTab(x, y, width, height, parentScreen, Component.literal("Global Settings")) {
    val editBranchesButton: Button = addChild(Button(x + 10, y + 10, 100, 20, Component.literal("Edit Branches"), ::openBranchScreen))


    init {
        populateBranchList()
    }

    override fun refresh() {

    }

    private fun populateBranchList() {

    }

    private fun openBranchScreen(button: Button) {
        Minecraft.getInstance().setScreen(EditBranchesScreen(parentScreen))
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}
}
