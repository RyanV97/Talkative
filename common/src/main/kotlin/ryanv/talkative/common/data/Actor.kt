package ryanv.talkative.common.data

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import ryanv.talkative.common.data.tree.BranchReference
import ryanv.talkative.common.consts.NBTConstants

class Actor {
    var markerData: MarkerData? = MarkerData()
    var dialogBranches: ArrayList<BranchReference> = ArrayList()

    fun getBranchFromPath(path: String): BranchReference? {
        dialogBranches.forEach {
            if(it.fileString.equals(path))
                return it
        }
        return null
    }

    fun getBranchForPlayer(player: ServerPlayer): BranchReference? {
        var branch: BranchReference? = null
        dialogBranches.forEach {
//            if(it.conditional == null) { // || it.rootConditional!!.eval()) {
            if(it.conditional == null || it.conditional!!.eval(player)) {
                if(branch == null || branch!!.branchPriority < it.branchPriority)
                    branch = it
            }
        }
        return branch
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

    companion object {
        fun deserialize(tag: CompoundTag): Actor {
            val actor = Actor()
            actor.markerData = MarkerData.deserialize(tag.getCompound(NBTConstants.MARKER_DATA))
            val tagList = tag.getList(NBTConstants.BRANCH_REFERENCES, 10)
            for (branchTag in tagList)
                actor.dialogBranches.add(BranchReference.deserialize(branchTag as CompoundTag))
            return actor
        }
    }
}