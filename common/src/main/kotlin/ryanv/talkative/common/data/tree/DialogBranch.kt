package ryanv.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.storage.LevelResource
import ryanv.talkative.common.data.conditional.Conditional
import ryanv.talkative.common.consts.NBTConstants

class DialogBranch(var fileString: String, var branchPriority: Int = 0, var rootConditional: Conditional? = null) {

    fun loadBranchData(level: ServerLevel): DialogNode? {
        val file = level.server.getWorldPath(LevelResource.ROOT).resolve(fileString)

        return null
    }

    fun saveBranchData(level: ServerLevel) {

    }

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.putString(NBTConstants.BRANCH_FILE, fileString)

        if(branchPriority != 0)
            tag.putInt(NBTConstants.BRANCH_PRIORITY, branchPriority)
        if(rootConditional != null)
            tag.put(NBTConstants.CONDITIONAL, rootConditional?.serialize(CompoundTag()))

        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): DialogBranch {
            val branch = DialogBranch(tag.getString(NBTConstants.BRANCH_FILE))

            if(tag.contains(NBTConstants.BRANCH_PRIORITY))
                branch.branchPriority = tag.getInt(NBTConstants.BRANCH_PRIORITY)
            if(tag.contains(NBTConstants.CONDITIONAL))
                branch.rootConditional = Conditional.deserialize(tag.getCompound(NBTConstants.CONDITIONAL))

            return branch
        }
    }

}