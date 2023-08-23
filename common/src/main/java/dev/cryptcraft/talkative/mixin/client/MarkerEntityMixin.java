package dev.cryptcraft.talkative.mixin.client;

import dev.cryptcraft.talkative.api.actor.markers.Marker;
import dev.cryptcraft.talkative.api.actor.markers.MarkerEntity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public class MarkerEntityMixin implements MarkerEntity {
    private Marker marker = null;

    @Nullable
    @Override
    public Marker getMarker() {
        return this.marker;
    }

    @Override
    public void setMarker(@Nullable Marker marker) {
        this.marker = marker;
    }
}
