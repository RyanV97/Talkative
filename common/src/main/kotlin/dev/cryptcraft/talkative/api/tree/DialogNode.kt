package dev.cryptcraft.talkative.api.tree

import com.google.common.collect.Lists
import net.minecraft.nbt.*
import dev.cryptcraft.talkative.api.conditional.ConditionalHolder
import dev.cryptcraft.talkative.api.conditional.Conditional
import dev.cryptcraft.talkative.common.util.NBTConstants

/**
 * A Node used within a [DialogBranch].
 * Can be either a Dialog node or Response node. All children are expected to be of the same [NodeType].
 */
class DialogNode(val nodeId: Int, var nodeType: NodeType = NodeType.Dialog, var content: String = "Hello World", private var conditional: Conditional? = null) : ConditionalHolder {
    /**
     * The [NodeType] of this node's children. Used to ensure all Child [NodeType]s are the same.
     */
    private var childType: NodeType? = null
    private var children: ArrayList<Int> = ArrayList()

    /**
     * The Commands attached to this Node to be executed when this Node is reached during a [Conversation][dev.cryptcraft.talkative.server.conversations.Conversation].
     */
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

    /**
     * @return List of Child Node IDs if children [NodeType] is [NodeType.Response], otherwise Null.
     */
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

    fun serialize(tag: CompoundTag = CompoundTag()): CompoundTag {
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