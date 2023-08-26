package dev.cryptcraft.talkative.client.gui.editor.tabs

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.client.gui.editor.MainEditorScreen
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchSelectionScreen
import dev.cryptcraft.talkative.client.gui.editor.widgets.ActorBranchList
import dev.cryptcraft.talkative.client.gui.editor.widgets.CallbackCheckbox
import dev.cryptcraft.talkative.client.gui.widgets.TalkativeButton
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.Checkbox
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class ActorTab(x: Int, y: Int, width: Int, height: Int, val parent: MainEditorScreen) : EditorTab(x, y, width, height, parent, Component.literal("General Actor Settings")) {
    private val overrideDisplayName: Checkbox = addChild(CallbackCheckbox(0,0, 20, 20, Component.literal("Override Entity Name"), TalkativeClient.editingActorData?.displayData?.overrideDisplayName ?: false, ::changeDisplayOverride))
    private val actorDisplayName: EditBox = addChild(EditBox(Minecraft.getInstance().font, 0,0, 150, 20, Component.literal("Actor Display Name")))
    private val branchList: ActorBranchList = addChild(ActorBranchList(this, 0, 0, width / 2, height - 40, Component.literal("Attached Branches List")))
    private val attachButton: TalkativeButton = addChild(TalkativeButton(0, 0, 15, 15, Component.literal("+"), { openAttachBranchScreen() }))

    init {
        recalculateChildren()
        refresh()

        actorDisplayName.setResponder {
            TalkativeClient.editingActorData?.displayData?.displayName = it
        }
        actorDisplayName.setSuggestion(TalkativeClient.editingActorEntity!!.displayName.string)
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        GuiComponent.drawString(poseStack, Minecraft.getInstance().font, Component.literal("Attached Branches").withStyle { it.withBold(true).withUnderlined(true) }, x + (width / 2) - 5, y + 20, 0xFFFFFF)
        this.actorDisplayName.setEditable(this.overrideDisplayName.selected())
        super.renderButton(poseStack, mouseX, mouseY, partialTicks)
    }

    override fun onClose() {}

    override fun refresh() {
        this.actorDisplayName.value = TalkativeClient.editingActorData?.displayData?.displayName.toString()
        this.branchList.clear()
        var index = 0
        TalkativeClient.editingActorData?.dialogBranches?.forEach {
            this.branchList.addEntry(index++, it)
        }
    }

    override fun recalculateChildren() {
        //Checkbox - Override Display Name
        this.overrideDisplayName.x = x + 9
        this.overrideDisplayName.y = y + 15

        //Checkbox - Name to Override Display Name with
        this.actorDisplayName.x = x + 10
        this.actorDisplayName.y = y + 40

        //List - Attached Branch List
        this.branchList.x = width / 2
        this.branchList.y = y + 35
        this.branchList.width = (width / 2) - 5
        this.branchList.height = height - 35
        this.branchList.recalculateChildren()
        this.branchList.renderBackground = false

        //Button - Attach Branch Button
        this.attachButton.x = x + width - 26
        this.attachButton.y = y + 17

        super.recalculateChildren()
    }

    override fun tick() {
        branchList.tick()
    }

    private fun changeDisplayOverride() {
        TalkativeClient.editingActorData?.displayData?.overrideDisplayName = overrideDisplayName.selected()
    }

    private fun openAttachBranchScreen() {
        Minecraft.getInstance().setScreen(BranchSelectionScreen(parent, BranchSelectionScreen.ListMode.ATTACH))
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}
}