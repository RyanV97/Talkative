package dev.cryptcraft.talkative.common.util

import net.minecraft.nbt.CompoundTag
import dev.cryptcraft.talkative.api.ActorEntity
import dev.cryptcraft.talkative.common.data.ActorData

object ActorUtil {

    @JvmStatic
    fun serialize(entityData: ActorEntity, tag: CompoundTag) {
        tag.putInt(NBTConstants.ACTOR_DATA_VERSION, 1)
        entityData.getActorData()?.serialize(tag)
    }

    @JvmStatic
    fun deserialize(entityData: ActorEntity, tag: CompoundTag?) {
        val data = ActorData.deserialize(tag!!)
        entityData.setActorData(data)
    }

    @JvmStatic
    fun legacyDeserialize(entityData: ActorEntity, tag: CompoundTag?) {
        val data = ActorData()

        //Load Old Data
        entityData.setActorData(data)
    }

}