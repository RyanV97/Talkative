package dev.cryptcraft.talkative.api.actor

import dev.cryptcraft.talkative.common.util.NBTConstants
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation

class DisplayData {
    var overrideDisplayName: Boolean = false
    var displayName: String? = null

    var displayInConversation: Boolean = true
    var displayAsEntity: Boolean = true
    var displayTexture: ResourceLocation? = null
    var textureSize: IntArray? = null

    fun serialize(tag: CompoundTag = CompoundTag()): CompoundTag {
        tag.putBoolean(NBTConstants.DISPLAY_NAME_OVERRIDE, overrideDisplayName)
        if (displayName != null) tag.putString(NBTConstants.DISPLAY_NAME, displayName)

        tag.putBoolean(NBTConstants.DISPLAY_CONVERSATION, displayInConversation)
        tag.putBoolean(NBTConstants.DISPLAY_ENTITY, displayAsEntity)

        if (displayTexture != null) tag.putString(NBTConstants.DISPLAY_TEXTURE, displayTexture.toString())
        if (textureSize != null && (textureSize!![0] != 0 || textureSize!![1] != 0)) tag.putIntArray(NBTConstants.DISPLAY_TEXTURE_SIZE, textureSize)
        return tag
    }

    companion object {
        fun deserialize(tag: CompoundTag?): DisplayData? {
            if (tag == null) return null
            val data = DisplayData()

            if (tag.contains(NBTConstants.DISPLAY_NAME_OVERRIDE))
                data.overrideDisplayName = tag.getBoolean(NBTConstants.DISPLAY_NAME_OVERRIDE)
            if (tag.contains(NBTConstants.DISPLAY_NAME))
                data.displayName = tag.getString(NBTConstants.DISPLAY_NAME)

            if (tag.contains(NBTConstants.DISPLAY_CONVERSATION))
                data.displayInConversation = tag.getBoolean(NBTConstants.DISPLAY_CONVERSATION)
            if (tag.contains(NBTConstants.DISPLAY_ENTITY))
                data.displayAsEntity = tag.getBoolean(NBTConstants.DISPLAY_ENTITY)

            if (tag.contains(NBTConstants.DISPLAY_TEXTURE))
                data.displayTexture = ResourceLocation.tryParse(tag.getString(NBTConstants.DISPLAY_TEXTURE))
            if (tag.contains(NBTConstants.DISPLAY_TEXTURE_SIZE))
                data.textureSize = tag.getIntArray(NBTConstants.DISPLAY_TEXTURE_SIZE)

            return data
        }
    }

}