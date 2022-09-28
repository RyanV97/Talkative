package ryanv.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag
import ryanv.talkative.common.consts.NBTConstants

class DialogBranch(var nodes: HashMap<Int, DialogNode> = HashMap()) {

    var highestId: Int = 0
    get() {
        return ++field
    }

    fun serialize(tag: CompoundTag): CompoundTag {
        val nodeList = CompoundTag()
        nodes.forEach { (id, node) ->
            nodeList.put(id.toString(), node.serialize(CompoundTag()))
        }
        tag.put(NBTConstants.BRANCH_NODES, nodeList)
        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): DialogBranch {
            var nodes = HashMap<Int, DialogNode>()
            var nodeList = tag.get(NBTConstants.BRANCH_NODES) as CompoundTag
            nodeList.allKeys.forEach {
                val id: Int = it.toInt()
                val node = DialogNode.deserialize(nodeList.get(it) as CompoundTag)
                nodes[id] = node
            }
            return DialogBranch(nodes)
        }
    }
}