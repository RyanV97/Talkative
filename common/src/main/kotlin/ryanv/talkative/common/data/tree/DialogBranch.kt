package ryanv.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag

class DialogBranch(var rootNode: DialogNode = DialogNode()) {

    companion object {
        fun deserialize(tag: CompoundTag): DialogBranch {
            var branch = DialogBranch()

            return branch
        }
    }

}