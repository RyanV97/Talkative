package dev.cryptcraft.talkative.api.actor.markers

import com.mojang.math.Vector3f
import dev.cryptcraft.talkative.api.conditional.Conditional
import dev.cryptcraft.talkative.common.util.NBTConstants
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag

data class Marker(
    var modelLocation: ModelResourceLocation = DEFAULT_LOCATION,
    var overlayColour: Int = 0xFFFFFF,
    var positionOffset: Vector3f? = null,
    var conditional: Conditional? = null
) {
    fun serialize(tag: CompoundTag = CompoundTag()): CompoundTag {
        if (modelLocation != DEFAULT_LOCATION)
            tag.putString(NBTConstants.MARKER_LOCATION, modelLocation.toString())
        if (overlayColour != 0xFFFFFF)
            tag.putInt(NBTConstants.MARKER_COLOUR, overlayColour)
        if (positionOffset != null && positionOffset != Vector3f.ZERO) {
            val list = ListTag()
            list.add(FloatTag.valueOf(positionOffset!!.x()))
            list.add(FloatTag.valueOf(positionOffset!!.y()))
            list.add(FloatTag.valueOf(positionOffset!!.z()))
            tag.put(NBTConstants.MARKER_OFFSET, list)
        }
        if (conditional != null)
            tag.put(NBTConstants.CONDITIONAL, conditional!!.serialize())

        return tag
    }

    companion object {
        val DEFAULT_LOCATION = ModelResourceLocation("talkative:markers/marker#marker")

        fun deserialize(tag: CompoundTag?): Marker? {
            if (tag == null) return null
            val marker = Marker()

            if (tag.contains(NBTConstants.MARKER_LOCATION))
                marker.modelLocation = ModelResourceLocation(tag.getString(NBTConstants.MARKER_LOCATION))
            if (tag.contains(NBTConstants.MARKER_COLOUR))
                marker.overlayColour = tag.getInt(NBTConstants.MARKER_COLOUR)
            if (tag.contains(NBTConstants.MARKER_OFFSET)) {
                val list = tag.getList(NBTConstants.MARKER_OFFSET, Tag.TAG_FLOAT.toInt())
                marker.positionOffset = Vector3f((list[0] as FloatTag).asFloat, (list[1] as FloatTag).asFloat, (list[2] as FloatTag).asFloat)
            }
            if (tag.contains(NBTConstants.CONDITIONAL))
                marker.conditional = Conditional.deserialize(tag.getCompound(NBTConstants.CONDITIONAL))

            return marker
        }
    }
}