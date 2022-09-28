package ryanv.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntArrayTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import ryanv.talkative.common.data.conditional.Conditional
import ryanv.talkative.common.consts.NBTConstants

class DialogNode(var nodeType: NodeType = NodeType.Dialog, var content: String = "Hello World", var conditional: Conditional? = null, var children: ArrayList<Int> = ArrayList(), val nodeId: Int) {

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.putInt(NBTConstants.NODE_ID, nodeId)
        tag.putString(NBTConstants.NODE_TYPE, nodeType.name)
        tag.putString(NBTConstants.NODE_CONTENT, content)

        if(conditional != null)
            tag.put(NBTConstants.CONDITIONAL, conditional!!.serialize(CompoundTag()))

        if(children.isNotEmpty())
            tag.put(NBTConstants.NODE_CHILDREN, IntArrayTag(children))

        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): DialogNode {
            val node = DialogNode(nodeType = NodeType.valueOf(tag.getString(NBTConstants.NODE_TYPE)), nodeId = tag.getInt(NBTConstants.NODE_ID))
            node.content = tag.getString(NBTConstants.NODE_CONTENT)
            if(tag.contains(NBTConstants.CONDITIONAL))
                node.conditional = Conditional.deserialize(tag.getCompound(NBTConstants.CONDITIONAL))
            if(tag.contains(NBTConstants.NODE_CHILDREN))
                node.children = tag.getIntArray(NBTConstants.NODE_CHILDREN).toCollection(ArrayList())
            return node
        }
    }

    enum class NodeType { Dialog, Response }

}