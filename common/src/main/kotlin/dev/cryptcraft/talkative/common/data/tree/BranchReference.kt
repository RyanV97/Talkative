package dev.cryptcraft.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag
import dev.cryptcraft.talkative.api.ConditionalHolder
import dev.cryptcraft.talkative.common.data.conditional.Conditional
import dev.cryptcraft.talkative.common.util.FileUtil
import dev.cryptcraft.talkative.common.util.NBTConstants

class BranchReference(var fileString: String, private var conditional: Conditional? = null) : ConditionalHolder {
    var valid: Boolean = FileUtil.branchExists(fileString)

    override fun getConditional(): Conditional? {
        return conditional
    }

    override fun setConditional(newConditional: Conditional?) {
        conditional = newConditional
    }

    fun validate() {
        valid = FileUtil.branchExists(fileString)
    }

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.putString(NBTConstants.BRANCH_FILE, fileString)
        if (conditional != null) tag.put(NBTConstants.CONDITIONAL, conditional!!.serialize(CompoundTag()))
        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): BranchReference {
            val branch = BranchReference(tag.getString(NBTConstants.BRANCH_FILE))
            if (tag.contains(NBTConstants.CONDITIONAL)) branch.conditional = Conditional.deserialize(tag.getCompound(NBTConstants.CONDITIONAL))
            return branch
        }
    }
}