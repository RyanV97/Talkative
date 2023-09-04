package dev.cryptcraft.talkative.client.gui.editor.branch.widgets.nodes

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.api.tree.node.BridgeNode
import dev.cryptcraft.talkative.client.gui.GuiConstants
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchNodeEditorScreen
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchSelectionScreen
import dev.cryptcraft.talkative.client.gui.widgets.popup.PopupWidget
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component

class BridgeNodeWidget(x: Int, y: Int, node: BridgeNode, parentWidget: NodeWidget?, parentScreen: BranchNodeEditorScreen) : NodeWidget(x, y, 40, node, parentWidget, parentScreen) {
    override fun renderNode(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.renderNode(poseStack, mouseX, mouseY, delta)
        val posX = this.x + parentScreen.offsetX
        val posY = this.y + parentScreen.offsetY
        GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "Branch: ${(node as BridgeNode).destinationBranchPath}", posX + 2, posY + 15, 0xFFFFFFFF.toInt())
        GuiComponent.drawString(poseStack, Minecraft.getInstance().font, "Node ID: ${node.destinationNodeId}", posX + 2, posY + 27, 0xFFFFFFFF.toInt())
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val result = super.mouseClicked(mouseX, mouseY, button)
        if (result && button == 0)
            parentScreen.openPopup(createPopup())
        return result
    }

    private fun createPopup(): PopupWidget {
        val popupWidth = 225
        val popupHeight = 50

        val popupX = (parentScreen.width / 2) - (popupWidth / 2)
        val popupY = (parentScreen.height / 2) - (popupHeight / 2)

        return BridgePopup(popupX, popupY, popupWidth, popupHeight, parentScreen, this)
    }

    fun setDestinationBranch(branchPath: String) {
        (node as BridgeNode).destinationBranchPath = branchPath
    }

    override fun getBackgroundColour(): Int {
        return 0xFF913F3F.toInt()
    }

    class BridgePopup(x: Int, y: Int, width: Int, height: Int, parentScreen: BranchNodeEditorScreen, val parentWidget: BridgeNodeWidget): PopupWidget(x, y, width, height, parentScreen) {
        private val branchLinkButton = iconButton(width - 25, 6, 20, 20, GuiConstants.EDIT_ICON, ::openBranchSelection)
        val branchDestinationLabel = label(5, 16, "...")
        private val nodeIdEntry = textField(width - 46, 31, 40, 14, (parentWidget.node as BridgeNode).destinationNodeId.toString())

        init {
            label(5, 6, Component.literal("Destination Branch:").withStyle(ChatFormatting.GRAY))
            label(5, 35, Component.literal("Destination Node ID:").withStyle(ChatFormatting.GRAY))

            val destination = (parentWidget.node as BridgeNode).destinationBranchPath
            if (destination.isNotBlank())
                branchDestinationLabel.contents = Component.literal(destination)

            nodeIdEntry.setFilter { return@setFilter it.isEmpty() || it.toIntOrNull() != null }
            nodeIdEntry.setResponder {
                if (it.isNullOrEmpty()) return@setResponder
                (parentWidget.node as BridgeNode).destinationNodeId = it.toInt()
            }
        }

        private fun openBranchSelection(button: Button) {
            (parent as BranchNodeEditorScreen).selectedNode = parentWidget
            Minecraft.getInstance().setScreen(BranchSelectionScreen(parent, BranchSelectionScreen.ListMode.BRIDGE_LINK))
        }

        override fun onClose() {
            parentWidget.parentScreen.selectedNode = null
        }
    }
}