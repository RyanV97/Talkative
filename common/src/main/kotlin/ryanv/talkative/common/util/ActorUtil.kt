package ryanv.talkative.common.util

import net.minecraft.nbt.CompoundTag
import ryanv.talkative.api.IActorEntity
import ryanv.talkative.common.data.Actor
import ryanv.talkative.common.consts.NBTConstants

object ActorUtil {

    @JvmStatic
    fun serialize(entityData: IActorEntity, tag: CompoundTag) {
        tag.putInt(NBTConstants.ACTOR_DATA_VERSION, 1)
        entityData.actorData.serialize(tag)
    }

    @JvmStatic
    fun deserialize(entityData: IActorEntity, tag: CompoundTag?) {
        val data = Actor.deserialize(tag!!)
        entityData.actorData = data
    }

    @JvmStatic
    fun legacyDeserialize(entityData: IActorEntity, tag: CompoundTag?) {
        val data = Actor()

        //Load Old Data
        entityData.actorData = data
    }

}