package dev.cryptcraft.talkative.common.data

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.level.ServerPlayer
import dev.cryptcraft.talkative.common.data.tree.BranchReference
import dev.cryptcraft.talkative.common.util.NBTConstants

class ActorData {
    var displayData: DisplayData? = DisplayData()
    var dialogBranches: ArrayList<BranchReference> = ArrayList()

    fun getBranchFromPath(path: String): BranchReference? {
        dialogBranches.forEach {
            if(it.fileString == path)
                return it
        }
        return null
    }

    fun getBranchForPlayer(player: ServerPlayer): BranchReference? {
        dialogBranches.forEach {
            if(it.getConditional() == null || it.getConditional()!!.eval(player))
                return it
        }
        return null
    }

    fun shouldOverrideDisplayName(): Boolean {
        //ToDo Implement This
        return true
    }

    fun serialize(tag: CompoundTag = CompoundTag()): CompoundTag {
        if (displayData != null)
            tag.put(NBTConstants.DISPLAY_DATA, displayData!!.serialize(CompoundTag()))

        val branchList = ListTag()
        for (branch in dialogBranches)
            branchList.add(branch.serialize(CompoundTag()))
        tag.put(NBTConstants.BRANCH_REFERENCES, branchList)

        return tag
    }

    fun validate() {
        dialogBranches.forEach { it.validate() }
    }

    companion object {
        fun deserialize(tag: CompoundTag): ActorData {
            val serverActorData = ActorData()

            serverActorData.displayData = DisplayData.deserialize(tag)

            val tagList = tag.getList(NBTConstants.BRANCH_REFERENCES, 10)
            for (branchTag in tagList)
                serverActorData.dialogBranches.add(BranchReference.deserialize(branchTag as CompoundTag))

            return serverActorData
        }
    }
}