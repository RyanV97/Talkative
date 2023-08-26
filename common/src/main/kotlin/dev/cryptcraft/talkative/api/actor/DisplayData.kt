package dev.cryptcraft.talkative.api.actor

import dev.cryptcraft.talkative.common.util.NBTConstants
import net.minecraft.nbt.CompoundTag

class DisplayData {
    var overrideDisplayName: Boolean = false
    var displayName: String = "Actor"

    fun serialize(tag: CompoundTag = CompoundTag()): CompoundTag {
        tag.putBoolean(NBTConstants.DISPLAY_NAME_OVERRIDE, overrideDisplayName)
        tag.putString(NBTConstants.DISPLAY_NAME, displayName)
        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag): DisplayData {
            val data = DisplayData()

            if (tag.contains(NBTConstants.DISPLAY_NAME_OVERRIDE))
                data.overrideDisplayName = tag.getBoolean(NBTConstants.DISPLAY_NAME_OVERRIDE)
            if (tag.contains(NBTConstants.DISPLAY_NAME))
                data.displayName = tag.getString(NBTConstants.DISPLAY_NAME)

            return data
        }
    }

}