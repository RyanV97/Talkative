package ryanv.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.storage.LevelResource
import ryanv.talkative.common.data.conditional.Conditional
import ryanv.talkative.common.consts.NBTConstants
import ryanv.talkative.common.util.FileUtil

class BranchReference(var fileString: String, var branchPriority: Int = 0, var rootConditional: Conditional? = null) {
    fun serialize(tag: CompoundTag): CompoundTag {
        tag.putString(NBTConstants.BRANCH_FILE, fileString)

        if(branchPriority != 0)
            tag.putInt(NBTConstants.BRANCH_PRIORITY, branchPriority)
        if(rootConditional != null)
            tag.put(NBTConstants.CONDITIONAL, rootConditional?.serialize(CompoundTag()))

        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): BranchReference {
            val branch = BranchReference(tag.getString(NBTConstants.BRANCH_FILE))

            if(tag.contains(NBTConstants.BRANCH_PRIORITY))
                branch.branchPriority = tag.getInt(NBTConstants.BRANCH_PRIORITY)
            if(tag.contains(NBTConstants.CONDITIONAL))
                branch.rootConditional = Conditional.deserialize(tag.getCompound(NBTConstants.CONDITIONAL))

            return branch
        }
    }
}