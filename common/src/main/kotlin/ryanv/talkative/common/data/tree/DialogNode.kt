package ryanv.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntArrayTag
import ryanv.talkative.common.consts.NBTConstants
import ryanv.talkative.common.data.conditional.Conditional

class DialogNode(var nodeType: NodeType = NodeType.Dialog, var content: String = "Hello World", var conditional: Conditional? = null, val nodeId: Int) {
    private var children: ArrayList<Int> = ArrayList()
    private var childType: NodeType? = null

    fun addChild(child: Int, type: NodeType) {
        if(childType == null)
            childType = type
        if(type == childType)
            children.add(child)
    }

    fun setChildren(children: ArrayList<Int>, type: NodeType) {
        this.children = children
        childType = type
    }

    fun getChildren(): ArrayList<Int> {
        return children
    }

    fun getChildType(): NodeType? {
        return childType
    }

    fun getResponses(branch: DialogBranch): Array<String>? {
        if(childType != NodeType.Response)
            return null
        var list: Array<String> = emptyArray()
        children.forEach {
            //ToDo: Conditional Check
            list = list.plus(branch.nodes[it]!!.content)
        }
        return list
    }

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.putInt(NBTConstants.NODE_ID, nodeId)
        tag.putString(NBTConstants.NODE_TYPE, nodeType.name)
        tag.putString(NBTConstants.NODE_CONTENT, content)

        if(conditional != null)
            tag.put(NBTConstants.CONDITIONAL, conditional!!.serialize(CompoundTag()))
        if(children.isNotEmpty())
            tag.put(NBTConstants.NODE_CHILDREN, IntArrayTag(children))
        if(childType != null)
            tag.putString(NBTConstants.NODE_CHILD_TYPE, childType.toString())

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
            if(tag.contains(NBTConstants.NODE_CHILD_TYPE))
                node.childType = NodeType.valueOf(tag.getString(NBTConstants.NODE_CHILD_TYPE))

            return node
        }
    }

    enum class NodeType { Dialog, Response }

}