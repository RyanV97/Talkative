package ryanv.talkative.common.data.tree

import net.minecraft.nbt.CompoundTag
import ryanv.talkative.api.ConditionalHolder
import ryanv.talkative.common.data.conditional.Conditional
import ryanv.talkative.common.util.NBTConstants

class BranchReference(var fileString: String, var actorBranchIndex: Int = 0, private var conditional: Conditional? = null) : ConditionalHolder {
    fun getData(): CompoundTag {
        val tag = CompoundTag()
        tag.putString(NBTConstants.CONDITIONAL_HOLDER_BRANCH, fileString)
        if (conditional != null)
            tag.put(NBTConstants.CONDITIONAL, conditional!!.serialize(CompoundTag()))
        return tag
    }

    override fun getConditional(): Conditional? {
        return conditional
    }

    override fun setConditional(newConditional: Conditional?) {
        conditional = newConditional
    }

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.putString(NBTConstants.BRANCH_FILE, fileString)

        if (actorBranchIndex != 0)
            tag.putInt(NBTConstants.BRANCH_INDEX, actorBranchIndex)
        if (conditional != null)
            tag.put(NBTConstants.CONDITIONAL, conditional?.serialize(CompoundTag()))

        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): BranchReference {
            val branch = BranchReference(tag.getString(NBTConstants.BRANCH_FILE))

            if (tag.contains(NBTConstants.BRANCH_INDEX))
                branch.actorBranchIndex = tag.getInt(NBTConstants.BRANCH_INDEX)
            if (tag.contains(NBTConstants.CONDITIONAL))
                branch.conditional = Conditional.deserialize(tag.getCompound(NBTConstants.CONDITIONAL))

            return branch
        }
    }
}