package ryanv.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag

class DialogBranch(var rootNode: DialogNode = DialogNode()) {

    fun serialize(tag: CompoundTag): CompoundTag {
        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): DialogBranch {
            var branch = DialogBranch()

            return branch
        }
    }

}