package ryanv.talkative.common.data.tree

import com.google.common.collect.Lists
import net.minecraft.nbt.*
import ryanv.talkative.api.ConditionalHolder
import ryanv.talkative.common.data.conditional.Conditional
import ryanv.talkative.common.util.NBTConstants

class DialogNode(val nodeId: Int, var nodeType: NodeType = NodeType.Dialog, var content: String = "Hello World", private var conditional: Conditional? = null) : ConditionalHolder {
    private var childType: NodeType? = null
    private var children: ArrayList<Int> = ArrayList()
    var commands: ArrayList<String>? = null

    fun addChild(child: Int, type: NodeType) {
        if (childType == null)
            childType = type
        if (type == childType)
            children.add(child)
    }

    fun getChildren(): ArrayList<Int> {
        return children
    }

    fun getChildType(): NodeType? {
        return childType
    }

    fun getResponseIDs(): List<Int>? {
        if (childType != NodeType.Response)
            return null

        val list = ArrayList<Int>()
        children.forEach {
            //ToDo: Conditional Check
            list.add(it)
        }

        return list
    }

    override fun getConditional(): Conditional? {
        return conditional
    }

    override fun setConditional(newConditional: Conditional?) {
        conditional = newConditional
    }

    fun clone(): DialogNode {
        val clone = DialogNode(nodeId, nodeType, content, conditional)
        clone.childType = childType
        clone.children = children.clone() as ArrayList<Int>
        return clone
    }

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.putInt(NBTConstants.NODE_ID, nodeId)
        tag.putString(NBTConstants.NODE_TYPE, nodeType.name)
        tag.putString(NBTConstants.NODE_CONTENT, content)

        if (conditional != null)
            tag.put(NBTConstants.CONDITIONAL, conditional!!.serialize(CompoundTag()))

        if (children.isNotEmpty())
            tag.put(NBTConstants.NODE_CHILDREN, IntArrayTag(children))
        if (childType != null)
            tag.putString(NBTConstants.NODE_CHILD_TYPE, childType.toString())

        if (commands != null) {
            val commandsList = ListTag()
            commands!!.forEach { commandsList.add(StringTag.valueOf(it)) }
            tag.put(NBTConstants.NODE_COMMANDS, commandsList)
        }

        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): DialogNode? {
            if (tag.isEmpty) return null

            val node = DialogNode(tag.getInt(NBTConstants.NODE_ID), NodeType.valueOf(tag.getString(NBTConstants.NODE_TYPE)), tag.getString(NBTConstants.NODE_CONTENT))

            if (tag.contains(NBTConstants.CONDITIONAL))
                node.conditional = Conditional.deserialize(tag.getCompound(NBTConstants.CONDITIONAL))

            if (tag.contains(NBTConstants.NODE_CHILDREN))
                node.children = tag.getIntArray(NBTConstants.NODE_CHILDREN).toCollection(ArrayList())
            if (tag.contains(NBTConstants.NODE_CHILD_TYPE))
                node.childType = NodeType.valueOf(tag.getString(NBTConstants.NODE_CHILD_TYPE))

            if (tag.contains(NBTConstants.NODE_COMMANDS)) {
                node.commands = Lists.newArrayList()
                tag.getList(NBTConstants.NODE_COMMANDS, Tag.TAG_STRING.toInt()).forEach { node.commands!!.add(it.asString) }
            }

            return node
        }
    }

    enum class NodeType { Dialog, Response }
}