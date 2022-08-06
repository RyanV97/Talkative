package ryanv.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag
import ryanv.talkative.common.consts.NBTConstants

class DialogBranch(val rootNode: DialogNode) {

    companion object {
        fun deserialize(tag: CompoundTag): DialogBranch {
            val rootNode: DialogNode = DialogNode.deserialize(tag.getCompound(NBTConstants.BRANCH_ROOT))
            return DialogBranch(rootNode)
        }
    }
}