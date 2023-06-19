package ryanv.talkative.common.data

import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import ryanv.talkative.common.util.NBTConstants

class MarkerData(var modelLocation: ResourceLocation = ResourceLocation("talkative", "models/marker.json"), var baseColour: Int = 0xFFFFFF, var outlineColour: Int = 0xFFFFFF) {

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.putString(NBTConstants.MARKER_LOCATION, modelLocation.toString())
        if (baseColour != 0xFFFFFF)
            tag.putInt(NBTConstants.MARKER_COLOUR, baseColour)
        if (outlineColour != 0xFFFFFF)
            tag.putInt(NBTConstants.MARKER_OUTLINE, outlineColour)
        return tag
    }

    companion object {
        fun deserialize(root: CompoundTag): MarkerData? {
            if (!root.contains(NBTConstants.MARKER_DATA))
                return null

            val tag = root.getCompound(NBTConstants.MARKER_DATA)
            val modelLocation = ResourceLocation(tag.getString(NBTConstants.MARKER_LOCATION))
            val data = MarkerData(modelLocation)

            if (tag.contains(NBTConstants.MARKER_COLOUR))
                data.baseColour = tag.getInt(NBTConstants.MARKER_COLOUR)
            if (tag.contains(NBTConstants.MARKER_OUTLINE))
                data.outlineColour = tag.getInt(NBTConstants.MARKER_OUTLINE)

            return data
        }
    }

}