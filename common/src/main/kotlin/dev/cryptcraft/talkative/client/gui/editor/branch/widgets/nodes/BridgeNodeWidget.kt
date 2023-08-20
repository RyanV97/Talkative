package dev.cryptcraft.talkative.client.gui.editor.branch.widgets.nodes

import dev.cryptcraft.talkative.api.tree.node.NodeBase
import dev.cryptcraft.talkative.client.gui.editor.branch.BranchNodeEditorScreen

class BridgeNodeWidget(x: Int, y: Int, node: NodeBase, parentWidget: NodeWidget?, parentScreen: BranchNodeEditorScreen) : NodeWidget(x, y, 40, node, parentWidget, parentScreen) {
    override fun getBackgroundColour(): Int {
        return 0xFFCC0000.toInt()
    }
}