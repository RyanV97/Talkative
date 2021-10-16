package ryanv.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag

class DialogTree {

    var branches: List<DialogBranch> = ArrayList()

    companion object {
        fun deserialize(tag: CompoundTag): DialogTree {
            var tree = DialogTree()

            return tree
        }
    }

}