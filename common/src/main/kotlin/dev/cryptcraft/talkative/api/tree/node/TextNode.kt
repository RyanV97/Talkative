package dev.cryptcraft.talkative.api.tree.node

import dev.cryptcraft.talkative.api.conditional.Conditional
import net.minecraft.network.chat.Component

abstract class TextNode(nodeId: Int, conditional: Conditional?) : NodeBase(nodeId, conditional) {
    abstract fun setContents(contents: Component)
    abstract fun getContents(): Component
}