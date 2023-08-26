package dev.cryptcraft.talkative.mixin.entity;

import dev.cryptcraft.talkative.api.actor.ActorData;
import dev.cryptcraft.talkative.api.actor.ActorEntity;
import dev.cryptcraft.talkative.api.actor.markers.Marker;
import dev.cryptcraft.talkative.common.network.NetworkHandler;
import dev.cryptcraft.talkative.common.network.clientbound.SyncMarkerPacket;
import dev.cryptcraft.talkative.common.util.ActorUtil;
import dev.cryptcraft.talkative.common.util.NBTConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityActorDataMixin extends Entity implements ActorEntity {
    public LivingEntityActorDataMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private ActorData actorData;

    public ActorData getActorData() {
        return actorData;
    }

    public @NotNull ActorData getOrCreateActorData() {
        if (actorData == null)
            actorData = new ActorData();
        return actorData;
    }

    public void setActorData(ActorData serverActorDataData) {
        this.actorData = serverActorDataData;
        syncMarker();
    }

    @Unique
    private void syncMarker() {
        for (ServerPlayer player : NetworkHandler.getTrackingPlayers(this)) {
            Marker syncMarker = null;
            for(Marker marker : getActorData ().getMarkers()) {
                if (marker.getConditional() == null || marker.getConditional().eval(player)) {
                    syncMarker = marker;
                    break;
                }
            }
            new SyncMarkerPacket(getId(), syncMarker).sendToPlayer(player);
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        if (level.isClientSide || getActorData() == null || tickCount % 100 != 0) return;
        syncMarker();
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void saveTalkativeData(CompoundTag entityTag, CallbackInfo ci) {
        if (this.level.isClientSide || this.actorData == null)
            return;

        CompoundTag dataTag = new CompoundTag();
        ActorUtil.serialize(this, dataTag);
        entityTag.put(NBTConstants.ACTOR_DATA, dataTag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void loadTalkativeData(CompoundTag compoundTag, CallbackInfo ci) {
        if (this.level.isClientSide || !compoundTag.contains(NBTConstants.ACTOR_DATA))
            return;

        CompoundTag tag = compoundTag.getCompound(NBTConstants.ACTOR_DATA);
        if (tag.contains(NBTConstants.ACTOR_DATA_VERSION))
            ActorUtil.deserialize(this, tag);
        else
            ActorUtil.legacyDeserialize(this, tag);
    }
}
