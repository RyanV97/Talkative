package dev.cryptcraft.talkative.api.actor

import net.minecraft.nbt.CompoundTag
import dev.cryptcraft.talkative.common.util.NBTConstants

class DisplayData {
    var overrideDisplayName: Boolean = false
    var displayName: String = "Actor"

    fun serialize(tag: CompoundTag): CompoundTag {

        return tag
    }

    companion object {
        fun deserialize(root: CompoundTag): DisplayData {
            val data = DisplayData()
            val tag = root.getCompound(NBTConstants.DISPLAY_DATA)

            if (tag.contains(NBTConstants.DISPLAY_NAME_OVERRIDE))
                data.overrideDisplayName = tag.getBoolean(NBTConstants.DISPLAY_NAME_OVERRIDE)
            if (tag.contains(NBTConstants.DISPLAY_NAME))
                data.displayName = tag.getString(NBTConstants.DISPLAY_NAME)

            return data
        }
    }

}