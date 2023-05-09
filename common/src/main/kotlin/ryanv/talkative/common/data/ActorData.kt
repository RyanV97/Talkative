package ryanv.talkative.common.data

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.level.ServerPlayer
import ryanv.talkative.common.data.tree.BranchReference
import ryanv.talkative.common.util.NBTConstants

class ActorData {
    var markerData: MarkerData? = MarkerData()
    var dialogBranches: ArrayList<BranchReference> = ArrayList()

    fun getBranchFromPath(path: String): BranchReference? {
        dialogBranches.forEach {
            if(it.fileString == path)
                return it
        }
        return null
    }

    fun getBranchForPlayer(player: ServerPlayer): BranchReference? {
        var branch: BranchReference? = null
        dialogBranches.forEach {
            if(it.getConditional() == null || it.getConditional()!!.eval(player)) {
                if(branch == null || branch!!.actorBranchIndex < it.actorBranchIndex)
                    branch = it
            }
        }
        return branch
    }

    fun shouldOverrideDisplayName(): Boolean {
        //ToDo Implement This
        return true
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
        fun deserialize(tag: CompoundTag): ActorData {
            val serverActorData = ActorData()
            serverActorData.markerData = MarkerData.deserialize(tag.getCompound(NBTConstants.MARKER_DATA))
            val tagList = tag.getList(NBTConstants.BRANCH_REFERENCES, 10)
            for (branchTag in tagList)
                serverActorData.dialogBranches.add(BranchReference.deserialize(branchTag as CompoundTag))
            return serverActorData
        }
    }
}