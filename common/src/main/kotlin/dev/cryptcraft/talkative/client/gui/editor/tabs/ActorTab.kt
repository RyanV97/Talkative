package dev.cryptcraft.talkative.client.gui.editor.tabs

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.api.ActorEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Checkbox
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import dev.cryptcraft.talkative.client.TalkativeClient
import dev.cryptcraft.talkative.client.gui.editor.MainEditorScreen
import dev.cryptcraft.talkative.client.gui.editor.branch.AttachBranchScreen
import dev.cryptcraft.talkative.client.gui.editor.widgets.ActorBranchList
import dev.cryptcraft.talkative.client.gui.editor.widgets.CallbackCheckbox
import dev.cryptcraft.talkative.client.gui.widgets.TalkativeButton
import dev.cryptcraft.talkative.common.network.serverbound.AttachBranchPacket

class ActorTab(x: Int, y: Int, width: Int, height: Int, val parent: MainEditorScreen) :
    EditorTab(x, y, width, height, parent, Component.literal("General Actor Settings")) {
    private val overrideDisplayName: Checkbox = addChild(CallbackCheckbox(0,0, 20, 20, Component.literal("Override Entity Name"), TalkativeClient.editingActorData?.displayData?.overrideDisplayName ?: false, ::changeDisplayOverride))
    private val actorDisplayName: EditBox = addChild(EditBox(Minecraft.getInstance().font, 0,0, 150, 20, Component.literal("Actor Display Name")))
    private val branchList: ActorBranchList = addChild(ActorBranchList(this, 0, 0, width / 2, height - 40, Component.literal("Attached Branches List")))
    private val attachButton: TalkativeButton = addChild(TalkativeButton(0, 0, 15, 15, Component.literal("+"), { openAttachBranchScreen() }))

    private var horizontalMid: Int = 0

    init {
        recalculateChildren()
        refresh()

        actorDisplayName.setResponder {
            (parent.actorEntity as ActorEntity).getActorData()?.displayData?.displayName = it
        }
        actorDisplayName.setSuggestion(parent.actorEntity!!.displayName.string)
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        GuiComponent.drawString(poseStack, Minecraft.getInstance().font, Component.literal("Attached Branches").withStyle { it.withBold(true).withUnderlined(true) }, x + (width / 2) + 2, y + 20, 0xFFFFFF)
        this.actorDisplayName.setEditable(this.overrideDisplayName.selected())
        super.renderButton(poseStack, mouseX, mouseY, partialTicks)
    }

    override fun refresh() {
        this.branchList.clear()
        var index = 0
        TalkativeClient.editingActorData?.dialogBranches?.forEach {
            this.branchList.addEntry(index++, it)
        }
    }

    override fun recalculateChildren() {
        this.horizontalMid = x + (width / 2)

        //Checkbox - Override Display Name
        this.overrideDisplayName.x = x + 9
        this.overrideDisplayName.y = y + 15

        //Checkbox - Name to Override Display Name with
        this.actorDisplayName.x = x + 10
        this.actorDisplayName.y = y + 40

        //List - Attached Branch List
        this.branchList.x = this.horizontalMid
        this.branchList.y = y + 35
        this.branchList.width = width / 2
        this.branchList.height = height - 35
        this.branchList.recalculateChildren()
        this.branchList.renderBackground = false

        //Button - Attach Branch Button
        this.attachButton.x = x + width - 18
        this.attachButton.y = y + 17

        super.recalculateChildren()
    }

    //ToDo: Finish implementing Name Override
    private fun changeDisplayOverride() {
        (parent.actorEntity as ActorEntity).getActorData()?.displayData?.overrideDisplayName = overrideDisplayName.selected()
    }

    private fun openAttachBranchScreen() {
        Minecraft.getInstance().setScreen(AttachBranchScreen(parent) {
            if (it!!.value.isNotBlank())
                addBranchToActor(it.value)
        })
    }

    private fun addBranchToActor(path: String) {
        AttachBranchPacket(parentScreen.actorEntity!!.id, path).sendToServer()
    }

    override fun updateNarration(narrationElementOutput: NarrationElementOutput) {
    }
}