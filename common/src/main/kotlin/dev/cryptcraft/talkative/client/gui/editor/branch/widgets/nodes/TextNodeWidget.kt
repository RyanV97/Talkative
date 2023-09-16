package dev.cryptcraft.talkative.client.gui.editor.branch.widgets.nodes

import com.mojang.blaze3d.vertex.PoseStack
import dev.cryptcraft.talkative.api.tree.node.NodeBase
import dev.cryptcraft.talkative.api.tree.node.TextNode
import dev.cryptcraft.talkative.client.gui.editor.branch.NodeEditorScreen
import dev.cryptcraft.talkative.common.markdown.MarkdownParser
import dev.cryptcraft.talkative.mixin.client.AbstractWidgetAccessor
import net.minecraft.network.chat.Component

class TextNodeWidget(x: Int, y: Int, node: TextNode, parentWidget: NodeWidget?, parentScreen: NodeEditorScreen) : NodeWidget(x, y, if (node.getNodeType() == NodeBase.NodeType.Dialog) 75 else 40, node, parentWidget, parentScreen) {
    val editBox = addChild(NodeEditBox(this, x, y + 10, width, height - 10))

    init {
        editBox.setValueListener {
            val list = ArrayList<Component>()
            for (line in it.split("\n", ignoreCase = true))
                list.add(Component.literal(line))
            node.setContents(list)
        }
        editBox.value = MarkdownParser.componentsToMarkdown(node.getContents())
    }

    override fun renderNode(poseStack: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.renderNode(poseStack, mouseX, mouseY, delta)

        editBox.x = x + parentScreen.offsetX
        editBox.y = y + 12 + parentScreen.offsetY
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
            NodeBase.NodeType.Dialog -> 0xFF419634.toInt()
            NodeBase.NodeType.Response -> 0xFF005682.toInt()
            else -> 0xFFFFFFFF.toInt()
        }
    }
}