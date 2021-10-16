package ryanv.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import ryanv.talkative.consts.NBTConstants

class DialogTree {

    var branches: List<DialogBranch> = ArrayList()

    fun serialize(tag: CompoundTag): CompoundTag {
        val branchList: ListTag = ListTag()
        for (branch: DialogBranch in branches)
            branchList.add(branch.serialize(CompoundTag()))
        tag.put(NBTConstants.TREE_BRANCHES, branchList)
        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): DialogTree {
            val tree = DialogTree()

            val branchList: ListTag = tag.getList(NBTConstants.TREE_BRANCHES, 10)

            return tree
        }
    }

}