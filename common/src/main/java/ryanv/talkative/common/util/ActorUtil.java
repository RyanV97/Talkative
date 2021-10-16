package ryanv.talkative.common.util;

import net.minecraft.nbt.CompoundTag;

import ryanv.talkative.api.IActorEntity;
import ryanv.talkative.common.data.Actor;
import ryanv.talkative.common.data.MarkerData;
import ryanv.talkative.consts.NBTConstants;
import ryanv.talkative.mixin.entity.LivingEntityActorDataMixin;

public class ActorUtil {

    public static void serialize(IActorEntity entityData, CompoundTag tag) {
        tag.putInt(NBTConstants.ACTOR_DATA_VERSION, 1);
    }

    public static void deserialize(IActorEntity entityData, CompoundTag tag) {
        Actor data = new Actor();

        data.markerData = MarkerData.Companion.deserialize(tag.getCompound(NBTConstants.MARKER_DATA));

        entityData.setActorData(data);
    }

    public static void legacyDeserialize(IActorEntity entityData, CompoundTag tag) {
        Actor data = new Actor();

        //Load Old Data

        entityData.setActorData(data);
    }

}
