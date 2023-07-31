package dev.cryptcraft.talkative.common.data.markers

import dev.cryptcraft.talkative.common.data.conditional.Conditional
import dev.cryptcraft.talkative.common.util.NBTConstants
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.nbt.CompoundTag

data class Marker(
    var modelLocation: ModelResourceLocation = DEFAULT_LOCATION,
    var overlayColour: Int = 0xFFFFFF,
    var conditional: Conditional? = null
) {
    fun serialize(tag: CompoundTag = CompoundTag()): CompoundTag {
        if (modelLocation != DEFAULT_LOCATION)
            tag.putString(NBTConstants.MARKER_LOCATION, modelLocation.toString())
        if (overlayColour != 0xFFFFFF)
            tag.putInt(NBTConstants.MARKER_COLOUR, overlayColour)
        if (conditional != null)
            tag.put(NBTConstants.CONDITIONAL, conditional!!.serialize())

        return tag
    }

    companion object {
        val DEFAULT_LOCATION = ModelResourceLocation("talkative:markers/marker#marker")

        fun deserialize(tag: CompoundTag): Marker {
            val marker = Marker()

            if (tag.contains(NBTConstants.MARKER_LOCATION))
                marker.modelLocation = ModelResourceLocation(tag.getString(NBTConstants.MARKER_LOCATION))
            if (tag.contains(NBTConstants.MARKER_COLOUR))
                marker.overlayColour = tag.getInt(NBTConstants.MARKER_COLOUR)
            if (tag.contains(NBTConstants.CONDITIONAL))
                marker.conditional = Conditional.deserialize(tag.getCompound(NBTConstants.CONDITIONAL))

            return marker
        }
    }
}