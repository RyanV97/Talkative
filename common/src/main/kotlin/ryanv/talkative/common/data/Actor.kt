package ryanv.talkative.common.data

import net.minecraft.nbt.CompoundTag
import ryanv.talkative.common.data.tree.DialogTree
import ryanv.talkative.consts.NBTConstants

class Actor {

    lateinit var markerData: MarkerData
    lateinit var dialogTree: DialogTree

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.put(NBTConstants.MARKER_DATA, markerData.serialize(CompoundTag()))
        tag.put(NBTConstants.TREE_DATA, dialogTree.serialize(CompoundTag()))
        return tag
    }

}