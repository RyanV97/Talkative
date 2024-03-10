package dev.cryptcraft.talkative.server

import dev.cryptcraft.talkative.Talkative
import dev.cryptcraft.talkative.api.actor.DisplayData
import dev.cryptcraft.talkative.common.util.NBTConstants
import net.minecraft.nbt.CompoundTag

data class TalkativeWorldConfig(val playerDisplay: DisplayData?) {

    fun serialize(tag: CompoundTag = CompoundTag()): CompoundTag {
        if (playerDisplay != null)
            tag.put(NBTConstants.DISPLAY_DATA, playerDisplay.serialize())

        return tag
    }

    companion object {
        var INSTANCE: TalkativeWorldConfig? = null

        fun load() {
            Talkative.LOGGER.info("Loading World Config...")

            val tag = FileUtil.loadWorldConfigData()

            if (tag != null)
                INSTANCE = deserialize(tag)
            else
                INSTANCE = TalkativeWorldConfig(DisplayData())

            Talkative.LOGGER.info("Finished loading World Config")
        }

        fun save() {
            Talkative.LOGGER.info("Saving World Config...")
            FileUtil.writeWorldConfigToFile()
            Talkative.LOGGER.info("Finished saving World Config")
        }

        fun deserialize(tag: CompoundTag): TalkativeWorldConfig {
            return TalkativeWorldConfig(
                DisplayData.deserialize(tag.getCompound(NBTConstants.DISPLAY_DATA))
            )
        }
    }
}