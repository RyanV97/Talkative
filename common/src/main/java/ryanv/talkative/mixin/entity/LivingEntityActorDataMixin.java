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
import ryanv.talkative.api.ActorData;
import ryanv.talkative.api.ActorEntity;
import ryanv.talkative.common.util.ActorUtil;
import ryanv.talkative.common.util.NBTConstants;

@Mixin(LivingEntity.class)
public abstract class LivingEntityActorDataMixin extends Entity implements ActorEntity {
    public LivingEntityActorDataMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    private ActorData serverActorDataData;

    public ActorData getActorData() {
        return serverActorDataData;
    }

    public void setActorData(ActorData serverActorDataData) {
        this.serverActorDataData = serverActorDataData;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void saveTalkativeData(CompoundTag entityTag, CallbackInfo ci) {
        if(this.level.isClientSide || this.serverActorDataData == null)
            return;

        CompoundTag dataTag = new CompoundTag();
        ActorUtil.serialize(this, dataTag);
        entityTag.put(NBTConstants.ACTOR_DATA, dataTag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void loadTalkativeData(CompoundTag compoundTag, CallbackInfo ci) {
        if(this.level.isClientSide || !compoundTag.contains(NBTConstants.ACTOR_DATA))
            return;

        CompoundTag tag = compoundTag.getCompound(NBTConstants.ACTOR_DATA);
        if(tag.contains(NBTConstants.ACTOR_DATA_VERSION))
            ActorUtil.deserialize(this, tag);
        else
            ActorUtil.legacyDeserialize(this, tag);
    }
}
