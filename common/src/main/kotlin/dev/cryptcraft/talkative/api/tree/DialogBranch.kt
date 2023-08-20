package dev.cryptcraft.talkative.api.tree

import dev.cryptcraft.talkative.api.tree.node.DialogNode
import dev.cryptcraft.talkative.api.tree.node.NodeBase
import dev.cryptcraft.talkative.common.util.NBTConstants
import it.unimi.dsi.fastutil.ints.Int2ReferenceLinkedOpenHashMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerPlayer

/**
 * Data class for a Dialog Tree Branch. Made up of a root [NodeBase] and it's children.
 * For attaching a branch to an Actor, use a [BranchReference]
 */
class DialogBranch(private val nodes: Int2ReferenceLinkedOpenHashMap<NodeBase> = Int2ReferenceLinkedOpenHashMap()) {
    var highestId: Int = 0
        get() {
            field += 1
            return field
        }

    fun addNode(node: NodeBase) {
        nodes.put(node.nodeId, node)
    }

    fun getNode(id: Int): NodeBase? {
        return nodes.get(id)
    }

    fun clearNodes() {
        nodes.clear()
    }

    fun getNextDialogForPlayer(parentId: Int, player: ServerPlayer): DialogNode? {
        return getNextDialogForPlayer(getNode(parentId), player)
    }

    fun getNextDialogForPlayer(parent: NodeBase?, player: ServerPlayer): DialogNode? {
        var node: DialogNode? = null
        parent?.getChildren()?.forEach {
            val child = nodes[it.nodeId]
            if (child?.getConditional() == null || child.getConditional()!!.eval(player)) {
                if (child is DialogNode) {
                    node = child
                    return@forEach
                }
                //ToDo Implement BridgeNode here
            }
        }
        return node
    }

    fun serialize(tag: CompoundTag = CompoundTag()): CompoundTag {
        val nodeList = ListTag()

        nodes.values.forEach { node ->
            nodeList.add(node.serialize())
        }
        tag.put(NBTConstants.BRANCH_NODES, nodeList)

        if (nodes.isNotEmpty())
            tag.putInt(NBTConstants.BRANCH_HIGH_ID, nodes.maxByOrNull { it.key }!!.key)

        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): DialogBranch? {
            val nodes = Int2ReferenceLinkedOpenHashMap<NodeBase>()
            val nodeList = tag.getList(NBTConstants.BRANCH_NODES, Tag.TAG_COMPOUND.toInt())

            nodeList.forEach {
                val nodeData = it as CompoundTag
                val node = NodeBase.deserialize(nodeData)
                nodes.put(nodeData.getInt(NBTConstants.NODE_ID), node!!)
            }

            var highestId = 0
            if (tag.contains(NBTConstants.BRANCH_HIGH_ID)) {
                highestId = tag.getInt(NBTConstants.BRANCH_HIGH_ID)
            }

            val branch = DialogBranch(nodes)
            branch.highestId = highestId
            return branch
        }
    }
}