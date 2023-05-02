package ryanv.talkative.common.util

import net.minecraft.nbt.CompoundTag
import ryanv.talkative.api.ActorEntity
import ryanv.talkative.common.data.ServerActorData

object ActorUtil {

    @JvmStatic
    fun serialize(entityData: ActorEntity, tag: CompoundTag) {
        tag.putInt(NBTConstants.ACTOR_DATA_VERSION, 1)
        entityData.actorData.serialize(tag)
    }

    @JvmStatic
    fun deserialize(entityData: ActorEntity, tag: CompoundTag?) {
        val data = ServerActorData.deserialize(tag!!)
        entityData.actorData = data
    }

    @JvmStatic
    fun legacyDeserialize(entityData: ActorEntity, tag: CompoundTag?) {
        val data = ServerActorData()

        //Load Old Data
        entityData.actorData = data
    }

}