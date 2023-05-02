package ryanv.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag
import ryanv.talkative.common.data.conditional.Conditional
import ryanv.talkative.common.util.NBTConstants

class BranchReference(var fileString: String, var branchPriority: Int = 0, var conditional: Conditional? = null) {
    fun getData(): CompoundTag {
        val tag = CompoundTag()
        tag.putString(NBTConstants.CONDITIONAL_HOLDER_BRANCH, fileString)
        if(conditional != null)
            tag.put(NBTConstants.CONDITIONAL, conditional!!.serialize(CompoundTag()))
        return tag
    }

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.putString(NBTConstants.BRANCH_FILE, fileString)

        if(branchPriority != 0)
            tag.putInt(NBTConstants.BRANCH_PRIORITY, branchPriority)
        if(conditional != null)
            tag.put(NBTConstants.CONDITIONAL, conditional?.serialize(CompoundTag()))

        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): BranchReference {
            val branch = BranchReference(tag.getString(NBTConstants.BRANCH_FILE))

            if(tag.contains(NBTConstants.BRANCH_PRIORITY))
                branch.branchPriority = tag.getInt(NBTConstants.BRANCH_PRIORITY)
            if(tag.contains(NBTConstants.CONDITIONAL))
                branch.conditional = Conditional.deserialize(tag.getCompound(NBTConstants.CONDITIONAL))

            return branch
        }
    }
}