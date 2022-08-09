package ryanv.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag
import ryanv.talkative.common.consts.NBTConstants

class DialogBranch(var rootNode: DialogNode) {

    var highestId: Int = 0
    get() {
        return ++field
    }

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.put(NBTConstants.BRANCH_ROOT, rootNode.serialize(CompoundTag()))
        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): DialogBranch {
            val rootNode: DialogNode = DialogNode.deserialize(tag.getCompound(NBTConstants.BRANCH_ROOT))
            return DialogBranch(rootNode)
        }
    }
}