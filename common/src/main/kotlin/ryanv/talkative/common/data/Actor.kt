package ryanv.talkative.common.data

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import ryanv.talkative.common.data.tree.BranchReference
import ryanv.talkative.common.consts.NBTConstants

class Actor {
    var markerData: MarkerData? = MarkerData()
    var dialogBranches: ArrayList<BranchReference> = ArrayList()

    fun deserialize(tag: CompoundTag): Actor {
        markerData = MarkerData.deserialize(tag.getCompound(NBTConstants.MARKER_DATA))
        val tagList = tag.getList(NBTConstants.BRANCH_REFERENCES, 10)
        for (branchTag in tagList)
            dialogBranches.add(BranchReference.deserialize(branchTag as CompoundTag))
        return this
    }

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.put(NBTConstants.MARKER_DATA, markerData?.serialize(CompoundTag()))
        val tagList = ListTag()
        for (branch in dialogBranches) {
            tagList.add(branch.serialize(CompoundTag()))
        }
        tag.put(NBTConstants.BRANCH_REFERENCES, tagList)
        return tag
    }
}