package ryanv.talkative.mixin.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ryanv.talkative.api.IActorEntity;
import ryanv.talkative.common.data.Actor;
import ryanv.talkative.common.util.ActorUtil;
import ryanv.talkative.common.consts.NBTConstants;

@Mixin(LivingEntity.class)
public abstract class LivingEntityActorDataMixin extends Entity implements IActorEntity {

    private Actor actorData = new Actor();

    public LivingEntityActorDataMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public Actor getActorData() {
        return actorData;
    }

    public void setActorData(Actor actorData) {
        this.actorData = actorData;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void save(CompoundTag entityTag, CallbackInfo ci) {
        if(this.level.isClientSide)
            return;
        CompoundTag dataTag = new CompoundTag();
        ActorUtil.serialize(this, dataTag);
        entityTag.put(NBTConstants.ACTOR_DATA, dataTag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void load(CompoundTag compoundTag, CallbackInfo ci) {
        if(this.level.isClientSide)
            return;
        if(compoundTag.contains(NBTConstants.ACTOR_DATA)) {
            CompoundTag tag = compoundTag.getCompound(NBTConstants.ACTOR_DATA);
            if(tag.contains(NBTConstants.ACTOR_DATA_VERSION))
                ActorUtil.deserialize(this, tag);
            else
                ActorUtil.legacyDeserialize(this, tag);
        }
    }

}
