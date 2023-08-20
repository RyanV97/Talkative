package dev.cryptcraft.talkative.client.gui.editor.branch.widgets.nodes

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.api.tree.node.NodeBase
import dev.cryptcraft.talkative.api.tree.node.TextNode
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchNodeEditorScreen
import dev.cryptcraft.talkative.mixin.client.AbstractWidgetAccessor
import net.minecraft.network.chat.Component

class TextNodeWidget(x: Int, y: Int, node: NodeBase, parentWidget: NodeWidget?, parentScreen: BranchNodeEditorScreen) : NodeWidget(x, y, if (node.getNodeType() == NodeBase.NodeType.Dialog) 75 else 40, node, parentWidget, parentScreen) {
    val editBox = addChild(NodeEditBox(this, x, y + 10, width, height - 10))

    init {
        editBox.value = (node as TextNode).getContents().string
        editBox.setValueListener {
            node.setContents(Component.literal(it))
        }
    }

    override fun renderNode(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.renderNode(poseStack, mouseX, mouseY, delta)

        editBox.x = x + parentScreen.offsetX
        editBox.y = y + 10 + parentScreen.offsetY
        editBox.render(poseStack, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val b = super.mouseClicked(mouseX, mouseY, button)
        if (b && button == 0)
            (editBox as AbstractWidgetAccessor).pleaseSetFocused(true)
        else
            (editBox as AbstractWidgetAccessor).pleaseSetFocused(false)
        return b
    }

    override fun getBackgroundColour(): Int {
        return when(node.getNodeType()) {
            NodeBase.NodeType.Dialog -> 0xFF116611.toInt()
            NodeBase.NodeType.Response -> 0xFF111166.toInt()
            else -> 0xFFFFFFFF.toInt()
        }
    }
}