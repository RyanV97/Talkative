package dev.cryptcraft.talkative.api.tree.node

import com.google.common.collect.Lists
import dev.cryptcraft.talkative.api.conditional.Conditional
import dev.cryptcraft.talkative.api.conditional.ConditionalHolder
import dev.cryptcraft.talkative.api.tree.node.NodeBase.NodeType
import dev.cryptcraft.talkative.common.util.NBTConstants
import net.minecraft.nbt.*
import net.minecraft.network.chat.Component
import java.util.*

/**
 * A Node used within a [DialogBranch].
 * Can be either a Dialog node or Response node. All children are expected to be of the same [NodeType].
 */
abstract class NodeBase(val nodeId: Int, private var conditional: Conditional? = null) : ConditionalHolder {
    private val childNodes: ArrayList<NodeReference> = ArrayList()

    /**
     * The Commands attached to this Node to be executed when this Node is reached during a [Conversation][dev.cryptcraft.talkative.server.conversations.Conversation].
     */
    var commands: ArrayList<String>? = null

    /**
     * Adds a reference to this Node to the [childNodes] list.
     * This function checks that, if there are already child references, that this new reference will also be of the same type, as all children types must match.
     */
    fun addChild(node: NodeBase): Boolean {
        val nodeRef = NodeReference(node)
        if (childNodes.isNotEmpty() && childNodes[0].nodeType != nodeRef.nodeType)
            return false

        return childNodes.add(nodeRef)
    }

    fun removeChild(nodeId: Int): Boolean {
        if (childNodes.isEmpty()) return false
        return childNodes.remove(NodeReference(nodeId, childNodes[0].nodeType))
    }

    fun removeChild(node: NodeBase): Boolean {
        if (childNodes.isEmpty() || childNodes[0].nodeType != node.getNodeType()) return false
        return childNodes.remove(NodeReference(node))
    }

    override fun getConditional(): Conditional? {
        return conditional
    }

    override fun setConditional(newConditional: Conditional?) {
        conditional = newConditional
    }

    /**
     * @return A Mutable copy of the children [NodeReference]s of this Node.
     */
    fun getChildren(): List<NodeReference> {
        return childNodes.toMutableList()
    }

    /**
     * @return [NodeType] of the Children of this Node if it has any, otherwise Null.
     */
    fun getChildrenType(): NodeType? {
        if (childNodes.isEmpty()) return null
        return childNodes[0].nodeType
    }

    /**
     * @return An [IntArray] containing the Node ID of every child [NodeBase] of this Node.
     */
    fun getChildIds(): IntArray {
        val array = IntArray(childNodes.size)
        childNodes.forEachIndexed { index, nodeReference -> array[index] = nodeReference.nodeId }
        return array
    }

    /**
     * @return The NodeType of this Node, for use in Serialization.
     */
    abstract fun getNodeType(): NodeType

    open fun serialize(tag: CompoundTag = CompoundTag()): CompoundTag {
        tag.putInt(NBTConstants.NODE_ID, nodeId)
        tag.putInt(NBTConstants.NODE_TYPE, getNodeType().ordinal)

        if (conditional != null)
            tag.put(NBTConstants.CONDITIONAL, conditional!!.serialize(CompoundTag()))

        if (childNodes.isNotEmpty()) {
            tag.putInt(NBTConstants.NODE_CHILD_TYPE, getChildrenType()!!.ordinal)
            tag.put(NBTConstants.NODE_CHILDREN, IntArrayTag(getChildIds()))
        }

        if (commands != null) {
            val commandsList = ListTag()
            commands!!.forEach { commandsList.add(StringTag.valueOf(it)) }
            tag.put(NBTConstants.NODE_COMMANDS, commandsList)
        }

        return tag
    }

    abstract fun deserialize(tag: CompoundTag): NodeBase

    companion object {
        fun deserialize(tag: CompoundTag): NodeBase? {
            if (tag.isEmpty) return null

            try {
                val nodeId = tag.getInt(NBTConstants.NODE_ID)
                val nodeType = NodeType.values()[tag.getInt(NBTConstants.NODE_TYPE)]
                val conditional = if (tag.contains(NBTConstants.CONDITIONAL)) Conditional.deserialize(tag.getCompound(NBTConstants.CONDITIONAL)) else null

                val node = createNodeFromType(nodeType, nodeId) ?: return null
                node.setConditional(conditional)
                node.deserialize(tag)

                if (tag.contains(NBTConstants.NODE_CHILDREN)) {
                    val childType = NodeType.values()[tag.getInt(NBTConstants.NODE_CHILD_TYPE)]
                    for (childId in tag.getIntArray(NBTConstants.NODE_CHILDREN)) {
                        node.childNodes.add(NodeReference(childId, childType))
                    }
                }

                if (tag.contains(NBTConstants.NODE_COMMANDS)) {
                    node.commands = Lists.newArrayList()
                    tag.getList(NBTConstants.NODE_COMMANDS, Tag.TAG_STRING.toInt())
                        .forEach { node.commands!!.add(it.asString) }
                }

                return node
            }
            catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
            }

            return null
        }

        fun createNodeFromType(nodeType: NodeType, nodeId: Int): NodeBase? {
            return when (nodeType) {
                NodeType.Dialog -> DialogNode(nodeId, Component.empty())
                NodeType.Response -> ResponseNode(nodeId, Component.empty())
                NodeType.Bridge -> BridgeNode(nodeId, 0, "")
                else -> return null
            }
        }
    }

    /**
     * A simple data class for referencing a Node ID and [NodeType]
     *
     * Used for storing [NodeBase] children, specifically for ensuring all children share a [NodeType]
     */
    data class NodeReference(val nodeId: Int, val nodeType: NodeType) {
        constructor(node: NodeBase) : this(node.nodeId, node.getNodeType())
    }

    enum class NodeType {
        Dialog, Response, Bridge
    }
}