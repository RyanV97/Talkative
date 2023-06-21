package ryanv.talkative.common.data

import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import ryanv.talkative.common.util.NBTConstants

class DisplayData {
    var overrideDisplayName: Boolean = false
    var displayName: String = "Actor"
    var markerModelLocation: ResourceLocation = ResourceLocation("talkative", "models/marker.json")
    var markerBaseColour: Int = 0xFFFFFF
    var markerOutlineColour: Int = 0xFFFFFF

    fun serialize(tag: CompoundTag): CompoundTag {
        tag.putString(NBTConstants.DISPLAY_MARKER_LOCATION, markerModelLocation.toString())
        if (markerBaseColour != 0xFFFFFF)
            tag.putInt(NBTConstants.DISPLAY_MARKER_COLOUR, markerBaseColour)
        if (markerOutlineColour != 0xFFFFFF)
            tag.putInt(NBTConstants.DISPLAY_MARKER_OUTLINE, markerOutlineColour)
        return tag
    }

    companion object {
        fun deserialize(root: CompoundTag): DisplayData? {
            if (!root.contains(NBTConstants.DISPLAY_DATA))
                return null

            val data = DisplayData()
            val tag = root.getCompound(NBTConstants.DISPLAY_DATA)

            if (tag.contains(NBTConstants.DISPLAY_NAME_OVERRIDE))
                data.overrideDisplayName = tag.getBoolean(NBTConstants.DISPLAY_NAME_OVERRIDE)
            if (tag.contains(NBTConstants.DISPLAY_NAME))
                data.displayName = tag.getString(NBTConstants.DISPLAY_NAME)

            data.markerModelLocation = ResourceLocation(tag.getString(NBTConstants.DISPLAY_MARKER_LOCATION))
            if (tag.contains(NBTConstants.DISPLAY_MARKER_COLOUR))
                data.markerBaseColour = tag.getInt(NBTConstants.DISPLAY_MARKER_COLOUR)
            if (tag.contains(NBTConstants.DISPLAY_MARKER_OUTLINE))
                data.markerOutlineColour = tag.getInt(NBTConstants.DISPLAY_MARKER_OUTLINE)

            return data
        }
    }

}