package dev.cryptcraft.talkative.client.gui.editor.tabs

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.client.gui.editor.MainEditorScreen
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchSelectionScreen
import dev.cryptcraft.talkative.client.gui.editor.widgets.ActorBranchList
import dev.cryptcraft.talkative.client.gui.editor.widgets.DisplayDataWidget
import dev.cryptcraft.talkative.client.gui.widgets.TalkativeButton
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class ActorTab(x: Int, y: Int, width: Int, height: Int, val parent: MainEditorScreen) : EditorTab(x, y, width, height, parent, Component.literal("General Actor Settings")) {
    private val displayData = addChild(DisplayDataWidget(0, 0, TalkativeClient.editingActorData?.displayData))
    private val branchList = addChild(ActorBranchList(this, 0, 0, width / 2, height - 40, Component.literal("Attached Branches List")))
    private val attachButton = addChild(TalkativeButton(0, 0, 15, 15, Component.literal("+"), { openAttachBranchScreen() }))

    init {
        recalculateChildren()
        refresh()
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        fill(poseStack, branchList.x - 5, branchList.y - 20, branchList.x + branchList.width + 5, branchList.y + branchList.height, 0x600F0F0F) //Background

        val font = Minecraft.getInstance().font
        GuiComponent.drawString(poseStack, font, Component.literal("Attached Branches").withStyle { it.withBold(true).withUnderlined(true) }, x + (width / 2) - 5, y + 20, 0xFFFFFF)

        super.renderButton(poseStack, mouseX, mouseY, partialTicks)
    }

    override fun onClose() {}

    override fun refresh() {
        displayData.refresh(TalkativeClient.editingActorData?.displayData!!)

        this.branchList.clear()
        var index = 0
        TalkativeClient.editingActorData?.dialogBranches?.forEach {
            this.branchList.addEntry(index++, it)
        }
    }

    override fun recalculateChildren() {
        displayData.x = x + 5
        displayData.y = y + 15
        displayData.recalculateChildren()

        //List - Attached Branch List
        branchList.x = width / 2
        branchList.y = y + 35
        branchList.width = (width / 2) - 5
        branchList.height = height - 35
        branchList.recalculateChildren()
        branchList.renderBackground = false

        //Button - Attach Branch Button
        attachButton.x = x + width - 26
        attachButton.y = y + 17

        super.recalculateChildren()
    }

    override fun tick() {
        branchList.tick()
    }

    private fun openAttachBranchScreen() {
        Minecraft.getInstance().setScreen(BranchSelectionScreen(parent, BranchSelectionScreen.ListMode.ATTACH))
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}
}