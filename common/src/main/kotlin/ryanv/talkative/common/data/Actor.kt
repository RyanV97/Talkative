package ryanv.talkative.common.data

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import ryanv.talkative.common.data.tree.DialogBranch
import ryanv.talkative.consts.NBTConstants

class Actor {

    var markerData: MarkerData? = null
    var dialogBranches: ArrayList<DialogBranch> = ArrayList()

    fun deserialize(tag: CompoundTag) {
        markerData = MarkerData.deserialize(tag.getCompound(NBTConstants.MARKER_DATA))
        val tagList = tag.getList(NBTConstants.BRANCH_DATA, 10)
        for (branchTag in tagList)
            dialogBranches.add(DialogBranch.deserialize(branchTag as CompoundTag))
    }

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.put(NBTConstants.MARKER_DATA, markerData?.serialize(CompoundTag()))
        val tagList = ListTag()
        for (branch in dialogBranches) {
            tagList.add(branch.serialize(CompoundTag()))
        }
        tag.put(NBTConstants.BRANCH_DATA, tagList)
        return tag
    }

}