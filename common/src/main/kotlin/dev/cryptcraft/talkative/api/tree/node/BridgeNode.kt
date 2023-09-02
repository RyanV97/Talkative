package dev.cryptcraft.talkative.api.tree.node

import dev.cryptcraft.talkative.api.conditional.Conditional
import dev.cryptcraft.talkative.common.util.NBTConstants
import net.minecraft.nbt.CompoundTag

class BridgeNode(nodeId:Int, var destinationBranchPath: String, var destinationNodeId: Int, conditional: Conditional? = null) : NodeBase(nodeId, conditional) {
    override fun getNodeType(): NodeType {
        return NodeType.Bridge
    }

    override fun serialize(tag: CompoundTag): CompoundTag {
        tag.putString(NBTConstants.NODE_BRIDGE_DESTINATION_BRANCH, destinationBranchPath)
        tag.putInt(NBTConstants.NODE_BRIDGE_DESTINATION_ID, destinationNodeId)
        return super.serialize(tag)
    }

    override fun deserialize(tag: CompoundTag): NodeBase {
        destinationBranchPath = tag.getString(NBTConstants.NODE_BRIDGE_DESTINATION_BRANCH)
        destinationNodeId = tag.getInt(NBTConstants.NODE_BRIDGE_DESTINATION_ID)
        return this
    }
}