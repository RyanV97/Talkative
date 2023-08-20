package dev.cryptcraft.talkative.api.tree.node

import dev.cryptcraft.talkative.api.conditional.Conditional
import dev.cryptcraft.talkative.common.util.NBTConstants
import net.minecraft.nbt.CompoundTag

class BridgeNode(nodeId:Int, var destinationNodeId: Int, var destinationBranchPath: String, conditional: Conditional? = null) : NodeBase(nodeId, conditional) {
    override fun getNodeType(): NodeType {
        return NodeType.Bridge
    }

    override fun deserialize(tag: CompoundTag): NodeBase {
        destinationNodeId = tag.getInt(NBTConstants.NODE_BRIDGE_DESTINATION_ID)
        destinationBranchPath = tag.getString(NBTConstants.NODE_BRIDGE_DESTINATION_BRANCH)
        return this
    }
}