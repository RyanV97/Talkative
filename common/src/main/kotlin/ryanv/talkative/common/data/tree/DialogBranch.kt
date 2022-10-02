package ryanv.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.api.IConditional
import ryanv.talkative.common.consts.NBTConstants

class DialogBranch(var nodes: HashMap<Int, DialogNode> = HashMap()) {

    var highestId: Int = 0
    get() {
        field += 1
        return field
    }

    fun addNode(node: DialogNode) {
        nodes[node.nodeId] = node
    }

    fun getChildNodeForPlayer(parentId: Int, player: ServerPlayer): DialogNode? {
        return getChildNodeForPlayer(nodes[parentId], player)
    }

    fun getChildNodeForPlayer(parent: DialogNode?, player: ServerPlayer): DialogNode? {
        var node: DialogNode? = null
        parent?.getChildren()?.forEach {
            val child = nodes[it]
            if(child?.conditional == null) { // || child?.conditional!!.eval()) {
                node = child
            }
        }
        return node
    }

    fun serialize(tag: CompoundTag): CompoundTag {
        val nodeList = CompoundTag()
        nodes.forEach { (id, node) ->
            nodeList.put(id.toString(), node.serialize(CompoundTag()))
        }
        tag.put(NBTConstants.BRANCH_NODES, nodeList)
        if(nodes.isNotEmpty())
            tag.putInt(NBTConstants.BRANCH_HIGH_ID, nodes.maxByOrNull { it.key }!!.key)
        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): DialogBranch? {
            val nodes = HashMap<Int, DialogNode>()
            val nodeList = tag.get(NBTConstants.BRANCH_NODES) as CompoundTag
            if(!nodeList.contains("0")) {
                //uh oh, no root? :c
                //Throw exception
                return null
            }
            nodeList.allKeys.forEach {
                val id: Int = it.toInt()
                val node = DialogNode.deserialize(nodeList.get(it) as CompoundTag)
                nodes[id] = node
            }
            var highestId = 0
            if(tag.contains(NBTConstants.BRANCH_HIGH_ID)) {
                highestId = tag.getInt(NBTConstants.BRANCH_HIGH_ID)
            }
            val branch = DialogBranch(nodes)
            branch.highestId = highestId
            return branch
        }
    }
}