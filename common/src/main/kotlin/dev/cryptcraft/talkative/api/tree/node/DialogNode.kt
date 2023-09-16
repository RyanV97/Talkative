package dev.cryptcraft.talkative.api.tree.node

import dev.cryptcraft.talkative.api.conditional.Conditional
import net.minecraft.network.chat.Component

class DialogNode(nodeId: Int, private var contents: List<Component>, conditional: Conditional? = null) : TextNode(nodeId, conditional) {
    override fun getNodeType(): NodeType {
        return NodeType.Dialog
    }

    override fun setContents(contents: List<Component>) {
        this.contents = contents
    }

    override fun getContents(): List<Component> {
        return contents
    }
}