package ryanv.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag
import ryanv.talkative.api.IConditional
import ryanv.talkative.common.data.conditional.Conditional
import ryanv.talkative.common.consts.NBTConstants

class BranchReference(var fileString: String, var branchPriority: Int = 0, private var conditional: Conditional? = null): IConditional {

    override fun getConditionalType(): IConditional.Type {
        return IConditional.Type.BRANCH
    }

    fun setConditional(value: Conditional) {
        this.conditional = value
    }

    override fun getConditional(): Conditional? {
        return conditional
    }

    override fun getData(): CompoundTag {
        val tag = CompoundTag()
        tag.putString(NBTConstants.CONDITIONAL_HOLDER_TYPE, conditionalType.toString())
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