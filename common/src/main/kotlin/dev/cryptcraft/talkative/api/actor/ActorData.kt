package dev.cryptcraft.talkative.api.actor

import dev.cryptcraft.talkative.api.actor.markers.Marker
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.level.ServerPlayer
import dev.cryptcraft.talkative.api.tree.BranchReference
import dev.cryptcraft.talkative.common.util.NBTConstants
import net.minecraft.nbt.Tag

class ActorData {
    var dialogBranches: ArrayList<BranchReference> = ArrayList()
    var displayData: DisplayData = DisplayData()
    var markers: ArrayList<Marker> = ArrayList()


    fun getBranchWithPath(path: String): BranchReference? {
        this.dialogBranches.forEach {
            if(it.fileString == path)
                return it
        }
        return null
    }

    fun getBranchForPlayer(player: ServerPlayer): BranchReference? {
        this.dialogBranches.forEach {
            if(it.getConditional() == null || it.getConditional()!!.eval(player))
                return it
        }
        return null
    }

    fun serialize(tag: CompoundTag = CompoundTag()): CompoundTag {
        val branchList = ListTag()
        for (branch in this.dialogBranches)
            branchList.add(branch.serialize(CompoundTag()))
        tag.put(NBTConstants.BRANCH_REFERENCES, branchList)

        tag.put(NBTConstants.DISPLAY_DATA, this.displayData.serialize(CompoundTag()))

        val markerList = ListTag()
        for (marker in this.markers)
            markerList.add(marker.serialize())
        tag.put(NBTConstants.MARKER_DATA, markerList)

        return tag
    }

    fun validate() {
        this.dialogBranches.forEach { it.validate() }
    }

    companion object {
        fun deserialize(tag: CompoundTag): ActorData {
            val actorData = ActorData()

            val tagList = tag.getList(NBTConstants.BRANCH_REFERENCES, Tag.TAG_COMPOUND.toInt())
            for (branchTag in tagList)
                actorData.dialogBranches.add(BranchReference.deserialize(branchTag as CompoundTag))

            actorData.displayData = DisplayData.deserialize(tag)

            val markerList = tag.getList(NBTConstants.MARKER_DATA, Tag.TAG_COMPOUND.toInt())
            for (markerTag in markerList)
                actorData.markers.add(Marker.deserialize(markerTag as CompoundTag))

            return actorData
        }
    }
}