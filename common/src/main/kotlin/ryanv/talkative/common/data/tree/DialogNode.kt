package ryanv.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import ryanv.talkative.common.data.conditional.Conditional
import ryanv.talkative.common.consts.NBTConstants

class DialogNode(var nodeType: NodeType, var content: String = "", var conditional: Conditional? = null, val children: ArrayList<DialogNode> = ArrayList(), val nodeId: Int) {

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.putInt(NBTConstants.NODE_ID, nodeId)
        tag.putString(NBTConstants.NODE_TYPE, nodeType.name)
        tag.putString(NBTConstants.NODE_CONTENT, content)

        if(conditional != null)
            tag.put(NBTConstants.CONDITIONAL, conditional!!.serialize(CompoundTag()))

        if(children.isNotEmpty()) {
            val childrenTag = ListTag()
            for(child in children)
                childrenTag.add(child.serialize(CompoundTag()))
            tag.put(NBTConstants.NODE_CHILDREN, childrenTag)
        }

        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): DialogNode {
            val node = DialogNode(nodeType = NodeType.valueOf(tag.getString(NBTConstants.NODE_TYPE)), nodeId = tag.getInt(NBTConstants.NODE_ID))

            node.content = tag.getString(NBTConstants.NODE_CONTENT)

            if(tag.contains(NBTConstants.CONDITIONAL))
                node.conditional = Conditional.deserialize(tag.getCompound(NBTConstants.CONDITIONAL))

            if(tag.contains(NBTConstants.NODE_CHILDREN)) {
                val listChildren: ListTag = tag.getList(NBTConstants.NODE_CHILDREN, 10)
                for(tagChild: Tag in listChildren)
                    node.children.add(deserialize(tagChild as CompoundTag))
            }

            return node
        }
    }

    enum class NodeType { Dialog, Response }

}