package dev.cryptcraft.talkative.client.gui.editor.tabs

import dev.cryptcraft.talkative.client.gui.editor.MainEditorScreen
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchSelectionScreen
import dev.cryptcraft.talkative.client.gui.editor.widgets.DisplayDataWidget
import dev.cryptcraft.talkative.client.gui.widgets.TalkativeButton
import dev.cryptcraft.talkative.server.TalkativeWorldConfig
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class GlobalTab(x: Int, y: Int, width: Int, height: Int, parentScreen: MainEditorScreen) : EditorTab(x, y, width, height, parentScreen, Component.literal("Global Settings")) {
    private val editBranchesButton = addChild(TalkativeButton(x + 10, y + 10, 100, 20, Component.literal("Edit Branches")) {
        Minecraft.getInstance().setScreen(BranchSelectionScreen(parentScreen, BranchSelectionScreen.ListMode.EDIT))
    })
    private val playerDisplayData = addChild(DisplayDataWidget(x + 10, y + 10, null))

    init {
        recalculateChildren()
    }

    override fun recalculateChildren() {
        val half = width / 2
        editBranchesButton.x = x + half + (half / 2) - 50
        editBranchesButton.y = y + 10

        playerDisplayData.x = x + 5
        playerDisplayData.y = y + 20
    }

    override fun onClose() {
    }

    override fun refresh() {
        playerDisplayData.refresh(TalkativeWorldConfig.INSTANCE!!.playerDisplay)
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}
}
