package ryanv.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag

class DialogNode {

    var children: List<DialogNode> = ArrayList()

    companion object {
        fun deserialize(tag:CompoundTag): DialogNode {
            var node = DialogNode()

            return node
        }
    }

}