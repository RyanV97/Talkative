package dev.cryptcraft.talkative.api.tree

import it.unimi.dsi.fastutil.ints.Int2ReferenceLinkedOpenHashMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import dev.cryptcraft.talkative.common.util.NBTConstants
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag

/**
 * Data class for a Dialog Tree Branch. Made up of a root [DialogNode] and it's children.
 * For attaching a branch to an Actor, use a [BranchReference]
 */
class DialogBranch(private val nodes: Int2ReferenceLinkedOpenHashMap<DialogNode> = Int2ReferenceLinkedOpenHashMap()) {
    var highestId: Int = 0
        get() {
            field += 1
            return field
        }

    fun addNode(node: DialogNode) {
        nodes.put(node.nodeId, node)
    }

    fun getNode(id: Int): DialogNode? {
        return nodes.get(id)
    }

    fun clearNodes() {
        nodes.clear()
    }

    fun getChildNodeForPlayer(parentId: Int, player: ServerPlayer): DialogNode? {
        return getChildNodeForPlayer(getNode(parentId), player)
    }

    fun getChildNodeForPlayer(parent: DialogNode?, player: ServerPlayer): DialogNode? {
        var node: DialogNode? = null
        parent?.getChildren()?.forEach {
            val child = nodes[it]
            if (child?.getConditional() == null || child.getConditional()!!.eval(player)) {
                node = child
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
            val nodes = Int2ReferenceLinkedOpenHashMap<DialogNode>()
            val nodeList = tag.getList(NBTConstants.BRANCH_NODES, Tag.TAG_COMPOUND.toInt())

            nodeList.forEach {
                val nodeData = it as CompoundTag
                val node = DialogNode.deserialize(nodeData)
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