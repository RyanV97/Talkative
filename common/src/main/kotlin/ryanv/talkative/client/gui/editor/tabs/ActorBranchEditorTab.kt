package ryanv.talkative.client.gui.editor.tabs

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.TextComponent
import ryanv.talkative.client.TalkativeClient
import ryanv.talkative.client.gui.editor.ActorEditorScreen
import ryanv.talkative.client.gui.editor.BranchDirectoryScreen
import ryanv.talkative.client.gui.editor.ConditionalEditorScreen
import ryanv.talkative.client.gui.widgets.NestedWidget
import ryanv.talkative.client.gui.widgets.lists.WidgetList
import ryanv.talkative.client.util.ConditionalContext
import ryanv.talkative.common.data.ActorData
import ryanv.talkative.common.data.tree.BranchReference
import ryanv.talkative.common.network.serverbound.AttachBranchPacket
import ryanv.talkative.common.network.serverbound.RequestBranchForEditPacket
import ryanv.talkative.common.network.serverbound.UnAttachBranchPacket

class ActorBranchEditorTab(x: Int, y: Int, width: Int, height: Int, parent: ActorEditorScreen) :
    EditorTab(x, y, width, height, parent, TextComponent("Actor Branches")) {
    private var list: WidgetList<ActorEditorScreen> = addChild(WidgetList(parent, x, y, width, height - 30, TextComponent("Attached Branches List")))
    private var details: BranchDetailsWidget = addChild(BranchDetailsWidget(this, x + (width / 2), y, width / 2, height))

    init {
        list.renderBackground = false

        addChild(Button(5, y + height - 35, 90, 20, TextComponent("Attach Branch")) {
            Minecraft.getInstance().setScreen(BranchDirectoryScreen(parent) {
                if (it!!.value.isNotBlank())
                    addBranchToActor(it.value)
            })
        })

        populateBranchList()
    }

    override fun refresh() {
        details.setBranch(null, null)
        populateBranchList()
    }

    private fun populateBranchList() {
        list.width = width / 2
        list.clear()
        var index = 0
        TalkativeClient.editingActorData?.dialogBranches?.forEach {
            list.addChild(BranchListEntry(this, index++, it))
        }
        list.recalculateChildren()
    }

    private fun onSelectionChange(entry: BranchListEntry) {
        details.setBranch(entry.value, entry.index)
    }

    private fun addBranchToActor(path: String) {
        AttachBranchPacket(parentScreen.actorEntity.id, path).sendToServer()
    }

    class BranchListEntry(val parent: ActorBranchEditorTab, val index: Int, val value: BranchReference) : AbstractWidget(0, 0, 0, 12, TextComponent(value.fileString)) {
        override fun renderButton(poseStack: PoseStack?, mouseX: Int, mouseY: Int, partialTicks: Float) {
            GuiComponent.fill(poseStack, x, y, x + width, y + height, 0x55cccccc)
            val color: Int = if (isSelected()) 0x33d4ff else if (isMouseOver(mouseX.toDouble(), mouseY.toDouble())) 0xFFFFFF else 0xCCCCCC
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, value.fileString, x + 2, y + 2, color)
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            if (isValidClickButton(button) && clicked(mouseX, mouseY)) {
                parent.onSelectionChange(this)
                return true
            }
            return false
        }

        private fun isSelected(): Boolean {
            return parent.details.isBranchSelected(value)
        }
    }

    class BranchDetailsWidget(val parentTab: ActorBranchEditorTab, x: Int, y: Int, width: Int, height: Int) : NestedWidget(x, y, width, height, TextComponent("Branch Details")) {
        private var branchIndex: Int? = null
        private var branch: BranchReference? = null

        fun setBranch(branch: BranchReference?, index: Int?) {
            this.branch = branch
            this.branchIndex = index
            repopulateDetails()
        }

        fun isBranchSelected(branch: BranchReference): Boolean {
            return this.branch == branch
        }

        private fun repopulateDetails() {
            children.clear()
            if (branch != null && branchIndex != null) {
                addChild(Button(x, y, 70, 20, TextComponent("Edit Branch")) {
                    RequestBranchForEditPacket(branch!!.fileString).sendToServer()
                })

                addChild(Button(x, y + 20, 100, 20, TextComponent("Edit Conditional")) {
                    val context = ConditionalContext.BranchConditionalContext(parentTab.parentScreen.actorEntity.id, branchIndex!!, branch!!.getConditional())
                    Minecraft.getInstance().setScreen(ConditionalEditorScreen(parentTab.parentScreen, context))
                })

                addChild(Button(x, y + 40, 60, 20, TextComponent("Un-Attach")) {
                    UnAttachBranchPacket(parentTab.parentScreen.actorEntity.id, branchIndex!!).sendToServer()
                })
            }
        }
    }
}
