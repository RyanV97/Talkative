package dev.cryptcraft.talkative.client.gui.editor.tabs

import dev.cryptcraft.talkative.client.gui.editor.MainEditorScreen
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchSelectionScreen
import dev.cryptcraft.talkative.client.gui.widgets.TalkativeButton
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class GlobalTab(x: Int, y: Int, width: Int, height: Int, parentScreen: MainEditorScreen) : EditorTab(x, y, width, height, parentScreen, Component.literal("Global Settings")) {
    private val editBranchesButton: TalkativeButton = addChild(TalkativeButton(x + 10, y + 10, 100, 20, Component.literal("Edit Branches")) {
        Minecraft.getInstance().setScreen(BranchSelectionScreen(parentScreen, BranchSelectionScreen.ListMode.EDIT))
    })

    override fun recalculateChildren() {
        editBranchesButton.x = x + 10
        editBranchesButton.y = y + 10
    }

    override fun onClose() {
    }

    override fun refresh() {}

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}
}
